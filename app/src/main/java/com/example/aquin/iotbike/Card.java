package com.example.aquin.iotbike;

import android.util.Log;

import java.io.Serializable;

public class Card implements Serializable {
    private String time_;
    private String speed_;

    public Card(String color, String time, String speed) {
        time_ = time;
        speed_ = speed;
    }

    public Card(CardPic in) {
        time_ = in.getTime();
        speed_ = in.getSpeed();
    }

    public String getTime() {
        return time_;
    }

    public int getTimeInt() {
        return Integer.parseInt(time_);
    }

    public void setTime(String time_) {
        this.time_ = time_;
    }

    public String getSpeed() {
        return speed_;
    }

    public int getSpeedInt() {
        return Integer.parseInt(speed_);
    }

    public void setSpeed(String speed_) {
        this.speed_ = speed_;
    }

    public String getOutput() {
        return time_ + " " + speed_;
    }
}
