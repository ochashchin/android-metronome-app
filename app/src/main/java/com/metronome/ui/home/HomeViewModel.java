package com.metronome.ui.home;

import android.app.Application;
import android.content.Intent;
import android.os.Build;

import androidx.lifecycle.ViewModel;

import com.metronome.core.engine.HomeEngine;
import com.metronome.core.model.Mode;
import com.metronome.core.model.HomeState;
import com.metronome.core.util.AppState;
import com.metronome.service.MetronomeService;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HomeViewModel extends ViewModel {

    private final HomeEngine engine;
    private final Application application;
    public final AppState<HomeState> state;

    @Inject
    public HomeViewModel(HomeEngine engine, Application application) {
        this.application = application;
        this.engine = engine;
        this.state = engine.getState();
    }

    public void toggle() {
        boolean wasPlaying = state.getValue().isPlaying();
        Intent intent = new Intent(application, MetronomeService.class);

        if (wasPlaying) {
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

    public void stop() {
        Intent intent = new Intent(application, MetronomeService.class);
        intent.setAction("STOP");
        application.startService(intent);
    }

    public void setMode(Mode mode) {
        engine.setMode(mode);
    }

    public void setProgress(int progress) {
        engine.setProgress(progress);
    }
}
