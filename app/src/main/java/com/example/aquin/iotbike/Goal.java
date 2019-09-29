package com.example.aquin.iotbike;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class Goal extends AppCompatActivity {
    BluetoothCom bt;
    double speed = 0;
    TextView speedView;
    TextView connectView;
    Location lastLocation = null;
    long startTime;
    int time;
    long updateTime;
    boolean running;
    int[] color;
    Button startBtn;
    Button addBtn;
    LinearLayout scroll;
    Activity context = this;
    Card[] cards;

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (lastLocation != null) {
                long now = System.currentTimeMillis();
                double elapsedTime = (now - updateTime) / 1_000; // Convert milliseconds to seconds
                updateTime = now;
                if (elapsedTime > 0) {
                    speed = lastLocation.distanceTo(location) / elapsedTime;
                }
                time = (int)((System.currentTimeMillis() - startTime) / 1000);
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
                if (running) {
                    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                    speedView.setText("speed: " + speed);
                }
            } catch (SecurityException e) { }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed);

        startBtn = (Button) findViewById(R.id.startBtn);
        addBtn = (Button) findViewById(R.id.addBtn);
        scroll = (LinearLayout) findViewById(R.id.scrollLayout);
        connectView = (TextView) findViewById(R.id.connectView);
        bt = new BluetoothCom();
        color = new int[3];
        speedView = (TextView) findViewById(R.id.speedView);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cards = new Card[scroll.getChildCount()];
                for (int i = 0; i < scroll.getChildCount(); i++) {
                    cards[i] = new Card((CardPic)scroll.getChildAt(i));
                }
                addBtn.setEnabled(false);
                startTime = System.currentTimeMillis();
                updateTime = System.currentTimeMillis();
                running = true;
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardPic temp = new CardPic(context);
                scroll.addView(temp);
                startBtn.setEnabled(true);

            }

        });

        bt = new BluetoothCom();
        if(bt.BTinit(this))
        {
            if(bt.BTconnect()) {
                connectView.setText("Bluetooth Connected!");
            }
        }

        //Start thread that tracks speed
        timerHandler.postDelayed(timerRunnable, 0);

    }

    void setColor() {
        //based on cards and time, set rgb
        int timeSum = 0;

        for(int i = 0; i < cards.length; i++) {
            timeSum += cards[i].getTimeInt();
            if (time < timeSum) {
                //set color and end function
                scroll.getChildAt(i).setBackgroundColor(Color.RED);
                setColor(cards[i].getSpeedInt(), (int)Math.round(speed));
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

    public void onPause() {
        super.onPause();
        bt.stopService();
    }
}
