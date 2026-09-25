package com.metronome.ui.components;

import android.content.Context;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.metronome.core.model.Mode;

public class FlashOverlayView extends View {

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
        setVisibility(View.GONE);
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
            setVisibility(View.VISIBLE);
            animate().cancel();
            setAlpha(0.8f);
            animate()
                    .alpha(0f)
                    .setDuration(80)
                    .withEndAction(() -> setVisibility(View.GONE))
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
            setVisibility(View.GONE);
        };
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action.run();
        } else {
            post(action);
        }
    }
}
