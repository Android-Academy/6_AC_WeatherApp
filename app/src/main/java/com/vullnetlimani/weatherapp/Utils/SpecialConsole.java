package com.vullnetlimani.weatherapp.Utils;

import android.util.Log;

public class SpecialConsole {

    public static void LONG_LOG_PRINT(String LOG_TAG, String yourLog) {
        final int chunkSize = 2048;
        for (int i = 0; i < yourLog.length(); i += chunkSize) {
            Log.d(LOG_TAG, yourLog.substring(i, Math.min(yourLog.length(), i + chunkSize)));
        }
    }

}
