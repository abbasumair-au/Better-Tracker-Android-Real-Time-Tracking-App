package com.example.murtaza.bettertracker.ui.circles;

/**
 * Created by murtaza on 2/13/18.
 */

public class CircleModel {

    private String circleName;
    private Boolean circleStatus;
    String id;

    CircleModel(String name, Boolean status,String id) {
        this.circleName = name;
        this.circleStatus = status;
        this.id=id;
    }

    public String getName() {
        return this.circleName;
    }

    public String getId() {
        return this.id;
    }

    public Boolean getStatus() {
        return this.circleStatus;
    }

    @Override
    public String toString() {
        return "Name: " + this.circleName + "Status: " + this.circleStatus;
    }
}
