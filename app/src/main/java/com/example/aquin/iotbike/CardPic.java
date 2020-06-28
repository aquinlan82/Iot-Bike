package com.example.aquin.iotbike;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.util.Strings;

/**
CardPic class
Contains the actual gui element seen when creating a card in goal mode
Also stores data about desired speed and time
**/
public class CardPic extends LinearLayout {
    private EditText speed_;
    private EditText time_;

	//Create Linear Layout with no data and add to screen
    public CardPic(Context context) {
        super(context);
		//set appearance of card
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier("card", "drawable",
                context.getPackageName());
        setBackground(resources.getDrawable(resourceId));

		//display place to insert speed
        TextView speedView = new TextView(context);
        speedView.setText("    Speed:");
        addView(speedView);
        speed_ = new EditText(context);
        speed_.setInputType(InputType.TYPE_CLASS_NUMBER);
        addView(speed_);

		//display place to insert time
        TextView timeView = new TextView(context);
        timeView.setText("Time:");
        addView(timeView);
        time_ = new EditText(context);
        time_.setInputType(InputType.TYPE_CLASS_NUMBER  | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        addView(time_);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
    }

	//Getter for time
    String getTime() {
        return time_.getText().toString();
    }

	//Getter for speed
    String getSpeed() {
        return speed_.getText().toString();
    }
}

