package com.example.murtaza.bettertracker.ui.login;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.murtaza.bettertracker.data.DataManager;
import com.example.murtaza.bettertracker.network.MySingleton;
import com.example.murtaza.bettertracker.ui.base.BasePresenter;
import com.example.murtaza.bettertracker.ui.base.MvpView;
import com.example.murtaza.bettertracker.ui.member.memberclass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by murtaza on 2/6/18.
 */


public class LoginPresenter <V extends ILoginView & MvpView> extends BasePresenter<V> implements ILoginPresenter {



    Context context;
    private final String login_server_url;

    public LoginPresenter(DataManager dataManager, Context context) {
        super(dataManager);
        this.context = context;
        this.login_server_url = "https://geotracker-app.herokuapp.com/api/userverify";
    }

    @Override
    public void validateLoginWithEmailOrUserName(final String userId, final String userPassword) {
        Toast.makeText(context, "UserId: " + userId + "Password: " + userPassword, Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, login_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("VOLLEY", response);
                        getMvpView().hideLoadingBar();

                        try {
                            JSONObject jsonObj = new JSONObject(response);

                            if (jsonObj.getString("status").equals("success")) {


                                String data = jsonObj.getString("data");
                                JSONObject jsonObject2 = new JSONObject(data);

                                memberclass member = new memberclass(jsonObject2.getString("_id"),jsonObject2.getString("userName"),jsonObject2.getString("userFirstName"), jsonObject2.getString("userLastName"), jsonObject2.getString("userEmail"), AccountType.EMAIL.toString(), jsonObject2.getString("userPhoneNumber"));

                                getDataManager().saveMemberData(member);
                                Log.i("userId", getDataManager().getId());
                                getDataManager().setLoggedIn();
                                getMvpView().openMainActivity();
                            } else if (jsonObj.getString("status").equals("fail")) {
                                getMvpView().displayAlert("Login Err...",jsonObj.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.i("Volley", error.toString());
                        getMvpView().hideLoadingBar();
                        getMvpView().displayAlert("Unknown error occurs");
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userName", userId);
                params.put("userPassword", userPassword);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }

        };


        MySingleton.getmInstance(context).addToRequestque(stringRequest);

    }
}
