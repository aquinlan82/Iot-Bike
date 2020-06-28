package com.example.aquin.iotbike;

import java.io.Serializable;
import java.util.ArrayList;

/**
Wrapper class for Card 
Needed in order to move Card Objects to different screens
**/
public class CardWrapper implements Serializable {
    private Card[] array;

    public CardWrapper(Card[] in){
        array = in;
    }

    public Card[] getArray() {
        return array;
    }

}
