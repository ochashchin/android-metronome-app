package com.metronome.core.model;

public class ReviewDialogState {
    private final int count;
    private final boolean show;
    private static final int SHOW_COUNTS = 3;

    public ReviewDialogState(boolean show, int count) {
        this.show = show;
        this.count = count;
    }

    public ReviewDialogState copyIsCount(int count) {
        if (count < SHOW_COUNTS) {
            return new ReviewDialogState(false, count);
        } else {
            return new ReviewDialogState(true, count);
        }
    }

    public ReviewDialogState copyIsShow(boolean show) {
        if (show && count == SHOW_COUNTS) {
            return new ReviewDialogState(true, count);
        } else {
            return new ReviewDialogState(show, count);
        }
    }

    public boolean isShow() { return show; }
    public int getCount()   { return count; }
}
