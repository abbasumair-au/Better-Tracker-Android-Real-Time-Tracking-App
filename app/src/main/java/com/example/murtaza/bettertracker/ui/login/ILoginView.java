package com.example.murtaza.bettertracker.ui.login;

import android.view.View;

/**
 * Created by murtaza on 2/6/18.
 */

public interface ILoginView {

    void setHomeLogo();

    void showLoadingBar();

    void hideLoadingBar();

    void openMainActivity();

    void onLoginButtonClick(View view);

    boolean checkEmpty(String userId, String userPassword);

    void displayAlert(String message);

    void displayAlert(String title, String message);


}
