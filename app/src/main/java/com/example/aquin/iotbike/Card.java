package com.example.aquin.iotbike;

import java.io.Serializable;

public class Card implements Serializable {
    private String color_;
    private String time_;
    private String speed_;

    public Card(String color, String time, String speed) {
        color_ = color;
        time_ = time;
        speed_ = speed;
    }

    public Card(CardPic in) {
        color_ = in.getColor();
        time_ = in.getTime();
        speed_ = in.getSpeed();
    }

    public String getColor() {
        return color_;
    }

    public void setColor(String color_) {
        this.color_ = color_;
    }

    public String getTime() {
        return time_;
    }

    public void setTime(String time_) {
        this.time_ = time_;
    }

    public String getSpeed() {
        return speed_;
    }

    public void setSpeed(String speed_) {
        this.speed_ = speed_;
    }

    public String getOutput() {
        return color_ + " " + time_ + " " + speed_;
    }
}
