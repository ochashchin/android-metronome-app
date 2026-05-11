package com.metronome.di;

import android.content.Context;

import com.metronome.hardware.FlashController;
import com.metronome.hardware.SoundManager;
import com.metronome.hardware.VibrationManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class HardwareModule {

    @Provides
    @Singleton
    public SoundManager provideSoundManager(@ApplicationContext Context context) {
        return new SoundManager(context);
    }

    @Provides
    @Singleton
    public FlashController provideFlashController(@ApplicationContext Context context) {
        return new FlashController(context);
    }

    @Provides
    @Singleton
    public VibrationManager provideVibrationController(@ApplicationContext Context context) {
        return new VibrationManager(context);
    }
}
