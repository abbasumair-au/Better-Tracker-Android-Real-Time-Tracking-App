package com.example.murtaza.bettertracker.ui.circles;

import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.murtaza.bettertracker.MvpApp;
import com.example.murtaza.bettertracker.R;
import com.example.murtaza.bettertracker.data.DataManager;
import com.example.murtaza.bettertracker.network.CheckNetwork;
import com.example.murtaza.bettertracker.network.MySingleton;
import com.example.murtaza.bettertracker.ui.friendlist.FriendList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by murtaza on 2/12/18.
 */

public class Circle extends Fragment {
    final FragmentActivity c = getActivity();
    final String get_circle_url;
    final List<CircleModel> circleArrayList = new ArrayList<CircleModel>();
    CircleAdapter adapter;
    RecyclerView circleList;

    public Circle(){
        get_circle_url = "https://geotracker-app.herokuapp.com/api/circle/";

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

     View v = inflater.inflate(R.layout.fragment_circle, container, false);

        setHasOptionsMenu(true);

        circleList =  (RecyclerView) v.findViewById(R.id.circleRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(c);

        circleList.setLayoutManager(layoutManager);


        if(CheckNetwork.isInternetAvailable(getActivity())) //returns true if internet available
        {
            getActivity().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            getCircle();
        }
        else
        {
//            Toast.makeText(getActivity(),"No Internet Connection",Toast.LENGTH_LONG).show();
            Snackbar.make(getActivity().findViewById(android.R.id.content),"No Internet Connection", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

        }



        return v;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Circle");
    }

    private void getCircle() {
        JSONObject jsonBody = null;
        final String[] combined = new String[1];

        DataManager dataManager = ((MvpApp) getActivity().getApplication()).getDataManager();
//        Toast.makeText(getActivity().getApplicationContext(), "ID: " + dataManager.getId(), Toast.LENGTH_SHORT).show();
        try {
            jsonBody = new JSONObject("{\"_id\":"+  dataManager.getId() + "}");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jobReq = new JsonObjectRequest(Request.Method.POST, get_circle_url, jsonBody,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        Log.e("Tag", String.valueOf(jsonObject));

                        try {

                            JSONArray array1 = jsonObject.getJSONArray("userCircle");

                            if(array1 != null && array1.length() > 0 ) {

                                for (int i = 0; i < array1.length(); i++) {
                                    JSONObject o = array1.getJSONObject(i);

                                    CircleModel model = new CircleModel(o.getString("circleName"), Boolean.parseBoolean(o.getString("circleStatus")), o.getString("_id"));
                                    circleArrayList.add(model);
                                }
//                            Toast.makeText(getActivity().getApplicationContext(), "Array: " + Arrays.toString(circleArrayList.toArray()), Toast.LENGTH_SHORT).show();
                                adapter = new CircleAdapter(circleArrayList, getActivity().getApplicationContext());
                                circleList.setAdapter(adapter);
                            }

                            getActivity().findViewById(R.id.progressBar).setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {

            }


        });

        MySingleton.getmInstance(getActivity().getApplicationContext()).addToRequestque(jobReq);


    }

    public void openFriendList(String id) {
        Intent intent = FriendList.getStartIntent(getActivity());
        startActivity(intent);
    }
    @Override
    public void onResume() {
        super.onResume();
    }


}
