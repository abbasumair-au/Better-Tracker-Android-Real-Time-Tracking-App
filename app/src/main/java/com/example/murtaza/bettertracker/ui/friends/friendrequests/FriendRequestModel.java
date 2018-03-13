package com.example.murtaza.bettertracker.ui.friends.friendrequests;

/**
 * Created by hassans on 3/12/18.
 */

public class FriendRequestModel {

    private String id;
    private String friendName;
    private String friendPicture;

//    String id;
    FriendRequestModel(String name, String id) {
        this.friendName = name;
        this.id=id;
    }

    FriendRequestModel(String name, String id, String picture) {
        this.friendName = name;
        this.id=id;
        this.friendPicture = picture;
    }

    public String getName() {
        return this.friendName;
    }

    public String getId() {
        return this.id;
    }

    public String getPicture() {
        return this.friendPicture;
    }

}
