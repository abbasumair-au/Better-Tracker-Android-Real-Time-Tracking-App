package com.example.murtaza.bettertracker.ui.friends;

/**
 * Created by murtaza on 2/17/18.
 */

public class FriendModel {

    private String friendName;
    private String id;
//    String id;

    FriendModel(String name, String id) {
        this.friendName = name;
        this.id=id;
    }

    public String getName() {
        return this.friendName;
    }

    public String getId() {
        return this.id;
    }

}
