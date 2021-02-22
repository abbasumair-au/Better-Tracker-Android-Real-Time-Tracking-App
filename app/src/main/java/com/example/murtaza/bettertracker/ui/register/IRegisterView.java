package com.example.murtaza.bettertracker.ui.register;

import android.view.View;

/**
 * Created by murtaza on 2/6/18.
 */

public interface IRegisterView {

    void setHomeLogo();

    void showLoadingBar();

    void hideLoadingBar();

    void openMainActivity();

    void openLoginActivity();

    void onRegisterButtonClick(View view);

    boolean checkEmpty(String name, String userPassword, String firstName, String lastName, String email, String phone, String gender, String date);

    void displayAlert(String message);

    void displayAlert(String title, String message);


}
