package com.example.chromecastone.Activity;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_AUDIO;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.chromecastone.R;

public class StartActivity extends AppCompatActivity {

    private final int BELOW_ANDROID_13 = 101;
    private final int ABOVE_ANDROID_13 = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        findViewById(R.id.continue_application_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPermissionGranted()) {
                    moveMainScreen();
                } else {
                    takePermission();
                }
            }
        });

        findViewById(R.id.sa_share_app).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                    String shareMessage = "\nLet me recommend you this application\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + getPackageName() + "\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.sa_rate_us).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(myAppLinkToMarket);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.sa_privacy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(StartActivity.this, "Privacy Policy", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {

            int read_external_storage = ContextCompat.checkSelfPermission(StartActivity.this, READ_EXTERNAL_STORAGE);

            return read_external_storage == PackageManager.PERMISSION_GRANTED;

        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {

            int read_media_images = ContextCompat.checkSelfPermission(StartActivity.this, READ_MEDIA_IMAGES);
            int read_media_video = ContextCompat.checkSelfPermission(StartActivity.this, READ_MEDIA_VIDEO);
            int read_media_audio = ContextCompat.checkSelfPermission(StartActivity.this, READ_MEDIA_AUDIO);

            return read_media_images == PackageManager.PERMISSION_GRANTED
                    && read_media_video == PackageManager.PERMISSION_GRANTED
                    && read_media_audio == PackageManager.PERMISSION_GRANTED;

        }

        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case BELOW_ANDROID_13:
                boolean gotPermission1 = grantResults.length > 0;

                for (int result : grantResults) {
                    gotPermission1 &= result == PackageManager.PERMISSION_GRANTED;
                }

                if (gotPermission1) {
                    moveMainScreen();
                } else {
                    Toast.makeText(StartActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    // finish();
                }
                break;

            case ABOVE_ANDROID_13:
                boolean gotPermission2 = grantResults.length > 0;

                for (int result : grantResults) {
                    gotPermission2 &= result == PackageManager.PERMISSION_GRANTED;
                }

                if (gotPermission2) {
                    moveMainScreen();
                } else {
                    Toast.makeText(StartActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    // finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void moveMainScreen() {
        startActivity(new Intent(StartActivity.this, DashBoardActivity.class));
    }

    private void takePermission() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {

            ActivityCompat.requestPermissions(StartActivity.this,
                    new String[]{READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_AUDIO}, ABOVE_ANDROID_13);

        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {

            ActivityCompat.requestPermissions(StartActivity.this,
                    new String[]{READ_EXTERNAL_STORAGE}, BELOW_ANDROID_13);
        }
    }
}