package com.deerslab.mathbomb;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

/**
 * Created by keeper on 20.12.2016.
 */
public class BonusAds implements RewardedVideoAdListener {

    private static final String AD_UNIT_ID = "ca-app-pub-4902424516454995/8969717665";
    private static final String APP_ID = "ca-app-pub-4902424516454995~8747708063";

    private RewardedVideoAd mRewardedVideoAd;
    protected boolean isReadyAdBool = false;
    private int countTryDownAD = 50;

    SharedPreferences preferences;

    public static ADS_CATEGORY category = ADS_CATEGORY.None;

    private static BonusAds ourInstance = new BonusAds();

    private static Activity context;

    private GameActivity gameActivity;

    public static synchronized BonusAds getInstance(Activity context1) {
        context = context1;
        return ourInstance;
    }

    private BonusAds() {


    }

    public void gameInit(GameActivity gameActivity){
        this.gameActivity = gameActivity;
    }

    public void createAd(){
        try {
            Log.d("Ads", "create video Ads");

            MobileAds.initialize(context, APP_ID);

            mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
            mRewardedVideoAd.setRewardedVideoAdListener(this);

            loadAd();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAd(){
        try {
            if (mRewardedVideoAd != null && countTryDownAD>0) {
                countTryDownAD--;
                mRewardedVideoAd.loadAd(AD_UNIT_ID, new AdRequest.Builder().build());
                Log.d("Ads", "start load");
            }
        } catch (Exception e) {
            e.printStackTrace();
            //sendError(e);
        }
    }

    public void showAd(Activity activity) {
        try {

            context = activity;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mRewardedVideoAd != null) {
                        if (mRewardedVideoAd.isLoaded()) {
                            mRewardedVideoAd.show();
                            isReadyAdBool = false;
                        } else {
                            loadAd();
                        }
                    } else {
                        createAd();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            //sendError(e);
        }
        isReadyAdBool = false;

    }

    @Override
    public void onRewardedVideoAdLoaded() {
        isReadyAdBool = true;
        countTryDownAD = 50;
        Log.d("ads", "onRewardedVideoAdLoaded");
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadAd();

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
/*
        try {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = preferences.edit();

            switch (category){
                case Life:
                    int life = preferences.getInt("life", 0);
                    editor.putInt("life", ++life);
                    break;
                case Time:
                    int time = preferences.getInt("time", 0);
                    time += 3;
                    editor.putInt("time", time);
                    break;
            }
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
*/

        try {
            if (gameActivity != null){
                gameActivity.startGameAfterAd();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        loadAd();

    }
/*
    public boolean isReadyAdBool(){
        try {
            Log.d("ads", "isReadyAdBool " + isReadyAdBool);
            return mRewardedVideoAd.isLoaded();

        } catch (Exception e){
            return false;
        }

    }
*/

    enum ADS_CATEGORY{
        None, Life, Time
    }
}
