package com.example.murtaza.bettertracker.ui.home;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.HashMap;

/**
 * Created by murtaza on 2/6/18.
 */

public interface IHomePresenter {

    void openLoginActivity();

    void openRegisterActivity();

    void loginThroughFacebook();

    void updateUI(GoogleSignInAccount account);

    void FacebookSignInNetwork(HashMap<String, String> userFbData);

}
