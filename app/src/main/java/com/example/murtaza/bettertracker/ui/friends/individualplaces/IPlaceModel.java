package com.example.murtaza.bettertracker.ui.friends.individualplaces;

/**
 * Created by murtaza on 2/18/18.
 */

public class IPlaceModel {

    private String placeName;
    private int count;

    IPlaceModel(String placeName, int count) {
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
