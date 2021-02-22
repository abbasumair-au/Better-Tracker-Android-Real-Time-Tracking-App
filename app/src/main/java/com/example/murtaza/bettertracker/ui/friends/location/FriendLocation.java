package com.example.murtaza.bettertracker.ui.friends.location;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.murtaza.bettertracker.R;
import com.example.murtaza.bettertracker.network.CheckNetwork;
import com.example.murtaza.bettertracker.network.MySingleton;
import com.example.murtaza.bettertracker.ui.friends.individualplaces.IPlace2;
import com.example.murtaza.bettertracker.ui.main.MainActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class FriendLocation extends AppCompatActivity {

    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    ArrayList<LatLng> points;
    LatLng position;
    private Circle circle;
    Marker melbourne;
    ArrayList<LocationObj> markers = new ArrayList<LocationObj>();
    // Maps code goes here
    LocationObj locationObj;

    private Handler handler;
    private Runnable runnable;

//    location apis
public String getlocation_url = "https://geotracker-app.herokuapp.com/api/getlocation/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_location);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        final String id = intent.getStringExtra("friendId");

        if(CheckNetwork.isInternetAvailable(FriendLocation.this)) //returns true if internet available
        {
            getLastLocation(id);

            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {

                    if(CheckNetwork.isInternetAvailable(FriendLocation.this)) //returns true if internet available
                    {

                        getLastLocationAndUpdate(id);
                        handler.postDelayed(runnable, 30000);

                    }else {
                        Toast.makeText(FriendLocation.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                        handler.removeCallbacks(runnable);
                    }
                }
            };

            handler.postDelayed(runnable, 30000);
        }
        else
        {
            Toast.makeText(FriendLocation.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }


        points = new ArrayList<>();

        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }
        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
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
                    if (ActivityCompat.checkSelfPermission(FriendLocation.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FriendLocation.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }

        MainActivity.hideSpinner();
    }

//    resizing bitmap
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler != null)
        handler.removeCallbacks(runnable);
    }

    private void getLastLocation(final String id) {
        //        Making image small for map

        Drawable myDrawable= getResources().getDrawable(R.drawable.profile2);
        Bitmap anImage = ((BitmapDrawable) myDrawable).getBitmap();
        final Bitmap resize = getResizedBitmap(anImage, 150, 150);
        // JSON OBJECT REQUEST
//        Toast.makeText(HomeActivity.this, "Alrighty", Toast.LENGTH_SHORT).show();
        final StringRequest stringRequests = new StringRequest(Request.Method.GET, getlocation_url + id,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        Log.d("VOLLEYFR", response);
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            String data = jsonObj.getString("data");
                            JSONObject jsonObject2 = new JSONObject(data);
                            String coordinates = jsonObject2.getString("coordinates");
                            String[] coordinatesclean = coordinates.substring(1, coordinates.length() - 1).split(",");

                            locationObj = getLocObj(Double.parseDouble(coordinatesclean[0]),Double.parseDouble(coordinatesclean[1]));

                            final String date = jsonObject2.getString("date");

                            final String accuracy = jsonObject2.getString("accuracy");

//                            final Instant instant = Instant.parse ( date );


                            String g = getCompleteAddressString(Double.parseDouble(coordinatesclean[1]),Double.parseDouble(coordinatesclean[0]));

                            position = new LatLng(locationObj.lat, locationObj.lng);

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

                                    Context context = getApplicationContext(); //or getActivity(), YourActivity.this, etc.

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
                                    accuracy.setText(date);
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
                                    .title(g).snippet("***" +"\n" + "accuracy ± "
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

                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(placeLocation, 17);
//                map.animateCamera(cameraUpdate);

                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(placeLocation));
                            googleMap.animateCamera(cameraUpdate);

//                            Toast.makeText(HomeActivity.this, coordinatesclean[0] + " " + coordinatesclean[1], Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(FriendLocation.this, "Internet Err!", Toast.LENGTH_SHORT).show();
                    }
                });

        MySingleton.getmInstance(FriendLocation.this).addToRequestque(stringRequests);

    }

    private void getLastLocationAndUpdate(final String id) {
//        Toast.makeText(this, "Another Req Sent Bro!", Toast.LENGTH_SHORT).show();

        Drawable myDrawable= getResources().getDrawable(R.drawable.profile2);
        Bitmap anImage = ((BitmapDrawable) myDrawable).getBitmap();
        final Bitmap resize = getResizedBitmap(anImage, 150, 150);

        // JSON OBJECT REQUEST
//        Toast.makeText(HomeActivity.this, "Alrighty", Toast.LENGTH_SHORT).show();
        final StringRequest stringRequests = new StringRequest(Request.Method.GET, getlocation_url + id,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        Log.d("VOLLEYFR", response);
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            String data = jsonObj.getString("data");
                            JSONObject jsonObject2 = new JSONObject(data);
                            String coordinates = jsonObject2.getString("coordinates");
                            String[] coordinatesclean = coordinates.substring(1, coordinates.length() - 1).split(",");

                            locationObj = getLocObj(Double.parseDouble(coordinatesclean[0]),Double.parseDouble(coordinatesclean[1]));

                            String date = jsonObject2.getString("date");

                            final String accuracy = jsonObject2.getString("accuracy");

                            final Instant instant = Instant.parse ( date );


                            String Address = getCompleteAddressString(Double.parseDouble(coordinatesclean[1]),Double.parseDouble(coordinatesclean[0]));

                            position = new LatLng(locationObj.lat, locationObj.lng);

//                          Toast.makeText(HomeActivity.this, "lat: " + locationObj.lat + "lng: " + locationObj.lng, Toast.LENGTH_SHORT).show();
                            points.add(position);

                            updateMap(locationObj.lat, locationObj.lng, accuracy, date, Address);


//                            Toast.makeText(HomeActivity.this, coordinatesclean[0] + " " + coordinatesclean[1], Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(FriendLocation.this, "Location not available!", Toast.LENGTH_SHORT).show();
                    }
                });

        MySingleton.getmInstance(FriendLocation.this).addToRequestque(stringRequests);

    }

    private void updateMap(Double lat, Double lng, String accuracy, final String date, String Address) {
//        LatLng placeLocation = new LatLng(lat, lon); //Make them global

        Drawable myDrawable= getResources().getDrawable(R.drawable.profile2);
        Bitmap anImage = ((BitmapDrawable) myDrawable).getBitmap();
        final Bitmap resize = getResizedBitmap(anImage, 150, 150);

        LatLng placeLocation;

        PolylineOptions lineOptions = new PolylineOptions();
        //drawing lines
        if(points.size() == 2 ) {
            lineOptions.addAll(points);
            lineOptions.width(10);
            lineOptions.color(Color.BLUE);
            points.clear();
            points.add(position);
        }else {
//            Toast.makeText(this, "No polylines: " +  points.size(), Toast.LENGTH_SHORT).show();
        }
        if(lineOptions != null) {
            googleMap.addPolyline(lineOptions);
        }
        else {
            Toast.makeText(getApplicationContext(),"lineOptions is null",Toast.LENGTH_SHORT).show();
        }

        melbourne.remove();
        circle.remove();

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                Context context = getApplicationContext(); //or getActivity(), YourActivity.this, etc.

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

        melbourne = googleMap.addMarker(new MarkerOptions().
                position(placeLocation)
                .anchor(1,1)
                .title(Address).snippet("***" +"\n" + "accuracy ± "
                        +accuracy.toString())
                .icon(BitmapDescriptorFactory.fromBitmap(resize)));
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
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
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
            Log.w("My location address", "Canont get Address!");
        }
        return strAdd;
    }

    @OnClick(R.id.individualPlaces) void getPlaces() {
        handler.removeCallbacks(runnable);
        Intent intent = getIntent();
        final String id = intent.getStringExtra("friendId");
//        Log.d("IDNEW", id);
        startActivity(new Intent(this, IPlace2.class).putExtra("friendId", id));
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

    public static class TimeAgo {

        public static final List<Long> times = Arrays.asList(
                TimeUnit.DAYS.toMillis(365),
                TimeUnit.DAYS.toMillis(30),
                TimeUnit.DAYS.toMillis(1),
                TimeUnit.HOURS.toMillis(1),
                TimeUnit.MINUTES.toMillis(1),
                TimeUnit.SECONDS.toMillis(1) );
        public static final List<String> timesString = Arrays.asList("year","month","day","hour","minute","second");

        public static String toDuration(long duration) {

            StringBuffer res = new StringBuffer();
            for(int i=0;i< TimeAgo.times.size(); i++) {
                Long current = TimeAgo.times.get(i);
                long temp = duration/current;
                if(temp>0) {
                    res.append(temp).append(" ").append( TimeAgo.timesString.get(i) ).append(temp != 1 ? "s" : "").append(" ago");
                    break;
                }
            }
            if("".equals(res.toString()))
                return "0 seconds ago";
            else
                return res.toString();
        }
        }

    @OnClick(R.id.refresh) void refreshActivity() {
        finish();
        startActivity(getIntent());
    }

}
