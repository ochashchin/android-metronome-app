package com.metronome.ui.banner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gmail.specifickarma.metronome.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BannerFragment extends Fragment {

    private static final String ADMOB_UNIT_ID =
//            "ca-app-pub-2874567203173670/6239190546";
            "ca-app-pub-3940256099942544/6300978111";  // test id

    private BannerViewModel vm;
    private CardView cardView;
    private AdView adView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ad, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(this).get(BannerViewModel.class);

        cardView = view.findViewById(R.id.cardView);
        adView = view.findViewById(R.id.adView);

        vm.bannerOnLoad();

        vm.state.observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            if (state.isOnLoad() && !state.isOnPlayed() && !state.isOnAnim()) {
                onLoad();
                vm.bannerOnAnim();
            }
            if (state.isOnLoad() && state.isOnAnim() && !state.isOnPlayed()) {
                animate(true);
                vm.bannerOnPlay();
            }
            if (state.isOnPlayed() && state.isOnAnim() && state.isOnLoad()) {
                animate(false);
            }
            if (state.isOnPlayed() && state.isOnAnim() && state.isOnLoad() && state.isOnReset()) {
                onLoad();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (adView != null) {
            adView.destroy();
            adView = null;
        }

        vm.bannerOnReset();
    }

    private void onLoad() {
        adView.loadAd(new AdRequest.Builder().build());
    }

    private void animate(boolean animate) {
        if (animate) {
            cardView.animate()
                    .alpha(1f)
                    .setDuration(1000)
                    .start();
        } else {
            cardView.setAlpha(1);
        }
    }
}