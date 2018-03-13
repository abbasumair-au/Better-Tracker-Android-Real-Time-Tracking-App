package com.example.murtaza.bettertracker.ui.main;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.example.murtaza.bettertracker.ui.friends.location.FriendLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by murtaza on 2/17/18.
 */

public class MainFragment extends Fragment {

    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    ArrayList<LatLng> points;
    LatLng position;
    private com.google.android.gms.maps.model.Circle circle;
    Marker melbourne;
    ArrayList<FriendLocation.LocationObj> markers = new ArrayList<FriendLocation.LocationObj>();
    // Maps code goes here
    LocationObj locationObj;
    private String get_friends_url = "https://geotracker-app.herokuapp.com/api/getlocationcombined/";
    private Handler handler;
    private Runnable runnable;


//    alert box like google maps to turn on location

    protected static final String TAG = "LocationOnOff";


    private GoogleApiClient googleApiClient;
    final static int REQUEST_LOCATION = 199;
// geo code variable

    @Nullable
    String g = "";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View v =  inflater.inflate(R.layout.fragment_main, container, false);


        points = new ArrayList<>();

        getActivity().findViewById(R.id.progressBar).setVisibility(View.GONE);

        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }

        mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView));

        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    googleMap = map;
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    try {
                        googleMap.setMyLocationEnabled(true);
                    } catch (SecurityException se) {
                    }
                    //Edit the following as per you needs
                    googleMap.setTrafficEnabled(true);
                    googleMap.setIndoorEnabled(true);
                    googleMap.setBuildingsEnabled(true);
                    if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    googleMap.setMyLocationEnabled(false);
                    googleMap.getUiSettings().setZoomControlsEnabled(true);
                }
            });
        } else {
            Toast.makeText(getActivity(), "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if(CheckNetwork.isInternetAvailable(getActivity())){
                    getAllFriendLocationsAndUpdate();
                    handler.postDelayed(runnable, 30000);
                }
                else
                {
//                    Toast.makeText(getActivity(),"No Internet Connection",Toast.LENGTH_LONG).show();
                    Snackbar.make(getActivity().findViewById(android.R.id.content),"No Internet Connection", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        };

        if(CheckNetwork.isInternetAvailable(getActivity())){
            Drawable myDrawable= getResources().getDrawable(R.drawable.profile2);
            Bitmap anImage = ((BitmapDrawable) myDrawable).getBitmap();
            final Bitmap resize = getResizedBitmap(anImage, 150, 150);
            JSONObject jsonBody = null;
            final DataManager dataManager = ((MvpApp) getActivity().getApplication()).getDataManager();

            getAllFriendLocations(new VolleyListener() {
                @Override
                public void onSuccess(JSONArray jsonArray) {
                    Log.d("newTag", String.valueOf(jsonArray));
                    String justString = "";

                    try {
                        Log.d("Array", jsonArray.toString());

                        for(int i=0; i<jsonArray.length(); i++) {
                            JSONObject o1 = jsonArray.getJSONObject(i);
                            Log.d("OBS", o1.toString());

                            JSONArray array2 = o1.getJSONArray("geometry");
                            JSONObject o2 = array2.getJSONObject(0);
                            JSONArray array3 = o2.getJSONArray("coordinates");
                            String accuracy = o2.getString("accuracy").toString();
                            final String date = o2.getString("date").toString();
                            String longitude = array3.get(0).toString();
                            String latitude = array3.get(1).toString();


                            locationObj = getLocObj(Double.parseDouble(longitude),Double.parseDouble(latitude));

                            @Nullable
                            String g = getCompleteAddressString(Double.parseDouble(latitude),Double.parseDouble(longitude));

                            position = new LatLng(locationObj.lat, locationObj.lng);

                            Log.d("Mylatlng","Lat: " + locationObj.lat +" Lng: " +  locationObj.lng);
//                          Toast.makeText(HomeActivity.this, "lat: " + locationObj.lat + "lng: " + locationObj.lng, Toast.LENGTH_SHORT).show();
                            points.add(position);

//                            Toast.makeText(FriendLocation.this, "Date: " + TimeAgo.toDuration(Long.parseLong(date)), Toast.LENGTH_SHORT).show();
//                            markers.add(new LocationObj(locationObj.lat, locationObj.lng));

//                            Setting up info window
                            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                                @Override
                                public View getInfoWindow(Marker arg0) {
                                    return null;
                                }

                                @Override
                                public View getInfoContents(Marker marker) {

                                    Context context = getActivity().getApplicationContext(); //or getActivity(), YourActivity.this, etc.

                                    LinearLayout info = new LinearLayout(context);
                                    info.setOrientation(LinearLayout.VERTICAL);

                                    TextView title = new TextView(context);
                                    title.setTextColor(Color.BLACK);
                                    title.setGravity(Gravity.CENTER);
                                    title.setTypeface(null, Typeface.BOLD);
                                    title.setText(marker.getTitle());
                                    title.setPadding(15,15,15,15);
                                    TextView snippet = new TextView(context);
                                    snippet.setTextColor(Color.GRAY);
                                    snippet.setText(marker.getSnippet());
                                    snippet.setGravity(Gravity.CENTER);
                                    TextView accuracy = new TextView(context);
                                    accuracy.setTextColor(Color.LTGRAY);
                                    accuracy.setGravity(Gravity.CENTER);
                                    accuracy.setTypeface(null, Typeface.BOLD_ITALIC);
                                    accuracy.setText(date.toString());
                                    info.addView(accuracy);
                                    info.addView(title);
                                    info.addView(snippet);

                                    return info;
                                }
                            });

                            LatLng placeLocation = new LatLng(locationObj.lat, locationObj.lng);

                            melbourne = googleMap.addMarker(new MarkerOptions().
                                    position(placeLocation)
                                    .anchor(1,1)
                                    .title(g).snippet(dataManager.getName() + "\n" +"***" +"\n" + "accuracy ± "
                                            +accuracy.toString())
                                    .icon(BitmapDescriptorFactory.fromBitmap(resize)));

//                                    .icon(BitmapDescriptorFactory.defaultMarker(
//                                            BitmapDescriptorFactory.HUE_RED)));
//
//                            melbourne = googleMap.addMarker(new MarkerOptions()
//                                    .position(placeLocation)
//                                    .title("Address")
//                                    .anchor((float) 0.5, (float) 0.5)
//                                    .snippet(g)
//                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_white_edited)));

                            circle = googleMap.addCircle(new CircleOptions()
                                    .center(new LatLng(locationObj.lat, locationObj.lng))
                                    .strokeWidth(0)
                                    .strokeColor(Color.WHITE)
                                    .fillColor(Color.argb(50, 0, 0, 255))
                                    .zIndex(3));

                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(placeLocation, 20);
//                map.animateCamera(cameraUpdate);

                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(placeLocation));
                            googleMap.animateCamera(cameraUpdate);
                            handler.postDelayed(runnable,5000);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
//            handler.postDelayed(runnable, 5000);
        }
        else
        {
            Snackbar.make(getActivity().findViewById(android.R.id.content),"No Internet Connection", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
//            Toast.makeText(getActivity(),"No Internet Connection",Toast.LENGTH_LONG).show();
        }



        getActivity().setFinishOnTouchOutside(true);

        // Todo Location Already on  ... start
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
//            Toast.makeText(this, "Gps already enabled", Toast.LENGTH_SHORT).show();
//            finish();
//        }
        // Todo Location Already on  ... end

        if (!hasGPSDevice(getActivity())) {
            Toast.makeText(getActivity(), "Gps not Supported", Toast.LENGTH_SHORT).show();
        }

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(getActivity())) {
//            Log.e("keshav", "Gps already enabled");
            Toast.makeText(getActivity(), "Gps not enabled", Toast.LENGTH_SHORT).show();
            enableLoc();
        } else {
//            Log.e("keshav", "Gps already enabled");
//            Toast.makeText(getActivity(), "Gps already enabled", Toast.LENGTH_SHORT).show();
        }


        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(handler != null)
        handler.removeCallbacks(runnable);
    }

    public interface VolleyListener{
        void onSuccess(JSONArray array);
    }
    protected void getAllFriendLocations(final VolleyListener listener) {
        Drawable myDrawable= getResources().getDrawable(R.drawable.profile2);
        Bitmap anImage = ((BitmapDrawable) myDrawable).getBitmap();
        final Bitmap resize = getResizedBitmap(anImage, 150, 150);

        final DataManager dataManager = ((MvpApp) getActivity().getApplication()).getDataManager();
        String userEmailId = dataManager.getEmailId();
//        Toast.makeText(getApplicationContext(), "ID: " + get_friends_url + userEmailId, Toast.LENGTH_SHORT).show();

        JsonArrayRequest jobReq = new JsonArrayRequest(Request.Method.GET, get_friends_url + userEmailId ,null ,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        listener.onSuccess(jsonArray);
                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("newTag", String.valueOf(error));
            }

        });

        MySingleton.getmInstance(getActivity().getApplicationContext()).addToRequestque(jobReq);
    }

    protected void getAllFriendLocationsAndUpdate() {

        DataManager dataManager = ((MvpApp) getActivity().getApplication()).getDataManager();
        String userEmailId = dataManager.getEmailId();
//        Toast.makeText(getApplicationContext(), "ID: " + get_friends_url + userEmailId, Toast.LENGTH_SHORT).show();

        JsonArrayRequest jobReq = new JsonArrayRequest(Request.Method.GET, get_friends_url + userEmailId ,null ,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray jsonArray) {

                        Log.d("newTag", String.valueOf(jsonArray));
                        String justString = "";

                        try {
                            Log.d("Array", jsonArray.toString());
                            if(jsonArray.length() > 0 || jsonArray != null){


                                for(int i=0; i<jsonArray.length(); i++) {
                                    JSONObject o1 = jsonArray.getJSONObject(i);
                                    Log.d("OBS", o1.toString());

                                    JSONArray array2 = o1.getJSONArray("geometry");
                                    JSONObject o2 = array2.getJSONObject(0);
                                    JSONArray array3 = o2.getJSONArray("coordinates");
                                    String accuracy = o2.getString("accuracy").toString();
                                    final String date = o2.getString("date").toString();
                                    String longitude = array3.get(0).toString();
                                    String latitude = array3.get(1).toString();


                                    locationObj = getLocObj(Double.parseDouble(longitude), Double.parseDouble(latitude));

                                    String Address = getCompleteAddressString(Double.parseDouble(latitude), Double.parseDouble(longitude));

                                    position = new LatLng(locationObj.lat, locationObj.lng);

                                    Log.d("Mylatlng", "Lat: " + locationObj.lat + " Lng: " + locationObj.lng);
//                          Toast.makeText(HomeActivity.this, "lat: " + locationObj.lat + "lng: " + locationObj.lng, Toast.LENGTH_SHORT).show();
                                    points.add(position);

//
                                    updateMap(locationObj.lat, locationObj.lng, accuracy, date, Address);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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
    private LocationObj getLocObj(Double lat, Double lng){

        // LocationObj object
        LocationObj loc = new LocationObj(lat, lng);

        return loc;
    }

    private LocationObj getLocObj(Double lat, Double lng, String loctimestamp, Float Accuracy){

        // LocationObj object
        LocationObj loc = new LocationObj(lat, lng, loctimestamp, Accuracy);

        return loc;
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        if(geocoder != null) {
            try {
                List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
                if (addresses != null) {
                    Address returnedAddress = addresses.get(0);
                    StringBuilder strReturnedAddress = new StringBuilder("");

                    for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                    }
                    strAdd = strReturnedAddress.toString();
                    Log.w("My location address", strReturnedAddress.toString());
                } else {
                    Log.w("My location address", "No Address returned!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.w("My location address", "Cannot get Address!");
            }
        }
        return strAdd;
    }

    public static Bitmap getResizedBitmap(Bitmap image, int newHeight, int newWidth) {
        int width = image.getWidth();
        int height = image.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }

    private void updateMap(Double lat, Double lng, String accuracy, final String date, String Address) {
//        LatLng placeLocation = new LatLng(lat, lon); //Make them global

        Drawable myDrawable= getResources().getDrawable(R.drawable.profile2);
        Bitmap anImage = ((BitmapDrawable) myDrawable).getBitmap();
        final Bitmap resize = getResizedBitmap(anImage, 150, 150);

        LatLng placeLocation;

//        PolylineOptions lineOptions = new PolylineOptions();
        //drawing lines
        if(points.size() == 2 ) {
//            lineOptions.addAll(points);
//            lineOptions.width(10);
//            lineOptions.color(Color.BLUE);
            points.clear();
            points.add(position);
        }else {
//            Toast.makeText(this, "No polylines: " +  points.size(), Toast.LENGTH_SHORT).show();
        }
//        if(lineOptions != null) {
//            googleMap.addPolyline(lineOptions);
//        }
//        else {
//            Toast.makeText(getActivity().getApplicationContext(),"lineOptions is null",Toast.LENGTH_SHORT).show();
//        }


        melbourne.remove();
        circle.remove();

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                Context context = getActivity().getApplicationContext(); //or getActivity(), YourActivity.this, etc.

                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());
                title.setPadding(15,15,15,15);
                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());
                snippet.setGravity(Gravity.CENTER);
                TextView accuracy = new TextView(context);
                accuracy.setTextColor(Color.LTGRAY);
                accuracy.setGravity(Gravity.CENTER);
                accuracy.setTypeface(null, Typeface.BOLD_ITALIC);
                accuracy.setText(date.toString());
                info.addView(accuracy);
                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });


        placeLocation = new LatLng(locationObj.lat, locationObj.lng);

        DataManager dataManager = ((MvpApp) getActivity().getApplication()).getDataManager();

        melbourne = googleMap.addMarker(new MarkerOptions().
                position(placeLocation)
                .anchor(1,1)
                .title(Address).snippet( dataManager.getName() + "***" +"\n" + "accuracy ± "
                        +accuracy.toString())
                .icon(BitmapDescriptorFactory.fromBitmap(resize)));

//                            melbourne = googleMap.addMarker(new MarkerOptions()
//                                    .position(placeLocation)
//                                    .title("Address")
//                                    .anchor((float) 0.5, (float) 0.5)
//                                    .snippet(g)
//                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_white_edited)));

        circle = googleMap.addCircle(new CircleOptions()
                .center(new LatLng(locationObj.lat, locationObj.lng))
                .strokeWidth(0)
                .strokeColor(Color.WHITE)
                .fillColor(Color.argb(50, 0, 0, 255))
                .zIndex(2));
    }

    // location class:
    public static class LocationObj {

        public Double lng;
        public Double lat;
        public String loctimestamp;
        public Float accuracy;

        public LocationObj(Double lng, Double lat) {
            this.lng = lng;
            this.lat = lat;
            this.loctimestamp = loctimestamp;
        }

        public LocationObj(Double lng, Double lat, String loctimestamp) {
            this.lng = lng;
            this.lat = lat;
            this.loctimestamp = loctimestamp;
        }

        public LocationObj(Double lat, Double lng, String loctimestamp, Float accuracy) {
            this.lng = lng;
            this.lat = lat;
            this.loctimestamp = loctimestamp;
            this.accuracy = accuracy;
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public Double getLng(){
            return this.lng;
        }

        public Double getLat(){
            return this.lat;
        }

        public String getLocTimeStamp(){
            return this.loctimestamp;
        }

        public Float getAccuracy(){
            return this.accuracy;
        }

    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Home");
    }

    //  everything related to location builder goes here:
    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onStop() {
        super.onStop();
        MySingleton.getmInstance(getActivity()).StopRequestQueue();
    }

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(getActivity(), REQUEST_LOCATION);

//                                finish();
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }
    }
}
