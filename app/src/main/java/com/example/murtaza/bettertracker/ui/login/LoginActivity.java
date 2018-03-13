package com.example.murtaza.bettertracker.ui.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.murtaza.bettertracker.MvpApp;
import com.example.murtaza.bettertracker.R;
import com.example.murtaza.bettertracker.data.DataManager;
import com.example.murtaza.bettertracker.network.CheckNetwork;
import com.example.murtaza.bettertracker.ui.base.BaseActivity;
import com.example.murtaza.bettertracker.ui.login.forgotpassword.ForgotActivity;
import com.example.murtaza.bettertracker.ui.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//import com.example.murtaza.bettertracker.databinding.ActivityLoginBinding;

public class LoginActivity extends BaseActivity implements ILoginView {

//    ActivityLoginBinding binding;
    private LoginPresenter presenter;

    // Dialog Boxes
    private ProgressDialog pd;
    private AlertDialog.Builder builder;


//    UI Fields

    @BindView(R.id.logo)
    ImageView logo;

    @BindView(R.id.uname)
    EditText username;

    @BindView(R.id.password)
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        enabling butter knife
        ButterKnife.bind(this);

//        setting home logo
//        setHomeLogo();

//        reference to databinding
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

//        controlling progress bar
//        binding.setDisableIt(Boolean.FALSE);

//       reference to datamanager class
        DataManager dataManager = ((MvpApp) getApplication()).getDataManager();

//       setting presenter

        presenter = new LoginPresenter(dataManager, getApplicationContext());
        presenter.onAttach(this);

//        setting up dialog
        pd = new ProgressDialog(LoginActivity.this);
        builder = new AlertDialog.Builder(LoginActivity.this);
//       setting progress dialog
        pd.setCancelable(false);
        pd.setMessage("Signing in..");

    }



    public static Intent getStartIntent(Context context) {

        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }


    @Override
    public void openMainActivity() {
        Intent intent = MainActivity.getStartIntent(this);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.login) void LoginClick(View view){
        onLoginButtonClick(view);
    }

    @Override
    public void onLoginButtonClick(View view) {
        if(CheckNetwork.isInternetAvailable(LoginActivity.this)) {

            View focusedView = getCurrentFocus();
            if(focusedView!=null){
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(view.getWindowToken(),0);
            }
            showLoadingBar();
            String userId = username.getText().toString().toLowerCase();
            String userPassword = password.getText().toString();
            if(checkEmpty(userId, userPassword)){
    //            Toast.makeText(RegisterActivity.this, "Login Success!", Toast.LENGTH_LONG).show();

    //            checking if internet is available
                   presenter.validateLoginWithEmailOrUserName(userId, userPassword);


            }else{
                hideLoadingBar();
            }
        }else {
            displayAlert("Internet not available");
        }
    }

//    checking if userId (can be emailid, phonenumber) and userPassword is empty or not
    @Override
    public boolean checkEmpty(String userId, String userPassword ) {
        builder.setTitle("Something went wrong");
        if(userId == null || userId.isEmpty()) {
            displayAlert("Enter a valid username or email...");
            return false;
        }else if(userPassword == null || userPassword.isEmpty()) {
            displayAlert("Enter a valid password...");
            return false;
        }

        return true;
    }

//    display alert box with custom message
    @Override
    public void displayAlert(String message) {

        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                username.setText("");
                password.setText("");
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

//  display alert with custom title and custom message
    public void displayAlert(String title, String message) {

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                username.setText("");
                password.setText("");
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


//    setting home logo on acivity start
    @Override
    public void setHomeLogo() {
        logo.setImageResource(R.drawable.current_logo);
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

    @OnClick(R.id.forgot) void onClickForgot() {
        Intent intent = ForgotActivity.getStartIntent(this);
        startActivity(intent);
    }

}
