package com.metronome.hardware;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class VibrationManager {

    private final Vibrator vibrator;
    private static final int DURATION = 50;

    @Inject
    public VibrationManager(@ApplicationContext Context context) {
        // Initialize the vibrator once during injection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager vm = (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            this.vibrator = vm != null ? vm.getDefaultVibrator() : null;
        } else {
            this.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
    }

    /**
     * Triggers a single vibration pulse based on the Android version.
     */
    public void vibrate() {
        if (vibrator == null || !vibrator.hasVibrator()) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Use VibrationEffect for API 26+
            vibrator.vibrate(
                    VibrationEffect.createOneShot(DURATION, VibrationEffect.DEFAULT_AMPLITUDE)
            );
        } else {
            // Deprecated vibration call for older versions
            vibrator.vibrate(DURATION);
        }
    }
}