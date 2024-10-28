package com.example.wilproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String appointmentId = intent.getStringExtra("appointmentId");
        

        // Create a notification channel for Android 8.0 and above
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("appointment_channel", "Appointment Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "appointment_channel")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Appointment Reminder")
                .setContentText("You have an appointment in 3 days.")
                .setAutoCancel(true);

        // Display the notification
        notificationManager.notify(1, builder.build());
    }
}
