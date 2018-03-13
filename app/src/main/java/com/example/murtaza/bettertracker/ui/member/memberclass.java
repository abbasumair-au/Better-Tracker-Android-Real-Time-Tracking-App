package com.example.murtaza.bettertracker.ui.member;

/**
 * Created by murtaza on 2/6/18.
 */

public class memberclass {

    private String _id;
    private String userName;
    private String userPassword;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userPhone;
    private String accountType;
    private String profilePic;

    public memberclass(String id, String userName, String userFirstName, String userLastName, String userEmail, String accountType, String userPhone) {
        this._id = id;
        this.userName = userName;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.accountType = accountType;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
    }

    public String getId() {
        return  this._id;
    }

    public String getUserName() {
        return  this.userName;
    }

    public String getFirstName() {
        return  this.userFirstName;
    }

    public String getLastName() {
        return  this.userLastName;
    }

    public String getAccountType() {
        return  this.accountType;
    }

    public String getEmail() {
        return  this.userEmail;
    }

    public String getPhone() { return this.userPhone; }


}
