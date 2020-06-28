package com.example.aquin.iotbike;

import android.app.Activity;
import android.content.Intent;
import android.icu.util.Measure;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
IoT Bike
Created by Allison Quinlan for CS 498 IT
December 2019
**/



/**
Main Activity Class
Displays buttons for user to select which mode
**/
public class MainActivity extends AppCompatActivity {
    private Button goal;
    private Button measure;
    private Button rainbow;
    private Button graph;
    private Activity context = this;

	//setup GUI and go to indicated mode on button press
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        goal = (Button) findViewById(R.id.goal);
        measure = (Button) findViewById(R.id.measure);
        rainbow = (Button) findViewById(R.id.rainbow);
        graph = (Button) findViewById(R.id.graph);

        goal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Goal.class));
            }
        });

        measure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Measured.class));
            }
        });

        rainbow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Rainbow.class));
            }
        });

        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Graph.class));
            }
        });



    }

}
