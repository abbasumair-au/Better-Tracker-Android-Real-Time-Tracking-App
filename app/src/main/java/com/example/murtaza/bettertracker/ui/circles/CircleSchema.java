
package com.example.murtaza.bettertracker.ui.circles;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CircleSchema {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("circleStatus")
    @Expose
    private Boolean circleStatus;
    @SerializedName("circleName")
    @Expose
    private String circleName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getCircleStatus() {
        return circleStatus;
    }

    public void setCircleStatus(Boolean circleStatus) {
        this.circleStatus = circleStatus;
    }

    public String getCircleName() {
        return circleName;
    }

    public void setCircleName(String circleName) {
        this.circleName = circleName;
    }

}
