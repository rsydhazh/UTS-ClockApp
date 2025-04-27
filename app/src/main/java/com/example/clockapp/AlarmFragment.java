package com.example.clockapp;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Date;

public class AlarmFragment extends Fragment {

    private TimePicker timePicker;
    private Button btnSetAlarm;
    private int jam, menit;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        timePicker = view.findViewById(R.id.timePicker);
        btnSetAlarm = view.findViewById(R.id.btnSetAlarm);

        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener((view1, hourOfDay, minute) -> {
            jam = hourOfDay;
            menit = minute;
        });

        createNotificationChannel(); // Create notification channel once

        btnSetAlarm.setOnClickListener(v -> {
            if (checkAlarmPermission()) {
                setAlarm();
                showAlarmSetNotification();
            } else {
                requestAlarmPermission();
            }
        });

        return view;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
            NotificationChannel channel = new NotificationChannel(
                    "alarm_channel",
                    "Alarm Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel untuk alarm Clock App");

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Handle permission request for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    private boolean checkAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
            return alarmManager != null && alarmManager.canScheduleExactAlarms();
        }
        return true;
    }

    private void requestAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            startActivity(intent);
        }
    }

    private void setAlarm() {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

        Calendar cal_alarm = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();

        cal_now.setTime(new Date());
        cal_alarm.setTime(new Date());

        cal_alarm.set(Calendar.HOUR_OF_DAY, jam);
        cal_alarm.set(Calendar.MINUTE, menit);
        cal_alarm.set(Calendar.SECOND, 0);

        if (cal_alarm.before(cal_now)) {
            cal_alarm.add(Calendar.DATE, 1); // If the alarm time is before the current time, add one day
        }

        PendingIntent pendingIntent = createPendingIntent();
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);

        Toast.makeText(requireContext(), "Alarm disetel untuk pukul " + String.format("%02d:%02d", jam, menit), Toast.LENGTH_SHORT).show();
    }

    private PendingIntent createPendingIntent() {
        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        return PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    private void showAlarmSetNotification() {
        Calendar now = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.set(Calendar.HOUR_OF_DAY, jam);
        target.set(Calendar.MINUTE, menit);
        target.set(Calendar.SECOND, 0);

        if (target.before(now)) {
            target.add(Calendar.DATE, 1);
        }

        long millisUntilAlarm = target.getTimeInMillis() - now.getTimeInMillis();
        long minutesUntilAlarm = millisUntilAlarm / (1000 * 60);

        String waktuAlarm = String.format("%02d:%02d", jam, menit);
        String isiNotifikasi = "Alarm telah diatur pada " + waktuAlarm + ", akan berbunyi dalam " + minutesUntilAlarm + " menit.";

        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), "alarm_channel")
                    .setSmallIcon(R.drawable.baseline_alarm_24)
                    .setContentTitle("Alarm Disetel")
                    .setContentText(isiNotifikasi)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
            notificationManager.notify(201, builder.build());
        }
    }
}
