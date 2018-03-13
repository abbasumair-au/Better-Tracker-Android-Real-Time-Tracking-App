package com.example.murtaza.bettertracker.ui.friends.friendrequests;

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
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.murtaza.bettertracker.MvpApp;
import com.example.murtaza.bettertracker.R;
import com.example.murtaza.bettertracker.data.DataManager;
import com.example.murtaza.bettertracker.network.CheckNetwork;
import com.example.murtaza.bettertracker.network.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by hassans on 3/12/18.
 */

public class FriendRequest extends Fragment{

    final FragmentActivity c = getActivity();
    final List<FriendRequestModel> friendModelArrayList = new ArrayList<FriendRequestModel>();
    FriendRequestAdapter adapter;
    RecyclerView friendRequestList;
    final String get_friend_requests_url = "https://geotracker-app.herokuapp.com/api/getfriendrequests/";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_friend_requests, container, false);
        ButterKnife.bind(this, v);

        friendRequestList =  v.findViewById(R.id.friendRequestRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(c);

        friendRequestList.setLayoutManager(layoutManager);

        if(CheckNetwork.isInternetAvailable(getActivity())) //returns true if internet available
        {
            getActivity().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            getFriendRequests();
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
        getActivity().setTitle("Friend Request");
    }

    public void getFriendRequests() {
        DataManager dataManager = ((MvpApp) getActivity().getApplication()).getDataManager();
        String userEmailId = dataManager.getEmailId();
//        Toast.makeText(getActivity().getApplicationContext(), "ID: " + get_circle_url + userEmailId, Toast.LENGTH_SHORT).show();

        JsonArrayRequest jobReq = new JsonArrayRequest(Request.Method.GET, get_friend_requests_url + userEmailId ,null ,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray jsonArray) {

                        Log.d("newTag", String.valueOf(jsonArray));
                        String justString = "";
                        if(jsonArray.length() > 0 && jsonArray != null ) try {

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject o = jsonArray.getJSONObject(i);
                                JSONArray bListArray = o.getJSONArray("bList");
                                JSONObject o2 = bListArray.getJSONObject(0);
                                Log.d("Array2", o2.toString());
                                FriendRequestModel model = new FriendRequestModel(o2.getString("userName"), o2.getString("userEmail"));
                                friendModelArrayList.add(model);
                            }

                            adapter = new FriendRequestAdapter(friendModelArrayList, getActivity());
                            friendRequestList.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        getActivity().findViewById(R.id.progressBar).setVisibility(View.GONE);

                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("newTag", String.valueOf(error));
            }


        });

        MySingleton.getmInstance(getActivity().getApplicationContext()).addToRequestque(jobReq);
    }

}
