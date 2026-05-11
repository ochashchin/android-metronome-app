package com.metronome.core.engine;

import com.metronome.core.model.ReviewDialogState;
import com.metronome.core.util.AppState;

public interface ReviewDialogEngine {
    void dialogCount(int n);
    AppState<ReviewDialogState> getReviewState();
}
