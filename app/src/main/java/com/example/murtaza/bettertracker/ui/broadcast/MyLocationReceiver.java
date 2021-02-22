package com.example.murtaza.bettertracker.ui.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.murtaza.bettertracker.MvpApp;
import com.example.murtaza.bettertracker.data.DataManager;
import com.example.murtaza.bettertracker.network.CheckNetwork;
import com.example.murtaza.bettertracker.network.MySingleton;
import com.example.murtaza.bettertracker.ui.location.models.GeoJSON;
import com.example.murtaza.bettertracker.ui.location.models.Geometry;
import com.example.murtaza.bettertracker.ui.location.models.Location;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by root on 2/28/18.
 */
public class MyLocationReceiver extends BroadcastReceiver {

    private static final String TAG = "MyBroadcastReceiver";
    Double Latitude, Longitude;
    Float Accuracy;
    String Provider;
    public static final String RECEIVE_JSON = "org.umair.locationdataupload.RECEIVE_JSON";
    private String location_url = "https://geotracker-app.herokuapp.com/api/setlocation";
    private String location_url_file = "https://geotracker-app.herokuapp.com/api/setLocationFromFile";
    private static boolean FILE_FLAG = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        StringBuilder sb = new StringBuilder();
        sb.append("Action: " + intent.getAction() + "\n");
        sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
        String log = sb.toString();
        Log.d(TAG, log);
//        Toast.makeText(context, log, Toast.LENGTH_LONG).show();

        if (intent.getAction().equals(RECEIVE_JSON)) {
            Provider = intent.getStringExtra("Provider");
            Latitude = (Double) intent.getExtras().get("Latitude");
            Longitude = (Double) intent.getExtras().get("Longitude");
            Accuracy = (Float) intent.getExtras().get("Accuracy");

            DataManager dataManager = ((MvpApp) context.getApplicationContext()).getDataManager();

            dataManager.saveLoc(Latitude + "," + Longitude);
            if(CheckNetwork.isInternetAvailable(context)) {
                if (FILE_FLAG) {
                    readFromJson(context);
                    FILE_FLAG = false;
                }
                sendVolleyRequestToServer(Latitude, Longitude, Accuracy, context);
            }
            else {
                FILE_FLAG = true;
                writeInJson(dataManager, Latitude, Longitude, Accuracy);
            }
        }
    }

    private void readFromJson(Context context) {
        Gson gson = new Gson();
        BufferedReader br = null;
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "geo.json");

        try {

            br = new BufferedReader(new FileReader(file));
            List<Location> myGson = (List<Location>) gson.fromJson(br, List.class);

            String response = gson.toJson(myGson);

            JSONArray jsonArray = new JSONArray(response);

            sendVolleyRequestJSON(jsonArray, context);

            GeoJSON finalObj = new GeoJSON();
            finalObj.clear();

            Log.d("TotalArray", jsonArray.toString());


//             Log.d("JSONArray", jsonArray.toString());
//
//            for(int i=0; i<jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                sendVolleyRequestJSON(jsonObject, context);
//            }
//
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private void writeInJson(DataManager dataManager,Double lat, Double lng, Float accuracy) {
        Location location = new Location();
        location.setId(dataManager.getId());
        location.setUserId(dataManager.getName());
        Geometry geometry = new Geometry();
        geometry.setType("point");
        geometry.setAccuracy(accuracy);
        geometry.setCoordinates(Arrays.asList(lng, lat));

        TimeZone tz = TimeZone.getTimeZone("GMT");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
        geometry.setDate(nowAsISO);

        location.setGeometry(geometry);
        GeoJSON finalObj = new GeoJSON();
        finalObj.getLocations();
        finalObj.add(location);
        finalObj.update();
    }

    public void sendVolleyRequestToServer(Double lat, Double lng, Float accuracy, Context context) {

        DataManager dataManager = ((MvpApp) context.getApplicationContext() ).getDataManager();

//      Toast.makeText(this, userId, Toast.LENGTH_SHORT).show();

//      JSON OBJECT REQUEST
        JSONObject jsonBody = null;
        try {
            jsonBody = new JSONObject("{\"_id\":" + dataManager.getId() + ",\"userId\": " + dataManager.getName() + ",\"geometry\": { \"type\":\"point\",\"accuracy\":" + accuracy.toString() + " ,\"coordinates\": [" + lng.toString() + ", " + lat.toString() + "] } }");
//            Toast.makeText(this, jsonBody.toString(), Toast.LENGTH_SHORT).show();
            Log.d("Jb:", jsonBody.toString());
        } catch (JSONException e) {

            e.printStackTrace();

        }

        JsonObjectRequest jobReq = new JsonObjectRequest(Request.Method.POST, location_url, jsonBody,
                new Response.Listener<JSONObject>() {
                    public static final String TAG = "123";

                    @Override
                    public void onResponse(JSONObject jsonObject) {
//                        Log.e(TAG, "JSON Object is = " + jsonObject);
                        Log.d("Locset : ", jsonObject.toString());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                    }
                });

        MySingleton.getmInstance(context.getApplicationContext()).addToRequestque(jobReq);

    }

    public void sendVolleyRequestJSON(final JSONArray jsonArray, Context context) {
        JsonArrayRequest jobReq = new JsonArrayRequest(Request.Method.POST, location_url_file, jsonArray,
                new Response.Listener<JSONArray>() {

                    public static final String TAG = "123";

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(0);
                            Log.d("ResponseFile", jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                    }
                });
        MySingleton.getmInstance(context.getApplicationContext()).addToRequestque(jobReq);
    }

}