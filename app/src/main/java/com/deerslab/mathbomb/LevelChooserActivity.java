package com.deerslab.mathbomb;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class LevelChooserActivity extends Activity {

    private SharedPreferences preferences;
    private Resources resources;
    private ListView listView;
    String TAG = this.getClass().getSimpleName();
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_category_chooser);

        resources = getResources();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        CategoriesEnum currentCategory = Database.currentCategory;
        final int progress = preferences.getInt("progress"+currentCategory, 0);

        LevelChooserItem levelItems[] = new LevelChooserItem[10];

        for(int i=0; i<10; i++){
            levelItems[i] = new LevelChooserItem(getResources().getString(R.string.level) + " " + (i+1), i<=progress ? true: false);
        }

        LevelChooserAdapter adapter = new LevelChooserAdapter(this, R.layout.level_item, levelItems);
        listView = (ListView) findViewById(R.id.lvCategories);

        View header = (View)getLayoutInflater().inflate(R.layout.level_header, null);
        listView.addHeaderView(header, null, false);
        listView.setDivider(getResources().getDrawable(android.R.color.transparent));

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position <= progress+1) {
                    Database.currentLevel = position;
                    startActivity(new Intent(LevelChooserActivity.this, GameActivity.class));
                    finish();
                }
            }
        });

        try {
            mTracker = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
            mTracker.setScreenName(TAG);
            mTracker.send(new HitBuilders.EventBuilder().setAction("start").build());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(LevelChooserActivity.this, CategoryChooserActivity.class));
        finish();
    }
}
