package com.example.murtaza.bettertracker.ui.settings.updateaccount;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.murtaza.bettertracker.MvpApp;
import com.example.murtaza.bettertracker.R;
import com.example.murtaza.bettertracker.data.DataManager;
import com.example.murtaza.bettertracker.network.CheckNetwork;
import com.example.murtaza.bettertracker.network.MySingleton;
import com.example.murtaza.bettertracker.ui.main.MainActivity;
import com.example.murtaza.bettertracker.utils.CommonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class updateprofile extends AppCompatActivity {

    @BindView(R.id.userNameUpdate)
    TextView username;

    @BindView(R.id.userFirstNameUpdate)
    EditText firstname;

    @BindView(R.id.userLastNameUpdate)
    EditText lastname;

    @BindView(R.id.userEmailUpdate)
    TextView email;

    //    progress dialog
    private ProgressDialog pd;
    private AlertDialog.Builder builder;
    private String update_profile_url = "https://geotracker-app.herokuapp.com/api/updateprofile";

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateprofile);

        ButterKnife.bind(this);

//        mDrawerLayout = findViewById(R.id.drawer_layout);

//        mDrawerToggle = new ActionBarDrawerToggle(
//                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//

        DataManager dataManager = ((MvpApp) getApplication()).getDataManager();

        username.setText(dataManager.getName());
        firstname.setText(dataManager.getFirstName());
        lastname.setText(dataManager.getLastName());
        email.setText(dataManager.getEmailId());

//        setting up dialog
        pd = new ProgressDialog(updateprofile.this);
        builder = new AlertDialog.Builder(updateprofile.this);

//       setting progress dialog
        pd.setCancelable(false);
        pd.setMessage("Updating Profile...");

        setTitle("Update Profile");

    }

    @OnClick(R.id.updateProfile) void updateProfile(View view) {
        if (CheckNetwork.isInternetAvailable(this)) {
            View focusedView = getCurrentFocus();
            if(focusedView!=null){
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(view.getWindowToken(),0);
            }
            showLoadingBar();
            String Firstname = firstname.getText().toString();
            String Lastname = lastname.getText().toString();
            DataManager dataManager = ((MvpApp) getApplication()).getDataManager();
            String id = dataManager.getId();
        if(checkEmpty(Firstname, Lastname)){

            updateProfile(Firstname, Lastname, id, dataManager);

        }else {
            hideLoadingBar();
        }

        }
    }

    private void updateProfile(final String firstname, final String lastname, final String id, final DataManager dataManager) {

        StringRequest registerRequest = new StringRequest(Request.Method.POST, update_profile_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("VOLLEY", response);
                        hideLoadingBar();
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            if (jsonObj.getString("status").equals("success")) {
                                dataManager.saveFirstName(jsonObj.getString("userFirstName"));
                                dataManager.saveLastName(jsonObj.getString("userLastName"));
                                displayAlert("Success","Profile Updated Successfully");
                            }else if(jsonObj.getString("status").equals("fail")) {
                                displayAlert( "Profile Update Err...","Unable to update profile.");
                            }else {
                                displayAlert("Profile Update Err...","Unable to update profile.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        getMvpView().hideLoadingBar();
                        displayAlert("Unable to update profile. Please try again.");
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("userFirstName", firstname);
                params.put("userLastName", lastname);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        MySingleton.getmInstance(this).addToRequestque(registerRequest);
    }

    public boolean checkEmpty(String firstName, String lastName) {

        builder.setTitle("Something went wrong");
        if(CommonUtils.isEmpty(firstName)){
            displayAlert("Enter a valid first name...");
            return false;
        }else if(CommonUtils.isEmpty(lastName)) {
            displayAlert("Enter a valid last name...");
            return false;
        }
        return true;
    }

    public void displayAlert(String message) {

        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                userName.setText("");
//                userPassword.setText("");
//                userFirstName.setText("");
//                userLastName.setText("");
//                userEmail.setText("");
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void displayAlert(String title, String message) {
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                userName.setText("");
//                userPassword.setText("");
//                userFirstName.setText("");
//                userLastName.setText("");
//                userEmail.setText("");
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
    
    public void showLoadingBar() {
        pd.show();
    }

    public void hideLoadingBar() {
        pd.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            Intent intent = MainActivity.getStartIntent(this);
            startActivity(intent);
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

}
