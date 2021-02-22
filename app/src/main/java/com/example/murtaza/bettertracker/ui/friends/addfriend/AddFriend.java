package com.example.murtaza.bettertracker.ui.friends.addfriend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.murtaza.bettertracker.MvpApp;
import com.example.murtaza.bettertracker.R;
import com.example.murtaza.bettertracker.data.DataManager;
import com.example.murtaza.bettertracker.network.MySingleton;
import com.example.murtaza.bettertracker.ui.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by murtaza on 2/14/18.
 */

public class AddFriend extends Fragment {

//    EditText textEmailorId;
   @BindView(R.id.TxtAddFriend) EditText editText;
   final String add_friend_url;

    public AddFriend() {
        super();
        add_friend_url = "https://geotracker-app.herokuapp.com/api/addfriend/";

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
            View v =  inflater.inflate(R.layout.fragment_find_friend, container, false);
            ButterKnife.bind(this, v);

            MainActivity.hideSpinner();
            return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @OnClick(R.id.BtnAddFriend) void onSendRequestButton() {

        DataManager dataManager = ((MvpApp) getActivity().getApplication()).getDataManager();

        // JSON OBJECT REQUEST
        JSONObject jsonBody = null;

        try {
            jsonBody = new JSONObject("{\"userId\":"+ dataManager.getEmailId() +",\"friendId\":"+ editText.getText().toString() +",\"status\":"+ "pending" + " }");
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        Toast.makeText(getActivity().getApplicationContext(), "ID: " + add_friend_url, Toast.LENGTH_SHORT).show();

        JsonObjectRequest jobReq = new JsonObjectRequest(Request.Method.POST, add_friend_url, jsonBody,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        Log.e("MyaTag", String.valueOf(jsonObject));
//                        Log.i("Tag", String.valueOf(jsonObject));
//                        Toast.makeText(getActivity().getApplicationContext(), "ID: " + jsonObject, Toast.LENGTH_SHORT).show();
                        try {

                            if (jsonObject.getString("status").equals("not_found")) {

                                 Toast.makeText(getActivity(), "User doesn't exist", Toast.LENGTH_LONG).show();
//                                Toast.makeText(getActivity(), "Friend Added", Toast.LENGTH_SHORT).show();
//                                Intent intent = MainActivity.getStartIntent(getActivity());
//                                startActivity(intent);
                            } else if (jsonObject.getString("status").equals("failure")) {
                                Toast.makeText(getActivity(), "Failure! User already in your circle", Toast.LENGTH_SHORT).show();
                            } else if (jsonObject.getString("status").equals("success")){
                                Toast.makeText(getActivity(), "Success! Friend Added to Your Circle", Toast.LENGTH_SHORT).show();
//                                Intent intent = MainActivity.getStartIntent(getActivity());
//                                startActivity(intent);
                            } else {
                                Toast.makeText(getActivity(), "Failure" + jsonObject.toString(), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();

                Toast.makeText(getActivity(), "Proper Failure! Not sent :3", Toast.LENGTH_SHORT).show();

            }


        });

        MySingleton.getmInstance(getActivity().getApplicationContext()).addToRequestque(jobReq);


    }

    @Override
    public void onStop () {
        super.onStop();
        if (MySingleton.getmInstance(getActivity()).getRequestQueue() != null) {
            MySingleton.getmInstance(getActivity()).StopRequestQueue();
        }
    }
}
