package com.example.aquin.iotbike;

import java.io.Serializable;
import java.util.ArrayList;

public class CardWrapper implements Serializable {
    private Card[] array;

    public CardWrapper(Card[] in){
        array = in;
    }

    public Card[] getArray() {
        return array;
    }

}
