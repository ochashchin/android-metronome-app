package com.metronome.ui.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.gmail.specifickarma.metronome.R;

public class TopSliderBar extends ConstraintLayout implements Transition {

    private ImageView d45, d90, d135, d180, d225, d270;
    private static final float MAX_TOTAL_ROTATION = 270f;
    boolean isOnOff = false;

    public interface OnProgressChangeListener {
        void onProgressChanged(int progress);
    }

    private OnProgressChangeListener listener;
    private ConstraintLayout topSliderButton;
    private ConstraintLayout top_slider_button_off;

    public TopSliderBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_top_slider, this);

        topSliderButton = findViewById(R.id.top_slider);
        top_slider_button_off = findViewById(R.id.top_slider_button_off);
        d45 = findViewById(R.id.d45);
        d90 = findViewById(R.id.d90);
        d135 = findViewById(R.id.d135);
        d180 = findViewById(R.id.d180);
        d225 = findViewById(R.id.d225);
        d270 = findViewById(R.id.d270);

        setupTouchListener();
    }

    public void setOnProgressChangeListener(OnProgressChangeListener listener) {
        this.listener = listener;
    }

    public void setPlaying(boolean isPlaying) {
        this.isOnOff = isPlaying;
        updateUI();
    }

    private void updateUI() {
        delayedTransition(100, top_slider_button_off);
        top_slider_button_off.setVisibility(isOnOff ? View.INVISIBLE : View.VISIBLE);
    }

    public void setProgress(float progress) {
        float totalRotationDegrees = (progress * MAX_TOTAL_ROTATION) / 100f;
        float viewRotation;
        if (totalRotationDegrees <= 45) {
            viewRotation = -45 + totalRotationDegrees;
        } else {
            viewRotation = -45 + totalRotationDegrees;
            if (viewRotation > 180) {
                viewRotation -= 360;
            }
        }
        rotateControls(viewRotation);
        fillProgress(progress);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupTouchListener() {
        this.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {

                if(!isOnOff) return true;

                if (constrainAngleSearchRegion((View) topSliderButton.getParent(), event)) {

                    int angle = getRotDeg(v, event);

                    rotateControls(angle);
                    fillProgress(getPercentage(angle));

                    if (listener != null) {
                        listener.onProgressChanged((int) getPercentage(angle));
                    }
                }

                return true;
            }
            return true;
        });
    }

    private void rotateControls(float angle) {
        View parent = (View) topSliderButton.getParent();
        parent.setRotation(angle);
        topSliderButton.setRotation(-angle);
    }

    private int calculateBpmFromAngle(float angle) {
        int result = (int) (getPercentage(angle) * 1.8 + 40);

        if(result % 2 == 0){
            return result;
        } else {
            return result +1;
        }
    }

    private boolean constrainAngleSearchRegion(View button, MotionEvent event) {
        int rotDeg = getRotDeg(button, event);
        if (Math.abs(Math.abs(rotDeg) - Math.abs(button.getRotation())) <= 30) {
            if (rotDeg >= -45 || rotDeg <= -135) {
                return true;
            }
        }
        return false;
    }

    private int getRotDeg(View button, MotionEvent event) {
        int rotDeg;
        Rect rectf = new Rect();
        button.getGlobalVisibleRect(rectf);
        rotDeg = (int) (Math.toDegrees(Math.atan2(rectf.centerY() - event.getRawY(), rectf.centerX() - event.getRawX())));
        return rotDeg;
    }

    private float getPercentage(float value) {
        //getting percentage from 270 angle
        if (value >= -45) {
            value += 45;
            return (int) (value / 2.7);
        } else {
            return (int) ((180 + 45 + 180 - Math.abs(value)) / 2.7);
        }
    }

    private void fillProgress(float percent) {
        float totalRotation = percent * MAX_TOTAL_ROTATION / 100f;
        d45.setRotation(clamp(totalRotation, 0f, 45f));
        d90.setRotation(clamp(totalRotation, 0f, 90f));
        d135.setRotation(clamp(totalRotation, 0f, 135f));
        d180.setRotation(clamp(totalRotation, 0f, 180f));
        d225.setRotation(clamp(totalRotation, 0f, 225f));
        d270.setRotation(clamp(totalRotation, 0f, 270f));
    }

    private float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }
}