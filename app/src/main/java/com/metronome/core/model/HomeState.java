package com.metronome.core.model;

public class HomeState {
    private final boolean isPlaying;
    private final Mode mode;
    private final float progress;

    public HomeState(boolean isPlaying, Mode mode, float progress) {
        this.isPlaying = isPlaying;
        this.mode = mode;
        this.progress = progress;
    }

    public int getBpm() {
        int result = (int) (progress * 1.8 + 40);
        return (result % 2 == 0) ? result : result + 1;
    }

    public int getMs() {
        int bpm = getBpm();
        return (int) Math.round(60000.0 / bpm);
    }

    public HomeState copyIsPlaying(boolean isPlaying) {
        return new HomeState(isPlaying, this.mode, this.progress);
    }

    public HomeState copyMode(Mode mode) {
        return new HomeState(this.isPlaying, mode, this.progress);
    }

    public HomeState copyProgress(float progress) {
        return new HomeState(this.isPlaying, this.mode, progress);
    }

    public boolean isPlaying() { return isPlaying; }
    public Mode getMode() { return mode; }
    public float getProgress() { return progress; }
}