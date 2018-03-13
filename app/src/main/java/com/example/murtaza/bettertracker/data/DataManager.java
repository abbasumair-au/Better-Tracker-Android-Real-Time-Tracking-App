package com.example.murtaza.bettertracker.data;

import com.example.murtaza.bettertracker.ui.member.memberclass;

/**
 * Created by murtaza on 05-02-2018.
 */

public class DataManager {

    SharedPrefsHelper mSharedPrefsHelper;

    public DataManager(SharedPrefsHelper sharedPrefsHelper) {
        mSharedPrefsHelper = sharedPrefsHelper;
    }

    public void clear() {
        mSharedPrefsHelper.clear();
    }

    public void saveMemberData(memberclass member) {
        mSharedPrefsHelper.putMemberData(member);
    }

    public void saveId(String id) {
        mSharedPrefsHelper.putId(id);
    }

    public String getId() {
        return mSharedPrefsHelper.getId();
    }

    public void saveName(String name) {
        mSharedPrefsHelper.putName(name);
    }

    public String getName() {
        return mSharedPrefsHelper.getName();
    }

    public void saveEmailId(String email) {
        mSharedPrefsHelper.putEmail(email);
    }

    public String getEmailId() {
        return mSharedPrefsHelper.getEmail();
    }

    public void setFirstTime() {
        mSharedPrefsHelper.setFirstTime(false);
    }

    public Boolean getFirstTime() {
        return mSharedPrefsHelper.isFirstTime();
    }

    public void setLoggedIn() {
        mSharedPrefsHelper.setLoggedInMode(true);
    }

    public Boolean getLoggedInMode() {
        return mSharedPrefsHelper.getLoggedInMode();
    }

    public String getAccountType() {
        return mSharedPrefsHelper.getType();
    }

    public void saveFCMToken(String token) {
        mSharedPrefsHelper.putFCMToken(token);
    }

    public String getFCMToken() {
        return mSharedPrefsHelper.getFCMToken();
    }

    public void saveFirstName(String firstname) {
        mSharedPrefsHelper.putFirstName(firstname);
    }

    public String getFirstName() {
        return mSharedPrefsHelper.getFirstName();
    }

    public void saveLastName(String lastname) {
        mSharedPrefsHelper.putLastName(lastname);

    }

    public String getLastName() {
        return mSharedPrefsHelper.getLastName();
    }

    public void saveProfilePIc(String token) {
        mSharedPrefsHelper.putFCMToken(token);
    }

    public String getProfilePIc() {
        return mSharedPrefsHelper.getFCMToken();
    }

    public void saveLoc(String loc) {
        mSharedPrefsHelper.saveCoordinates(loc);
    }

    public String getLoc() {
        return mSharedPrefsHelper.getCoordinates();
    }

}
