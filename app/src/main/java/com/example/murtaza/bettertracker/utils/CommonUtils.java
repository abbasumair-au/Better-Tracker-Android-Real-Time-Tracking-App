/*
 *    Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.example.murtaza.bettertracker.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by murtaza on 05-02-2018.
 */

public class CommonUtils {
    //    alert dialog
    public static AlertDialog.Builder builder;

    //    progress dialog
    public static ProgressDialog pd;

    public static boolean isEmailValid(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isPasswordValid(String password) {
        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{4,8}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }


    public static boolean isPhoneNumberValid(String phone) {

        Pattern pattern;
        Matcher matcher;

        final String PHONE_PATTERN = "^((\\+92)|(0092))-{0,1}\\d{3}-{0,1}\\d{7}$|^\\d{11}$|^\\d{4}-\\d{7}$";
        pattern = Pattern.compile(PHONE_PATTERN);
        matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    public static boolean isUserNameValid(String name) {
        Pattern pattern;
        Matcher matcher;

        final String NAME_PATTERN = "^.{4,}$";
        pattern = Pattern.compile(NAME_PATTERN);
        matcher = pattern.matcher(name);
        return matcher.matches();
    }

    public static boolean isFirstNameValid(String name) {
        Pattern pattern;
        Matcher matcher;

        final String NAME_PATTERN = "^.{4,}$";
        pattern = Pattern.compile(NAME_PATTERN);
        matcher = pattern.matcher(name);
        return matcher.matches();
    }

    public static boolean isLastNameValid(String name) {
        Pattern pattern;
        Matcher matcher;

        final String NAME_PATTERN = "^.{4,}$";
        pattern = Pattern.compile(NAME_PATTERN);
        matcher = pattern.matcher(name);
        return matcher.matches();
    }

    public static boolean isEmpty(String field) {

        return field == null || field.isEmpty();
    }

    public static boolean isEmptyForError(EditText field) {
        return field.getText().toString().trim() == null || field.getText().toString().trim().isEmpty();
    }

    public static String stripSpaces(String data) {
        return data.replaceAll("\\s","");
    }

    public static void displayAlert(Context context, String message) {

        builder = new AlertDialog.Builder(context);

        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public static void displayAlert(Context context, String title, String message) {

        builder = new AlertDialog.Builder(context);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void setupProgressBar(Context context, String message) {
        //        setting up progress dialog
        pd = new ProgressDialog(context);
//       setting progress dialog
        pd.setCancelable(false);
        pd.setMessage(message);
    }

    //    showing progress bar
    public static void showLoadingBar() {
        pd.show();
    }

    //    dismissing progress bar
    public static void hideLoadingBar() {
        pd.dismiss();
    }

    public static void hideKeyboard(@NonNull Activity activity, View view){
        View focusedView = activity.getCurrentFocus();
            if(focusedView!=null){
            InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

}
