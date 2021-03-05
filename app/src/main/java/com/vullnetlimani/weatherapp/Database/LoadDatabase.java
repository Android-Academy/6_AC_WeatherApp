package com.vullnetlimani.weatherapp.Database;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.vullnetlimani.weatherapp.Activity.MainActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.vullnetlimani.weatherapp.Database.DatabaseHelper.DATABASE_HELPER_LOG;

public class LoadDatabase {

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private final AppCompatActivity mAppCompatActivity;

    public LoadDatabase(AppCompatActivity mAppCompatActivity) {
        this.mAppCompatActivity = mAppCompatActivity;
    }

    public void doInBackground() {

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                DatabaseHelper databaseHelper = new DatabaseHelper(mAppCompatActivity);

                databaseHelper.createDatabase();

                databaseHelper.close();

                Log.d(DATABASE_HELPER_LOG, "doInBackground - finished");

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) mAppCompatActivity).openDatabase();
                    }
                });
            }
        });
    }
}