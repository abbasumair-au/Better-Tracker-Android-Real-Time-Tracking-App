package com.example.murtaza.bettertracker.ui.login.forgotpassword;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.murtaza.bettertracker.utils.CommonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by root on 3/2/18.
 */

public class ForgotFragment extends Fragment {

    @BindView(R.id.userEmail)
    EditText userEmail;

    String send_email = "https://geotracker-app.herokuapp.com/api/sendEmail/";

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v =  inflater.inflate(R.layout.fragment_forgot_password, container, false);

        ButterKnife.bind(this, v);

        return v;
    }

    @OnClick(R.id.forgotBtn) void sendEmail(View v) {

    CommonUtils.hideKeyboard(getActivity(), v);

    CommonUtils.setupProgressBar(getActivity(), "Sending you email");

    final String userEmailId = userEmail.getText().toString();

//        Toast.makeText(getActivity(), "Email: " + userEmailId, Toast.LENGTH_SHORT).show();
    if(checkEmpty(userEmail)) {
        if (CheckNetwork.isInternetAvailable(getActivity())) {
            CommonUtils.showLoadingBar();
            JsonObjectRequest jobReq = new JsonObjectRequest(Request.Method.GET, send_email + userEmailId, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            CommonUtils.hideLoadingBar();
//                                Log.e("Tag", String.valueOf(jsonObject));
//                                Log.d("Res", jsonObject.toString());
                            try {
                                if (jsonObject.getString("status").equals("success")) {
                                    CommonUtils.displayAlert(getActivity(), "Email sent! Please check email and enter PIN to set new password");
                                    DataManager dataManager = ((MvpApp) getActivity().getApplication()).getDataManager();
                                    dataManager.saveEmailId(userEmailId);
                                    Fragment fragment = null;
                                    fragment = new FragmentPin();

                                    if(fragment != null) {
                                        FragmentManager fragmentManager = getFragmentManager();

                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.content_forgot_password, fragment);
                                        fragmentTransaction.commit();
                                    }

                                } else if (jsonObject.getString("status").equals("not_found")) {
                                    CommonUtils.displayAlert(getActivity(), "User not found. Please try again.");
                                }else {
                                    CommonUtils.displayAlert(getActivity(), "Unable to send an email. Please try again.");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    CommonUtils.hideLoadingBar();
                    CommonUtils.displayAlert(getActivity(), "UUnable to send an email. Please try again.");
                }

            });

            MySingleton.getmInstance(getApplicationContext()).addToRequestque(jobReq);
        } else {
            Toast.makeText(getApplicationContext(), "Internet Not Available", Toast.LENGTH_LONG).show();
        }
    }else {
        CommonUtils.displayAlert(getActivity(), "Empty Err...", "Please enter a valid username or email");
    }
}

    private boolean checkEmpty(EditText userNameoruserEmail) {
        if(CommonUtils.isEmptyForError(userNameoruserEmail)){
            return false;
        }
        return true;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Forgot Password");
    }
}
