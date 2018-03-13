package com.example.murtaza.bettertracker.ui.friends.individualplaces;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.murtaza.bettertracker.MvpApp;
import com.example.murtaza.bettertracker.R;
import com.example.murtaza.bettertracker.data.DataManager;
import com.example.murtaza.bettertracker.network.MySingleton;
import com.example.murtaza.bettertracker.ui.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class IPlace2 extends AppCompatActivity {

    IPlaceAdapter adapter;

    final String get_place_url = "http://ec2-54-213-89-147.us-west-2.compute.amazonaws.com/analysis/";
    final List<IPlaceModel> placeModelArrayList = new ArrayList<IPlaceModel>();
    RecyclerView placeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iplace2);

        placeList =  (RecyclerView) findViewById(R.id.placeRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        placeList.setLayoutManager(layoutManager);
        Intent intent  = getIntent();

        String id = intent.getStringExtra("friendId");

        getPlace(id);


    }

    private IPlaceAdapter getPlace(String id) {

        DataManager dataManager = ((MvpApp) getApplication()).getDataManager();
        String userEmailId = dataManager.getEmailId();
//        Toast.makeText(getApplicationContext(), "ID: " + get_place_url + userEmailId, Toast.LENGTH_SHORT).show();
        Log.d("myLink", get_place_url + "5a2de22f887b7900046449f6");
        StringRequest registerRequest = new StringRequest(Request.Method.GET, get_place_url + "5a2de22f887b7900046449f6",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("VOLLEY", response);
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            JSONObject jsonObject2 = new JSONObject(jsonObj.getString("5a2de22f887b7900046449f6"));

                            Log.d("MyObjBro", jsonObj.toString());

                            for(int i = 0; i<jsonObject2.names().length(); i++){
//                                Log.d("MyObj", "key = " + jsonObject2.names().getString(i) + " value = " + jsonObject2.get(jsonObject2.names().getString(i)));
                                IPlaceModel model = new IPlaceModel(jsonObject2.names().getString(i), (Integer) jsonObject2.get(jsonObject2.names().getString(i)));
                                placeModelArrayList.add(model);
                            }

                            adapter = new IPlaceAdapter(placeModelArrayList, getApplicationContext());
                            placeList.setAdapter(adapter);
                            MainActivity.hideSpinner();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });


        registerRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getmInstance(getApplicationContext()).addToRequestque(registerRequest);

        return adapter;
    }
}
