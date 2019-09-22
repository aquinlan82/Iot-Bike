package com.example.aquin.iotbike;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class Speed extends AppCompatActivity {
    BluetoothCom bt;
    double speed = 0;
    double longitude;
    double latitude;
    TextView speedView;
    Location lastLocation = null;
    LocationManager lm;
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            Log.v("ASDF", "change");
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            if (lastLocation != null) {
                double elapsedTime = (location.getTime() - lastLocation.getTime()) / 1_000; // Convert milliseconds to seconds
                speed = lastLocation.distanceTo(location) / elapsedTime;
                bt.sendData(speed+"");
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
            timerHandler.postDelayed(this, 0);
            try {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                speedView.setText("speed: " + speed);
            } catch (SecurityException e) {}

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed);
        bt = new BluetoothCom();
        if(bt.BTinit(this))
        {
            bt.BTconnect();
        }
        speedView = (TextView) findViewById(R.id.speedView);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Start thread that tracks speed
        timerHandler.postDelayed(timerRunnable, 0);

    }

}
