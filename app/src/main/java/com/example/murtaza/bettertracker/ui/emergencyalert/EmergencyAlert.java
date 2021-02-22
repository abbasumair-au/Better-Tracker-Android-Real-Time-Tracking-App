package com.example.murtaza.bettertracker.ui.emergencyalert;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.murtaza.bettertracker.MvpApp;
import com.example.murtaza.bettertracker.R;
import com.example.murtaza.bettertracker.data.DataManager;
import com.example.murtaza.bettertracker.network.CheckNetwork;
import com.example.murtaza.bettertracker.network.MySingleton;
import com.example.murtaza.bettertracker.ui.main.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by murtaza on 2/20/18.
 */

public class EmergencyAlert extends Fragment {
    private String get_phone_numbers_url= "https://geotracker-app.herokuapp.com/api/getphonenumbers/";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View v =  inflater.inflate(R.layout.fragment_emergency_alert, container, false);

        ButterKnife.bind(this, v);
//        getPhoneNumbers();
//        MainActivity.hideSpinner();
//        getActivity().findViewById(R.id.progressBar).setVisibility(View.GONE);

        return v;
    }

    @OnClick(R.id.emergencyBtn) void onClickEmergency(){

        if(CheckNetwork.isInternetAvailable(getActivity())) //returns true if internet available
        {
            getPhoneNumbers();
        }
        else
        {
            Toast.makeText(getActivity(),"No Internet Connection",Toast.LENGTH_LONG).show();
        }

    }

    private void getPhoneNumbers() {
        DataManager dataManager = ((MvpApp) getActivity().getApplication()).getDataManager();
        String userEmailId = dataManager.getEmailId();
//        Toast.makeText(getApplicationContext(), "ID: " + get_friends_url + userEmailId, Toast.LENGTH_SHORT).show();

        JsonArrayRequest jobReq = new JsonArrayRequest(Request.Method.GET, get_phone_numbers_url + userEmailId ,null ,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray jsonArray) {

                        Log.d("newTag", String.valueOf(jsonArray));
                        String justString = "";

                        if(jsonArray.length() > 0 && jsonArray != null ) {

                            try {
                                Log.d("Array", jsonArray.toString());

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject o1 = jsonArray.getJSONObject(i);
//                                Log.d("OBS", o1.toString());
//                                Log.d("OBS DATA", o1.getString("userPhoneNumber"));

                               /*Failed to used SMS MANAGER API */

                                    DataManager dataManager = ((MvpApp) getActivity().getApplication()).getDataManager();

                                    String[] LatLng = dataManager.getLoc().split(",");

                                    String message = "http://maps.google.com/?q=" + LatLng[0] + "," + LatLng[1];
                                    Log.d("Phonenumber", o1.getString("userPhoneNumber"));

                                    sendSms(o1.getString("userPhoneNumber"), "Your friend " + dataManager.getName() + " needs your help. Here is its coordinates now: \n" + message);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            Toast.makeText(getActivity().getApplicationContext(), "User doesn't have a number", Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("newTag", String.valueOf(error));
            }


        });

        MySingleton.getmInstance(getActivity().getApplicationContext()).addToRequestque(jobReq);

    }

    private void sendSms(String number, String message) {
        Log.d("number & message", number + "and" + message);

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, message, null, null);
            Toast.makeText(getActivity().getApplicationContext(), "Alert Successfully Sent!",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {

            Toast.makeText(getActivity().getApplicationContext(),
                    "Alert failed, please try again later!",
                    Toast.LENGTH_LONG).show();
            Log.e("YOUR_APP_LOG_TAG", "I got an error", e);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.hideSpinner();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Emergency Alert!");
    }
}
