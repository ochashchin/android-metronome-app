package com.metronome.core.model;

public class BannerState {

    private final boolean onLoad;
    private final boolean onAnim;
    private final boolean onPlayed;
    private final boolean onReset;

    public BannerState(boolean onLoad, boolean onAnim, boolean onPlayed, boolean onReset) {
        this.onLoad = onLoad;
        this.onAnim = onAnim;
        this.onPlayed = onPlayed;
        this.onReset = onReset;
    }

    public boolean isOnLoad() {
        return onLoad;
    }

    public boolean isOnAnim() {
        return onAnim;
    }

    public boolean isOnPlayed() {
        return onPlayed;
    }

    public boolean isOnReset() {
        return onReset;
    }

    public BannerState onPlayed(boolean played) {
        return new BannerState(this.onLoad, this.onAnim, played, this.onReset);
    }

    public BannerState copyOnAnim(boolean anim) {
        return new BannerState(this.onLoad, anim, this.onPlayed, this.onReset);
    }

    public BannerState copyOnLoaded(boolean load) {
        return new BannerState(load, this.onAnim, this.onPlayed, this.onReset);
    }

    public BannerState copyOnReset(boolean reset) {
        return new BannerState(this.onLoad, this.onAnim, this.onPlayed, reset);
    }
}