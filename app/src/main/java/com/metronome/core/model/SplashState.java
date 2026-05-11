package com.metronome.core.model;

public class SplashState {
    private final boolean isPlaying;

    public SplashState(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public SplashState copyIsPlaying(boolean isPlaying) {
        return new SplashState(isPlaying);
    }

    public boolean isPlaying() { return isPlaying; }

}