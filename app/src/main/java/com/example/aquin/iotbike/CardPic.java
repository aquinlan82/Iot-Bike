package com.example.aquin.iotbike;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


public class CardPic extends LinearLayout {
    private EditText color_;
    private EditText speed_;
    private EditText time_;

    public CardPic(Context context) {
        super(context);
        setBackgroundColor(Color.GRAY);

        TextView temp1 = new TextView(context);
        temp1.setText("Color:");
        addView(temp1);
        color_ = new EditText(context);
        addView(color_);

        TextView temp2 = new TextView(context);
        temp2.setText("Speed:");
        addView(temp2);
        speed_ = new EditText(context);
        speed_.setInputType(InputType.TYPE_CLASS_NUMBER);
        addView(speed_);

        TextView temp3 = new TextView(context);
        temp3.setText("Time:");
        addView(temp3);
        time_ = new EditText(context);
        time_.setInputType(InputType.TYPE_CLASS_NUMBER);
        addView(time_);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
    }

    String getColor() {
        return color_.getText().toString();
    }

    String getTime() {
        return time_.getText().toString();
    }

    String getSpeed() {
        return speed_.getText().toString();
    }
}

