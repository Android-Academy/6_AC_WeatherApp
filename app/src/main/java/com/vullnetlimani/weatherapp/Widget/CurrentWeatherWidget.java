package com.vullnetlimani.weatherapp.Widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class CurrentWeatherWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        Log.d(CurrentWidgetService.LOG_TAG, "updateAppWidget() - " + appWidgetId);

        int[] allWidgetIds = new int[]{appWidgetId};

        launchBackground(context, allWidgetIds);

        // Instruct the widget manager to update the widget
        // appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void launchBackground(Context context, int[] allWidgetIds) {
        Intent intent = new Intent(context, CurrentWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}