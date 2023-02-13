package com.jar89.timer;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity {

    private long seconds;
    private long startTime;
    private long stopTime;
    private long lastTimeBeforePause;
    private long totalTimeBeforePause;
    private boolean running;
    private boolean pause = false;
    private boolean pauseWasUse = false;
    private boolean timerOn = false;
    private boolean startBtnVisible = true;
    private boolean stopBtnVisible = false;


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SharedPreferences sharedPreferences = this.getSharedPreferences("StopwatchSetting", Context.MODE_PRIVATE);
        boolean firstStartApp = sharedPreferences.getBoolean("firstStartApp", true);

        if (!firstStartApp) {

            final Button startBtn = (Button) findViewById(R.id.start);
            final Button stopBtn = (Button) findViewById(R.id.stop);
            final Button resetUnavailableBtn = (Button) findViewById(R.id.reset_unavailable);
            final Button resetAvailableBtn = (Button) findViewById(R.id.reset_available);
            ConstraintLayout mainBg = findViewById(R.id.main_activity);


            //Получаем значения из настроек, сохраненных в памяти устройства, на случай закрытия приложения во время работы
            seconds = sharedPreferences.getLong("seconds", 0);
            startTime = sharedPreferences.getLong("startTime", 0);
            stopTime = sharedPreferences.getLong("stopTime", 0);
            lastTimeBeforePause = sharedPreferences.getLong("lastTimeBeforePause", 0);
            totalTimeBeforePause = sharedPreferences.getLong("totalTimeBeforePause", 0);
            running = sharedPreferences.getBoolean("running", false);
            pause = sharedPreferences.getBoolean("pause", false);
            pauseWasUse = sharedPreferences.getBoolean("pauseWasUse", false);
            timerOn = sharedPreferences.getBoolean("timerOn", false);
            startBtnVisible = sharedPreferences.getBoolean("startBtnVisible", true);
            stopBtnVisible = sharedPreferences.getBoolean("stopBtnVisible", false);

            if (startBtnVisible) {
                startBtn.setVisibility(View.VISIBLE);
                stopBtn.setVisibility(View.GONE);
            } else {
                startBtn.setVisibility(View.GONE);
                stopBtn.setVisibility(View.VISIBLE);

            }

            if (running) {
                resetUnavailableBtn.setVisibility(View.GONE);
                resetAvailableBtn.setVisibility(View.VISIBLE);
                //Окрашиваем фон зеленым
                mainBg.setBackgroundResource(R.drawable.green_gradient_bg);
            } else if (pause) {
                resetUnavailableBtn.setVisibility(View.GONE);
                resetAvailableBtn.setVisibility(View.VISIBLE);
                //Окрашиваем фон желтым
                mainBg.setBackgroundResource(R.drawable.yellow_gradient_bg);
            } else {
                resetUnavailableBtn.setVisibility(View.VISIBLE);
                resetAvailableBtn.setVisibility(View.GONE);
            }

        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("firstStartApp", false);
        editor.apply();


        runTimer();


    }


    public void onClickStartStop(View view) {

        final Button startBtn = (Button) findViewById(R.id.start);
        final Button stopBtn = (Button) findViewById(R.id.stop);
        final Button resetUnavailableBtn = (Button) findViewById(R.id.reset_unavailable);
        final Button resetAvailableBtn = (Button) findViewById(R.id.reset_available);
        ConstraintLayout mainBg = findViewById(R.id.main_activity);

        resetUnavailableBtn.setVisibility(View.GONE);
        resetAvailableBtn.setVisibility(View.VISIBLE);


        if (!timerOn) {
            if (!running) {
                startTime = SystemClock.elapsedRealtime();
                running = true;
                pause = false;
            }
            timerOn = true;
        } else {
            if (!pause) {
                stopTime = SystemClock.elapsedRealtime();
                lastTimeBeforePause = stopTime - startTime;
                totalTimeBeforePause = totalTimeBeforePause + lastTimeBeforePause;
                lastTimeBeforePause = 0;
                stopTime = 0;
                pause = true;
                running = false;
                pauseWasUse = true;
            }
            timerOn = false;
        }

        if (timerOn) {
            startBtn.setVisibility(View.GONE);
            stopBtn.setVisibility(View.VISIBLE);
            startBtnVisible = false;
            stopBtnVisible = true;
            //Окрашиваем фон зеленым
            mainBg.setBackgroundResource(R.drawable.green_gradient_bg);
        } else {
            startBtn.setVisibility(View.VISIBLE);
            stopBtn.setVisibility(View.GONE);
            startBtnVisible = true;
            stopBtnVisible = false;
            //Окрашиваем фон желтым
            mainBg.setBackgroundResource(R.drawable.yellow_gradient_bg);
        }


        //Сохранение изменений в настройки
        SharedPreferences sharedPreferences = this.getSharedPreferences("StopwatchSetting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("seconds", seconds);
        editor.putLong("startTime", startTime);
        editor.putLong("stopTime", stopTime);
        editor.putLong("lastTimeBeforePause", lastTimeBeforePause);
        editor.putLong("totalTimeBeforePause", totalTimeBeforePause);
        editor.putBoolean("running", running);
        editor.putBoolean("pause", pause);
        editor.putBoolean("pauseWasUse", pauseWasUse);
        editor.putBoolean("timerOn", timerOn);
        editor.putBoolean("startBtnVisible", startBtnVisible);
        editor.putBoolean("stopBtnVisible", stopBtnVisible);
        editor.apply();
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    public void onClickReset(View view) {

        //Получаем значения из настроек, сохраненных в памяти устройства, на случай закрытия приложения во время работы
        SharedPreferences sharedPreferences = this.getSharedPreferences("StopwatchSetting", Context.MODE_PRIVATE);


        final Button startBtn = (Button) findViewById(R.id.start);
        final Button stopBtn = (Button) findViewById(R.id.stop);
        final Button resetUnavailableBtn = (Button) findViewById(R.id.reset_unavailable);
        final Button resetAvailableBtn = (Button) findViewById(R.id.reset_available);


        //Подсвечиваем фон красным при нжатии Reset, если были нажаты пуск или пауза
        // (можно использовать разные варианты цвета Color.argb, Color.rgb, hex
        if (running || pause) {
            ConstraintLayout mainBg = (ConstraintLayout) findViewById(R.id.main_activity);
            ObjectAnimator colorFade = ObjectAnimator.ofObject(mainBg, "backgroundColor",
                    new ArgbEvaluator(), (0xFFFFD4D4), 0xFFFFFFFF);
            colorFade.setDuration(500);
            colorFade.start();
        }


        running = false;
        pause = false;
        seconds = 0;
        startTime = 0;
        stopTime = 0;
        lastTimeBeforePause = 0;
        totalTimeBeforePause = 0;
        pauseWasUse = false;
        timerOn = false;

        startBtn.setVisibility(View.VISIBLE);
        stopBtn.setVisibility(View.GONE);
        startBtnVisible = true;
        stopBtnVisible = false;

        resetUnavailableBtn.setVisibility(View.VISIBLE);
        resetAvailableBtn.setVisibility(View.GONE);

        //Сохранение изменений в настройки
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("seconds", seconds);
        editor.putLong("startTime", startTime);
        editor.putLong("stopTime", stopTime);
        editor.putLong("lastTimeBeforePause", lastTimeBeforePause);
        editor.putLong("totalTimeBeforePause", totalTimeBeforePause);
        editor.putBoolean("running", running);
        editor.putBoolean("pause", pause);
        editor.putBoolean("pauseWasUse", pauseWasUse);
        editor.putBoolean("timerOn", timerOn);
        editor.putBoolean("startBtnVisible", startBtnVisible);
        editor.putBoolean("stopBtnVisible", stopBtnVisible);
        editor.apply();


    }


    private void runTimer() {

        final TextView textView = (TextView) findViewById(R.id.time_view);
        final Handler handler = new Handler();

        handler.post(new Runnable() {

            @Override
            public void run() {

                int h = (int) (seconds / 3600000);
                int m = (int) (seconds - h * 3600000) / 60000;
                int s = (int) (seconds - h * 3600000 - m * 60000) / 1000;
                int msec = ((int) (seconds - h * 3600000 - m * 60000) / 100) % 10;
                String hh = h < 10 ? "0" + h : h + "";
                String mm = m < 10 ? "0" + m : m + "";
                String ss = s < 10 ? "0" + s : s + "";
                String ms = msec < 10 ? "" + msec : msec + "";
                textView.setText(hh + ":" + mm + ":" + ss + ":" + ms);

                if (running && !pauseWasUse) {
                    seconds = SystemClock.elapsedRealtime() - startTime;
                } else if (running && pauseWasUse) {
                    seconds = (SystemClock.elapsedRealtime() + totalTimeBeforePause) - startTime;
                }

                handler.postDelayed(this, 100);

            }
        });
    }
}