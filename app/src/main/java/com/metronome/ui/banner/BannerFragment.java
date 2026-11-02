package com.metronome.ui.banner;

import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowMetrics;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gmail.specifickarma.metronome.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BannerFragment extends Fragment {

    private BannerViewModel vm;
    private ConstraintLayout bannerLayout;
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

        bannerLayout = view.findViewById(R.id.banner);
        adContainer = view.findViewById(R.id.adContainer);

        if (bannerLayout != null) {
            bannerLayout.setAlpha(0f);
        }

        vm.bannerOnLoad();

        vm.state.observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            if (state.isOnLoad() && !state.isOnPlayed() && !state.isOnAnim()) {
                onLoad();
                vm.bannerOnAnim();
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

        if (bannerLayout != null) {
            bannerLayout.clearAnimation();
        }

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

            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    fadeInBanner();
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    if (bannerLayout != null) {
                        bannerLayout.clearAnimation();
                        bannerLayout.setAlpha(0f);
                    }
                }
            });
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

    private void fadeInBanner() {
        if (bannerLayout == null) return;

        AlphaAnimation fade = new AlphaAnimation(0f, 1f);
        fade.setStartOffset(300);
        fade.setDuration(1000);
        fade.setInterpolator(new LinearInterpolator());
        fade.setFillAfter(true);
        fade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (bannerLayout != null) {
                    bannerLayout.setAlpha(1f); // Ensure view is visible so the AlphaAnimation handles rendering
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (bannerLayout != null) {
                    bannerLayout.setAlpha(1f);
                }
                if (vm != null) {
                    vm.bannerOnPlay();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        bannerLayout.startAnimation(fade);
    }
}
