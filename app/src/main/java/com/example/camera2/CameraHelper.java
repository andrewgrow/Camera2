package com.example.camera2;

import android.annotation.SuppressLint;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import java.util.Arrays;

public class CameraHelper {

    private static final String TAG = CameraHelper.class.getSimpleName();

    private final CameraManager mCameraManager;
    private final String mCameraId;
    private final int displayWidth;
    private final int displayHeight;
    private CameraDevice mCameraDevice;
    private final CameraDevice.StateCallback mCameraCallBack;
    private TextureView mTextureView;
    private CameraCaptureSession mSession;


    public CameraHelper(@NonNull CameraManager mCameraManager, @NonNull final String mCameraId,
                        int displayWidth, int displayHeight) {
        this.mCameraManager = mCameraManager;
        this.mCameraId = mCameraId;
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;

        mCameraCallBack = new CameraDevice.StateCallback() {

            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                mCameraDevice = camera;
                createCameraPreviewSession();
                Log.d(TAG, "Open camera with id: " + mCameraId);
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice camera) {
                closeCamera();
                Log.d(TAG, "disconnect camera with id: " + mCameraId);
            }

            @Override
            public void onError(@NonNull CameraDevice camera, int error) {
                Log.e(TAG, "error camera with id: " + mCameraId + ", error = " + error);
                closeCamera();
            }
        };
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

    public boolean isOpen() {
        return mCameraDevice != null;
    }

    @SuppressLint("MissingPermission")
    public void openCamera() {
        try {
            mCameraManager.openCamera(mCameraId, mCameraCallBack, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void closeCamera() {
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    public void setTextureView(TextureView textureView) {
        mTextureView = textureView;
    }

    private void createCameraPreviewSession() {
        SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(displayWidth, displayHeight);
        Surface surface = new Surface(surfaceTexture);
        try {
            final CaptureRequest.Builder builder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(surface);

            mCameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    mSession = session;
                    try {
                        mSession.setRepeatingRequest(builder.build(), null, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            }, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }
}
