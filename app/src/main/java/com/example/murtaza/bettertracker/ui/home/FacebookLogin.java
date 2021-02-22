package com.example.murtaza.bettertracker.ui.home;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.murtaza.bettertracker.R;
import com.example.murtaza.bettertracker.network.CheckNetwork;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FacebookLogin extends AppCompatActivity {

    @BindView(R.id.fb)
    Button fb;
    private CallbackManager callbackManager;

    String id;
    String name;
    String email;
    String gender;
    String birthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);
        ButterKnife.bind(this);

        // Some code
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(
                callbackManager,
                new FacebookCallback< LoginResult >() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // Handle success
                        String accessToken = loginResult.getAccessToken()
                                .getToken();
                        Log.i("accessToken", accessToken);

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {@Override
                                public void onCompleted(JSONObject object,
                                                        GraphResponse response) {

                                    Log.i("RegisterActivity",
                                            response.toString());
                                    try {
                                        id = object.getString("id");
                                        try {
                                            URL profile_pic = new URL(
                                                    "http://graph.facebook.com/" + id + "/picture?type=large");
                                            Log.i("profile_pic",
                                                    profile_pic + "");

                                        } catch (MalformedURLException e) {
                                            e.printStackTrace();
                                        }
                                        name = object.getString("name");
                                        email = object.getString("email");
                                        gender = object.getString("gender");
                                        birthday = object.getString("birthday");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields",
                                "id,first_name,last_name,name,email,gender, birthday");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {
                    }
                }
        );

    }

    public void onClick(View v) {
        if (v == fb) {

            if(CheckNetwork.isInternetAvailable(FacebookLogin.this)) //returns true if internet available
            {
                LoginManager.getInstance().logInWithReadPermissions(
                        this,
                        Arrays.asList("user_photos", "email", "user_birthday", "public_profile")
                );
                Toast.makeText(this, "In IF", Toast.LENGTH_SHORT).show();

                //do something. loadwebview.
            }
            else
            {
                Toast.makeText(FacebookLogin.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
            }

        }
//        Toast.makeText(this, "Outside IF", Toast.LENGTH_SHORT).show();
//        Log.i("FB", v.toString());
    }

    // this part was missing thanks to wesely
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }


}
