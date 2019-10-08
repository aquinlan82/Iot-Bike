package com.example.aquin.iotbike;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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


public class Measured extends AppCompatActivity implements SensorEventListener {
    BluetoothCom bt;
    double speed = 0;
    boolean connected;
    TextView speedView;
    TextView avgSpeedView;
    TextView maxSpeedView;
    TextView connectView;
    ArrayList<Double> speeds;
    double maxSpeed;
    double avgSpeed;
    long updateTime;
    int[] color;
    Activity context = this;
    private SensorManager sensorManager;
    private Sensor sensor;
    double v0 = 0;
    double acc = 0;
    long startTime;



    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            timerHandler.postDelayed(this, 100);
            try {
                setupBt();
                if (connected) {
                    getSpeed();
                    speedView.setText("Speed: " + String.format("%.2f", speed));
                    avgSpeedView.setText("Average speed: " + String.format("%.2f", avgSpeed));
                    maxSpeedView.setText("Max speed: " + String.format("%.2f", maxSpeed));
                }
            } catch (SecurityException e) { }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measured);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        connectView = (TextView) findViewById(R.id.connectView);
        color = new int[3];
        connected = false;
        speedView = (TextView) findViewById(R.id.speedView);
        avgSpeedView = (TextView) findViewById(R.id.avgSpeedView);
        maxSpeedView = (TextView) findViewById(R.id.maxSpeedView);
        speeds = new ArrayList<Double>();
        bt = new BluetoothCom();

        //Start thread that tracks speed
        timerHandler.postDelayed(timerRunnable, 100);

    }

    void setColor() {
        double max = 10;
        double cutoff = max / 4;
        if (speed > (4 * cutoff)) {
            color[0] = 255;
            color[1] = 255;
            color[2] = 254;
        } else if (speed > (3 * cutoff)) {
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

    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }

    void setupBt() {
        if (!connected) {
            connectView.setText("Connecting...");
            if (bt.BTinit(context)) {
                if (bt.BTconnect()) {
                    connectView.setText("Bluetooth Connected!");
                    connected = true;
                    updateTime = System.currentTimeMillis();

                } else {
                    connectView.setText("Bluetooth NOT Connected");
                }
            } else {
                connectView.setText("Bluetooth NOT Connected");
            }
        }
    }

    public void getSpeed() {
        ////////////////
        //v = v0 + at
        double speedCalc = v0 + acc*((double)(System.currentTimeMillis() - startTime)/100.0);
        startTime = System.currentTimeMillis();
        ////////////////
        setValues(speedCalc);
        setColor();
        if (connected) {
            bt.sendData(color[0] + "/" + color[1] + "/" + color[2]);
        }
    }

    public void onSensorChanged(SensorEvent event){
        float[] accs = {event.values[0], event.values[1], event.values[2]};
        acc = 0;
        for (int i = 0; i < accs.length; i++) {
            if (Math.abs(accs[i]) < .01) {
                accs[i] = 0;
            }
            acc += Math.pow(accs[i], 2);
        }
        acc = Math.sqrt(acc);
    }

    public void onAccuracyChanged(Sensor event, int amt){
    }
}