package com.example.murtaza.bettertracker.ui.friends;

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
 * Created by murtaza on 2/12/18.
 */

public class Friend extends Fragment {

    final FragmentActivity c = getActivity();
    final List<FriendModel> friendModelArrayList = new ArrayList<FriendModel>();
    FriendAdapter adapter;
    RecyclerView friendList;
    final String get_circle_url = "https://geotracker-app.herokuapp.com/api/getfriend/";
    private String circleId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v =  inflater.inflate(R.layout.fragment_friend, container, false);
        ButterKnife.bind(this, v);

        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();

        if(bundle != null){
//            Will use it it future
//            circleId = (String) bundle.get("circleId");
//            Toast.makeText(getActivity(),"ID:" + bundle.get("circleId"), Toast.LENGTH_LONG).show();
        }

        friendList =  v.findViewById(R.id.friendRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(c);

        friendList.setLayoutManager(layoutManager);

        if(CheckNetwork.isInternetAvailable(getActivity())) //returns true if internet available
        {
            getActivity().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            getFriend();
        }
        else
        {
            Snackbar.make(getActivity().findViewById(android.R.id.content),"No Internet Connection", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Friend");
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.addfriend, menu);
//        super.onCreateOptionsMenu(menu, inflater);
    }

    private void getFriend() {

//        getActivity().findViewById(R.id.findFriend).setVisibility(View.GONE);

        JSONObject jsonBody = null;

        DataManager dataManager = ((MvpApp) getActivity().getApplication()).getDataManager();
        String userEmailId = dataManager.getEmailId();
//        Toast.makeText(getActivity().getApplicationContext(), "ID: " + get_circle_url + userEmailId, Toast.LENGTH_SHORT).show();

        JsonArrayRequest jobReq = new JsonArrayRequest(Request.Method.GET, get_circle_url + userEmailId ,null ,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray jsonArray) {

                        Log.d("newTag", String.valueOf(jsonArray));
                        String justString = "";
                        if(jsonArray.length() > 0 && jsonArray != null ) {

                            try {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject o = jsonArray.getJSONObject(i);
                                    JSONArray bListArray = o.getJSONArray("bList");
                                    JSONObject o2 = bListArray.getJSONObject(0);
                                    Log.d("Array2", o2.toString());
                                    FriendModel model = new FriendModel(o2.getString("userName"), o2.getString("_id"));
                                    friendModelArrayList.add(model);
                                }

                                adapter = new FriendAdapter(friendModelArrayList, getActivity());
                                friendList.setAdapter(adapter);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            getActivity().findViewById(R.id.findFriend).setVisibility(View.VISIBLE);
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

//    @OnClick(R.id.findFriend) void onClickButton(View v) {
//
////        Toast.makeText(getActivity().getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
//        Fragment fragment = null;
//        fragment = new AddFriend();
//
//        if(fragment != null) {
//            FragmentManager fragmentManager = getFragmentManager();
//
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.content_main, fragment);
//            fragmentTransaction.commit();
//        }
//
////        DrawerLayout drawer = (DrawerLayout) getChildFragmentManager().findViewById(R.id.drawer_layout);
/////        drawer.closeDrawer(GravityCompat.START);
//    }

}
