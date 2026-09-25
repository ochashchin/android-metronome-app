package com.metronome.ui.home;

import android.app.Application;
import android.content.Intent;
import android.os.Build;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.metronome.core.engine.HomeEngine;
import com.metronome.core.model.Mode;
import com.metronome.core.model.HomeState;
import com.metronome.service.MetronomeService;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HomeViewModel extends ViewModel {

    private final HomeEngine engine;
    private final Application application;

    private final MutableLiveData<HomeState> _state = new MutableLiveData<>();
    public final LiveData<HomeState> state = _state;

    @Inject
    public HomeViewModel(HomeEngine engine, Application application) {
        this.application = application;
        this.engine = engine;
        this.engine.getState().observe(s -> _state.postValue(s));
    }

    public void toggle() {
        HomeState current = state.getValue();
        if (current == null) return;

        Intent intent = new Intent(application, MetronomeService.class);
        if (current.isPlaying()) {
            intent.setAction("STOP");
            application.startService(intent);
        } else {
            intent.setAction("START");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                application.startForegroundService(intent);
            } else {
                application.startService(intent);
            }
        }
    }

    public void setMode(Mode mode) {
        engine.setMode(mode);
    }

    public void setProgress(int progress) {
        engine.setProgress(progress);
    }

    public void setTooltip(boolean tooltip) {
        engine.setTooltip(tooltip);
    }
}
