package com.deerslab.mathbomb;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

public class StartActivity extends Activity implements View.OnClickListener  {

    TextView tvCountBombsDefused, tvCountBombsExploded;
    Button btnPlay, btnSett, btnFame;
    private Tracker mTracker;
    String TAG = this.getClass().getSimpleName();

    GoogleApiClient gac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        tvCountBombsDefused = (TextView) findViewById(R.id.tvCountBombsDefused);
        tvCountBombsExploded = (TextView) findViewById(R.id.tvCountBombsExploded);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnSett = (Button) findViewById(R.id.btnSett);
        btnFame = (Button) findViewById(R.id.btnFame);
        btnPlay.setOnClickListener(this);
        btnSett.setOnClickListener(this);
        btnFame.setOnClickListener(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int countBombsDefused = preferences.getInt("countDefusedBomb", 0);
        int countBombsExploded = preferences.getInt("countExplodedBomb", 0);

        tvCountBombsDefused.setText(countBombsDefused + "");
        tvCountBombsExploded.setText(countBombsExploded + "");


        try {
            AnalyticsTrackers.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mTracker = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
            mTracker.setScreenName(TAG);
            mTracker.send(new HitBuilders.EventBuilder().setAction("start").build());

            gac = new GoogleApiClient.Builder(this).addApi(Games.API).build();
            gac.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onClick(View v) {

        Log.d(TAG, "click " + v.getId());

        switch (v.getId()){
            case R.id.btnPlay:
                startActivity(new Intent(StartActivity.this, CategoryChooserActivity.class));
                finish();
                break;
            case R.id.btnSett:
                Log.d(TAG, "settings click");
                startActivity(new Intent(this, MathBombSettings.class));
                break;
            case R.id.btnFame:
                try {
                    Log.d(TAG, "Hall of Fame click");
                    if (gac.isConnected()){
                        startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gac, getString(R.string.leaderboard_bombs_defused)), 1002);
                        mTracker.send(new HitBuilders.EventBuilder().setAction("Hall of Fame click").build());
                    } else{
                        gac.connect();
                        mTracker.send(new HitBuilders.EventBuilder().setAction("try connect google").build());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

        }
    }

}
