package com.example.aquin.iotbike;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.util.Strings;


public class CardPic extends LinearLayout {
    private EditText speed_;
    private EditText time_;

    public CardPic(Context context) {
        super(context);
        setBackgroundColor(Color.GRAY);

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

    String getTime() {
        return time_.getText().toString();
    }

    String getSpeed() {
        return speed_.getText().toString();
    }
}

