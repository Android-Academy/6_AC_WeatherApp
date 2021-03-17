package com.vullnetlimani.weatherapp.Widget;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.vullnetlimani.weatherapp.Activity.MainActivity;
import com.vullnetlimani.weatherapp.Helper.AsyncHelper;
import com.vullnetlimani.weatherapp.Helper.MySharedPreferences;
import com.vullnetlimani.weatherapp.Helper.WeatherHelper;
import com.vullnetlimani.weatherapp.R;
import com.vullnetlimani.weatherapp.Utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class CurrentWidgetService extends Service {

    public static final String WIDGET_PROGRESS_CHANNEL_ID = "WidgetProgressChannel_ID";
    public static final String WIDGET_PROGRESS = "WidgetProgress";
    public static String LOG_TAG = "CurrentWidgetServiceLog";
    private Context context;
    private AppWidgetManager appWidgetManager;
    private Handler mHandler;
    private String city, language, unit;
    private String latitude, longitude;
    private boolean coordinate_search;
    private WeatherHelper mWeatherHelper;
    private int[] allWidgetIds;

    public CurrentWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(LOG_TAG, "onStartCommand() - " + intent);

        mHandler = new Handler(Looper.getMainLooper());

        if (intent != null) {

            showProgressNotification();

            mWeatherHelper = new WeatherHelper();

            context = this.getApplicationContext();

            appWidgetManager = AppWidgetManager.getInstance(context);

            allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

            Log.d(LOG_TAG, "allWidgetIds() - " + allWidgetIds[0]);

            for (Integer widgetId : allWidgetIds) {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        getWeatherData(widgetId);
                    }
                });

            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void showProgressNotification() {

        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this, WIDGET_PROGRESS_CHANNEL_ID);

            NotificationChannel notificationChannel = new NotificationChannel(
                    WIDGET_PROGRESS_CHANNEL_ID,
                    WIDGET_PROGRESS,
                    NotificationManager.IMPORTANCE_NONE);

            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            notificationChannel.setSound(null, null);

            notificationChannel.setShowBadge(false);

            if (notificationManager.getNotificationChannel(WIDGET_PROGRESS_CHANNEL_ID) == null)
                notificationManager.createNotificationChannel(notificationChannel);


            nBuilder.setSmallIcon(R.drawable.ic_current_weather);

            startForeground(Math.toIntExact(System.currentTimeMillis() % 11000), nBuilder.build());

            stopForeground(false);

        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate()");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d(LOG_TAG, "onTaskRemoved()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(LOG_TAG, "onDestroy()");

    }

    private void getWeatherData(Integer appWidgetID) {

        preloadData(appWidgetID);

        city = MySharedPreferences.getNormalPref(this, Constants.edit_text_location_key, Constants.LOCATION_CITY_DEFAULT);
        latitude = MySharedPreferences.getNormalPref(this, Constants.latitude_pref_key, "0");
        longitude = MySharedPreferences.getNormalPref(this, Constants.longitude_pref_key, "0");
        coordinate_search = MySharedPreferences.getBooleanPref(this, Constants.coordinate_search_key, false);
        unit = MySharedPreferences.getNormalPref(this, Constants.list_unit_code_key, Constants.UNI_CODE_DEFAULT);
        language = Constants.Language_EN;

        Log.d(LOG_TAG, "Coordinate Search = " + coordinate_search);

        if (coordinate_search) {
            extractDataFromServer(
                    AsyncHelper.checkCurrentWeather(latitude, longitude, unit, language), appWidgetID
            );
        } else {
            extractDataFromServer(
                    AsyncHelper.checkCurrentWeather(city, unit, language), appWidgetID
            );
        }

    }

    private void preloadData(Integer appWidgetID) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.current_weather_widget);

        views.setTextViewText(R.id.current_location, getString(R.string.loading));

        views.setViewVisibility(R.id.refresh_button, View.INVISIBLE);

        views.setViewVisibility(R.id.refresh_button_progress, View.VISIBLE);

        appWidgetManager.updateAppWidget(appWidgetID, views);
    }

    private void extractDataFromServer(String yourCurrentRequest, Integer appWidgetId) {

        AsyncHttpClient client = new AsyncHttpClient();

        // Koha per Sot
        client.get(yourCurrentRequest, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String data = new String(responseBody);

                Log.d(LOG_TAG, "Your Current Data - " + data);

                if (!data.equals("")) {

                    mWeatherHelper.ParseTodayData(data);

                    updateTodayData(appWidgetId);

                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                Log.e(LOG_TAG, "error - ", error);

                stopSelf();

            }
        });

    }

    private void updateTodayData(Integer widgetId) {

        Log.d(LOG_TAG, "updateTodayData()");

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.current_weather_widget);

        views.setTextViewText(R.id.current_location, mWeatherHelper.getCity());

        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        views.setTextViewText(R.id.last_checked, getString(R.string.setLastCheck, currentTime));

        if (unit.contains(getString(R.string.metric))) {
            views.setTextViewText(R.id.temperature, getString(R.string.setTempC, mWeatherHelper.getCurrent_Temp()));
            views.setTextViewText(R.id.feels_like, getString(R.string.setFeelsLikeC, mWeatherHelper.getFeels_like()));
            views.setTextViewText(R.id.wind, getString(R.string.setKMH_SPEED, mWeatherHelper.getSpeed()));
        } else {
            views.setTextViewText(R.id.temperature, getString(R.string.setTempF, mWeatherHelper.getCurrent_Temp()));
            views.setTextViewText(R.id.feels_like, getString(R.string.setFeelsLikeF, mWeatherHelper.getFeels_like()));
            views.setTextViewText(R.id.wind, getString(R.string.setMPH_SPEED, mWeatherHelper.getSpeed()));
        }

        views.setTextViewText(R.id.desc, mWeatherHelper.getDescription());
        views.setTextViewText(R.id.humidity, getString(R.string.setHUMIDITY, mWeatherHelper.getHumidity()));
        views.setTextViewText(R.id.pressure, getString(R.string.setPRESSURE, mWeatherHelper.getPressure()));

        views.setImageViewResource(R.id.todayStat_ImageView, mWeatherHelper.getWeatherIcon());

        views.setInt(R.id.widget_main_layout, "setBackgroundColor", ContextCompat.getColor(this, mWeatherHelper.getWeatherColor()));

        Intent refresh_intent = new Intent(context, CurrentWeatherWidget.class);
        refresh_intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        refresh_intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, widgetId, refresh_intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.refresh_button, pendingIntent);

        Intent configIntent = new Intent(context, MainActivity.class);
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, widgetId, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.time_layout, configPendingIntent);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                views.setViewVisibility(R.id.refresh_button, View.VISIBLE);
                views.setViewVisibility(R.id.refresh_button_progress, View.INVISIBLE);
                appWidgetManager.updateAppWidget(widgetId, views);
                stopSelf();
            }
        }, 500);
    }
}