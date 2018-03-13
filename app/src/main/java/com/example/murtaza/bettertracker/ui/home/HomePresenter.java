package com.example.murtaza.bettertracker.ui.home;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.murtaza.bettertracker.data.DataManager;
import com.example.murtaza.bettertracker.network.MySingleton;
import com.example.murtaza.bettertracker.ui.base.BasePresenter;
import com.example.murtaza.bettertracker.ui.base.MvpView;
import com.example.murtaza.bettertracker.ui.login.AccountType;
import com.example.murtaza.bettertracker.ui.member.memberclass;
import com.example.murtaza.bettertracker.utils.CommonUtils;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by murtaza on 2/6/18.
 */

public class HomePresenter<V extends IHomeView & MvpView> extends BasePresenter<V> implements IHomePresenter {

    Context context;
    String social_verify_url = "https://geotracker-app.herokuapp.com/api/userverifysocial";
    public HomePresenter(DataManager dataManager, Context context) {
        super(dataManager);
        this.context = context;
    }

    @Override
    public void openLoginActivity() {
        getMvpView().onLoginButtonClick();
    }

    @Override
    public void openRegisterActivity() {
        getMvpView().onRegisterButtonClick();
    }

    @Override
    public void loginThroughFacebook() {
    }
    public void updateUI(GoogleSignInAccount account) {
        if(account != null) {
            getMvpView().showLoadingBar();
            GoogleSignInNetwork(account);
        }else {
            CommonUtils.displayAlert(context, "Google Err...", "Error signing in");
        }
    }


    private void GoogleSignInNetwork(final GoogleSignInAccount account) {
//        String[] ArrayToDisplay = {account.getGivenName(), account.getFamilyName(),account.getEmail()};
//            Toast.makeText(context, Arrays.toString(ArrayToDisplay), Toast.LENGTH_LONG).show();
        StringRequest registerRequest = new StringRequest(Request.Method.POST, social_verify_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("VOLLEY", response);
                        getMvpView().hideLoadingBar();
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            String data = jsonObj.getString("data");
                            JSONObject jsonObject2 = new JSONObject(data);
                            if (jsonObj.getString("status").equals("success")) {
                                if(jsonObj.getString("message").equals("user_already_in")) {

                                        memberclass member = new memberclass(jsonObject2.getString("_id"),jsonObject2.getString("userName"),jsonObject2.getString("userFirstName"), jsonObject2.getString("userLastName"), jsonObject2.getString("userEmail"), AccountType.GOOGLE.toString(), jsonObject2.getString("userPhoneNumber"));
                                        getDataManager().saveMemberData(member);
                                        getDataManager().setLoggedIn();
                                        getMvpView().openMainActivity();
//                                    String[] ArrayToDisplay2 = {jsonObject2.getString("_id"), jsonObject2.getString("userFirstName"), jsonObject2.getString("userLastName"), AccountType.GOOGLE.toString()};
//                                    Toast.makeText(context, Arrays.toString(ArrayToDisplay2), Toast.LENGTH_LONG).show();
//                                    memberclass member = new memberclass(jsonObject2.getString("_id"),jsonObject2.getString("userName"),jsonObject2.getString("userFirstName"), jsonObject2.getString("userLastName"), LoginPresenter.AccountType.EMAIL.toString());
                                }else if(jsonObj.getString("message").equals("new_user")){
//                                    memberclass member = new memberclass(jsonObject2.getString("_id"),jsonObject2.getString("userName"),jsonObject2.getString("userFirstName"), jsonObject2.getString("userLastName"), AccountType.GOOGLE.toString());
//                                    getDataManager().saveMemberData(member);

                                    HashMap<String, String> userData = new HashMap<String, String>();
                                    userData.put("_id", "");
                                    userData.put("userName", "");
                                    userData.put("userFirstName", account.getGivenName());
                                    userData.put("userLastName", account.getFamilyName());
                                    userData.put("userEmail", account.getEmail());
                                    userData.put("userAccountType", AccountType.GOOGLE.toString());

                                    getMvpView().openUserNameActivity(userData);

                                }
//                            getMvpView().openLoginActivity();
                            }else if(jsonObj.getString("status").equals("fail")){
                                if(jsonObj.getString("message").equals("user_already_exist"))
                                    CommonUtils.displayAlert(context, "Sign in Err...","User already exists. You may be Signup with Facebook or another Account");
                            }else{
                                CommonUtils.displayAlert(context, "Sign in Err...","Unable to sign in. Please try again.");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        getMvpView().hideLoadingBar();
                        CommonUtils.displayAlert(context, "Unknown error occurs");
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userName", "");
                params.put("userFirstName", account.getGivenName());
                params.put("userLastName", account.getFamilyName());
                params.put("userEmail", account.getEmail());
                params.put("userAccountType", AccountType.GOOGLE.toString());


                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };


        MySingleton.getmInstance(context).addToRequestque(registerRequest);
    }


    public void FacebookSignInNetwork(final HashMap<String, String> userFbData) {

//        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();

        StringRequest registerRequest = new StringRequest(Request.Method.POST, social_verify_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("VOLLEYFB", response);
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            String data = jsonObj.getString("data");
                            JSONObject jsonObject2 = new JSONObject(data);
                            if (jsonObj.getString("status").equals("success")) {
                                if(jsonObj.getString("message").equals("user_already_in")) {

                                    Log.d("Already", jsonObj.toString());

                                    memberclass member = new memberclass(jsonObject2.getString("_id"),jsonObject2.getString("userName"),jsonObject2.getString("userFirstName"), jsonObject2.getString("userLastName"), jsonObject2.getString("userEmail"), AccountType.FACEBOOK.toString(),jsonObject2.getString("userPhoneNumber"));
                                    getDataManager().saveMemberData(member);
                                    getDataManager().setLoggedIn();
                                    getMvpView().openMainActivity();

                                }else if(jsonObj.getString("message").equals("new_user")){
                                    getMvpView().openUserNameActivity(userFbData);
                                }
//                            getMvpView().openLoginActivity();
                            }else if(jsonObj.getString("status").equals("fail")) {
                                if(jsonObj.getString("message").equals("user_already_exist"))
                                CommonUtils.displayAlert(context, "Sign in Err...","CircleSchema already exists. You may be Signup with Facebook or another Account");
                            }else {
                                CommonUtils.displayAlert(context, "Sign in Err...","Unable to sign in. Please try again.");

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
                        CommonUtils.displayAlert(context,"Unknown error occurs");
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("userName", name);
                params.put("userFirstName", userFbData.get("userFirstName"));
                params.put("userLastName", userFbData.get("userLastName"));
                params.put("userEmail", userFbData.get("userEmail"));
                params.put("userAccountType", AccountType.FACEBOOK.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        MySingleton.getmInstance(context).addToRequestque(registerRequest);
    }

}





