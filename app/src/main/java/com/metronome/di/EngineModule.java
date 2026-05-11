package com.metronome.di;

import com.metronome.core.engine.HomeEngine;
import com.metronome.core.engine.MetronomeEngine;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class EngineModule {

    @Binds
    @Singleton
    public abstract HomeEngine bindMetronomeEngine(MetronomeEngine engine);

}