package com.example.aquin.iotbike;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


public class Rainbow extends AppCompatActivity {
    private boolean connected;
    private BluetoothCom bt;
    private TextView connectView;
    private Activity context = this;
    private Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            timerHandler.postDelayed(this, 100);
            setupBt();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rainbow);
        connected = false;

        connectView = (TextView) findViewById(R.id.connectView);
        bt = new BluetoothCom();
        timerHandler.postDelayed(timerRunnable, 100);

    }

    /**
     * Destroy bluetooth connection when hitting back button/closing app
     */
    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }

    /**
     * Creates bluetooth connection to arduino
     **/
    public void setupBt() {
        if (!connected) {
            connectView.setText("Connecting...");
            if (bt.BTinit(context)) {
                if (bt.BTconnect()) {
                    connectView.setText("Bluetooth Connected!");
                    connected = true;
                    //white light means run rainbow code on arduino
                    bt.sendData("255/255/255");

                } else {
                    connectView.setText("Bluetooth NOT Connected");
                }
            } else {
                connectView.setText("Bluetooth NOT Connected");
            }
        }
    }
}


