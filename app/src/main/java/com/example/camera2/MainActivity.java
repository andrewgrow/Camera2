package com.example.camera2;

import android.arch.lifecycle.LifecycleObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private int attemptPermissionRequest = 0;
    private CameraHelper cameraHelper;
    private long takePictureTimestamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraHelper = new CameraHelper();
        getLifecycle().addObserver(cameraHelper);

        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame, cameraHelper.getNewCameraFragment())
                    .commit();
        }

        findViewById(R.id.btnTakeSnapshot).setOnClickListener(v -> {
            long now = System.currentTimeMillis();
            if (takePictureTimestamp + TimeUnit.SECONDS.toMillis(1)
                    < now) {
                takePictureTimestamp = now;
                cameraHelper.takePicture();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        if (!CameraPermissionHelper.hasCameraPermission(this) && attemptPermissionRequest < 5) {
            attemptPermissionRequest++;
            CameraPermissionHelper.requestCameraPermission(this);
            return;
        } else if (!CameraPermissionHelper.hasCameraPermission(this) && attemptPermissionRequest >= 5) {
            finish();
            return;
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

}
