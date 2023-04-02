package com.oyasis.fruity.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.oyasis.fruity.R;
import com.oyasis.fruity.util.ImageClassifierHelper;

import org.jetbrains.annotations.Nullable;
import org.tensorflow.lite.support.label.Category;

import java.util.ArrayList;
import java.util.List;

public class LoadImageActivity extends AppCompatActivity implements ImageClassifierHelper.ClassifierListener {

    private TextView label, score;
    private ImageButton scanBtn, attachImageBtn;
   private ImageView imageView;

   private ImageClassifierHelper imageClassifierHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_image_btn);

        imageView = findViewById(R.id.cameraPreview);
        scanBtn = findViewById(R.id.scanBtn);
        attachImageBtn = findViewById(R.id.attachImage);
        imageClassifierHelper = new ImageClassifierHelper(
                this, this
        );

        label = findViewById(R.id.label);
        score = findViewById(R.id.score);

        attachImageBtn.setOnClickListener(v->{
            pickImage();
        });


        scanBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, ScannerActivity.class);
            startActivity(intent);
            finish();
        });


    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        imagePicker.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private void predictBitmap(Uri uri) {
        Bitmap imgBitmap = uriToBitmap(uri);

        imageView.setImageBitmap(imgBitmap);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        imageClassifierHelper.classify(imgBitmap, 0);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if(intent != null) {
            Bundle extras = intent.getExtras();
            String uri = extras.getString("PHOTO_URI");
            if (uri != null ) {
                Uri imgUri = Uri.parse(uri);
                predictBitmap(imgUri);
            }
        }
    }

    /**
     * Request for camera permision
     */
    private final ActivityResultLauncher<Intent> imagePicker =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), activityResult -> {
                if (activityResult.getResultCode() == Activity.RESULT_OK && activityResult.getData() != null) {
                    Uri uriObj = activityResult.getData().getData();
                    predictBitmap(uriObj);
                }
            });

    /**
     *
     * @param uri
     * @return
     */
    private Bitmap uriToBitmap(Uri uri) {
        try{
            return MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (Exception e) {
            return  null;
        }
    }

    @Override
    public void onError(@NonNull String error) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoadImageActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
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

        if (greatestCategory != null) {
            Category finalGreatestCategory = greatestCategory;
            runOnUiThread(()->{
                String placeholder = getString(R.string.score_val);
                int prob = (int) (finalGreatestCategory.getScore() * 100f);
                if (prob > 70) {
                    String predictedLabel = finalGreatestCategory.getLabel();
                    predictedLabel = predictedLabel.replace("_", " ");
                    label.setText(predictedLabel);
                    score.setText(String.format("%d%%", prob));
                }
            });
        }
    }
}