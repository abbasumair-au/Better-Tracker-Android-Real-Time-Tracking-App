package com.example.murtaza.bettertracker;


import android.app.Application;

import com.example.murtaza.bettertracker.data.DataManager;
import com.example.murtaza.bettertracker.data.SharedPrefsHelper;


/**
 * Created by murtaza on 05-02-2018.
 */

public class MvpApp extends Application {

    DataManager dataManager;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPrefsHelper sharedPrefsHelper = new SharedPrefsHelper(getApplicationContext());
        dataManager = new DataManager(sharedPrefsHelper);

    }

    public DataManager getDataManager() {
        return dataManager;
    }

}
