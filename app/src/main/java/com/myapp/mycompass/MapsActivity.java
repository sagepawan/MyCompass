package com.myapp.mycompass;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    Intent fromLandingIntent;

    Button btnTrace;

    Location location;

    private LocationManager locationManager;

    //private LocationListener mlocListener;

    GpsTracker gpsTracker;

    AppLocationService appLocationService;

    Double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        gpsTracker = new GpsTracker(this);

        appLocationService = new AppLocationService(this);

        btnTrace = (Button) findViewById(R.id.btn_trace);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fromLandingIntent = getIntent();


        btnTrace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    Toast.makeText(MapsActivity.this, "GPS is Enabled in your device", Toast.LENGTH_SHORT).show();
                    getCurrentLocation();

                } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    Toast.makeText(MapsActivity.this, "NW is Enabled in your device", Toast.LENGTH_SHORT).show();
                    getCurrentLocation();
                } else {

                    showGPSDisabledAlertToUser();
                }*/

                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                Criteria criteria = new Criteria();
                String bestProvider = locationManager.getBestProvider(criteria, true);

                Location currentLocation = appLocationService.getLocation(bestProvider);


                Log.d("LocationAtbtnPress", ": " + currentLocation);

                if (currentLocation != null) {

                    double latitude = currentLocation.getLatitude();
                    double longitude = currentLocation.getLongitude();
                    double altitude = currentLocation.getAltitude();
                    Toast.makeText(
                            getApplicationContext(),
                            "Mobile Location: \nLatitude: " + latitude
                                    + "\nLongitude: " + longitude,
                            Toast.LENGTH_LONG).show();


                    Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                    intent.putExtra("lat", latitude);
                    intent.putExtra("long", longitude);
                    intent.putExtra("alt", altitude);
                    startActivity(intent);
                } else {

                    if (locationManager.isProviderEnabled(bestProvider))
                        Toast.makeText(MapsActivity.this, "Waiting for GPS...", Toast.LENGTH_SHORT).show();

                    else
                        showGPSDisabledAlertToUser();

                }


            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        final LatLng latlng = new LatLng(-34, 151);

        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        if (fromLandingIntent.getStringExtra("flag").equals("yesInput")) {

            // Add a marker in Sydney and move the camera
            mMap.addMarker(new MarkerOptions().position(latlng).title("Marker is location").draggable(true).flat(true));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }

        mMap.setMyLocationEnabled(true);


        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {

                LatLng chosenLatLng = new LatLng(point.latitude, point.longitude);

                MarkerOptions marker = new MarkerOptions().position(chosenLatLng).title("Selected Location");
                mMap.clear();
                mMap.addMarker(marker);

                Log.d("New LatLang", ": " + chosenLatLng);
            }
        });
    }

    //Dialog box that appears when GPS is disabled
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                //Intent that shows interface to start GPS in user's device
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    //Method to get current Location of user
    public void getCurrentLocation() {
        //mMap.clear();


        Criteria criteria = new Criteria();
        String locationProvider = locationManager.getBestProvider(criteria, true);

        Log.d("BestProvider", ": " + locationProvider);

        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }

            //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,2000,0, mlocListener);
            Log.d("LocationManager", ": " + locationManager);

            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(locationProvider);

                if (location != null) {
                    //setAddress(location.getLatitude(), location.getLongitude());
                    Log.d("UserGPSCoordinates", ": " + location.getLatitude() + "," + location.getLongitude());
                    Toast.makeText(MapsActivity.this, "Your Coordinates - " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Waiting for location..", Toast.LENGTH_LONG).show();

                    locationManager.requestLocationUpdates(locationProvider, 2000, 0, mlocListener);
                    //locationManager.removeUpdates(mlocListener);
                }
            } else {
                Log.d("ReturnsNullAt", ": LocationListener");
            }
        }
    }

    LocationListener mlocListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.

            Log.d("NewLocation", ": " + location.getLatitude() + ", " + location.getLongitude());
            Toast.makeText(MapsActivity.this, "Changed Coordinates - " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();
            if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            locationManager.removeUpdates(mlocListener);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };


}
