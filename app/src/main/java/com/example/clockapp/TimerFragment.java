package com.example.clockapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.text.MessageFormat;
import java.util.Locale;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class TimerFragment extends Fragment {

    private TextView textView;
    private Button reset, start, stop;

    private int seconds, minutes, milliSeconds;
    private long millisecondTime, startTime, timeBuff, updateTime = 0L;
    private Handler handler;

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            millisecondTime = SystemClock.uptimeMillis() - startTime;
            updateTime = timeBuff + millisecondTime;
            seconds = (int) (updateTime / 1000);
            minutes = seconds / 60;
            seconds = seconds % 60;
            milliSeconds = (int) (updateTime % 1000);

            textView.setText(MessageFormat.format(
                    "{0}:{1}:{2}",
                    minutes,
                    String.format(Locale.getDefault(), "%02d", seconds),
                    String.format(Locale.getDefault(), "%01d", milliSeconds / 100)  // Biar 1 digit aja
            ));
            handler.postDelayed(this, 50);  // Update tiap 50ms supaya lebih smooth, tidak berat
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        textView = view.findViewById(R.id.tvNumberStopWatch);
        reset = view.findViewById(R.id.btnResetTimer);
        start = view.findViewById(R.id.btnStartTimer);
        stop = view.findViewById(R.id.btnStopTimer);

        handler = new Handler(Looper.getMainLooper());

        start.setOnClickListener(v -> {
            startTime = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, 0);
            reset.setEnabled(false);
            stop.setEnabled(true);
            start.setEnabled(false);
        });

        stop.setOnClickListener(v -> {
            timeBuff += millisecondTime;
            handler.removeCallbacks(runnable);
            reset.setEnabled(true);
            stop.setEnabled(false);
            start.setEnabled(true);
        });

        reset.setOnClickListener(v -> {
            millisecondTime = 0L;
            startTime = 0L;
            timeBuff = 0L;
            updateTime = 0L;
            seconds = 0;
            minutes = 0;
            milliSeconds = 0;
            textView.setText("00:00:0");
        });

        textView.setText("00:00:0");

        return view;
    }
}
