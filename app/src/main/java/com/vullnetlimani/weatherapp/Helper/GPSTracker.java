package com.vullnetlimani.weatherapp.Helper;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class GPSTracker extends Service implements LocationListener {

    public static final String GPS_TRACKER_LOG = "GPSTrackerLog";
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 5000;
    private final Context context;
    private final LocationInterface locationInterface;
    protected LocationManager mLocationManager;
    double latitude;
    double longitude;
    boolean isNetworkEnabled = false;
    private Location location;

    public GPSTracker(Context context, LocationInterface locationInterface) {
        this.context = context;
        this.locationInterface = locationInterface;
    }


    public boolean getLocation() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {

            mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Log.d(GPS_TRACKER_LOG, "isNetworkEnabled - " + isNetworkEnabled);

            if (isNetworkEnabled) {

                mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (location != null) {

                    Log.d(GPS_TRACKER_LOG, "Location - " + location.getLatitude());
                    Log.d(GPS_TRACKER_LOG, "mLocationManager - " + mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude());

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    locationInterface.autoLocateInfo(latitude, longitude);

                }

                return true;

            } else return false;

        }

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Log.d(GPS_TRACKER_LOG, "latitude OLD - " + latitude);
        Log.d(GPS_TRACKER_LOG, "longitude OLD - " + longitude);

        if (longitude != 0 && latitude != 0) {

            locationInterface.autoLocateInfo(latitude, longitude);

            Log.d(GPS_TRACKER_LOG, "latitude changed - " + latitude);
            Log.d(GPS_TRACKER_LOG, "longitude changed - " + longitude);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(GPSTracker.this);
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public interface LocationInterface {
        void autoLocateInfo(double latitude, double longitude);
    }

}
