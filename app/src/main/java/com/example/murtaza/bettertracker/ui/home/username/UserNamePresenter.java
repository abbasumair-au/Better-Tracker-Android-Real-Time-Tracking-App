package com.example.murtaza.bettertracker.ui.home.username;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.murtaza.bettertracker.data.DataManager;
import com.example.murtaza.bettertracker.network.MySingleton;
import com.example.murtaza.bettertracker.ui.base.BasePresenter;
import com.example.murtaza.bettertracker.ui.base.MvpView;
import com.example.murtaza.bettertracker.ui.member.memberclass;
import com.example.murtaza.bettertracker.utils.CommonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by murtaza on 2/9/18.
 */

public class UserNamePresenter<V extends IUserNameView & MvpView> extends BasePresenter<V> implements IUserNamePresenter {
    private Context context;
    private String user_check_url;

    public UserNamePresenter(DataManager dataManager, Context context) {
        super(dataManager);
        this.context = context;
        this.user_check_url = "https://geotracker-app.herokuapp.com/api/usercheck/";
    }

    @Override
    public void setUserName(final String userName, final  String number, String date, String gender, final HashMap<String, String> userData) {
//        Toast.makeText(context, userName + "Hash:" + userData.toString(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(context, user_check_url, Toast.LENGTH_SHORT).show();
        CommonUtils.setupProgressBar(context, "Signing in..");

        // JSON OBJECT REQUEST
        JSONObject jsonBody = null;
        try {

            jsonBody = new JSONObject(
                    "{\"userName\":"+ CommonUtils.stripSpaces(userName.toString())+",\"userFirstName\":"
                    +CommonUtils.stripSpaces(userData.get("userFirstName").toString())+",\"userLastName\":"
                    +CommonUtils.stripSpaces(userData.get("userLastName").toString() )+",\"userEmail\":"+
                    userData.get("userEmail").toString() +",\"userAccountType\":" + userData.get("userAccountType").toString()
                    +",\"userPhoneNumber\":" + number +
                    ",\"userBirthDate\":"+date+",\"userGender\":" + gender + ",\"userCircle\":{} }"
            );
//            Toast.makeText(context, "JSON Object: " + jsonBody.toString(), Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        CommonUtils.showLoadingBar();

        JsonObjectRequest jobReq = new JsonObjectRequest(Request.Method.POST, user_check_url, jsonBody,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        CommonUtils.hideLoadingBar();
                        Log.e("Tag", String.valueOf(jsonObject));
                        try {

                            switch(jsonObject.getString("status")){
                                case "user_exists":
                                    getMvpView().showUserError();
                                    break;
                                case  "phone_exists":
                                    getMvpView().showPhoneError();
                                    break;
                                case "unable_to_create":
                                    CommonUtils.displayAlert(context, "Sign in Err...", "Unable to sign in. Please try again.");
                                    break;
                                case "success":
                                    String data = jsonObject.getString("data");
                                    JSONObject jsonObject2 = new JSONObject(data);
                                    memberclass member = new memberclass(jsonObject2.getString("_id"), jsonObject2.getString("userName"), jsonObject2.getString("userFirstName"), jsonObject2.getString("userLastName"), jsonObject2.getString("userEmail"), jsonObject2.getString("userAccountType"),jsonObject2.getString("userPhoneNumber"));
                                    getDataManager().saveMemberData(member);
                                    getDataManager().setLoggedIn();
                                    getMvpView().openMainActivity();
                                    break;
                                default:
                                    CommonUtils.displayAlert(context, "Sign in Err...", "Unable to sign in. Please try again.");
                                }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            CommonUtils.hideLoadingBar();
                            CommonUtils.displayAlert(context, "Sign in Err...", "Unable to sign in. Please try again.");
                            error.printStackTrace();

                        }
                        
                });


        MySingleton.getmInstance(context).addToRequestque(jobReq);

    }

//    @Override
//    public void setUserName(final String userName, final HashMap<String, String> userData) {
//        Toast.makeText(context, userName + "Hash:" + userData.toString(), Toast.LENGTH_SHORT).show();
////        Toast.makeText(context, user_check_url, Toast.LENGTH_SHORT).show();
//        final StringRequest registerRequest = new StringRequest(Request.Method.POST, user_check_url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.i("VOLLEY", response);
////                        getMvpView().hideLoadingBar();
//
//                        JSONObject jsonObj = null;
//                        try {
//
//                            jsonObj = new JSONObject(response);
//                            String data = jsonObj.getString("data");
//                            JSONObject jsonObject2 = new JSONObject(data);
//
//                            if (jsonObj.getString("status").equals("success")) {
//                                if(jsonObj.getString("message").equals("user_created")) {
//
//                                    memberclass member = new memberclass(jsonObject2.getString("_id"),jsonObject2.getString("userName"),jsonObject2.getString("userFirstName"), jsonObject2.getString("userLastName"), jsonObject2.getString("userEmail"), AccountType.GOOGLE.toString());
//                                    getDataManager().saveMemberData(member);
//                                    getDataManager().setLoggedIn();
//                                    getMvpView().openMainActivity();
//
//                                }
////                            getMvpView().openLoginActivity();
//                            }else if(jsonObj.getString("status").equals("fail")){
//                                if(jsonObj.getString("message").equals("already_exists_user")) {
//                                    getMvpView().showError();
//                                }else if(jsonObj.getString("message").equals("unable_to_create")){
//                                    CommonUtils.displayAlert(context, "Sign in Err...","Unable to sign in. Please try again.");
//                                }
//
//                            }else {
//                                CommonUtils.displayAlert(context, "Sign in Err...","Unable to sign in. Please try again.");
//                            }
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
////                        getMvpView().hideLoadingBar();
////                        getMvpView().displayAlert("Unknown error occurs");
//                    }
//                }) {
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("userName", userName.toString());
//                params.put("userFirstName", userData.get("userFirstName").toString());
//                params.put("userLastName", userData.get("userLastName").toString());
//                params.put("userEmail", userData.get("userEmail").toString());
//                params.put("userAccountType", userData.get("userAccountType").toString());
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/x-www-form-urlencoded");
//                return params;
//            }
//        };
//
//        MySingleton.getmInstance(context).addToRequestque(registerRequest);
//
//    }
}
