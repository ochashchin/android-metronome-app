package com.metronome.core.engine;

import com.metronome.core.model.BannerState;
import com.metronome.core.util.AppState;

public interface BannerEngine {

    void bannerOnLoad(boolean load);

    void bannerOnAnim(boolean anim);

    void bannerOnReset();

    void bannerOnPlayed(boolean play);

    AppState<BannerState> getBannerState();
}
