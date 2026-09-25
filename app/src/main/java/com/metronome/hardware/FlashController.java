package com.metronome.hardware;

import android.content.Context;
import android.hardware.camera2.CameraManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class FlashController {

    private CameraManager cameraManager;
    private String cameraId;

    // Background scheduler for non-blocking torch timing synchronized with display pipeline
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // Latency compensation: delay torch onset slightly (~20ms) so it turns on
    // in exact unison with the UI display pipeline frame rendering
    private static final long TORCH_ON_DELAY_MS = 20;
    private static final long TORCH_DURATION_MS = 40;

    @Inject
    public FlashController(@ApplicationContext Context context) {
        try {
            cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (Exception ignored) {}
    }

    public void blink() {
        try {
            scheduler.schedule(() -> {
                try {
                    cameraManager.setTorchMode(cameraId, true);
                    scheduler.schedule(() -> {
                        try {
                            cameraManager.setTorchMode(cameraId, false);
                        } catch (Exception ignored) {}
                    }, TORCH_DURATION_MS, TimeUnit.MILLISECONDS);
                } catch (Exception ignored) {}
            }, TORCH_ON_DELAY_MS, TimeUnit.MILLISECONDS);
        } catch (Exception ignored) {}
    }

    public void off() {
        try {
            cameraManager.setTorchMode(cameraId, false);
        } catch (Exception ignored) {}
    }
}
