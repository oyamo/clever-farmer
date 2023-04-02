package com.oyasis.fruity.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    private final String[] permisions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermission(permisions)) {
            moveToScanner();
        } else {
            requestPermissionLauncher.launch(permisions);
        }

    }

    private boolean checkPermission(String []permission) {
        for (String s : permission) {
            if(ContextCompat.checkSelfPermission(this, s)
                    != PackageManager.PERMISSION_GRANTED)
                return false;
        }

        return true;
    }

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
    registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), booleanMap -> {

        if (booleanMap.containsValue(Boolean.FALSE) || booleanMap.containsValue(false)) {
            finish();
            return;
        }

        moveToScanner();

    });

    private void moveToScanner() {
        Intent intent = new Intent(this, NavHostActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        runOnUiThread(() -> {
            try {
                Thread.sleep(2000);

            } catch (Exception e) {
                // Pass
            } finally {
                startActivity(intent);
                finish();
            }
        });
    }
}