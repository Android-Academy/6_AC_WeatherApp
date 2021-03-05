package com.vullnetlimani.weatherapp.Helper;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {

    private static final String PREFS_NAME = "weatherapp";
    private static final String FIRST_TIME_PERMISSION_ASK = "first_time_permission_ask";

    public static void firstTimeAskingPermission(Context context, boolean isFirstTime) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(FIRST_TIME_PERMISSION_ASK, isFirstTime).apply();
    }

    public static boolean isFirstTimeAskingPermission(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(FIRST_TIME_PERMISSION_ASK, true);
    }

    public static void setNormalPref(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getNormalPref(Context context, String key, String defValue) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getString(key, defValue);
    }

    public static void setNormalPref_Integer(Context context, String key, int value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getNormalPref_Integer(Context context, String key, int defValue) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(key, defValue);
    }

    public static void setBooleanPref(Context context, String key, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBooleanPref(Context context, String key, boolean defValue) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, defValue);
    }

}
