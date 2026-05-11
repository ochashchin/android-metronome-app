package com.metronome.ui.review;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.metronome.core.engine.MetronomeEngine;
import com.metronome.core.model.ReviewDialogState;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ReviewViewModel extends ViewModel {

    private final MutableLiveData<ReviewDialogState> _state = new MutableLiveData<>();
    public final LiveData<ReviewDialogState> showReview = _state;

    private final MetronomeEngine engine;

    @Inject
    public ReviewViewModel(MetronomeEngine engine) {
        this.engine = engine;
    }

    public void dialogCount(int n) {
        engine.dialogCount(n);
    }

    public void scheduleReview(long delayMs) {
        Executors.newSingleThreadScheduledExecutor().schedule(
                () -> _state.postValue(engine.getReviewState().getValue()),
                delayMs,
                TimeUnit.MILLISECONDS
        );
    }
}
