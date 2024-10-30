package com.example.wilproject;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationManagerCompat;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {
    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");

        // Build and send the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "appointment_channel")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Upcoming Appointment")
                .setContentText("Your appointment is on " + date + " at " + time + ".")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }
}
