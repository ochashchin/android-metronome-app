package com.metronome.ui.home;

import android.widget.TextView;

public class TempoDisplay {
    private final TextView bpmText;
    private final TextView msText;
    private int lastBpm = -1;

    public TempoDisplay(TextView bpmText, TextView msText) {
        this.bpmText = bpmText;
        this.msText = msText;
    }

    public void update(int bpm, int ms) {
        if (lastBpm != bpm) {
            lastBpm = bpm;
            bpmText.setText(String.valueOf(bpm));
            msText.setText(String.valueOf(ms));
        }
    }

    public void reset() {
        lastBpm = -1;
    }
}
