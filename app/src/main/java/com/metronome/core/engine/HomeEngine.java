package com.metronome.core.engine;

import com.metronome.core.model.Mode;
import com.metronome.core.model.HomeState;
import com.metronome.core.util.AppState;

public interface HomeEngine {
    void start();
    void stop();
    void setMode(Mode mode);
    void setProgress(int progress);
    AppState<HomeState> getState();
}
