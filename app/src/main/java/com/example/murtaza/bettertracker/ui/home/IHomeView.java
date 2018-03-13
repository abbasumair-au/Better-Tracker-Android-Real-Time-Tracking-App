package com.example.murtaza.bettertracker.ui.home;

import com.example.murtaza.bettertracker.ui.member.memberclass;

import java.util.HashMap;

/**
 * Created by murtaza on 2/6/18.
 */

public interface IHomeView {

    void onLoginButtonClick();

    void onRegisterButtonClick();

    void onFacebookLoginClick();

    void setHomeLogo();

    void openMainActivity();

    void showLoadingBar();

    void hideLoadingBar();

    void openUserNameActivity(HashMap<String, String> userData);
}
