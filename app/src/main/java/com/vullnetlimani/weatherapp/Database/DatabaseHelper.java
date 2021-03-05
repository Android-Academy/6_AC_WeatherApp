package com.vullnetlimani.weatherapp.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_CITY = "city_ascii";
    public static final String DATABASE_LAT = "lat";
    public static final String DATABASE_LNG = "lng";
    public static final String DATABASE_COUNTRY = "country";
    public static final String DATABASE_ISO2 = "iso2";
    public static final String DATABASE_ADMIN_NAME = "admin_name";
    public static final String DATABASE_POPULATION = "population";
    public static final String DATABASE_ID = "_id";

    public static final String DATABASE_HELPER_LOG = "DatabaseHelperLog";
    private static final String DB_NAME = "cities.db";
    private final String DB_PATH;
    private final Context context;
    private String percentage;
    private SQLiteDatabase myDatabase;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
        this.DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
    }

    public void createDatabase() {

        boolean dbExist = checkDatabase();

        if (!dbExist) {

            this.getReadableDatabase();
            try {
                copyDatabase();
            } catch (IOException e) {
                throw new Error("Error Copying Database");
            }

        }

    }

    public boolean checkDatabase() {

        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLException ignored) {

        }

        if (checkDB != null)
            checkDB.close();


        return checkDB != null;

    }

    private void copyDatabase() throws IOException {

        InputStream myInput = context.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        long total = 0;

        int fileSize = myInput.available();

        Log.d(DATABASE_HELPER_LOG, "File Size - " + fileSize);

        while ((length = myInput.read(buffer)) > 0) {

            total += length;

            percentage = String.valueOf((total * 100) / fileSize);

            Log.d(DATABASE_HELPER_LOG, "Database Copy Process - " + percentage + "%");

            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDatabase() throws SQLException {

        Log.d(DATABASE_HELPER_LOG, "openDatabase()");

        String path = DB_PATH + DB_NAME;
        myDatabase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {

        if (myDatabase != null)
            myDatabase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try {
            this.getReadableDatabase();
            context.deleteDatabase(DB_NAME);
            copyDatabase();
        } catch (IOException e) {

            Log.e(DATABASE_HELPER_LOG, "IOException - ", e);

        }
    }

    public Cursor getSuggestions(String newText) {

        return myDatabase.rawQuery(
                "SELECT "
                        + DATABASE_ID + ","
                        + DATABASE_ISO2 + ","
                        + DATABASE_ADMIN_NAME + ","
                        + DATABASE_LAT + ","
                        + DATABASE_LNG + ","
                        + DATABASE_CITY + " FROM worldcities WHERE " + DATABASE_CITY + " LIKE '" + newText + "%' LIMIT 40", null);

    }
}
