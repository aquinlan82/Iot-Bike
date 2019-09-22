package com.example.aquin.iotbike;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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