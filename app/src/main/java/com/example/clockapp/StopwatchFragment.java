package com.example.clockapp;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class StopwatchFragment extends Fragment {

    private NumberPicker npHours, npMinutes, npSeconds;
    private Button btnStartStop;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        initializeViews(view);
        setupNumberPickers();
        setupButtonClickListener();

        return view;
    }

    // Method untuk inisialisasi view
    private void initializeViews(@NonNull View view) {
        npHours = view.findViewById(R.id.npHours);
        if (npHours == null) {
            Log.e("StopwatchFragment", "npHours is null");
        }
        npMinutes = view.findViewById(R.id.npMinutes);
        npSeconds = view.findViewById(R.id.npSeconds);
        btnStartStop = view.findViewById(R.id.btnStartTimer);
    }

    // Setting value range NumberPicker
    private void setupNumberPickers() {
        setupNumberPicker(npHours, 0, 99);
        setupNumberPicker(npMinutes, 0, 59);
        setupNumberPicker(npSeconds, 0, 59);
    }

    private void setupNumberPicker(NumberPicker picker, int min, int max) {
        picker.setMinValue(min);
        picker.setMaxValue(max);
    }

    // Setting tombol Start/Stop Timer
    private void setupButtonClickListener() {
        btnStartStop.setOnClickListener(v -> {
            if (isTimerRunning) {
                stopTimer();
            } else {
                startTimer();
            }
        });
    }

    // Mulai timer
    private void startTimer() {
        long totalTimeMillis = getTotalTimeInMillis();

        if (totalTimeMillis == 0) {
            Toast.makeText(getContext(), "Please set a valid time!", Toast.LENGTH_SHORT).show();
            return;
        }

        countDownTimer = new CountDownTimer(totalTimeMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTimerDisplay(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                onTimerFinished();
            }
        }.start();

        isTimerRunning = true;
        btnStartStop.setText("Stop");
    }

    // Berhenti timer
    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        isTimerRunning = false;
        btnStartStop.setText("Start");
    }

    // Hitung total waktu dalam millisecond
    private long getTotalTimeInMillis() {
        int hours = npHours.getValue();
        int minutes = npMinutes.getValue();
        int seconds = npSeconds.getValue();
        return (hours * 3600 + minutes * 60 + seconds) * 1000L;
    }

    // Update tampilan NumberPicker saat timer berjalan
    private void updateTimerDisplay(long millisUntilFinished) {
        int hours = (int) (millisUntilFinished / 3600000);
        int minutes = (int) (millisUntilFinished % 3600000) / 60000;
        int seconds = (int) ((millisUntilFinished % 60000) / 1000);

        npHours.setValue(hours);
        npMinutes.setValue(minutes);
        npSeconds.setValue(seconds);
    }

    // Aksi setelah timer selesai
    private void onTimerFinished() {
        Toast.makeText(getContext(), "Timer Finished!", Toast.LENGTH_SHORT).show();
        isTimerRunning = false;
        btnStartStop.setText("Start");
    }
}