package com.example.murtaza.bettertracker.ui.notifications;

import com.example.murtaza.bettertracker.MvpApp;
import com.example.murtaza.bettertracker.data.DataManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by murtaza on 2/15/18.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "REG_TOKEN";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

//        Toast.makeText(this, "Token: " + refreshedToken, Toast.LENGTH_SHORT).show();
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID   token to your app server.
        sendTokenRequest(refreshedToken);
    }

    private void sendTokenRequest(String Token) {
        DataManager dataManager = ((MvpApp) getApplication()).getDataManager();

        dataManager.saveFCMToken(Token);

    }
}

