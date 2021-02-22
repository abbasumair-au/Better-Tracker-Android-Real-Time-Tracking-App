package com.example.murtaza.bettertracker.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.murtaza.bettertracker.R;
import com.example.murtaza.bettertracker.ui.settings.updateaccount.updateprofile;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by murtaza on 2/19/18.
 */

public class Setting extends Fragment {

    @BindView(R.id.update)
    Button update;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, v);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity().getApplicationContext(), updateprofile.class));
                getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
//                context.startActivity(new Intent(context, FriendList.class));
            }
        });
//        MainActivity.hideSpinner();
//        getActivity().findViewById(R.id.progressBar).setVisibility(View.GONE);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Settings");
    }
}
