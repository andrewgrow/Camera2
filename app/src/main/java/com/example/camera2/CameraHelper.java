package com.example.camera2;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Size;

public class CameraHelper {

    private static final String TAG = CameraHelper.class.getSimpleName();

    private final CameraManager mCameraManager;
    private final String mCameraId;

    public CameraHelper(@NonNull CameraManager mCameraManager, @NonNull String mCameraId) {
        this.mCameraManager = mCameraManager;
        this.mCameraId = mCameraId;
    }

    public void viewFormatSize(int formatSize) {
        try {
            CameraCharacteristics cc = mCameraManager.getCameraCharacteristics(mCameraId);

            // Получение списка выходного формата, который поддерживает камера
            StreamConfigurationMap configurationMap =
                    cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            // Список разрешений которые поддерживают формат jpeg
            Size[] sizes = configurationMap.getOutputSizes(ImageFormat.JPEG);


            Log.d(TAG, "cameraId = " + mCameraId + ", characteristics = " + cc.toString());
            Log.d(TAG, "sizes:");
            for (Size size : sizes) {
                Log.d(TAG, "w: " + size.getWidth() + ", h: " + size.getHeight());
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.d(TAG, "CameraAccessException " + e.getMessage());
        }
    }
}
