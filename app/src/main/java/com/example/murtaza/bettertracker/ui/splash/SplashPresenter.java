package com.example.murtaza.bettertracker.ui.splash;

import com.example.murtaza.bettertracker.data.DataManager;
import com.example.murtaza.bettertracker.ui.base.BasePresenter;
import com.example.murtaza.bettertracker.ui.base.MvpView;

/**
 * Created by murtaza on 2/5/18.
 */

public class SplashPresenter<V extends ISplashView & MvpView> extends BasePresenter<V> implements ISplashPresenter {

    public SplashPresenter(DataManager dataManager) {
        super(dataManager);
    }

    public void decideNextActivity(){

        if(getDataManager().getLoggedInMode()){
            getMvpView().openMainActivity();
        }else{
            getMvpView().openHomeActivity();
        }

    }
}
