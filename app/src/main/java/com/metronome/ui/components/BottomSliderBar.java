package com.metronome.ui.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.gmail.specifickarma.metronome.R;
import com.metronome.core.model.Mode;

import java.util.EnumMap;
import java.util.Map;

public class BottomSliderBar extends ConstraintLayout implements Transition {

    private OnModeSelectedListener listener;
    private Mode activeMode = Mode.SOUND;
    private View bottomButton;
    private boolean isOnOff = true;
    private final Map<Mode, View> onViews = new EnumMap<>(Mode.class);
    private final Map<Mode, View> offViews = new EnumMap<>(Mode.class);

    private ConstraintLayout bottom_slider_button_off;
    private ConstraintLayout bottom_slider_button_sound_on;
    private ConstraintLayout bottom_slider_button_sound_off;
    private ConstraintLayout bottom_slider_button_flash_on;
    private ConstraintLayout bottom_slider_button_flash_off;
    private ConstraintLayout bottom_slider_button_pulse_on;
    private ConstraintLayout bottom_slider_button_pulse_off;

    public interface OnModeSelectedListener {
        void onModeSelected(Mode mode);
    }

    public BottomSliderBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_bottom_slider, this);

        bottomButton = findViewById(R.id.bottom_slider);
        bottom_slider_button_off = findViewById(R.id.bottom_slider_button_off);
        bottom_slider_button_sound_on = findViewById(R.id.bottom_slider_button_sound_on);
        bottom_slider_button_sound_off = findViewById(R.id.bottom_slider_button_sound_off);
        bottom_slider_button_flash_on = findViewById(R.id.bottom_slider_button_flash_on);
        bottom_slider_button_flash_off = findViewById(R.id.bottom_slider_button_flash_off);
        bottom_slider_button_pulse_on = findViewById(R.id.bottom_slider_button_pulse_on);
        bottom_slider_button_pulse_off = findViewById(R.id.bottom_slider_button_pulse_off);

        initViewMaps();

        initModeSelection();
    }

    private void initViewMaps() {
        onViews.put(Mode.SOUND, bottom_slider_button_sound_on);
        offViews.put(Mode.SOUND, bottom_slider_button_sound_off);
        onViews.put(Mode.FLASH, bottom_slider_button_flash_on);
        offViews.put(Mode.FLASH, bottom_slider_button_flash_off);
        onViews.put(Mode.PULSE, bottom_slider_button_pulse_on);
        offViews.put(Mode.PULSE,bottom_slider_button_pulse_off);
    }

    public void setOnModeSelectedListener(OnModeSelectedListener listener) {
        this.listener = listener;
    }

    public void setPlaying(boolean isPlaying) {
        this.isOnOff = isPlaying;
        delayedTransition(100, bottom_slider_button_off);
        setVisibility(bottom_slider_button_off, !isOnOff);
        updateUI(activeMode);
    }

    public void setMode(Mode mode) {
        activeMode = mode;
        transition(mode);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initModeSelection() {
        final PointF lastPoint = new PointF();
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isOnOff) return true;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        lastPoint.set(event.getX(), event.getY());
                        Mode mode = getCrossedRegion(bottomButton, lastPoint);

                        if (mode != activeMode) {
                            updateUI(mode);
                            transition(mode);

                            if (listener != null) {
                                listener.onModeSelected(mode);
                            }

                            activeMode = mode;
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        v.performClick();
                        break;
                }
                return true;
            }
        });
    }

    private Mode getCrossedRegion(View btn, PointF p) {
        View parent = (View) btn.getParent();
        if (parent == null || p == null) return Mode.FLASH;

        float x = p.x;
        float w = btn.getWidth();
        float pw = parent.getWidth();

        if (x < w) return Mode.SOUND;
        if (x > pw - w) return Mode.PULSE;

        return Mode.FLASH;
    }

    private void updateUI(Mode mode) {
        for (Mode c : Mode.values()) {
            boolean isActive = c == mode;
            setVisibility(onViews.get(c), isOnOff && isActive);
            setVisibility(offViews.get(c), !isOnOff && isActive);
            setVisibility(offViews.get(c), isActive);
        }
        activeMode = mode;
    }

    private void setVisibility(View view, boolean visible) {
        if (view == null) return;
        int vis = visible ? View.VISIBLE : View.INVISIBLE;
        view.setVisibility(vis);
    }

    private void transition(Mode mode) {
        ConstraintLayout parent = (ConstraintLayout) bottomButton.getParent();

        ConstraintSet set = new ConstraintSet();
        set.clone(parent);

        float bias;
        switch (mode) {
            case SOUND: bias = 0f; break;
            case FLASH: bias = 0.5f; break;
            case PULSE: bias = 1f; break;
            default: bias = 0f;
        }
        set.setHorizontalBias(bottomButton.getId(), bias);

        TransitionManager.beginDelayedTransition(
                parent,
                new ChangeBounds()
        );

        set.applyTo(parent);
    }
}