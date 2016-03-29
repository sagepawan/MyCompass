package com.myapp.mycompass;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

public class AppLocationService extends Service {

    private final Context mContext;

    protected LocationManager locationManager;
    Location location;

    private static final long MIN_DISTANCE_FOR_UPDATE = 10;
    private static final long MIN_TIME_FOR_UPDATE = 1000 * 5;

    public AppLocationService(Context context) {
        this.mContext = context;
        locationManager = (LocationManager) mContext
                .getSystemService(LOCATION_SERVICE);
    }


    public Location getLocation(String locationProvider) {

        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);



        if (locationManager.isProviderEnabled(locationProvider)) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(mContext,"Permission not granted",Toast.LENGTH_SHORT).show();

            }

            //locationManager.requestLocationUpdates(locationProvider,
              //      MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, mLocListener);
            if (locationManager != null) {

                Log.d("BestProvider", ": " + locationProvider);

                location = locationManager.getLastKnownLocation(locationProvider);
                Log.d("Location", ": " + location);
                return location;
            }
            else
                locationManager.requestLocationUpdates(locationProvider,
                        MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, mLocListener);
        }
        return null;
    }

    public LocationListener mLocListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            Log.d("NewLocation", ": " + location.getLatitude() + ", " + location.getLongitude());

            Toast.makeText(
                    mContext,
                    "Changed Location: \nLatitude: " + location.getLatitude()
                            + "\nLongitude: " + location.getLongitude(),
                    Toast.LENGTH_LONG).show();

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            locationManager.removeUpdates(mLocListener);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
