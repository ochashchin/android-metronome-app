package com.metronome.ui.splash;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gmail.specifickarma.metronome.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SplashFragment extends Fragment {

    private SplashViewModel vm;

    private Activity act;
    private View vignette;
    private ConstraintLayout metro;
    private ConstraintLayout nome;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(this).get(SplashViewModel.class);

        act = requireActivity();
        vignette = view.findViewById(R.id.vignette);
        metro = view.findViewById(R.id.metro);
        nome = view.findViewById(R.id.nome);

        observeState();
    }

    private void observeState() {
        vm.splashOn();
        vm.state.observe(getViewLifecycleOwner(), state -> {
            splashMove(metro, 1.5f, state.isPlaying());
            splashMove(nome, 1.0f, state.isPlaying());
            homeMove(R.id.controls, state.isPlaying());
            fadeView(state.isPlaying());
            vm.splashOff();
        });
    }

    private void splashMove(ViewGroup targetView,
                            float interpolatorValue,
                            boolean animate) {

        if (targetView == null || targetView.getWidth() == 0) {
            return;
        }

        ConstraintLayout parent =
                (ConstraintLayout) targetView.getParent();

        ConstraintSet set = new ConstraintSet();
        set.clone(parent);

        int viewId = targetView.getId();

        set.constrainWidth(viewId, targetView.getWidth());
        set.constrainHeight(viewId, targetView.getHeight());

        set.clear(viewId, ConstraintSet.START);
        set.clear(viewId, ConstraintSet.END);

        set.connect(
                viewId,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START
        );

        if (animate) {
            Transition transition = new AutoTransition();

            transition.setDuration(Build.VERSION.SDK_INT > Build.VERSION_CODES.P
                            ? 1500L
                            : 800L
            );

            transition.setInterpolator(
                    new AnticipateOvershootInterpolator(interpolatorValue));

            TransitionManager.beginDelayedTransition(
                    targetView,
                    transition
            );
        }

        set.applyTo(parent);
    }

    private void homeMove(int viewId, boolean animate) {
        ConstraintLayout targetView = act.findViewById(viewId);
        ConstraintLayout parent = (ConstraintLayout) targetView.getParent();

        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) targetView.getLayoutParams();

        boolean isAlreadyCentered = (lp.leftToLeft == ConstraintSet.PARENT_ID &&
                lp.rightToRight == ConstraintSet.PARENT_ID &&
                lp.topToTop == ConstraintSet.PARENT_ID &&
                lp.bottomToBottom == ConstraintSet.PARENT_ID &&
                lp.width == targetView.getWidth() &&
                lp.height == targetView.getHeight());

        if (isAlreadyCentered) return;

        ConstraintSet set = new ConstraintSet();

        set.connect(viewId, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
        set.connect(viewId, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
        set.connect(viewId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        set.connect(viewId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

        set.constrainWidth(viewId, targetView.getWidth());
        set.constrainHeight(viewId, targetView.getHeight());

        if (animate) {
            boolean isModern = Build.VERSION.SDK_INT > Build.VERSION_CODES.P;
            TransitionManager.beginDelayedTransition(targetView,
                    new TransitionSet()
                            .setStartDelay(isModern ? 2500L : 500L)
                            .setDuration(isModern ? 2000L : 800L)
                            .addTransition(new AutoTransition())
                            .setInterpolator(new AnticipateOvershootInterpolator(1f)));
        }

        set.applyTo(parent);
    }

    private void fadeView(boolean animate) {
        if (animate) {
            vignette.animate()
                    .alpha(1f)
                    .setDuration(800)
                    .start();
        } else {
            vignette.setAlpha(1f);
        }
    }
}
