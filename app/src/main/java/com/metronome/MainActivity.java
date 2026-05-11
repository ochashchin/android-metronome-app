package com.metronome;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.gmail.specifickarma.metronome.R;
import com.metronome.ui.banner.BannerFragment;
import com.metronome.ui.home.HomeFragment;
import com.metronome.ui.review.ReviewFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.controls, new HomeFragment());
            ft.add(R.id.nav_host_fragment, new ReviewFragment());
            ft.add(R.id.nav_host_fragment, new BannerFragment());
            ft.commit();
        }
    }
}
