package com.metronome.hardware;

import android.content.Context;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.Looper;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class FlashController {

    private CameraManager cameraManager;
    private String cameraId;

    @Inject
    public FlashController(@ApplicationContext Context context) {
        try {
            cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (Exception ignored) {}
    }

    public void blink() {
        try {
            cameraManager.setTorchMode(cameraId, true);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                try {
                    cameraManager.setTorchMode(cameraId, false);
                } catch (Exception ignored) {}
            }, 30);
        } catch (Exception ignored) {}
    }

    public void off() {
        try {
            cameraManager.setTorchMode(cameraId, false);
        } catch (Exception ignored) {}
    }
}
