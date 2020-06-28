package com.example.aquin.iotbike;

import android.util.Log;
import java.io.Serializable;

/**
Card Class
Stores data about time and speed desired in goal mode
**/
public class Card implements Serializable {
    private String time_;   //how long to go a given speed
    private String speed_;  //speed to go at

	//Constructor for known time and speed
    public Card(String time, String speed) {
        time_ = time;
        speed_ = speed;
    }

	//Construct Card from a linearLayout containing time and speed
    public Card(CardPic in) {
        time_ = in.getTime();
        speed_ = in.getSpeed();
    }

	//Getter for time
    public String getTime() {
        return time_;
    }

	//Get time as integer
    public int getTimeInt() {
        return Integer.parseInt(time_);
    }

	//Setter for time
    public void setTime(String time_) {
        this.time_ = time_;
    }

	//Getter for speed
    public String getSpeed() {
        return speed_;
    }

	//Get speed as integer
    public int getSpeedInt() {
        return Integer.parseInt(speed_);
    }

	//Setter for speed
    public void setSpeed(String speed_) {
        this.speed_ = speed_;
    }

	//Return both time and speed
    public String getOutput() {
        return time_ + " " + speed_;
    }
}
