package com.example.camera2;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private CameraManager mCameraManager    = null;
    CameraHelper[] myCameras = null;
    private int attemptPermissionRequest = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
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

    private void getCameraList() {
        try {
            String[] cameraIdList = mCameraManager.getCameraIdList();
            myCameras = new CameraHelper[cameraIdList.length];

            for (String cameraId : cameraIdList) {
                int id = Integer.parseInt(cameraId);
                // обработчик по камере
                myCameras[id] = new CameraHelper(mCameraManager, cameraId);
                // выводим информацию по камере
                myCameras[id].viewFormatSize(ImageFormat.JPEG);
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.d(TAG, "CameraAccessException " + e.getMessage());
        }
    }
}
