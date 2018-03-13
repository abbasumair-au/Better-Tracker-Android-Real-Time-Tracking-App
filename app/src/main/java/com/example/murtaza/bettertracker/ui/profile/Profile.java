package com.example.murtaza.bettertracker.ui.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.murtaza.bettertracker.MvpApp;
import com.example.murtaza.bettertracker.R;
import com.example.murtaza.bettertracker.data.DataManager;

/**
 * Created by murtaza on 2/19/18.
 */

public class Profile extends Fragment {

    TextView userName = null;

    TextView userFirstName  = null;

//    TextView userLastName  = null;

    TextView userEmail  = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_profile, container, false);

        userName = v.findViewById(R.id.userNameProfile);

        userFirstName = v.findViewById(R.id.userFirstNameProfile);

//        userLastName = v.findViewById(R.id.userLastNameProfile);

        userEmail = v.findViewById(R.id.userEmailProfile);

        DataManager dataManager = ((MvpApp) getActivity().getApplication()).getDataManager();
        userName.setText(dataManager.getName());
        userFirstName.setText(dataManager.getFirstName() + " " + dataManager.getLastName());
//        userLastName.setText(dataManager.getLastName());
        userEmail.setText(dataManager.getEmailId());
//        getActivity().findViewById(R.id.progressBar).setVisibility(View.GONE);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("My Account");

    }
}
