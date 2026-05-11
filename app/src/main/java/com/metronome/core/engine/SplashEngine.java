package com.metronome.core.engine;

import com.metronome.core.model.SplashState;
import com.metronome.core.util.AppState;

public interface SplashEngine {
    void splashOn();
    void splashOff();
    AppState<SplashState> getSplashState();
}
