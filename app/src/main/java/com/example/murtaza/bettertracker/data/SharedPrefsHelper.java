package com.example.murtaza.bettertracker.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.murtaza.bettertracker.ui.member.memberclass;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by murtaza 05-02-2018.
 */

public class SharedPrefsHelper {

    public static final String MY_PREFS = "USER";

    public static final String EMAIL = "EMAIL";

    public static final String ID = "ID";

    public static final String USERNAME = "USERNAME";

    public static final String FIRSTNAME = "FIRSTNAME";

    public static final String LASTNAME = "LASTNAME";

    public static final String ACCOUNTTYPE = "ACCOUNTTYPE";

    public static final String FCMTOKEN = "FCMTOKEN";

    public static final String PROFILEPIC = "PROFILEPIC";

    public static final String USERPHONE = "USERPHONE";

    public static final String COORDINATES = "COORDINATES";


    SharedPreferences mSharedPreferences;

    public SharedPrefsHelper(Context context) {
        mSharedPreferences = context.getSharedPreferences(MY_PREFS, MODE_PRIVATE);
    }

    public void clear() {
        mSharedPreferences.edit().clear().apply();
    }

    public void putEmail(String email) {
        mSharedPreferences.edit().putString(EMAIL, email).apply();
    }

    public void putFCMToken(String fcmtoken) {
        mSharedPreferences.edit().putString(FCMTOKEN, fcmtoken).apply();
    }

    public String getFCMToken() {
        return mSharedPreferences.getString(FCMTOKEN, null);
    }

    public void putId(String id) {
        mSharedPreferences.edit().putString(ID, id).apply();
    }

    public String getId() {
        return mSharedPreferences.getString(ID, null);
    }

    public void putName(String name) {
        mSharedPreferences.edit().putString(USERNAME, name).apply();
    }

    public String getName() {
        return mSharedPreferences.getString(USERNAME, null);
    }

    public String getEmail() {
        return mSharedPreferences.getString(EMAIL, null);
    }

    public boolean getLoggedInMode() {
        return mSharedPreferences.getBoolean("IS_LOGGED_IN", false);
    }

    public void setLoggedInMode(boolean loggedIn) {
        mSharedPreferences.edit().putBoolean("IS_LOGGED_IN", loggedIn).apply();
    }

    public boolean isFirstTime() {
        return mSharedPreferences.getBoolean("IS_FIRST_TIME", true);
    }

    public void setFirstTime(boolean firstTime) {
        mSharedPreferences.edit().putBoolean("IS_FIRST_TIME", firstTime).apply();
    }

    public void putMemberData(memberclass member) {
        mSharedPreferences.edit().putString(ID, member.getId()).apply();
        mSharedPreferences.edit().putString(FIRSTNAME, member.getFirstName()).apply();
        mSharedPreferences.edit().putString(LASTNAME, member.getLastName()).apply();
        mSharedPreferences.edit().putString(USERNAME, member.getUserName()).apply();
        mSharedPreferences.edit().putString(ACCOUNTTYPE, member.getAccountType()).apply();
        mSharedPreferences.edit().putString(EMAIL, member.getEmail()).apply();
        mSharedPreferences.edit().putString(USERPHONE, member.getEmail()).apply();
    }

    public String getType() {
        return mSharedPreferences.getString(ACCOUNTTYPE, null);
    }

    public void putPic(String pic) {
        mSharedPreferences.edit().putString(PROFILEPIC, pic).apply();
    }

    public String getProfilepic() {
        return mSharedPreferences.getString(PROFILEPIC, null);
    }

    public void putFirstName(String firstname) {
        mSharedPreferences.edit().putString(FIRSTNAME, firstname).apply();
    }

    public String getFirstName() {
        return mSharedPreferences.getString(FIRSTNAME, null);
    }

    public void putLastName(String lastname) {
        mSharedPreferences.edit().putString(LASTNAME, lastname).apply();
    }

    public String getLastName() {
        return mSharedPreferences.getString(LASTNAME, null);
    }

    public void savePhone(String phone) {
        mSharedPreferences.edit().putString(USERPHONE, null);
    }
//
    public String getPhone() {

        return mSharedPreferences.getString(USERPHONE, null);
    }

    public void saveCoordinates(String LatLng) {
        mSharedPreferences.edit().putString(COORDINATES, LatLng).apply();
    }

    public String getCoordinates() {
        return mSharedPreferences.getString(COORDINATES, null);
    }

}
