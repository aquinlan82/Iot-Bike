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
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
Speed Setting
Continuously monitors speed and gives up to 
date feedback to user
**/
public class Measured extends AppCompatActivity implements SensorEventListener {
    private BluetoothCom bt;
    private double speed = 0;     //speed calculated from accelerometer
    private boolean connected;    //connection status
    private TextView speedView;    //view to inform user of speed and bluetooth data
    private TextView avgSpeedView;
    private TextView maxSpeedView;
    private TextView connectView;
    private ArrayList<ArrayList<Double>> speeds;   //speeds stored for average calculation
    private double maxSpeed;   
    private double avgSpeed;
    private long updateTime;  //
    private int[] color;      //color to send to Pixels
    private Activity context = this;
    private SensorManager sensorManager;    //used to get accelerometer data
    private Sensor sensor; 
    private double v0 = 0;     //initial velocity used to calculate acceleration
    private double acc = 0;
    private long startTime;    //time since last speed datapoint collected
    private long appStart;		//time since starting speed mode
    private FirebaseFirestore db = FirebaseFirestore.getInstance();   //all data stored in firebase for later



    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
		//continously checks bluetooth connection and collects speed
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

	//displays GUI and initializes variables
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
        speeds = new ArrayList<ArrayList<Double>>();
        bt = new BluetoothCom();
        appStart = System.currentTimeMillis();

        //Start thread that tracks speed
        timerHandler.postDelayed(timerRunnable, 100);

    }

	//Based on speed, determine color
    void setColor() {
        double max = 6;
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

     // Helper function for converting input to rgb value
     // @param minSpeed Start of usable color spectrum
     // @param maxSpeed End of usable color spectrum
     // @param increasing True if higher input means higher output, false otherwise
     // @return speed scaled to 0-255 scale
    private int scaleColor(double minSpeed, double maxSpeed, boolean increasing) {
        double factor = 255 / (maxSpeed - minSpeed);
        int value = (int)(speed * factor);
        if (increasing) {
            return value;
        }
        return 255 - value;
    }

    // Sets speed, max, and average values
    void setValues(double newSpeed) {
        speed = newSpeed;
        double nowTime = (System.currentTimeMillis() - appStart) / 1000;

        if (speed > maxSpeed) {
            maxSpeed = speed;
        }
        ArrayList temp = new ArrayList<Double>();
        temp.add(speed);
        temp.add(nowTime);
        speeds.add(temp);
        averageSpeeds();
    }

	//Helper function to get average speed
    void averageSpeeds() {
        int sum = 0;
        for(ArrayList<Double> value : speeds) {
            sum += value.get(0);
        }
        avgSpeed = sum / speeds.size();
    }

    //Destroy bluetooth connection when leaving screen
    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
        sendToCloud();
    }

	//Create connection to HC-06
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

    //Calculate speed from acceleration using v = v0 + at and send to HC-06
    public void getSpeed() {
        double speedCalc = v0 + acc*((double)(System.currentTimeMillis() - startTime)/100.0);
        startTime = System.currentTimeMillis();

        setValues(speedCalc);
        setColor();
        if (connected) {
            bt.sendData(color[0] + "/" + color[1] + "/" + color[2]);
        }
    }

	//Update acceleration values using accelerometer
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

    //Create map of data points containing speed and time, plus an id to group data points
    public void sendToCloud() {
        String date = new SimpleDateFormat("M-d-yyyy", Locale.getDefault()).format(new Date());
        for (int i = 0; i < speeds.size(); i+= 10) {
            Map<String, Object> user = new HashMap<>();
            user.put("speed", speeds.get(i).get(0));
            user.put("time", speeds.get(i).get(1));
            user.put("id", appStart);

            // Add a new document with a generated ID
            db.collection(date)
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.v("Graph", "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.v("Graph", "Error adding document", e);
                        }
                    });
            }

    }
}
