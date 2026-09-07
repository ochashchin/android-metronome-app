package com.metronome.ui.banner;

import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowMetrics;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gmail.specifickarma.metronome.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BannerFragment extends Fragment {

    private BannerViewModel vm;
    private CardView cardView;
    private FrameLayout adContainer;
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
        adContainer = view.findViewById(R.id.adContainer);

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
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
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
        if (getContext() == null || getActivity() == null) return;

        if (adView == null) {
            adView = new AdView(requireContext());
            adView.setAdUnitId(getString(R.string.banner_ad_unit_prod_id));
            adContainer.removeAllViews();
            adContainer.addView(adView);
            adView.setAdSize(getAdSize());
        }

        adView.loadAd(new AdRequest.Builder().build());
    }

    private AdSize getAdSize() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int adWidthPixels = displayMetrics.widthPixels;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && getActivity() != null) {
            WindowMetrics windowMetrics = requireActivity().getWindowManager().getCurrentWindowMetrics();
            adWidthPixels = windowMetrics.getBounds().width();
        }

        float density = displayMetrics.density;
        int adWidth = (int) (adWidthPixels / density);

        // Deduct standard horizontal padding (16dp left + 16dp right)
        View bannerView = getView();
        int horizontalPaddingDp;
        if (bannerView != null && (bannerView.getPaddingLeft() > 0 || bannerView.getPaddingRight() > 0)) {
            horizontalPaddingDp = (int) ((bannerView.getPaddingLeft() + bannerView.getPaddingRight()) / density);
        } else {
            horizontalPaddingDp = 32; // 16dp left + 16dp right
        }

        adWidth = Math.max(0, adWidth - horizontalPaddingDp);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(requireContext(), adWidth);
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