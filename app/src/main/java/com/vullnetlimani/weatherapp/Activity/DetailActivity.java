package com.vullnetlimani.weatherapp.Activity;

import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.Explode;
import android.transition.Slide;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.SubtitleCollapsingToolbarLayout;
import com.vullnetlimani.weatherapp.Utils.Constants;
import com.vullnetlimani.weatherapp.Helper.MySharedPreferences;
import com.vullnetlimani.weatherapp.Helper.WeatherHelper;
import com.vullnetlimani.weatherapp.R;
import com.vullnetlimani.weatherapp.Utils.Helper;

public class DetailActivity extends BaseActivity {

    public static final String LOG_TAG = "DetailActivityLog";
    private WeatherHelper detail_object;
    private String comingFrom = "";

    private SubtitleCollapsingToolbarLayout mCollapsingToolbar;
    private CardView detail_card_view;
    private LinearLayout sunrise_layout;
    private LinearLayout sunset_layout;

    //Card Text
    private TextView day_name_text;
    private TextView detail_city_text;
    private TextView detail_description;
    private TextView detail_main_temp;
    private TextView detail_feels_like;
    private ImageView detail_main_icon;

    //List Text
    private TextView sunriseText;
    private TextView sunsetText;
    private TextView windText;
    private TextView humidityText;
    private TextView pressureText;

    private String unit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupTheme();
        setTheme(Theme);
        setContentView(R.layout.activity_detail);

        setupToolbar("");
        setToolbarBackIcon();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            detail_object = (WeatherHelper) getIntent().getSerializableExtra(Constants.DETAIL_VIEW_KEY);
            comingFrom = getIntent().getStringExtra(Constants.DETAIL_COMING_ACT_KEY);
        }

        loadViews();
        initData();

    }

    private void loadViews() {

        mCollapsingToolbar = findViewById(R.id.mCollapsingToolbar);

        detail_card_view = findViewById(R.id.detail_card_view);
        sunrise_layout = findViewById(R.id.sunrise_layout);
        sunset_layout = findViewById(R.id.sunset_layout);

        day_name_text = findViewById(R.id.day_name_text);
        detail_city_text = findViewById(R.id.detail_city_text);
        detail_description = findViewById(R.id.detail_description);
        detail_main_temp = findViewById(R.id.detail_main_temp);
        detail_feels_like = findViewById(R.id.detail_feels_like);
        detail_main_icon = findViewById(R.id.detail_main_icon);

        sunriseText = findViewById(R.id.sunriseText);
        sunsetText = findViewById(R.id.sunsetText);
        windText = findViewById(R.id.windText);
        humidityText = findViewById(R.id.humidityText);
        pressureText = findViewById(R.id.pressureText);

    }

    private void initData() {

        unit = MySharedPreferences.getNormalPref(DetailActivity.this, Constants.list_unit_code_key, Constants.UNI_CODE_DEFAULT);

        detail_card_view.setCardBackgroundColor(getResources().getColor(detail_object.getWeatherColor()));

        day_name_text.setText(detail_object.convertTime(detail_object.getTime(), "EEEE"));

        if (comingFrom.equals(Constants.DAILY_FORECAST)) {
            mToolbar.setTitle(detail_object.convertTime(detail_object.getTime(), "dd-MMM-yyyy"));
            sunriseText.setText(detail_object.convertTime(detail_object.getSunrise(), "HH:mm"));
            sunsetText.setText(detail_object.convertTime(detail_object.getSunset(), "HH:mm"));
        } else if (comingFrom.equals(Constants.HOURLY_FORECAST)) {

            String time = detail_object.convertTime(detail_object.getTime(), "HH:mm");
            String date = detail_object.convertTime(detail_object.getTime(), "dd-MMM-yyyy");

            mToolbar.setTitle(time);
            mToolbar.setSubtitle(date);

            sunrise_layout.setVisibility(View.GONE);
            sunset_layout.setVisibility(View.GONE);
        }

        detail_city_text.setText(MySharedPreferences.getNormalPref(this, Constants.edit_text_location_key, Constants.LOCATION_CITY_DEFAULT));
        detail_description.setText(detail_object.getDescription());
        detail_main_icon.setImageResource(detail_object.getWeatherIcon());

        if (unit.contains(getString(R.string.metric))) {
            detail_main_temp.setText(getString(R.string.setTempC, detail_object.getDaily_temp()));
            detail_feels_like.setText(getString(R.string.setFeelsLikeC, detail_object.getFeels_like()));
            windText.setText(getString(R.string.setKMH, detail_object.getSpeed()));
        } else {
            detail_main_temp.setText(getString(R.string.setTempF, detail_object.getDaily_temp()));
            detail_feels_like.setText(getString(R.string.setFeelsLikeF, detail_object.getFeels_like()));
            windText.setText(getString(R.string.setMPH, detail_object.getSpeed()));
        }

        humidityText.setText(getString(R.string.setHUM, detail_object.getHumidity()));
        pressureText.setText(getString(R.string.setPRE, detail_object.getPressure()));

    }

}