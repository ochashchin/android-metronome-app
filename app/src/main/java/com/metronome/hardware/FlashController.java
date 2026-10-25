package com.metronome.hardware;

import android.content.Context;
import android.hardware.camera2.CameraManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class FlashController {

    private CameraManager cameraManager;
    private String cameraId;

    // Background scheduler for non-blocking torch timing synchronized with display pipeline
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // Latency compensation: delay torch onset (~16ms) so it turns on in exact unison
    // with the UI display pipeline frame rendering (VSYNC cycle)
    private static final long TORCH_ON_DELAY_MS = 16;
    // Hardware torch pulse duration matching screen flash duration (35ms snappy strobe)
    private static final long TORCH_DURATION_MS = 35;

    @Inject
    public FlashController(@ApplicationContext Context context) {
        try {
            cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (Exception ignored) {}
    }

    public void blink() {
        if (cameraManager == null || cameraId == null) return;
        scheduler.execute(() -> {
            try {
                if (TORCH_ON_DELAY_MS > 0) {
                    try {
                        Thread.sleep(TORCH_ON_DELAY_MS);
                    } catch (InterruptedException ignored) {}
                }
                cameraManager.setTorchMode(cameraId, true);
                try {
                    Thread.sleep(TORCH_DURATION_MS);
                } catch (InterruptedException ignored) {}
                cameraManager.setTorchMode(cameraId, false);
            } catch (Exception ignored) {}
        });
    }

    public void off() {
        try {
            cameraManager.setTorchMode(cameraId, false);
        } catch (Exception ignored) {}
    }
}
