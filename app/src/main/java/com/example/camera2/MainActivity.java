package com.example.camera2;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.TextureView;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private int attemptPermissionRequest = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
