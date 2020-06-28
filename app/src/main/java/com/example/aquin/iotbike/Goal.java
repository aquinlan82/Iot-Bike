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

/**
Goal Setting
Tracks the cards added and then executes 
the program based on speed from the accelerometer

**/
public class Goal extends AppCompatActivity implements SensorEventListener {
    private BluetoothCom bt;
    private double speed = 0;    //from accelerometer
    private TextView speedView;  //view show user data about speed and connectivity
    private TextView connectView;
    private boolean connected;   //connection status to HC-06
    private long startTime;      //time at which cards started executing
    private long lastTime;       //last time to be used in velocity calculation
    private boolean running;	//true when executing cards, false before and after
    private int[] color;           //color to send to Pixels
    private Button startBtn;       //start execting cards button
    private Button addBtn;		   //add Card button
    private LinearLayout scroll;   //layout cards are place on
    private Activity context = this; 
    private Card[] cards;            //created cards
    private SensorManager sensorManager;    //get sensor data
    private Sensor sensor;
    private double v0 = 0;       //inital velocity used to calculate acceleration
    private double acc = 0;
    private ArrayList<ArrayList<Double>> speeds;   //all speeds stored for average calculation
    private FirebaseFirestore db = FirebaseFirestore.getInstance();   //all data stored in firebase for later

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
		//continuously test connection to HC-06 and measure speed
        @Override
        public void run() {
            timerHandler.postDelayed(this, 100);
            setupBt();
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

	//setup GUI and initialize values
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

		//Put all cards into cards array and prepare to execute them
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

		//Add card to screen
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

    //Based on speed, determine rgb
    void setColor() {
        int timeSum = 0;
        time = (int)(System.currentTimeMillis() - startTime) / 1000;
        for(int i = 0; i < cards.length; i++) {
            timeSum += cards[i].getTimeInt();
			//if haven't finished workout yet, set finished cards red and set Pixels
            if (time < timeSum) {
                scroll.getChildAt(i).setBackgroundColor(Color.RED);
                setColor(cards[i].getSpeedInt(), (int)Math.round(speed));
                return;
            }
        }
        //finished workout, rainbow mode
        bt.sendData("255/255/255");
        color[0] = 255;
        color[1] = 255;
        color[2] = 255;
        running = false;
        speedView.setText("Done");
    }

    //Based on speed, determine rgb
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

    //Destroy bluetooth connection when leaving screen
    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
        sendToCloud();
    }

    //Create connection to Arduino
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

    //Calculate speed from acceleration using v = v0 + at and send color to Pixels
    public void getSpeed() {
        speed = v0 + acc*((double)(System.currentTimeMillis() - lastTime) / 100.0);
        lastTime = System.currentTimeMillis();
        ArrayList<Double> temp = new ArrayList<Double>();
        temp.add(speed);
        double nowTime = (double)((System.currentTimeMillis() - startTime) / 1000);
        temp.add(nowTime);
        speeds.add(temp);

		//set color and send to HC-06
        setColor();
        if (connected && running) {
            bt.sendData(color[0] + "/" + color[1] + "/" + color[2]);
        }
    }

     //Update acceleration values from mobile accelerometer     
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
            user.put("id", startTime);

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
