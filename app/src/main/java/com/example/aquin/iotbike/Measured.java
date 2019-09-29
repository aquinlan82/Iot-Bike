package com.example.aquin.iotbike;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;


public class Measured extends AppCompatActivity {
    BluetoothCom bt;
    double speed = 0;
    TextView speedView;
    TextView avgSpeedView;
    TextView maxSpeedView;
    TextView connectView;
    Location lastLocation = null;
    ArrayList<Double> speeds;
    double maxSpeed;
    double avgSpeed;
    long updateTime;
    int[] color;
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (lastLocation != null) {
                long now = System.currentTimeMillis();
                double elapsedTime = (now - updateTime) / 1_000; // Convert milliseconds to seconds
                updateTime = now;
                if (elapsedTime > 0) {
                    setValues(lastLocation.distanceTo(location) / elapsedTime);
                }
                setColor();
                bt.sendData(color[0] + "/" + color[1] + "/" + color[2]);
            }
            lastLocation = location;
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

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            timerHandler.postDelayed(this, 100);
            try {
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                speedView.setText("speed: " + speed);
                avgSpeedView.setText("average speed: " + avgSpeed);
                maxSpeedView.setText("max speed: " + maxSpeed);
            } catch (SecurityException e) { }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measured);

        connectView = (TextView) findViewById(R.id.connectView);
        bt = new BluetoothCom();
        color = new int[3];
        if(bt.BTinit(this))
        {
            if(bt.BTconnect()) {
                connectView.setText("Bluetooth Connected!");
                updateTime = System.currentTimeMillis();
            }
        }
        speedView = (TextView) findViewById(R.id.speedView);
        avgSpeedView = (TextView) findViewById(R.id.avgSpeedView);
        maxSpeedView = (TextView) findViewById(R.id.maxSpeedView);
        speeds = new ArrayList<Double>();

        //Start thread that tracks speed

        timerHandler.postDelayed(timerRunnable, 0);

    }

    void setColor() {
        double max = 6.5;
        double cutoff = max / 4;
        if (speed > (3 * cutoff)) {
            color[0] = 255;
            color[1] = scaleColor(0, cutoff, true);
            color[2] = 0;
        } else if (speed > (2 * cutoff)) {
            color[0] = scaleColor(cutoff, 2 * cutoff, false);
            color[1] = 255;
            color[2] = 0;
        } else if (speed > cutoff) {
            color[0] = 0;
            color[1] = 255;
            color[2] = scaleColor(2 * cutoff, 3 * cutoff, true);
        } else {
            color[0] = 0;
            color[1] = scaleColor(3 * cutoff, max, false);
            color[2] = 255;
        }
    }

    int scaleColor(double minSpeed, double maxSpeed, boolean increasing) {
        double factor = 255 / (maxSpeed - minSpeed);
        int value = (int)(speed * factor);
        if (increasing) {
            return value;
        }
        return 255 - value;
    }

    void setValues(double newSpeed) {
        speed = newSpeed;

        if (speed > maxSpeed) {
            maxSpeed = speed;
        }
        speeds.add(speed);
        averageSpeeds();
    }

    void averageSpeeds() {
        int sum = 0;
        for(double speed : speeds) {
            sum += speed;
        }
        avgSpeed = sum / speeds.size();
    }

    public void onPause() {
        super.onPause();
        bt.stopService();
    }

}