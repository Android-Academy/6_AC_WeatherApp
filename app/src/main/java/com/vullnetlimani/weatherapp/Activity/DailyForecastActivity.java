package com.vullnetlimani.weatherapp.Activity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.view.Window;

import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.vullnetlimani.weatherapp.Helper.AsyncHelper;
import com.vullnetlimani.weatherapp.Utils.Constants;
import com.vullnetlimani.weatherapp.Helper.MySharedPreferences;
import com.vullnetlimani.weatherapp.Helper.WeatherHelper;
import com.vullnetlimani.weatherapp.R;
import com.vullnetlimani.weatherapp.Utils.Helper;
import com.vullnetlimani.weatherapp.adapters.ForecastOverviewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class DailyForecastActivity extends BaseActivity {

    public static final String LOG_TAG = "DailyForecastLog";
    public ArrayList<WeatherHelper> items;
    private String latitude, longitude, unit, language;
    private WeatherHelper mWeatherHelper;
    private ForecastOverviewAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerViewDays;
    private NestedScrollView error_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setExitTransition(null);
        getWindow().setEnterTransition(null);
        setContentView(R.layout.activity_daily_forecast);

        setupToolbar(getString(R.string.seven_days_weather));
        setToolbarBackIcon();

        mSwipeRefreshLayout = findViewById(R.id.mSwipeRefreshLayout);
        recyclerViewDays = findViewById(R.id.recyclerViewDays);
        error_layout = findViewById(R.id.error);

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.primary));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                items.clear();

                recyclerViewDays.setAlpha(1);
                recyclerViewDays.animate().setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        recyclerViewDays.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).setDuration(250).alpha(0);
                downloadJSON();
            }
        });

        mSwipeRefreshLayout.setRefreshing(true);

        downloadJSON();

        items = new ArrayList<>();
        recyclerViewDays.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ForecastOverviewAdapter(this, items);
        recyclerViewDays.setAdapter(adapter);

    }

    private void downloadJSON() {

        latitude = MySharedPreferences.getNormalPref(this, Constants.latitude_pref_key, "0");
        longitude = MySharedPreferences.getNormalPref(this, Constants.longitude_pref_key, "0");
        unit = MySharedPreferences.getNormalPref(this, Constants.list_unit_code_key, Constants.UNI_CODE_DEFAULT);
        language = Constants.Language_EN;

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(AsyncHelper.checkDailyWeather(latitude, longitude, unit, language), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String data = new String(responseBody);
                Log.d(LOG_TAG, "Your Forecast Data - " + data);

                if (!data.equals("")) {
                    parseDailyData(data);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(LOG_TAG, "Error", error);

                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                recyclerViewDays.setVisibility(View.INVISIBLE);
                error_layout.setVisibility(View.VISIBLE);

            }
        });

    }

    @SuppressLint("SimpleDateFormat")
    private void parseDailyData(String data) {

        JSONObject reader;

        try {
            reader = new JSONObject(data);

            JSONArray day_list_array = reader.getJSONArray(WeatherHelper.Weather_DAILY);

            Log.d(LOG_TAG, "Day count - " + day_list_array.length());

            for (int i = 1; i < day_list_array.length(); i++) {

                mWeatherHelper = new WeatherHelper();

                JSONObject day_list_object = day_list_array.getJSONObject(i);

                mWeatherHelper.setTime(day_list_object.getLong(WeatherHelper.Weather_DT));

                mWeatherHelper.setSunrise(day_list_object.getInt(WeatherHelper.Weather_SUNRISE));
                mWeatherHelper.setSunset(day_list_object.getInt(WeatherHelper.Weather_SUNSET));

                mWeatherHelper.setPressure(day_list_object.getDouble(WeatherHelper.Weather_PRESSURE));
                mWeatherHelper.setHumidity(day_list_object.getInt(WeatherHelper.Weather_HUMIDITY));

                mWeatherHelper.setSpeed(day_list_object.getDouble(WeatherHelper.Weather_WIND_SPEED));

                JSONObject temp = day_list_object.getJSONObject(WeatherHelper.Weather_TEMP);
                mWeatherHelper.setDaily_Temp(temp.getDouble(WeatherHelper.Weather_DAY_TEMP));

                JSONObject feels_like = day_list_object.getJSONObject(WeatherHelper.Weather_FEELS_LIKE);
                mWeatherHelper.setFeels_like(feels_like.getDouble(WeatherHelper.Weather_DAY_TEMP));

                JSONArray weather = day_list_object.getJSONArray(WeatherHelper.Weather_WEATHER);
                JSONObject JSONWeather = weather.getJSONObject(0);
                mWeatherHelper.setWeather_id(JSONWeather.getInt(WeatherHelper.Weather_ID));
                mWeatherHelper.setDescription(JSONWeather.getString(WeatherHelper.Weather_DESCRIPTION));
                mWeatherHelper.setWeatherColor(JSONWeather.getInt(WeatherHelper.Weather_ID));
                mWeatherHelper.setWeatherIcon(JSONWeather.getString(WeatherHelper.Weather_ICON));

                items.add(mWeatherHelper);

            }

            adapter.notifyDataSetChanged();

            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);

                recyclerViewDays.setAlpha(0);
                recyclerViewDays.animate().setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        recyclerViewDays.setVisibility(View.VISIBLE);

                        if (error_layout.getVisibility() == View.VISIBLE)
                            error_layout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).setDuration(250).alpha(1);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSONException", e);
        }

    }
}