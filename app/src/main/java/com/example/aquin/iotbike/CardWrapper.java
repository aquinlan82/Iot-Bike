package com.example.aquin.iotbike;

import java.io.Serializable;
import java.util.ArrayList;

public class CardWrapper implements Serializable {
    private Card[] array;

    public CardWrapper(ArrayList<Card> a){
        array = a.toArray(new Card[a.size()]);;
    }

    public Card[] getArray() {
        return array;
    }

}
