package com.example.collexahub;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.example.collexahub.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage message) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "events_channel")
                        .setSmallIcon(R.drawable.ic_event)
                        .setContentTitle(message.getNotification().getTitle())
                        .setContentText(message.getNotification().getBody())
                        .setAutoCancel(true);

        NotificationManager nm =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, builder.build());
    }
}
