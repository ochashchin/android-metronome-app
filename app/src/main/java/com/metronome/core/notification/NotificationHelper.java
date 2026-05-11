package com.metronome.core.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import javax.inject.Inject;
import dagger.hilt.android.qualifiers.ApplicationContext;

public class NotificationHelper {

    public static final String CHANNEL_ID = "metronome_service_channel";
    public static final int NOTIFICATION_ID = 1;

    private final Context context;

    @Inject
    public NotificationHelper(@ApplicationContext Context context) {
        this.context = context;
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Metronome Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public Notification getNotification() {
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Metronome Active")
                .setContentText("The metronome is running")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setOngoing(true)
                .build();
    }
}