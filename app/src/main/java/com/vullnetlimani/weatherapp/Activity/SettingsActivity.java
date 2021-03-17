package com.vullnetlimani.weatherapp.Activity;

import android.os.Bundle;

import com.vullnetlimani.weatherapp.R;
import com.vullnetlimani.weatherapp.fragments.SettingsFragment;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupTheme();
        setTheme(Theme);

        setContentView(R.layout.activity_settings);

        setupToolbar();
        setToolbarBackIcon();

        SettingsFragment settingsFragment = new SettingsFragment();
        settingsFragment.setActivity(SettingsActivity.this);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, settingsFragment).commit();

    }
}