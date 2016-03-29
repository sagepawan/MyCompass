package com.myapp.mycompass;

import android.content.Intent;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // define the display assembly compass picture
    private ImageView image;

    // record the compass picture angle turned
    private float currentDegree = 0f;

    // device sensor manager
    private SensorManager mSensorManager;

    TextView tvHeading;

    LocationManager locMananger;

    Location desLocation, currentLoc;

    final LatLng desLatLng = new LatLng(40.405900, 49.867374);

    GeomagneticField geomagneticField;

    double latidude, longitude, altitude;

    float azimuth;   //get azimuth from the orientation sensor (it's quite simple)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        latidude = intent.getDoubleExtra("lat",latidude);
        longitude = intent.getDoubleExtra("long", longitude);
        altitude = intent.getDoubleExtra("alt",altitude);


        desLocation = new Location("");
        desLocation.setLatitude(desLatLng.latitude);
        desLocation.setLongitude(desLatLng.longitude);

        currentLoc = new Location("");
        currentLoc.setLatitude(latidude);
        currentLoc.setLongitude(longitude);
        currentLoc.setAltitude(altitude);

        Log.d("CompassCurrent", ": " + currentLoc.getLatitude() + ", " + currentLoc.getLongitude() +", "+desLocation.getAltitude());

        Log.d("CompassDes", ": " + desLocation.getLatitude() + ", " + desLocation.getLongitude() +", "+desLocation.getAltitude());

        azimuth = (float) Math.toDegrees(azimuth);


        image = (ImageView) findViewById(R.id.imageViewCompass);

        // TextView that will tell the user what degree is he heading
        tvHeading = (TextView) findViewById(R.id.tvHeading);

        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //initialize location service for current device
        locMananger = (LocationManager) getSystemService(LOCATION_SERVICE);

        geomagneticField = new GeomagneticField(

                (float) currentLoc.getLatitude(),
                (float) currentLoc.getLongitude(),
                (float) currentLoc.getAltitude(),
                System.currentTimeMillis());

    }

    @Override
    protected void onResume() {
        super.onResume();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float degree = Math.round(event.values[0]);

        tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");
        //tvHeading.setText("");

        azimuth += geomagneticField.getDeclination(); // converts magnetic north into true north

        float bearing = currentLoc.bearingTo(desLocation); // (it's already in degrees)

        float direction = azimuth - bearing;

        float degrees = direction*180/(float)Math.PI;

        Log.d("bearing",": "+bearing+" direction: "+direction+"Degreees: "+degrees+"NorthPoleDegree: "+degree);

        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(200);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        image.startAnimation(ra);
        currentDegree = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
