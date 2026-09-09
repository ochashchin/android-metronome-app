package com.metronome.core.model;

public class HomeState {
    private final boolean isPlaying;
    private final Mode mode;
    private final float progress;
    private final boolean tooltip;

    public HomeState(boolean isPlaying, Mode mode, float progress, boolean tooltip) {
        this.isPlaying = isPlaying;
        this.mode = mode;
        this.progress = progress;
        this.tooltip = tooltip;
    }

    public HomeState(boolean isPlaying, Mode mode, float progress) {
        this(isPlaying, mode, progress, false);
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
        return new HomeState(isPlaying, this.mode, this.progress, this.tooltip);
    }

    public HomeState copyMode(Mode mode) {
        return new HomeState(this.isPlaying, mode, this.progress, this.tooltip);
    }

    public HomeState copyProgress(float progress) {
        return new HomeState(this.isPlaying, this.mode, progress, this.tooltip);
    }

    public HomeState copyTooltip(boolean tooltip) {
        return new HomeState(this.isPlaying, this.mode, this.progress, tooltip);
    }

    public boolean isPlaying() { return isPlaying; }
    public Mode getMode() { return mode; }
    public float getProgress() { return progress; }
    public boolean isTooltip() { return tooltip; }
}