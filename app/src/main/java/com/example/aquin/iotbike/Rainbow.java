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


public class Rainbow extends AppCompatActivity {
    BluetoothCom bt;
    TextView connectView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rainbow);

        connectView = (TextView) findViewById(R.id.connectView);
        bt = new BluetoothCom();
        if(bt.BTinit(this))
        {
            if(bt.BTconnect()) {
                connectView.setText("Bluetooth Connected!");
                //white light means run rainbow code on arduino
                bt.sendData("255/255/255");
            }
        }

    }

    public void onPause() {
        super.onPause();
        bt.stopService();
    }
}