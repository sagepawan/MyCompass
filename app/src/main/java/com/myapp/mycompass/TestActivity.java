package com.myapp.mycompass;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class TestActivity extends AppCompatActivity{

    TextView tvLocation;

    //LocationListener mLocListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        tvLocation = (TextView) findViewById(R.id.tvCoords);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Log.d("locProvider",": "+bestProvider);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER , 0, 0, mLocListener);
        Log.d("BeforeOnChanged",": "+locationManager.getLastKnownLocation(bestProvider));

    }

    public LocationListener mLocListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if(location!=null) {
                tvLocation.setText(location.getLatitude() + ", " + location.getLongitude());
            }
            else{
                Log.d("AfterOnChanged",": "+"Location is null");
            }
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


}
