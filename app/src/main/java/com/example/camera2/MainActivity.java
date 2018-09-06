package com.example.camera2;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private int attemptPermissionRequest = 0;

    private CameraManager mCameraManager    = null;
    private CameraHelper[] myCameras = null;
    private static final int CAMERA1 = 0;
    private static final int CAMERA2 = 1;
    private static Button mBtnCamera1;
    private static Button mBtnCamera2;
    private TextureView mTextureView;
    private int displayWidth;
    private int displayHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        displayWidth = displayMetrics.widthPixels;
        displayHeight = displayMetrics.heightPixels;

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        mTextureView = findViewById(R.id.textureView);

        mBtnCamera1 = findViewById(R.id.mBtnCamera1);
        mBtnCamera2 = findViewById(R.id.mBtnCamera2);

        mBtnCamera1.setOnClickListener(v -> {
           if (myCameras[CAMERA2].isOpen()) {
               myCameras[CAMERA2].closeCamera();
           }
           if (myCameras[CAMERA1] != null) {
               if (!myCameras[CAMERA1].isOpen()) {
                   myCameras[CAMERA1].openCamera();
               }
           }
        });

        mBtnCamera2.setOnClickListener(v -> {
            if (myCameras[CAMERA1].isOpen()) {
                myCameras[CAMERA1].closeCamera();
            }
            if (myCameras[CAMERA2] != null) {
                if (!myCameras[CAMERA2].isOpen()) {
                    myCameras[CAMERA2].openCamera();
                }
            }
        });

        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                closeAllCameras();
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

    private void closeAllCameras() {
        for (int i = 0; i < myCameras.length; i++) {
            if (myCameras[i].isOpen()) {
                myCameras[i].closeCamera();
            }
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

        getCameraList();
    }

    @Override
    protected void onStop() {
        super.onStop();

        closeAllCameras();
    }

    private void getCameraList() {
        try {
            String[] cameraIdList = mCameraManager.getCameraIdList();
            myCameras = new CameraHelper[cameraIdList.length];

            for (String cameraId : cameraIdList) {
                int id = Integer.parseInt(cameraId);
                // обработчик по камере
                myCameras[id] = new CameraHelper(mCameraManager, cameraId, displayWidth, displayHeight);
                myCameras[id].setTextureView(mTextureView);
                // выводим информацию по камере
                myCameras[id].viewFormatSize(ImageFormat.JPEG);
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.d(TAG, "CameraAccessException " + e.getMessage());
        }
    }
}
