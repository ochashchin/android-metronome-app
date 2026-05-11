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
import com.metronome.ui.components.TopSliderBar;
import com.metronome.ui.components.CenterButton;
import com.metronome.ui.components.BottomSliderBar;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private HomeViewModel vm;

    private TextView bpmText;
    private TextView msText;
    private TopSliderBar bpmSlider;
    private BottomSliderBar modeSlider;
    private CenterButton playBtn;

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

        bpmSlider.setOnProgressChangeListener(progress -> vm.setProgress(progress));

        modeSlider.setOnModeSelectedListener(mode -> vm.setMode(mode));

        playBtn.setOnClickListener(v -> vm.toggle());

        vm.state.observe(state -> {
            bpmText.setText(String.valueOf(state.getBpm()));
            msText.setText(String.valueOf(state.getMs()));
            modeSlider.setMode(state.getMode());
            bpmSlider.setProgress(state.getProgress());
            bpmSlider.setPlaying(state.isPlaying());
            playBtn.setPlaying(state.isPlaying());
            modeSlider.setPlaying(state.isPlaying());
        });
    }
}
