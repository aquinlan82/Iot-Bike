package com.example.aquin.iotbike;

import android.app.Activity;
import android.content.Intent;
import android.icu.util.Measure;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    private Button goal;
    private Button measure;
    private Button rainbow;
    private Button graph;
    private Activity context = this;

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