package com.example.aquin.iotbike;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    BluetoothCom bt;
    boolean deviceConnected=false;
    Button connectBtn;
    Button speedBtn;
    Button mapBtn;
    TextView statusView;
    Activity context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt = new BluetoothCom();
        connectBtn = (Button) findViewById(R.id.connectBtn);
        speedBtn = (Button) findViewById(R.id.speedBtn);
        mapBtn = (Button) findViewById(R.id.mapBtn);
        statusView = (TextView) findViewById(R.id.statusView);

        //Button listeners
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("ASDF", "Button Pressed");
                if(bt.BTinit(context))
                {
                    Log.v("ASDF", "Bluetooth initialized");
                    if(bt.BTconnect())
                    {
                        Log.v("ASDF", "Bluetooth Connected");
                        deviceConnected=true;
                        statusView.setText("Bluetooth connected");
                    }
                }
            }
        });

        speedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, Speed.class);
                startActivity(i);
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i = new Intent(context, Map.class);
                  startActivity(i);
                */
            }
        });
    }

}