package com.metronome.ui.banner;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.metronome.core.engine.MetronomeEngine;
import com.metronome.core.model.BannerState;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class BannerViewModel extends ViewModel {

    private final MutableLiveData<BannerState> _state = new MutableLiveData<>();
    public final LiveData<BannerState> state = _state;

    private final MetronomeEngine engine;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Inject
    public BannerViewModel(MetronomeEngine engine) {
        this.engine = engine;
    }

    public void bannerOnLoad() {
        scheduleTask(() -> engine.bannerOnLoad(true), 4000);
    }

    public void bannerOnAnim() {
        scheduleTask(() -> engine.bannerOnAnim(true), 4000);
    }

    public void bannerOnPlay() {
        scheduleTask(() -> engine.bannerOnPlayed(true), 1000);
    }

    public void bannerOnReset() {
        scheduleTask(() -> engine.bannerOnReset(), 0);
    }

    private void scheduleTask(Runnable engineAction, long delayMs) {
        scheduler.schedule(() -> {
            engineAction.run();
            BannerState currentState = engine.getBannerState().getValue();
            if (currentState != null) {
                _state.postValue(currentState);
            }
        }, delayMs, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        scheduler.shutdownNow();
    }
}