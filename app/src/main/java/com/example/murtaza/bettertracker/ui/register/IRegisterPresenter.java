package com.example.murtaza.bettertracker.ui.register;

/**
 * Created by murtaza on 2/6/18.
 */

public interface IRegisterPresenter {

    void validateLoginWithEmailOrID(String name, String userPassword, String firstName, String lastName, String email, String accountType, String profileImgPath, String number, String gender, String date);
}
