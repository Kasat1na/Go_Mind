package com.example.gomind.sound;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import com.example.gomind.R;

public class SoundManager {
    private static SoundManager instance;
    private SoundPool soundPool;
    private int soundId;
    private boolean isLoaded = false;

    private SoundManager(Context context) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build();

        soundId = soundPool.load(context, R.raw.zvuk41, 1);
        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> isLoaded = true);
    }

    public static SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context);
        }
        return instance;
    }

    public void playSound() {
        if (isLoaded && soundPool != null) {
            soundPool.play(soundId, 1, 1, 0, 0, 1);
        }
    }

    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
            instance = null;
        }
    }
}

