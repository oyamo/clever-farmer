package com.oyasis.fruity.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.oyasis.fruity.ui.fragments.InformationFragment;
import com.oyasis.fruity.util.DarwinConverter;
import com.oyasis.fruity.util.MikeConverter;
import com.oyasis.fruity.R;
import com.oyasis.fruity.util.ImageClassifierHelper;

import org.jetbrains.annotations.Nullable;
import org.tensorflow.lite.support.label.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//
public class ScannerActivity extends AppCompatActivity implements ImageClassifierHelper.ClassifierListener {
    private ImageCapture imageCapture = null;
    private ExecutorService cameraExecutor;
    private PreviewView viewFinder;

    private TextView label, score;
    private ImageButton flashBtn, attachImageBtn;
    private Button diagnosisBtn;

    private ImageClassifierHelper imageClassifierHelper;

    private static final String TAG = "ScannerActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        viewFinder = findViewById(R.id.cameraPreview);
        cameraExecutor = Executors.newSingleThreadExecutor();

        label = findViewById(R.id.label);
        score = findViewById(R.id.score);
        flashBtn = findViewById(R.id.flashBtn);
        attachImageBtn = findViewById(R.id.attachImage);
        diagnosisBtn = findViewById(R.id.diagnosisBtn);


        diagnosisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt = label.getText().toString();
                InformationFragment informationFragment = InformationFragment.newInstance(txt);
                informationFragment.show(getSupportFragmentManager(), "DISGNOSIS");
            }
        });

        imageClassifierHelper = new ImageClassifierHelper(
                 this, this
        );

        attachImageBtn.setOnClickListener(v->{
            pickImage();
        });

        starCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    @SuppressLint("SetTextI18n")
    private void starCamera() {
        ListenableFuture<ProcessCameraProvider> listenableCameraFuture = ProcessCameraProvider.getInstance(this);

        listenableCameraFuture.addListener(() -> {
           try {
               ProcessCameraProvider cameraProvider = listenableCameraFuture.get();
               Preview preview = new Preview.Builder().build();
               preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

               imageCapture = new ImageCapture.Builder()
                       .build();

               ImageAnalysis imageAnalysis =new ImageAnalysis.Builder()
                       .setTargetResolution(new Size(1280, 720))
                       .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                       .build();

               imageAnalysis.setAnalyzer(cameraExecutor, image -> {
                   Log.d(TAG, "starCamera: " + image.getFormat());
                   @SuppressLint("UnsafeOptInUsageError")
                   Image proxyImage = image.getImage();


                   if (proxyImage != null) {
                       Bitmap imgBitmap = image.getFormat() == 35 ?
                               MikeConverter.INSTANCE.toBitmap(proxyImage) :
                               DarwinConverter.INSTANCE.convertImageProxyToBitmap(image);

                       imageClassifierHelper.classify(imgBitmap, 0);
                   }

                   image.close();
               });



               CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

               cameraProvider.unbindAll();

               Camera camera = cameraProvider.bindToLifecycle( ScannerActivity.this, cameraSelector, imageAnalysis, preview);

               runOnUiThread(()->{
                   final int[] flashMode = {ImageCapture.FLASH_MODE_OFF};
                   flashBtn.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           if(camera.getCameraInfo().hasFlashUnit()) {
                               camera.getCameraControl().enableTorch( flashMode[0] == ImageCapture.FLASH_MODE_OFF);
                                flashBtn.setImageDrawable(
                                        flashMode[0] == ImageCapture.FLASH_MODE_OFF?
                                                AppCompatResources.getDrawable(ScannerActivity.this,R.drawable.ic_round_flash_off):
                                                AppCompatResources.getDrawable(ScannerActivity.this,R.drawable.ic_round_flash_on)
                                );
                               flashMode[0] = flashMode[0] == ImageCapture.FLASH_MODE_OFF?
                                       ImageCapture.FLASH_MODE_ON:
                                       ImageCapture.FLASH_MODE_OFF;
                           }
                       }
                   });
               });


           } catch (Exception ignored) {
               Log.d(TAG, "starCamera: Usecase Binding Faile");
           }
        }, ContextCompat.getMainExecutor(ScannerActivity.this));

    }

    /**
     *
     */
    private final ActivityResultLauncher<Intent> imagePicker =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), activityResult -> {
                if (activityResult.getResultCode() == Activity.RESULT_OK && activityResult.getData() != null) {
                    Uri uriObj = activityResult.getData().getData();
                    gotoLoadImage(uriObj);
                }
            });

    /**
     * @param uri
     */
    private void gotoLoadImage(Uri uri) {
        Intent intent = new Intent(this, LoadImageActivity.class);
        intent.putExtra("PHOTO_URI", uri.toString());
        startActivity(intent);
        finish();
    }

    /**
     *
     */
    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        imagePicker.launch(Intent.createChooser(intent, "Select Picture"));
    }



    @Override
    public void onResults(@Nullable List<Category> results, long inferenceTime) {
        List<Category> categories = new ArrayList<>();
        if (results == null) return;
        if (results.size() > 0) {
            categories = results;
        }

        Category greatestCategory = null;

        if (categories.size() > 0)
            greatestCategory = categories.get(0);


        for (Category category : categories) {
            if (category.getScore() > greatestCategory.getScore()) {
                greatestCategory = category;
            }
        }

        Log.d(TAG, "SCAN MADE");

        final long[] delay = {System.currentTimeMillis()};

        if (greatestCategory != null) {
            Category finalGreatestCategory = greatestCategory;
            runOnUiThread(()->{
                String placeholder = getString(R.string.score_val);
                int prob = (int) (finalGreatestCategory.getScore() * 100f);
                if (prob > 70) {
                    delay[0] = System.currentTimeMillis();
                    String predictedLabel = finalGreatestCategory.getLabel();
                    predictedLabel = predictedLabel.replace("_", " ");
                    label.setText(predictedLabel);
                    score.setText(String.format("%d%%", prob));
                    diagnosisBtn.setVisibility(View.VISIBLE);
                } else {
                    if(System.currentTimeMillis() - delay[0] > 5000) {
                        label.setText("Processing...");
                        score.setText("%-");
                        diagnosisBtn.setVisibility(View.GONE);
                    }
                }
            });
        }
    }




    @Override
    public void onError(@NonNull String error) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ScannerActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });

    }
}