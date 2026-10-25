package com.metronome.ui.components;

import android.content.Context;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.metronome.core.model.Mode;

public class FlashOverlayView extends View {

    // Screen flash duration matching hardware torch pulse duration (35ms = ~2 frames at 60Hz)
    private static final long FLASH_DURATION_MS = 35;

    public FlashOverlayView(Context context) {
        super(context);
        init();
    }

    public FlashOverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FlashOverlayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundColor(0xFFFFFFFF);
        setAlpha(0f);
        setVisibility(View.VISIBLE);
        setClickable(false);
        setFocusable(false);
    }

    public void setPlaying(boolean isPlaying, Mode mode) {
        if (!isPlaying || mode != Mode.FLASH) {
            reset();
        }
    }

    public void blink() {
        Runnable action = () -> {
            animate().cancel();
            setAlpha(0.85f);
            animate()
                    .alpha(0f)
                    .setDuration(FLASH_DURATION_MS)
                    .setInterpolator(new LinearInterpolator())
                    .start();
        };
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action.run();
        } else {
            post(action);
        }
    }

    public void reset() {
        Runnable action = () -> {
            animate().cancel();
            setAlpha(0f);
        };
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action.run();
        } else {
            post(action);
        }
    }
}
