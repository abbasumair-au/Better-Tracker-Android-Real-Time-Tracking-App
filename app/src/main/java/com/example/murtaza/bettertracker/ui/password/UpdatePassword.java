package com.example.murtaza.bettertracker.ui.password;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.murtaza.bettertracker.R;
import com.example.murtaza.bettertracker.ui.login.forgotpassword.ForgotActivity;

import butterknife.OnClick;

public class UpdatePassword extends AppCompatActivity {

    String update_pass_url = "https://geotracker-app.herokuapp.com/api/updatePass/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, ForgotActivity.class);
        return intent;
    }

    @OnClick(R.id.updatePassBtn) void updatePass() {

    }
}
