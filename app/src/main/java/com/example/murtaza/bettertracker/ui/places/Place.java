package com.example.murtaza.bettertracker.ui.places;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.murtaza.bettertracker.MvpApp;
import com.example.murtaza.bettertracker.R;
import com.example.murtaza.bettertracker.data.DataManager;
import com.example.murtaza.bettertracker.network.CheckNetwork;
import com.example.murtaza.bettertracker.network.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by murtaza on 2/12/18.
 */

public class Place extends Fragment {

    FragmentActivity c = null;
    PlaceAdapter adapter;

    final String get_place_url = "http://ec2-54-213-89-147.us-west-2.compute.amazonaws.com/analysis";
    final List<PlaceModel> placeModelArrayList = new ArrayList<PlaceModel>();
    RecyclerView placeList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

       View v = inflater.inflate(R.layout.fragment_place, container, false);

        placeList =  (RecyclerView) v.findViewById(R.id.placeRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(c);

        placeList.setLayoutManager(layoutManager);

        if(CheckNetwork.isInternetAvailable(getActivity())) //returns true if internet available
        {
            getActivity().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            getPlace();
        }
        else
        {
            Snackbar.make(getActivity().findViewById(android.R.id.content),"No Internet Connection", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
//            Toast.makeText(getActivity(),"No Internet Connection",Toast.LENGTH_LONG).show();
        }
        return v;
    }

    private PlaceAdapter getPlace() {

        DataManager dataManager = ((MvpApp) getActivity().getApplication()).getDataManager();
        String userEmailId = dataManager.getEmailId();
//        Toast.makeText(getActivity().getApplicationContext(), "ID: " + get_place_url + userEmailId, Toast.LENGTH_SHORT).show();

        StringRequest registerRequest = new StringRequest(Request.Method.GET, get_place_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("VOLLEY", response);
                        if(!response.isEmpty() || response != null) {
                            try {
                                JSONObject jsonObj = new JSONObject(response);

                                for (int i = 0; i < jsonObj.names().length(); i++) {
//                                Log.d("MyObj", "key = " + jsonObj.names().getString(i) + " value = " + jsonObj.get(jsonObj.names().getString(i)));
                                    PlaceModel model = new PlaceModel(jsonObj.names().getString(i), (Integer) jsonObj.get(jsonObj.names().getString(i)));
                                    placeModelArrayList.add(model);
                                }

                                adapter = new PlaceAdapter(placeModelArrayList, c);
                                placeList.setAdapter(adapter);
                                getActivity().findViewById(R.id.progressBar).setVisibility(View.GONE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        getActivity().findViewById(R.id.progressBar).setVisibility(View.GONE);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });


        MySingleton.getmInstance(getActivity().getApplicationContext()).addToRequestque(registerRequest);

        return adapter;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Place");
        c = getActivity();
    }

    @Override
    public void onStop() {
        super.onStop();
//        MySingleton.getmInstance(getActivity().getApplicationContext()).StopRequestQueue();
    }
}
