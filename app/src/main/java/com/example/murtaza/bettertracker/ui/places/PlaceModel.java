package com.example.murtaza.bettertracker.ui.places;

/**
 * Created by murtaza on 2/18/18.
 */

public class PlaceModel {

    private String placeName;
    private int count;

    PlaceModel(String placeName, int count) {
        this.placeName = placeName;
        this.count = count;
    }

    public String getPlaceName(){
        return this.placeName;
    }

    public int getCount(){
        return this.count;
    }
}
