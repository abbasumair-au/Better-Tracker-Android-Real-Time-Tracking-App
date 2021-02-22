package com.example.murtaza.bettertracker.ui.register;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by murtaza on 2/6/18.
 */

public class RegisterPresenter<V extends IRegisterView & MvpView> extends BasePresenter<V> implements IRegisterPresenter {

    private enum AccountType {
        EMAIL, PHONE, FACEBOOK, GOOGLE
    }

    Context context;
    private final String register_server_url;
    private final String profileUpload = "https://geotracker-app.herokuapp.com/api/upload";

    public RegisterPresenter(DataManager dataManager, Context context) {
        super(dataManager);
        this.context = context;
        this.register_server_url = "https://geotracker-app.herokuapp.com/api/users";
    }

    @Override
    public void validateLoginWithEmailOrID(final String name, final String firstName, final String lastName, final String password, final String email, final String accountType, final String profileImgPath, final String number, final String gender, final String date) {

        JSONObject jsonBody = null;
        Log.d("RegisterData", "Gender:" + gender + " Date:" + date);

        try {
            jsonBody = new JSONObject("{\"userName\":"+CommonUtils.stripSpaces(name)+",\"userFirstName\":"+CommonUtils.stripSpaces(firstName.toString())+",\"userLastName\":"+CommonUtils.stripSpaces(lastName)+",\"userEmail\":"+email +",\"userPassword\":" + password + ",\"userAccountType\":" + AccountType.EMAIL + ",\"userPhoneNumber\":"+ number + ",\"userProfilePicture\":'',\"userBirthDate\":"+date+",\"userGender\":" + gender + ",\"userCircle\":{} }");
            Log.d("Json", jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jobReq = new JsonObjectRequest(Request.Method.POST, register_server_url, jsonBody,
                new Response.Listener<JSONObject>() { @Override
                public void onResponse(JSONObject jsonObject) {

                    getMvpView().hideLoadingBar();
                    Log.e("Tag", String.valueOf(jsonObject));
                    try {

                            if (jsonObject.getString("status").equals("success")) {
                            if (jsonObject.getString("message").equals("user_created")) {
                                String data = jsonObject.getString("data");

                                JSONObject jsonObject2 = new JSONObject(data);
                                memberclass member = new memberclass(jsonObject2.getString("_id"), jsonObject2.getString("userName"), jsonObject2.getString("userFirstName"), jsonObject2.getString("userLastName"), jsonObject2.getString("userEmail"), jsonObject2.getString("userAccountType"), jsonObject2.getString("userPhoneNumber"));
                                getDataManager().saveMemberData(member);
                                getDataManager().setLoggedIn();
//                                if(profileImgPath != null || !profileImgPath.isEmpty())
//                                SavePic(getDataManager().getId(), profileImgPath);
                                getMvpView().openMainActivity();
                            }
//                            getMvpView().openLoginActivity();
                        } else if (jsonObject.getString("status").equals("fail")) {
                            if (jsonObject.getString("message").equals("already_exists_user")) {
                                getMvpView().displayAlert("Signup Err...", "Username, Email or Phone number already exists");
                            } else if (jsonObject.getString("message").equals("unable_to_create")) {
                                getMvpView().displayAlert("Signup Err...", "Registration failed!");
                            }

                        } else {
                            CommonUtils.displayAlert(context, "Sign in Err...", "Unable to sign in. Please try again.");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                getMvpView().hideLoadingBar();
                getMvpView().displayAlert("Unknown error occurs");
            }


        });

//
//
//        StringRequest registerRequest = new StringRequest(Request.Method.POST, register_server_url,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            Log.i("VOLLEY", response);
//                            getMvpView().hideLoadingBar();
//                            if (response.equals("Success")) {
//                                getMvpView().openLoginActivity();
//                            } else if (response.equals("already_exists_user")) {
//    //                                        Toast.makeText(getApplicationContext(), "Ops!" + response, Toast.LENGTH_LONG).show();
//                                getMvpView().displayAlert("Signup Err...", "Username already exists");
//                            } else if(response.equals("already_exists_email")){
//                                getMvpView().displayAlert("Signup Err...", "Email already exists");
//                            } if (response.equals("unable_to_create")) {
//                                getMvpView().displayAlert("Signup Err...", "Registration failed!");
//                            }
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            getMvpView().hideLoadingBar();
//                            getMvpView().displayAlert("Unknown error occurs");
//                        }
//                    }) {
//
//            };

            MySingleton.getmInstance(context).addToRequestque(jobReq);
        }

        private void SavePic(String id, String filePath) {
            Toast.makeText(context, "ID: " + id + "PATH: " + filePath, Toast.LENGTH_SHORT).show();
            Ion.with(context)
                    .load(profileUpload)
                    .setMultipartParameter("id", id)
                    .setMultipartFile("image", "image/jpeg", new File(filePath))
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {

                            if(!result.isJsonNull()){
                                Log.d("MyResult:", result.toString());
                                getDataManager().saveProfilePIc(result.get("path").toString());
                            }
//                            Log.d("Res: " , result.toString());
//                            Log.e("Err: ", e.toString());
                        }
                    });
        }
    }
