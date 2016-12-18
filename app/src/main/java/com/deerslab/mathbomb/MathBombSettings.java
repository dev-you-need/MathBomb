package com.deerslab.mathbomb;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class MathBombSettings extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
