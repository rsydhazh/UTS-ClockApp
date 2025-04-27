package com.example.clockapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("ALARM_RECEIVER", "AlarmReceiver triggered!");

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(2000);
        }

        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.samsung_galaxy_morning_flower);
        if (mediaPlayer != null) {
            mediaPlayer.start();
        } else {
            Toast.makeText(context, "Cannot play Alarm Sound!", Toast.LENGTH_SHORT).show();
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "alarm_channel")
                .setSmallIcon(R.drawable.baseline_alarm_24)
                .setContentTitle("Alarm")
                .setContentText("Its Clobberin' Time !")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        NotificationManagerCompat notificationManager = (NotificationManagerCompat.from(context));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(200, builder.build());
        }

        Toast.makeText(context, "Time Has Come!", Toast.LENGTH_SHORT).show();

    }
}
