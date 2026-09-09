package com.metronome.ui.components;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.TooltipCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.gmail.specifickarma.metronome.R;

public class CenterButton extends ConstraintLayout implements Transition {

    private ConstraintLayout viewOff;
    boolean isOnOff = false;
    private Runnable hideTooltipRunnable;

    public CenterButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_center_button, this);

        viewOff = findViewById(R.id.center_button_off);

        setClickable(true);
        setFocusable(true);
    }

    @Override
    public boolean performClick() {
        isOnOff = !isOnOff;
        if (isOnOff) {
            hideTooltip();
        }
        updateUI();
        return super.performClick();
    }

    private void updateUI() {
        delayedTransition(100, viewOff);
        viewOff.setVisibility(isOnOff ? View.INVISIBLE : View.VISIBLE);
    }

    public void setPlaying(boolean isPlaying) {
        this.isOnOff = isPlaying;
        if (isPlaying) {
            hideTooltip();
        }
        updateUI();
    }

    public void setTooltip(boolean show) {
        if (show) {
            showTooltip(3000L);
        } else {
            hideTooltip();
        }
    }

    public void showTooltip(long durationMs) {
        if (isOnOff) return;

        TooltipCompat.setTooltipText(this, getContext().getString(R.string.press_to_start));
        post(() -> {
            float x = getWidth() / 2f;
            float y = getHeight();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                performLongClick(x, y);
            } else {
                performLongClick();
            }

            if (hideTooltipRunnable != null) {
                removeCallbacks(hideTooltipRunnable);
            }
            hideTooltipRunnable = () -> {
                TooltipCompat.setTooltipText(this, null);
                if (!isOnOff) {
                    post(() -> TooltipCompat.setTooltipText(this, getContext().getString(R.string.press_to_start)));
                }
            };
            postDelayed(hideTooltipRunnable, durationMs);
        });
    }

    public void hideTooltip() {
        if (hideTooltipRunnable != null) {
            removeCallbacks(hideTooltipRunnable);
            hideTooltipRunnable = null;
        }
        TooltipCompat.setTooltipText(this, null);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (hideTooltipRunnable != null) {
            removeCallbacks(hideTooltipRunnable);
            hideTooltipRunnable = null;
        }
    }

}
