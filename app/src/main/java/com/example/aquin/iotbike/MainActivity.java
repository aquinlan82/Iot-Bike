package com.example.aquin.iotbike;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    BluetoothCom bt;
    boolean deviceConnected=false;
    Button connectBtn;
    Button startBtn;
    Button addBtn;
    LinearLayout scroll;
    TextView statusView;
    Activity context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt = new BluetoothCom();
        connectBtn = (Button) findViewById(R.id.connectBtn);
        startBtn = (Button) findViewById(R.id.startBtn);
        addBtn = (Button) findViewById(R.id.addBtn);
        statusView = (TextView) findViewById(R.id.statusView);
        scroll = (LinearLayout) findViewById(R.id.scrollLayout);

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

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Card[] cards = new Card[scroll.getChildCount()];
                for (int i = 0; i < scroll.getChildCount(); i++) {
                    cards[i] = new Card((CardPic)scroll.getChildAt(i));
                }

                CardWrapper send = new CardWrapper(cards);
                Intent i = new Intent(context, Speed.class);
                i.putExtra("cards", send);
                startActivity(i);
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardPic temp = new CardPic(context);
                scroll.addView(temp);
            }
        });


    }

}