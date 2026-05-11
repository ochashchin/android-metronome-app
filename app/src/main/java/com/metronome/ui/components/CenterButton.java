package com.metronome.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.gmail.specifickarma.metronome.R;

public class CenterButton extends ConstraintLayout implements Transition {

    private ConstraintLayout viewOff;
    boolean isOnOff = false;

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
        updateUI();
        return super.performClick();
    }

    private void updateUI() {
        delayedTransition(100, viewOff);
        viewOff.setVisibility(isOnOff ? View.INVISIBLE : View.VISIBLE);
    }

    public void setPlaying(boolean isPlaying) {
        this.isOnOff = isPlaying;
        updateUI();
    }

}
