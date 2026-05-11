package com.metronome.ui.review;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.metronome.ui.components.ReviewDialog;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ReviewFragment extends Fragment {

    private static final int TRIGGER_COUNT = 3;
    private static final String PREFS_KEY = "review";
    private static final long DIALOG_DELAY = 3200L;

    private ReviewViewModel vm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();

        vm = new ViewModelProvider(this).get(ReviewViewModel.class);

        vm.showReview.observe(this, state -> {
            if (state.isShow()) new ReviewDialog(requireActivity()).show();
        });

        countTo(TRIGGER_COUNT);
    }

    private void countTo(int n) {
        vm.dialogCount(n);
        int c = read();
        if (c < n) {
            save(++c);
        } else if (c == n) {
            save(++c);
            vm.scheduleReview(DIALOG_DELAY);
        }
    }

    private void save(int value) {
        prefs().edit().putInt(PREFS_KEY, value).apply();
    }

    private int read() {
        return prefs().getInt(PREFS_KEY, 1);
    }

    private SharedPreferences prefs() {
        return requireActivity().getPreferences(Context.MODE_PRIVATE);
    }
}

