package com.example.aquin.iotbike;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
Graph Mode
Displays time and speed data from firebase database
**/
public class Graph extends AppCompatActivity {
    private DatePicker picker;
    private Button show;
    private GraphView graph;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
	//each datapoint contains an id based on which consecutive mode it was collected in,
	//the speed the user was going and the time they were going it
    private Map<Long, ArrayList<DataPoint>> serieses = new HashMap<Long, ArrayList<DataPoint>>();

	//Sets up GUI and button listener
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        picker = (DatePicker)findViewById(R.id.simpleDatePicker);
        show = (Button) findViewById(R.id.show);
        graph = (GraphView) findViewById(R.id.graph);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollableY(true);

		//Get graph for given date
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date =  (picker.getMonth() + 1) + "-" + picker.getDayOfMonth() + "-" + picker.getYear();
                db.collection(date)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
									buildMap();
									graphData();
                                } else {
                                    Log.w("Graph", "Error getting documents.", task.getException());
                                }
                            }
                        });
            }
        });

    }

	//Each datapoint is stored as a document
	//Here they are sorted by id into datapoints for plotting
	private void buildMap(Task<QuerySnapshot> task) {
		for (QueryDocumentSnapshot document : task.getResult()) {
			serieses.putIfAbsent((long)document.getData().get("id"), new ArrayList<DataPoint>());
			double time = (double) document.getData().get("time");
			double speed = (double) document.getData().get("speed");
			serieses.get(document.getData().get("id")).add(new DataPoint(time, speed));
		}
	}

	//graph the time and speed for each datapoint, with different colors for 
	//different ids
	private void graphData() {
		//colors to use for datapoints
		int[] colors = {Color.GREEN, Color.BLUE, Color.RED, Color.YELLOW, Color.MAGENTA };
		int index = 0;
		for (ArrayList<DataPoint> array : serieses.values()) {
			for (DataPoint point : array) {
				LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
						point
				});
				series.setColor(colors[index]);
				series.setDrawDataPoints(true);
				series.setDataPointsRadius(10);
				series.setThickness(8);
				graph.addSeries(series);
			}
			index = (index + 1) % colors.length;
		}
	}

}
