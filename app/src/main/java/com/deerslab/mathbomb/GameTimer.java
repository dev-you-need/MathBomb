package com.deerslab.mathbomb;

import android.os.AsyncTask;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * Created by keeper on 09.02.2016.
 */
public class GameTimer extends AsyncTask<Void, Integer, Void> {

    private TextView tvTimer;
    private final long TIME = 60000;
    private long startTime, lastMarkTime;


    public GameTimer(TextView tvTimer) {
        this.tvTimer = tvTimer;
        startTime = System.currentTimeMillis();
        lastMarkTime = startTime;
    }

    @Override
    protected Void doInBackground(Void... params) {

        while (!isCancelled()) {

            long currentTime = System.currentTimeMillis();

            if (currentTime - lastMarkTime > 1000) {
                lastMarkTime = currentTime;

                int minutes = (int) ((TIME + startTime - currentTime)/1000) / 60;
                int seconds = (int) ((TIME + startTime - currentTime)/1000) % 60;

                publishProgress(minutes, seconds);
            }

            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        tvTimer.setText(values[0] + ":" + String.format("%02d",values[1]));
    }
}
