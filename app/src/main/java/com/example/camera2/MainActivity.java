package com.example.camera2;

import android.arch.lifecycle.LifecycleObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private int attemptPermissionRequest = 0;
    private CameraHelper cameraHelper;


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
