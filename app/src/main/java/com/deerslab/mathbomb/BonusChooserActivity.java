package com.deerslab.mathbomb;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

public class BonusChooserActivity extends Activity {

    private SharedPreferences preferences;
    private Resources resources;
    private ListView listView;
    String TAG = this.getClass().getSimpleName();
    private Tracker mTracker;
    private BonusAds bonusAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            bonusAds = BonusAds.getInstance(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_category_chooser);

        resources = getResources();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        LevelChooserItem bonusItems[] = new LevelChooserItem[]{
              new LevelChooserItem(resources.getString(R.string.butEarnLife), true),
              new LevelChooserItem(resources.getString(R.string.buttEarnTime), true),
              new LevelChooserItem(resources.getString(R.string.butMainMenu), true)
        };


        LevelChooserAdapter adapter = new LevelChooserAdapter(this, R.layout.level_item, bonusItems);
        listView = (ListView) findViewById(R.id.lvCategories);

        View header = (View)getLayoutInflater().inflate(R.layout.bonus_header, null);
        listView.addHeaderView(header, null, false);
        listView.setDivider(getResources().getDrawable(android.R.color.transparent));

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        if (bonusAds.isReadyAdBool){
                            bonusAds.category = BonusAds.ADS_CATEGORY.Life;
                            showDialog();
                        } else {
                            Toast.makeText(BonusChooserActivity.this, "Not ready yet", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 2:
                        if (bonusAds.isReadyAdBool){
                            bonusAds.category = BonusAds.ADS_CATEGORY.Time;
                            showDialog();
                        }
                        break;
                    case 3:
                       // startActivity(new Intent(BonusChooserActivity.this, StartActivity.class));
                        finish();
                        break;
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(BonusChooserActivity.this, StartActivity.class));
        finish();
    }

    private void showDialog(){
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                final AlertDialog.Builder builder = new AlertDialog.Builder(BonusChooserActivity.this);
                builder.setMessage(resources.getString(R.string.bonusQuest))
                        .setCancelable(true)
                        .setTitle(resources.getString(R.string.dialogBonusTitle))
                        .setPositiveButton(resources.getString(R.string.yes),
                                new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface arg0, int arg1){
                                        bonusAds.showAd(BonusChooserActivity.this);
                                    }
                                })
                        .setNegativeButton(resources.getString(R.string.no),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
}
