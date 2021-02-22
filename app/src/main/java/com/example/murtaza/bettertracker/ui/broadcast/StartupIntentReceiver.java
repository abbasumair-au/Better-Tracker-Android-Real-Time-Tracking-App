package com.example.murtaza.bettertracker.ui.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.murtaza.bettertracker.MvpApp;
import com.example.murtaza.bettertracker.data.DataManager;
import com.example.murtaza.bettertracker.ui.location.LocationService;

/**
 * Created by root on 2/28/18.
 */


public class StartupIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent arg1)
    {
        DataManager dataManager = ((MvpApp) context.getApplicationContext()).getDataManager();
        if(dataManager.getLoggedInMode()) {
            Intent intent = new Intent(context, LocationService.class);

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                ContextCompat.startForegroundService(context.getApplicationContext(), intent);
            } else {
                context.getApplicationContext().startService(intent);
            }
            Log.i("Autostart", "started");
        }
    }
}