package com.deerslab.mathbomb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class BoomActivity extends Activity implements View.OnClickListener, SoundPool.OnLoadCompleteListener {

    String TAG = this.getClass().getSimpleName();
    private Tracker mTracker;

    private SharedPreferences preferences;
    private boolean sound;
    private SoundPool soundPool;
    private final int MAX_STREAMS = 1;
    private int soundBoom;

    private static final String AD_UNIT_ID_INTERSTITIAL = "ca-app-pub-4902424516454995/1084840465";
    private static InterstitialAd mInterstitialAd;
    private static AdRequest interstitialRequest;

    public static boolean boolShowAd;

    protected static void adLoad(Context context){

        try {
            mInterstitialAd = new InterstitialAd(context);
            mInterstitialAd.setAdUnitId(AD_UNIT_ID_INTERSTITIAL);

            interstitialRequest = new AdRequest.Builder().build();
            mInterstitialAd.loadAd(interstitialRequest);

            //Toast.makeText(context, "Loading Interstitial", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boom);

        try {
            mTracker = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
            mTracker.setScreenName(TAG);
            mTracker.send(new HitBuilders.EventBuilder().setAction("start").build());

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    startGame();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sound = preferences.getBoolean("sound", true);

        if (sound) {
            soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
            soundPool.setOnLoadCompleteListener(this);
            soundBoom = soundPool.load(this, R.raw.boom, 1);
        }

    }


    @Override
    public void onClick(View v) {

        try {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (mInterstitialAd.isLoaded() && boolShowAd) {
                        mInterstitialAd.show();
                        //Toast.makeText(getApplicationContext(), "Showing Interstitial", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        interstitialRequest = new AdRequest.Builder().build();
                        mInterstitialAd.loadAd(interstitialRequest);
                        //Toast.makeText(getApplicationContext(), "Loading Interstitial", Toast.LENGTH_SHORT).show();

                        startGame();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(BoomActivity.this, LevelChooserActivity.class));
        finish();
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        soundPool.play(soundBoom, 1, 1, 0, 0, 1);
    }

    private void startGame(){
        startActivity(new Intent(BoomActivity.this, LevelChooserActivity.class));
        finish();
    }
}
