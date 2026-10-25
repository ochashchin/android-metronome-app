package com.metronome.ui.home;

import com.metronome.core.model.Mode;
import com.metronome.ui.components.BottomSliderBar;
import com.metronome.ui.components.CenterButton;
import com.metronome.ui.components.FlashOverlayView;
import com.metronome.ui.components.TopSliderBar;

public class PlaybackState {

    public enum State {
        IDLE,
        IDLE_WITH_TOOLTIP,
        PLAYING_SOUND,
        PLAYING_FLASH,
        PLAYING_PULSE
    }

    private final TopSliderBar bpmSlider;
    private final BottomSliderBar modeSlider;
    private final FlashOverlayView flashOverlay;
    private final CenterButton playBtn;

    private State currentState = null;

    public PlaybackState(TopSliderBar bpmSlider,
                         BottomSliderBar modeSlider,
                         FlashOverlayView flashOverlay,
                         CenterButton playBtn) {
        this.bpmSlider = bpmSlider;
        this.modeSlider = modeSlider;
        this.flashOverlay = flashOverlay;
        this.playBtn = playBtn;
    }

    public void update(boolean isPlaying, boolean isTooltip, Mode mode) {
        State nextState = resolveState(isPlaying, isTooltip, mode);
        if (this.currentState == nextState) return;

        this.currentState = nextState;
        bpmSlider.setPlaying(isPlaying, mode);
        modeSlider.setPlaying(isPlaying);
        flashOverlay.setPlaying(isPlaying, mode);
        playBtn.setPlayingState(isPlaying, isTooltip, mode);
    }

    private State resolveState(boolean isPlaying, boolean isTooltip, Mode mode) {
        if (!isPlaying) {
            return isTooltip ? State.IDLE_WITH_TOOLTIP : State.IDLE;
        }
        if (mode == Mode.FLASH) return State.PLAYING_FLASH;
        if (mode == Mode.PULSE) return State.PLAYING_PULSE;
        return State.PLAYING_SOUND;
    }

    public void reset() {
        currentState = null;
    }
}
