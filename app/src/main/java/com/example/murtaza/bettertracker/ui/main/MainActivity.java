package com.example.murtaza.bettertracker.ui.main;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.murtaza.bettertracker.MvpApp;
import com.example.murtaza.bettertracker.R;
import com.example.murtaza.bettertracker.data.DataManager;
import com.example.murtaza.bettertracker.network.MySingleton;
import com.example.murtaza.bettertracker.ui.circles.Circle;
import com.example.murtaza.bettertracker.ui.emergencyalert.EmergencyAlert;
import com.example.murtaza.bettertracker.ui.friends.Friend;
import com.example.murtaza.bettertracker.ui.friends.friendrequests.FriendRequest;
import com.example.murtaza.bettertracker.ui.home.HomeActivity;
import com.example.murtaza.bettertracker.ui.location.LocationService;
import com.example.murtaza.bettertracker.ui.places.Place;
import com.example.murtaza.bettertracker.ui.settings.Setting;
import com.facebook.CallbackManager;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private DataManager dataManager;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager callbackManager;

    public static int lastSelect = 0;

//    firebase token request
    private final String FCMTOKEN = "https://geotracker-app.herokuapp.com/api/fcmtoken/";

//    static int count = 0;

//    @BindView(R.id.imageViewHead)
//    ImageView imageView;

    //  everything related to location
    Intent locintent;
    public static final String RECEIVE_JSON = "org.umair.locationdataupload.RECEIVE_JSON";
    private static final int TAG_CODE_PERMISSION_LOCATION = 123;

    TextView name;
    TextView email;


    private boolean fabExpanded = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //        Toast.makeText(this, "Key: " + MyFirebaseInstanceIdService.Token, Toast.LENGTH_SHORT).show();
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        List<String> spinnerArray =  new ArrayList<String>();
//        spinnerArray.add("item1");
//        spinnerArray.add("item2");
//
//        Spinner spinner = toolbar.findViewById(R.id.planets_spinner);
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                this, android.R.layout.simple_spinner_item, spinnerArray);
//
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
////                if (fabExpanded == true){
////                    closeSubMenusFab();
////                } else {
////                    openSubMenusFab();
////                }
//
//            }
//        });

//        changing main with map fragment
        Fragment fragment = null;
        fragment = new MainFragment();

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main, fragment);
            ft.commit();
        }



        lastSelect = R.id.nav_map;

        DrawerLayout drawer2 = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer2.closeDrawer(GravityCompat.START);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        setting up our presenter
        dataManager = ((MvpApp) getApplication()).getDataManager();


//        Toast.makeText(this, "Username: " + dataManager.getName(), Toast.LENGTH_SHORT).show();

//        google login
        googleLogin();
//        googleSignout();
//        facebook login
//        fbLogin();

//        decideActivity();


//        Everything related to location sharing

        locPermissions();

        String data = dataManager.getProfilePIc();


        View headerView = navigationView.getHeaderView(0);
        name = (TextView) headerView.findViewById(R.id.nameNav);
        name.setText(dataManager.getName());
        email = headerView.findViewById(R.id.emailNav);
        email.setText(dataManager.getEmailId());

//       everything related to firebase tls
//      Tokens
        if(dataManager.getFirstTime()) {
            sendTokenRequest(dataManager.getFCMToken(), dataManager.getEmailId());
            dataManager.setFirstTime();
        }
//        writeInJson();
//        readFromJson();


//        IntentFilter filter = new IntentFilter(RECEIVE_JSON);
//        MyLocationReceiver r = new MyLocationReceiver();
//        registerReceiver(r, filter);
    }


    //closes FAB submenus
    private void closeSubMenusFab(){
//        fab.setImageResource(R.drawable.ic_settings_black_24dp);
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFab(){
        //Change settings icon to 'X' icon
//        fab.setImageResource(R.drawable.ic_close_black_24dp);
        fabExpanded = true;
    }

        //        setting user
//        Ion.with(this)
//                .load("https://cdn.arstechnica.net/wp-content/uploads/2016/05/r.amadeo-45843.jpg")
//                .withBitmap()
//                .placeholder(R.drawable.profile1)
//                .error(R.drawable.danger)
//                .intoImageView(serverImg);


//    private void writeInJson() {
//        Location location = new Location();
//        location.setId("5a912c5c984ab10004d69496");
//        location.setUserId("Haa");
//        Geometry geometry = new Geometry();
//        geometry.setType("point");
//        geometry.setAccuracy(35);
//        geometry.setCoordinates(Arrays.asList(73.16006549, 33.6568472));
//        location.setGeometry(geometry);
//        GeoJSON finalObj = new GeoJSON();
//        finalObj.getLocations();
//        finalObj.add(location);
//        finalObj.update();
//    }

//    private void readFromJson() {
//        Gson gson = new Gson();
//        BufferedReader br = null;
//        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "geo.json");
//
//        try {
//
//            br = new BufferedReader(new FileReader(file));
//            List<Location> myGson = (List<Location>) gson.fromJson(br, List.class);
//
//            String response = gson.toJson(myGson);
//
//            JSONArray jsonArray = new JSONArray(response);
//
//            Log.d("JSONArray", jsonArray.toString());
//
//            for(int i=0; i<jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    private void sendTokenRequest(final String Token, final String userId) {
//        Toast.makeText(this, "Test: " + Token + "userId" + userId, Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, FCMTOKEN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("VOLLEY", response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Volley", error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", userId);
                params.put("token", Token);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }

        };

        MySingleton.getmInstance(getApplicationContext()).addToRequestque(stringRequest);

    }

    private void googleLogin() {
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void decideActivity() {
        DataManager dataManager = ((MvpApp) getApplication()).getDataManager();
        if (dataManager.getLoggedInMode()) {

        } else {
            dataManager.clear();
            startActivity(HomeActivity.getStartIntent(this));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (!drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Fragment fragment = null;
            fragment = new MainFragment();

            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_main, fragment);
                ft.commit();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else

        if (id == R.id.nav_logout) {
//            android.os.Process.killProcess(android.os.Process.myPid());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Warning");
            builder.setMessage("Are you sure, you want to logout?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    Intent myService = new Intent(MainActivity.this, LocationService.class);
                    stopService(myService);
                    signout();
                } });

            builder.setNegativeButton("Cancel",  null);

            builder.show();

        }else {
            displaySelectedScreen(id);
        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);


        return true;
    }

    private void displaySelectedScreen(int id) {


        Fragment fragment = null;


        switch (id) {
            case R.id.nav_map:
                fragment = new MainFragment();
                break;
            case R.id.nav_circle:
                fragment = new Circle();
                break;
            case R.id.nav_friend:
                fragment = new Friend();
                break;

            case R.id.nav_friend_request:
                fragment = new FriendRequest();
                break;

            case R.id.nav_place:
                fragment = new Place();
                break;
            case R.id.nav_account:
                fragment = new com.example.murtaza.bettertracker.ui.profile.Profile();
                break;
            case R.id.nav_settings:
                fragment = new Setting();
                break;
            case R.id.nav_danger:
                fragment = new EmergencyAlert();
                break;
        }

        if (fragment != null && id != lastSelect) {
//            showSpinner();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main, fragment);
            ft.commit();
        }

        lastSelect = id;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    //    returning main activity start intent
    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    public void signout() {
        switch (dataManager.getAccountType()) {

            case "GOOGLE":
                googleSignout();
                break;
            case "FACEBOOK":
                facebookSignout();
                break;
            case "EMAIL":
                userNameOrEmailSignout();
                break;

        }
    }

    public void googleSignout() {

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        DataManager dataManager = ((MvpApp) getApplication()).getDataManager();
                        dataManager.clear();
                        startActivity(HomeActivity.getStartIntent(getApplicationContext()));
                        finish();
                    }
                });
    }

    public void userNameOrEmailSignout() {
        DataManager dataManager = ((MvpApp) getApplication()).getDataManager();
        dataManager.clear();
        startActivity(HomeActivity.getStartIntent(this));
        finish();
    }

    public void facebookSignout() {

        LoginManager.getInstance().logOut();
        Profile profile = Profile.getCurrentProfile().getCurrentProfile();

        if (profile == null) {
            // user has logged in
            DataManager dataManager = ((MvpApp) getApplication()).getDataManager();
            dataManager.clear();
            startActivity(HomeActivity.getStartIntent(getApplicationContext()));
            finish();
        }

    }

    /*
     EVERYTHING RELATED TO LOCATION GOES HERE
     */
// Handler:
//     JSON Request to store location
//    private Runnable runnableCode = new Runnable() {
//        @Override
//        public void run() {
//
//            sendVolleyRequestToServer(); // Volley Request
//
//            // Repeat this the same runnable code block again another 2 seconds
//            handler.postDelayed(runnableCode, 4000);
//        }
//    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MainActivity.this, LocationService.class);
//                    startService(intent);
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                        ContextCompat.startForegroundService(this, intent);
                    } else {
                        getApplicationContext().startService(intent);
                    }
//                    btnLocationSharing.setText("Stop location sharing");

                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void locPermissions() {

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    public static void showSpinner(){

    }

    public static void hideSpinner(){
    }

}




