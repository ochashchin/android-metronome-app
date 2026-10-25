package com.metronome.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gmail.specifickarma.metronome.R;
import com.metronome.ui.components.BottomSliderBar;
import com.metronome.ui.components.CenterButton;
import com.metronome.ui.components.FlashOverlayView;
import com.metronome.ui.components.TopSliderBar;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private HomeViewModel vm;

    private TextView bpmText;
    private TextView msText;
    private TopSliderBar bpmSlider;
    private BottomSliderBar modeSlider;
    private CenterButton playBtn;
    private FlashOverlayView flashOverlay;

    private TempoDisplay tempoDisplay;
    private PlaybackState playbackState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_controls, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(this).get(HomeViewModel.class);

        bpmText = view.findViewById(R.id.bmp_value);
        msText = view.findViewById(R.id.ms_value);
        bpmSlider = view.findViewById(R.id.t_s_b);
        modeSlider = view.findViewById(R.id.b_s);
        playBtn = view.findViewById(R.id.c_b);
        flashOverlay = view.findViewById(R.id.flash_overlay);

        tempoDisplay = new TempoDisplay(bpmText, msText);
        playbackState = new PlaybackState(bpmSlider, modeSlider, flashOverlay, playBtn);

        bpmSlider.setOnProgressChangeListener(progress -> vm.setProgress(progress));
        modeSlider.setOnModeSelectedListener(mode -> vm.setMode(mode));
        playBtn.setOnClickListener(v -> vm.toggle());
        playBtn.setOnTooltipShownListener(() -> vm.setTooltip(false));

        vm.state.observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            tempoDisplay.update(state.getBpm(), state.getMs());
            bpmSlider.setProgress(state.getProgress());
            modeSlider.setMode(state.getMode());
            playbackState.update(state.isPlaying(), state.isTooltip(), state.getMode());

            if (state.isFlashBlink()) flashOverlay.blink();
            if (state.isPulseBounce()) {
                bpmSlider.pulse();
                playBtn.pulse();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tempoDisplay.reset();
        playbackState.reset();
        flashOverlay.reset();
        bpmSlider.resetPulse();
        playBtn.resetPulse();
    }
}
