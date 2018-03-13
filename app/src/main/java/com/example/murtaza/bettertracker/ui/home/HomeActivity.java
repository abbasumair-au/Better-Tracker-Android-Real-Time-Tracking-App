package com.example.murtaza.bettertracker.ui.home;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.murtaza.bettertracker.MvpApp;
import com.example.murtaza.bettertracker.R;
import com.example.murtaza.bettertracker.data.DataManager;
import com.example.murtaza.bettertracker.network.CheckNetwork;
import com.example.murtaza.bettertracker.ui.base.BaseActivity;
import com.example.murtaza.bettertracker.ui.home.username.UserNameActivity;
import com.example.murtaza.bettertracker.ui.login.AccountType;
import com.example.murtaza.bettertracker.ui.login.LoginActivity;
import com.example.murtaza.bettertracker.ui.main.MainActivity;
import com.example.murtaza.bettertracker.ui.register.RegisterActivity;
import com.example.murtaza.bettertracker.utils.CommonUtils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity implements IHomeView {

    @BindView(R.id.logo) ImageView logo;
    private HomePresenter homePresenter;

    @BindView(R.id.facebook)
    Button facebook;

    private CallbackManager callbackManager;


//    google login fields
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "Google";
    @BindView(R.id.google)
    Button google;
    private GoogleSignInClient mGoogleSignInClient;

//    progress dialog
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

//        setting logo:
//        setHomeLogo();
//        getting permissions
        locPermissions();

//        facebook login
        fbLogin();

//        google login
        googleLogin();

//        google signout
//        signOut();

//      setting presenter: need data manager to pass to constructor:
        DataManager dataManager = ((MvpApp) getApplication()).getDataManager();
        homePresenter = new HomePresenter(dataManager, this);
        homePresenter.onAttach(this);

//        setting up progress dialog
        pd = new ProgressDialog(HomeActivity.this);

//       setting progress dialog
        pd.setCancelable(false);
        pd.setMessage("Signing in..");

    }
//    private void signOut() {
//        mGoogleSignInClient.signOut()
//                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        homePresenter.updateUI(null);
//
//
//                    }
//                });
////        homePresenter.getDataManager().clear();
//
//        DataManager dataManager = ((MvpApp) getApplication()).getDataManager();
//        dataManager.clear();
//    }

    public static Intent getStartIntent(Context context) {

        Intent intent = new Intent(context, HomeActivity.class);
        return intent;
    }

//    on login button click from home activity
    @OnClick (R.id.login) void loginClick() {
        homePresenter.openLoginActivity();
    }

//    on register button click from home activity
    @OnClick (R.id.register) void registerClick() {
        homePresenter.openRegisterActivity();
    }


    @Override
    public void openMainActivity() {
        Intent intent = MainActivity.getStartIntent(this);
        startActivity(intent);
        finish();
    }

    @Override
    public void openUserNameActivity( HashMap<String, String> userData ) {
        Intent intent = UserNameActivity.getStartIntent(this);
        intent.putExtra("userData", userData);
        startActivity(intent);
//        finish();
    }

    @Override
    public void onLoginButtonClick() {
        Intent intent = LoginActivity.getStartIntent(this);
        startActivity(intent);
    }

    public void onRegisterButtonClick() {
        Intent intent = RegisterActivity.getStartIntent(this);
        startActivity(intent);
    }

    // this part was missing thanks to wesely
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    @Override
    @OnClick(R.id.facebook) public void onFacebookLoginClick() {

        if(CheckNetwork.isInternetAvailable(HomeActivity.this)) //returns true if internet available
        {
            LoginManager.getInstance().logInWithReadPermissions(
                    this,
                    Arrays.asList("user_photos", "email", "user_birthday", "public_profile")
            );
//            Toast.makeText(this, "In IF", Toast.LENGTH_SHORT).show();

            //do something. loadwebview.
        }
        else
        {
            Toast.makeText(HomeActivity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
    }

    private void fbLogin(){
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
//                                        try {
//                                            URL profile_pic = new URL(
//                                                    "http://graph.facebook.com/" + id + "/picture?type=large");
//                                            Log.i("profile_pic",
//                                                    profile_pic + "");
//
//                                        } catch (MalformedURLException e) {
//                                            e.printStackTrace();
//                                        }

                                        //    facebook login hashmap
                                        HashMap<String, String> userFbData = new HashMap<String, String>();

                                        userFbData.put("_id", "");
                                        userFbData.put("userName", "");
                                        userFbData.put("userFirstName", object.getString("first_name"));
                                        userFbData.put("userLastName", object.getString("last_name"));
                                        userFbData.put("userEmail", object.getString("email"));
                                        userFbData.put("userAccountType", AccountType.FACEBOOK.toString());

                                        homePresenter.FacebookSignInNetwork(userFbData);

//                                        gender = object.getString("gender");
//                      g                  birthday = object.getString("birthday");
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
                        Toast.makeText(HomeActivity.this, "Error", Toast.LENGTH_SHORT).show();
//                        CommonUtils.displayAlert(getApplicationContext(), "Sign in Err...","Unable to sign in with Facebook. Please try again.");
                    }

                    @Override
                    public void onError(FacebookException exception) {
//                        CommonUtils.displayAlert(getApplicationContext(), "Sign in Err...","Unable to sign in with Facebook. Please try again.");
                        Toast.makeText(HomeActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public void setHomeLogo() {
        logo.setImageResource(R.drawable.bettertracker2);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            homePresenter.updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Log.e("GOOGLERR", e.getStackTrace().toString());
            Log.e("GOOGLERR", e.getLocalizedMessage());
            Log.e("GOOGLERR", e.toString());
            homePresenter.updateUI(null);
        }
    }

    private void googleLogin(){
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @OnClick(R.id.google) void signIn(){
        if(CheckNetwork.isInternetAvailable(this)) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }else {
            CommonUtils.displayAlert(this, "Internet Err...","Internet Not Available");
        }
    }


//    showing progress bar
    @Override
    public void showLoadingBar() {
        pd.show();
    }

//    dismissing progress bar
    @Override
    public void hideLoadingBar() {
        pd.dismiss();
    }



    private void locPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//
//                Log.d("permission", "permission denied to SEND_SMS - requesting it");
//                String[] permissions = {Manifest.permission.SEND_SMS};

                ActivityCompat.requestPermissions(HomeActivity.this,new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.SEND_SMS, Manifest.permission.READ_EXTERNAL_STORAGE }, 1);
                }

            }
        }
    }













//    getHashkey();

//    public void getHashkey(){
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//
//                Log.i("Base64", Base64.encodeToString(md.digest(),Base64.NO_WRAP));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            Log.d("Name not found", e.getMessage(), e);
//
//        } catch (NoSuchAlgorithmException e) {
//            Log.d("Error", e.getMessage(), e);
//        }
//    }

