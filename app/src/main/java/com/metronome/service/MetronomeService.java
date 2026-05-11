package com.metronome.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.metronome.core.engine.HomeEngine;
import com.metronome.core.notification.NotificationHelper;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MetronomeService extends Service {

    @Inject
    HomeEngine engine;
    @Inject
    NotificationHelper notificationHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationHelper.createNotificationChannel();
        startForeground(
                NotificationHelper.NOTIFICATION_ID,
                notificationHelper.getNotification()
        );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            return START_STICKY;
        }

        String action = intent.getAction();

        if ("START".equals(action)) {
            engine.start();
        } else if ("STOP".equals(action)) {
            engine.stop();
            stopForeground(true);
            stopSelf();
        }

        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        engine.stop();
        stopForeground(true);
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
