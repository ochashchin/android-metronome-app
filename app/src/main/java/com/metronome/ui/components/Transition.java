package com.metronome.ui.components;

import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

public interface Transition {

     default void delayedTransition(long duration, ViewGroup vg) {
        android.transition.Transition transition = new AutoTransition();
        transition.setInterpolator(new LinearInterpolator());
        transition.setDuration(duration); // optional
        TransitionManager.beginDelayedTransition(vg, transition);
    }
}
