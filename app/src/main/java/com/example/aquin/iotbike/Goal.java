package com.example.aquin.iotbike;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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


public class Goal extends AppCompatActivity implements SensorEventListener {
    private BluetoothCom bt;
    private double speed = 0;
    private TextView speedView;
    private TextView connectView;
    private boolean connected;
    private long startTime;
    private long lastTime;
    private int time;
    private boolean running;
    private int[] color;
    private Button startBtn;
    private Button addBtn;
    private LinearLayout scroll;
    private Activity context = this;
    private Card[] cards;
    private SensorManager sensorManager;
    private Sensor sensor;
    private double v0 = 0;
    private double acc = 0;
    private ArrayList<ArrayList<Double>> speeds;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            timerHandler.postDelayed(this, 100);
            //setupBt();
            try {
                if (running) {
                    getSpeed();
                    speedView.setText("speed: " + speed);
                } else {
                    speedView.setText("Done");
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
        connected = false;

        speeds = new ArrayList<ArrayList<Double>>();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cards = new Card[scroll.getChildCount()];
                for (int i = 0; i < scroll.getChildCount(); i++) {
                    cards[i] = new Card((CardPic)scroll.getChildAt(i));
                }
                addBtn.setEnabled(false);
                startTime = System.currentTimeMillis();
                lastTime = System.currentTimeMillis();
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

        //Start thread that tracks speed
        timerHandler.postDelayed(timerRunnable, 100);

    }

    /**
     * Based on speed, determine rgb
     */
    void setColor() {
        //based on cards and time, set rgb
        int timeSum = 0;
        time = (int)(System.currentTimeMillis() - startTime) / 1000;
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
        bt.sendData("255/255/255");
        color[0] = 255;
        color[1] = 255;
        color[2] = 255;
        running = false;
        speedView.setText("Done");
    }

    /**
     * Based on speed, determine rgb
     */
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

    /**
     * Destroy bluetooth connection when leaving screen
     */
    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
        sendToCloud();
    }

    /**
     * Create connection to Arduino
     */
    void setupBt() {
        if (!connected) {
            connectView.setText("Connecting...");
            if (bt.BTinit(context)) {
                if (bt.BTconnect()) {
                    connectView.setText("Bluetooth Connected!");
                    connected = true;
                } else {
                    connectView.setText("Bluetooth NOT Connected");
                }
            } else {
                connectView.setText("Bluetooth NOT Connected");
            }
        }
    }

    /**
     * Calculate speed from acceleration using v = v0 + at
     */
    public void getSpeed() {
        ////////////////
        //v = v0 + at
        speed = v0 + acc*((double)(System.currentTimeMillis() - lastTime) / 100.0);
        lastTime = System.currentTimeMillis();
        ArrayList<Double> temp = new ArrayList<Double>();
        temp.add(speed);
        double nowTime = (double)((System.currentTimeMillis() - startTime) / 1000);
        temp.add(nowTime);
        speeds.add(temp);
        ////////////////
        setColor();
        if (connected && running) {
            //bt.sendData(color[0] + "/" + color[1] + "/" + color[2]);
        }
    }

    /**
     * Update acceleration values
     * @param event
     */
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

    /**
     * Create map of data points containing speed and time, plus an id to group data points
     */
    public void sendToCloud() {
        String date = new SimpleDateFormat("M-d-yyyy", Locale.getDefault()).format(new Date());
        for (int i = 0; i < speeds.size(); i+= 10) {
            Map<String, Object> user = new HashMap<>();
            user.put("speed", speeds.get(i).get(0));
            user.put("time", speeds.get(i).get(1));
            user.put("id", startTime);

            // Add a new document with a generated ID
            db.collection(date)
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.v("ASDFS", "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.v("ASDFS", "Error adding document", e);
                        }
                    });
        }

    }
}
