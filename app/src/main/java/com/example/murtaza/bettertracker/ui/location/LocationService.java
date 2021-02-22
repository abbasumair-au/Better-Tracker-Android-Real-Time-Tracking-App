package com.example.murtaza.bettertracker.ui.location;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service {
    public static final String BROADCAST_ACTION = "Hello World";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;
    public static final String RECEIVE_JSON = "org.umair.locationdataupload.RECEIVE_JSON";
    HandlerThread handlerThread = null;
    Handler handler = null;
    Runnable runnable = null;

    Intent intent;
    int counter = 0;
    public static boolean LOC_FLAG = false;

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(RECEIVE_JSON);
        if (Build.VERSION.SDK_INT >= 26) {
            startForeground(1, new Notification());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 300000, 20, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000, 20, listener);

//        startHandlerThread();

// Or use LocationManager.GPS_PROVIDER


//        startHandler();

        //        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
//
//    private void startHandler() {
//
//      handler = new Handler();
//
//      runnable = new Runnable() {
//          @Override
//          public void run() {
//              if(!CheckNetwork.isInternetAvailable(getApplicationContext())) {
//
//                  if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
//                      Toast.makeText(getApplicationContext(),"Permission Denied", Toast.LENGTH_SHORT).show();
//                  }
//                  locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1800000, 0, listener);
//                  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1800000, 0, listener);
//
//              }
//          }
//      };
//
//      handler.postDelayed(runnable, 10000);
//
//    }

    //    @Override
//    public void onStart(Intent intent, int startId)
//    {
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        listener = new MyLocationListener();
//        if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
//            Toast.makeText(getApplicationContext(),"Permission Denied", Toast.LENGTH_SHORT).show();
//        }
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, listener);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);
//
//    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }


    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }
        if (listener != null)
            locationManager.removeUpdates(listener);
    }

    public Location getPreviousBestLocationNP() {
        String locationProvider = LocationManager.NETWORK_PROVIDER;
// Or use LocationManager.GPS_PROVIDER

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

        return lastKnownLocation;
    }

    public Location getPreviousBestLocationGPS() {
        String locationProvider = LocationManager.GPS_PROVIDER;
// Or use LocationManager.GPS_PROVIDER

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

        return lastKnownLocation;
    }

//    public void startHandlerThread(){
//
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                checkConnection();
//            }
//        };
//
//        handlerThread = new HandlerThread("HandlerThread");
//        handlerThread.start();
//        handler = new Handler(handlerThread.getLooper());
//
//        handler.postDelayed(runnable,1000);
//    }

//    private void checkConnection(){
//
//        if(CheckNetwork.isInternetAvailable(getApplicationContext()) && LOC_FLAG){
//
//            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            listener = new MyLocationListener();
//
//            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
//            }
//
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 0, listener);
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, listener);
//
//            LOC_FLAG = false;
//
//        }else if(!CheckNetwork.isInternetAvailable(getApplicationContext()) && !LOC_FLAG ){
//
//            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            listener = new MyLocationListener();
//
//            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
//            }
//
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3600000, 0, listener);
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3600000, 0, listener);
//
//            LOC_FLAG = true;
//
//        }
//
//    }
    public class MyLocationListener implements LocationListener
    {

        public void onLocationChanged(final Location loc)
        {
//            checkConnection();
            Log.i("*********************", "Location changed");
//            Log.d("Previous", "Lat:" + getPreviousBestLocation().getLatitude() + "Lng: " + getPreviousBestLocation().getLongitude());
            if(isBetterLocation(loc, getPreviousBestLocationNP())) {
                intent.putExtra("Latitude", loc.getLatitude());
                intent.putExtra("Longitude", loc.getLongitude());
                intent.putExtra("Provider", loc.getProvider());
                intent.putExtra("Accuracy", loc.getAccuracy());
                sendBroadcast(intent);
            }else if(isBetterLocation(loc, getPreviousBestLocationGPS())){
                intent.putExtra("Latitude", loc.getLatitude());
                intent.putExtra("Longitude", loc.getLongitude());
                intent.putExtra("Provider", loc.getProvider());
                intent.putExtra("Accuracy", loc.getAccuracy());
                sendBroadcast(intent);
            }
        }

        public void onProviderDisabled(String provider)
        {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }


        public void onProviderEnabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }


        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }

    }
}
