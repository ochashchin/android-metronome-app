package com.metronome.core.engine;

import com.metronome.core.model.BannerState;
import com.metronome.core.model.HomeState;
import com.metronome.core.model.Mode;
import com.metronome.core.model.ReviewDialogState;
import com.metronome.core.model.SplashState;
import com.metronome.core.util.AppState;
import com.metronome.hardware.FlashController;
import com.metronome.hardware.SoundManager;
import com.metronome.hardware.VibrationManager;

import java.util.concurrent.locks.LockSupport;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MetronomeEngine implements HomeEngine, SplashEngine, ReviewDialogEngine, BannerEngine {

    private final SoundManager sound;
    private final FlashController flash;
    private final VibrationManager vibrator;

    private final AppState<HomeState> state =
            new AppState<>(new HomeState(false, Mode.SOUND, 15.56f));
    private final AppState<SplashState> splashState =
            new AppState<>(new SplashState(false));
    private final AppState<ReviewDialogState> reviewState =
            new AppState<>(new ReviewDialogState(false, 3));
    private final AppState<BannerState> bannerState =
            new AppState<>(new BannerState(false, false, false, false));

    private Thread engineThread;
    private volatile boolean running = false;

    @Inject
    public MetronomeEngine(SoundManager sound, FlashController flash, VibrationManager vibrator) {
        this.sound = sound;
        this.flash = flash;
        this.vibrator = vibrator;
    }

    @Override
    public void start() {
        stop();
        running = true;

        engineThread = new Thread(() -> {
            long nextTick = System.nanoTime();

            while (running) {
                int bpm = state.getValue().getBpm();
                long interval = 60_000_000_000L / bpm;

                long now = System.nanoTime();

                if (now >= nextTick) {

                    Mode currentMode = state.getValue().getMode();
                    executeHardwareAction(currentMode);

                    nextTick += interval;
                } else {
                    long sleepNanos = nextTick - now;
                    LockSupport.parkNanos(sleepNanos);
                }
            }
        });

        engineThread.start();
        state.setValue(state.getValue().copyIsPlaying(true));
    }

    private void executeHardwareAction(Mode mode) {
        switch (mode) {
            case SOUND:
                sound.tick();
                break;
            case FLASH:
                flash.blink();
                break;
            case PULSE:
                vibrator.vibrate();
                break;
        }
    }

    @Override
    public void stop() {
        running = false;

        if (engineThread != null) {
            engineThread.interrupt();
        }

        sound.stop();
        flash.off();
        vibrator.vibrate();

        state.setValue(state.getValue().copyIsPlaying(false));
    }

    @Override
    public void setMode(Mode mode) {
        state.setValue(state.getValue().copyMode(mode));
    }

    @Override
    public void setProgress(int progress) {
        state.setValue(state.getValue().copyProgress(progress));
    }

    @Override
    public void splashOn() {
        splashState.setValue(splashState.getValue().copyIsPlaying(true));
    }

    @Override
    public void splashOff() {
        splashState.setValue(splashState.getValue().copyIsPlaying(false));
    }

    @Override
    public void dialogCount(int n) {
        reviewState.setValue(reviewState.getValue().copyIsCount(n));
    }

    @Override
    public AppState<HomeState> getState() {
        return state;
    }

    @Override
    public AppState<SplashState> getSplashState() {
        return splashState;
    }

    @Override
    public AppState<ReviewDialogState> getReviewState() {
        return reviewState;
    }

    @Override
    public void bannerOnLoad(boolean load) {
        bannerState.setValue(bannerState.getValue().copyOnLoaded(load));
    }

    @Override
    public void bannerOnAnim(boolean anim) {
        bannerState.setValue(bannerState.getValue().copyOnAnim(anim));
    }

    @Override
    public void bannerOnReset() {
        bannerState.setValue(bannerState.getValue().copyOnReset(true));
    }

    @Override
    public void bannerOnPlayed(boolean played) {
        bannerState.setValue(bannerState.getValue().onPlayed(played));
    }

    @Override
    public AppState<BannerState> getBannerState() {
        return bannerState;
    }
}
