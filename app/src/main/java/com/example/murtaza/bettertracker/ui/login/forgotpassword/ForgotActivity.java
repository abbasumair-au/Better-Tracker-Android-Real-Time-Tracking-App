package com.example.murtaza.bettertracker.ui.login.forgotpassword;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.murtaza.bettertracker.R;

public class ForgotActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        Fragment fragment = null;
        fragment = new ForgotFragment();
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_forgot_password, fragment);
            ft.commit();
        }
    }

    //    returning main activity start intent
    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, ForgotActivity.class);
        return intent;
    }


}
