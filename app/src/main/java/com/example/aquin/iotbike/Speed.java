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


public class Speed extends AppCompatActivity {
    BluetoothCom bt;
    double speed = 0;
    double longitude;
    double latitude;
    TextView speedView;
    TextView connectView;
    Location lastLocation = null;
    long startTime;
    int time;
    long updateTime;
    int[] color;
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //Log.v("ASDF", "change");
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            if (lastLocation != null) {
                long now = System.currentTimeMillis();
                double elapsedTime = (now - updateTime) / 1_000; // Convert milliseconds to seconds
                updateTime = now;
                if (elapsedTime > 0) {
                    speed = lastLocation.distanceTo(location) / elapsedTime;
                }
                time = (int)((System.currentTimeMillis() - startTime) / 1000);
                setColor();
                //Log.v("ASDF",color[0] + " " + color[1] + " " + color[2]);
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
            } catch (SecurityException e) { }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed);

        connectView = (TextView) findViewById(R.id.connectView);
        bt = new BluetoothCom();
        color = new int[3];
        if(bt.BTinit(this))
        {
            if(bt.BTconnect()) {
                connectView.setText("Bluetooth Connected!");
                startTime = System.currentTimeMillis();
                updateTime = System.currentTimeMillis();
            }
        }
        speedView = (TextView) findViewById(R.id.speedView);
        //Start thread that tracks speed

        timerHandler.postDelayed(timerRunnable, 0);

    }

    void setColor() {
        //based on cards and time, set rgb
        CardWrapper cards = (CardWrapper) getIntent().getSerializableExtra("cards");
        int timeSum = 0;

        for(int i = 0; i < cards.getArray().length; i++) {
            timeSum += cards.getArray()[i].getTimeInt();
            if (time < timeSum) {
                //set color and end function
                setColor(cards.getArray()[i].getSpeedInt(), (int)Math.round(speed));
                return;
            }
        }
        //finished workout
        color[0] = 255;
        color[1] = 255;
        color[2] = 255;
    }

    private void setColor(int goal, int actual) {
        int diff = actual - goal;
        if (Math.abs(diff) < 2) {
            color[0] = 0;
            color[1] = 255;
            color[2] = 0;
        } else if (diff < 0) {
            color[0] = 0;
            color[1] = 0;
            color[2] = 255;
        } else {
            color[0] = 255;
            color[1] = 0;
            color[2] = 0;
        }
    }
}
