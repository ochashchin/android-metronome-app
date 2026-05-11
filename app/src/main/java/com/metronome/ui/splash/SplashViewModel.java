package com.metronome.ui.splash;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.metronome.core.engine.MetronomeEngine;
import com.metronome.core.model.SplashState;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SplashViewModel extends ViewModel {

    private final MutableLiveData<SplashState> _state = new MutableLiveData<>();
    public final LiveData<SplashState> state = _state;

    private final MetronomeEngine engine;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Inject
    public SplashViewModel(MetronomeEngine engine) {
        this.engine = engine;
    }

    public void splashOn() {
        scheduleTask(() -> engine.splashOn(), 1500);
    }

    public void splashOff() {
        scheduleTask(() -> engine.splashOff(), 0);
    }

    private void scheduleTask(Runnable engineAction, long delayMs) {
        scheduler.schedule(() -> {
            engineAction.run();
            SplashState currentState = engine.getSplashState().getValue();
            if (currentState != null) {
                _state.postValue(currentState);
            }
        }, delayMs, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (!scheduler.isShutdown()) scheduler.shutdownNow();
    }
}