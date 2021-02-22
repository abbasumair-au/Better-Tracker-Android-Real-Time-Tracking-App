package com.example.murtaza.bettertracker.ui.login.forgotpassword;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.murtaza.bettertracker.MvpApp;
import com.example.murtaza.bettertracker.R;
import com.example.murtaza.bettertracker.data.DataManager;
import com.example.murtaza.bettertracker.network.CheckNetwork;
import com.example.murtaza.bettertracker.network.MySingleton;
import com.example.murtaza.bettertracker.ui.login.LoginActivity;
import com.example.murtaza.bettertracker.utils.CommonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetPassword extends AppCompatActivity {


    @BindView(R.id.userNewPassword)
    EditText userNewPassword;

    String set_pass_url = "https://geotracker-app.herokuapp.com/api/setPassword/";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        ButterKnife.bind(this);
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, SetPassword.class);
        return intent;
    }

    @OnClick(R.id.setPassBtn) void setPass(View v) {


        CommonUtils.hideKeyboard(this, v);

        CommonUtils.setupProgressBar(this, "Setting Password");

        final String usernewPassword = userNewPassword.getText().toString();
        DataManager dataManager = ((MvpApp) getApplication()).getDataManager();
        String userEmail = dataManager.getEmailId();
//        Toast.makeText(getActivity(), "Email: " + userEmailId, Toast.LENGTH_SHORT).show();
        if(checkEmpty(userNewPassword)) {
            if (CheckNetwork.isInternetAvailable(this)) {
                CommonUtils.showLoadingBar();
                JsonObjectRequest jobReq = new JsonObjectRequest(Request.Method.GET, set_pass_url + userEmail+ "&" + usernewPassword , null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                CommonUtils.hideLoadingBar();
//                                Log.e("Tag", String.valueOf(jsonObject));
                                Log.d("Res", jsonObject.toString());
                                try {
                                    if (jsonObject.getString("status").equals("success")) {
                                        CommonUtils.displayAlert(SetPassword.this, "Password Changed. Login now.");
                                        Intent intent = LoginActivity.getStartIntent(getApplicationContext());
                                        startActivity(intent);
                                    } else {
                                        CommonUtils.displayAlert(SetPassword.this, "Unable to set password.");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        CommonUtils.hideLoadingBar();
                        CommonUtils.displayAlert(SetPassword.this, "UUnable to send an email. Please try again.");
                    }

                });

                MySingleton.getmInstance(getApplicationContext()).addToRequestque(jobReq);
            } else {
                Toast.makeText(getApplicationContext(), "Internet Not Available", Toast.LENGTH_LONG).show();
            }
        }else {
            CommonUtils.displayAlert(SetPassword.this, "Empty Err...", "Please enter a valid password");
        }

    }
    private boolean checkEmpty(EditText userNewPassword) {
        if(CommonUtils.isEmptyForError(userNewPassword)){
            return false;
        }else if(!CommonUtils.isPasswordValid(userNewPassword.getText().toString())){
            return false;
        }
        return true;
    }
}
