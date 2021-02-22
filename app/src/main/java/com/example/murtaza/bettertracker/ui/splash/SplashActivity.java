package com.example.murtaza.bettertracker.ui.splash;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.example.murtaza.bettertracker.MvpApp;
import com.example.murtaza.bettertracker.ui.base.BaseActivity;
import com.example.murtaza.bettertracker.ui.home.HomeActivity;
import com.example.murtaza.bettertracker.R;
import com.example.murtaza.bettertracker.data.DataManager;
import com.example.murtaza.bettertracker.ui.main.MainActivity;

public class SplashActivity extends BaseActivity implements ISplashView {

    private SplashPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

//         setting presenter
        DataManager mDataManager = ((MvpApp) getApplication()).getDataManager();

        presenter = new SplashPresenter(mDataManager);

//        attaching current view to the presenter
        presenter.onAttach(this);

//        handling view with presenter
        presenter.decideNextActivity();



    }

//    Splash Screen Activity
    public static Intent startSplashActivity(Context context) {

        Intent intent = new Intent(context, SplashActivity.class);
        return intent;
    }

    @Override
    public void openMainActivity() {

        Intent intent = MainActivity.getStartIntent(this);
        startActivity(intent);
        finish();
    }

    @Override
    public void openHomeActivity() {

        Intent intent = HomeActivity.getStartIntent(this);
        startActivity(intent);
        finish();
    }



}
