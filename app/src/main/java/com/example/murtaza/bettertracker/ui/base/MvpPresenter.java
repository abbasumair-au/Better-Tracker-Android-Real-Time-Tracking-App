package com.example.murtaza.bettertracker.ui.base;

/**
 * Created by murtaza on 05-02-2018.
 */

public interface MvpPresenter<V extends MvpView> {

    void onAttach(V mvpView);

}
