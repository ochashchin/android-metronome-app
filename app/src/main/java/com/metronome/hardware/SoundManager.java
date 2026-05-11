package com.metronome.hardware;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;

import com.gmail.specifickarma.metronome.R;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SoundManager {

    private static final String TAG = "SoundManager";

    private SoundPool soundPool;
    private int soundId = -1;
    private boolean loaded = false;

    @Inject
    public SoundManager(Context context) {
        init(context);
    }

    private void init(Context context) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(4)
                .setAudioAttributes(audioAttributes)
                .build();

        soundPool.setOnLoadCompleteListener((sp, sampleId, status) -> {
            if (status == 0) {
                loaded = true;
                Log.d(TAG, "Sound loaded successfully");
            } else {
                Log.e(TAG, "Failed to load sound, status=" + status);
            }
        });

        soundId = soundPool.load(context, R.raw.hit, 1);
        Log.d(TAG, "SoundPool initialized, soundId=" + soundId);
    }

    public void tick() {
        if (!loaded || soundId == -1) {
            Log.w(TAG, "tick() called before sound is loaded");
            return;
        }
        soundPool.play(
                soundId,
                1f,   // left volume
                1f,   // right volume
                0,    // priority
                0,    // loop (0 = no loop)
                1f    // playback rate
        );
    }

    public void stop() {
        if (soundPool != null) {
            soundPool.autoPause();
        }
    }

    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
            loaded = false;
            soundId = -1;
            Log.d(TAG, "SoundPool released");
        }
    }
}
