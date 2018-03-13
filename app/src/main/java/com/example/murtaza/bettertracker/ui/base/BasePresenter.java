package com.example.murtaza.bettertracker.ui.base;


import com.example.murtaza.bettertracker.data.DataManager;

/**
 * Created by murtaza on 05-02-2018.
 */

public class BasePresenter<V extends MvpView> implements MvpPresenter<V> {

    DataManager mDataManager;
    private V mMvpView;


    public BasePresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void onAttach(V mvpView) {
        mMvpView = mvpView;
    }

    public V getMvpView() {
        return mMvpView;
    }

    public DataManager getDataManager() {
        return mDataManager;
    }
}
