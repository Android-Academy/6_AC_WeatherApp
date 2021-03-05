package com.vullnetlimani.weatherapp.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.vullnetlimani.weatherapp.BuildConfig;
import com.vullnetlimani.weatherapp.Helper.Constants;
import com.vullnetlimani.weatherapp.Helper.MySharedPreferences;
import com.vullnetlimani.weatherapp.Helper.WeatherHelper;
import com.vullnetlimani.weatherapp.R;

public class BaseActivity extends AppCompatActivity {

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    public DrawerLayout drawerLayout;
    public NavigationView navigationView;
    public WeatherHelper mWeatherHelper;
    public TabLayout bottomTabLayout;
    public Toolbar mToolbar;
    private ConstraintLayout navigation_draw_bag;
    private ImageView nav_image;
    private TextView city_id;
    private AlertDialog.Builder changeLogDialog;

    public void setupToolbar(String title) {
        mToolbar = findViewById(R.id.mToolbar);
        mToolbar.setTitle(title);
        setSupportActionBar(mToolbar);
    }

    public void setupToolbar() {
        mToolbar = findViewById(R.id.mToolbar);
        setSupportActionBar(mToolbar);
    }

    public void setVisibilityToolbar(boolean isVisible) {
        if (isVisible)
            mToolbar.setVisibility(View.VISIBLE);
        else
            mToolbar.setVisibility(View.GONE);
    }

    public void setToolbarBackIcon() {
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setupBottomBar() {

        bottomTabLayout.addTab(bottomTabLayout.newTab().setText(getString(R.string.metric)).setIcon(R.drawable.ic_celsius));
        bottomTabLayout.addTab(bottomTabLayout.newTab().setText(getString(R.string.imperial)).setIcon(R.drawable.ic_kelvin));

        bottomTabLayout.setBackgroundColor(getResources().getColor(R.color.primary));
        bottomTabLayout.setTabRippleColor(ColorStateList.valueOf(Color.WHITE));

        bottomTabLayout.setTabTextColors(getResources().getColor(R.color.primary_dark), Color.WHITE);

        bottomTabLayout.setTabIconTint(new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_selected},
                        new int[]{android.R.attr.state_enabled}
                },
                new int[]{
                        Color.WHITE,
                        getResources().getColor(R.color.primary_dark)
                }
        ));

    }

    public void setupNavigationDrawer() {

        View header = navigationView.getHeaderView(0);

        if (mToolbar != null) {
            //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationIcon(R.drawable.ic_menu);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

        navigation_draw_bag = header.findViewById(R.id.navigation_draw_bag);
        navigation_draw_bag.setBackgroundColor(getResources().getColor(R.color.primary));

        nav_image = header.findViewById(R.id.nav_image);
        city_id = header.findViewById(R.id.city_id);

        final int HOME = R.id.home;
        final int DAILY_WEATHER = R.id.daily_weather;
        final int HOURLY_WEATHER = R.id.hourly_weather;
        final int HELP = R.id.help;
        final int SETTINGS = R.id.settings;
        final int CHANGELOG = R.id.changelog;
        final int ABOUT = R.id.about;

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                drawerLayout.closeDrawer(GravityCompat.START);

                switch (id) {

                    case HOME:
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        }, 250);
                        return true;

                    case DAILY_WEATHER:
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(getApplicationContext(), DailyForecastActivity.class));
                            }
                        }, 250);
                        return true;
                    case HOURLY_WEATHER:
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(getApplicationContext(), HourlyForecastActivity.class));
                            }
                        }, 250);
                        return true;
                    case HELP:

                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                Uri data = Uri.parse(getString(R.string.help_uri));
                                intent.setData(data);
                                startActivity(intent);
                            }
                        }, 250);

                        return true;

                    case SETTINGS:
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                            }
                        }, 250);
                        return true;

                    case CHANGELOG:
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showChangeLog();
                            }
                        }, 250);
                        return true;

                    case ABOUT:
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                            }
                        }, 250);
                        return true;

                }

                return true;
            }
        });

    }

    private void showChangeLog() {
        changeLogDialog = new MaterialAlertDialogBuilder(this);
        changeLogDialog.setTitle(getString(R.string.change_log_title, BuildConfig.VERSION_NAME));
        changeLogDialog.setMessage(Html.fromHtml(getString(R.string.change_log_message)));

        changeLogDialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MySharedPreferences.setBooleanPref(BaseActivity.this, Constants.new_update_pref, true);
            }
        });
        changeLogDialog.show();
    }

    public void updatedNavigationDetails() {
        nav_image.setImageResource(mWeatherHelper.getWeatherIcon());
        city_id.setText(mWeatherHelper.getCity());
    }

}