package com.vullnetlimani.weatherapp.Activity;

import android.animation.Animator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.vullnetlimani.weatherapp.Helper.AsyncHelper;
import com.vullnetlimani.weatherapp.Helper.Constants;
import com.vullnetlimani.weatherapp.Helper.MySharedPreferences;
import com.vullnetlimani.weatherapp.Helper.WeatherHelper;
import com.vullnetlimani.weatherapp.R;
import com.vullnetlimani.weatherapp.adapters.ForecastOverviewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class HourlyForecastActivity extends BaseActivity {

    public static final String LOG_TAG = "HourlyForecastLog";
    public ArrayList<WeatherHelper> items;
    private String latitude, longitude, unit, language;
    private WeatherHelper mWeatherHelper;
    private ForecastOverviewAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerViewDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_forecast);

        setupToolbar(getString(R.string.hourly_weather));
        setToolbarBackIcon();

        mSwipeRefreshLayout = findViewById(R.id.mSwipeRefreshLayout);
        recyclerViewDays = findViewById(R.id.recyclerViewDays);

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

        client.get(AsyncHelper.checkHourlyWeather(latitude, longitude, unit, language), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String data = new String(responseBody);

                // SpecialConsole.LONG_LOG_PRINT(LOG_TAG, data);

                if (!data.equals("")) {
                    parseHourlyData(data);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(LOG_TAG, "Error", error);

                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void parseHourlyData(String data) {

        JSONObject reader;

        try {

            reader = new JSONObject(data);

            JSONArray hour_list_array = reader.getJSONArray(WeatherHelper.Weather_HOURLY);

            Log.d(LOG_TAG, "Hour count - " + hour_list_array.length());

            for (int i = 1; i < hour_list_array.length(); i++) {

                mWeatherHelper = new WeatherHelper();

                JSONObject hour_list_object = hour_list_array.getJSONObject(i);

                mWeatherHelper.setTime(hour_list_object.getLong(WeatherHelper.Weather_DT));

                mWeatherHelper.setPressure(hour_list_object.getDouble(WeatherHelper.Weather_PRESSURE));

                mWeatherHelper.setHumidity(hour_list_object.getInt(WeatherHelper.Weather_HUMIDITY));

                mWeatherHelper.setSpeed(hour_list_object.getDouble(WeatherHelper.Weather_WIND_SPEED));

                mWeatherHelper.setDaily_Temp(hour_list_object.getDouble(WeatherHelper.Weather_TEMP));

                mWeatherHelper.setFeels_like(hour_list_object.getDouble(WeatherHelper.Weather_FEELS_LIKE));

                JSONArray weather = hour_list_object.getJSONArray(WeatherHelper.Weather_WEATHER);
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