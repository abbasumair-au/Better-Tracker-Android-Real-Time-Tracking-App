package com.example.murtaza.bettertracker.ui.home.username;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.murtaza.bettertracker.MvpApp;
import com.example.murtaza.bettertracker.R;
import com.example.murtaza.bettertracker.data.DataManager;
import com.example.murtaza.bettertracker.ui.base.BaseActivity;
import com.example.murtaza.bettertracker.ui.main.MainActivity;
import com.example.murtaza.bettertracker.utils.CommonUtils;
import com.example.murtaza.bettertracker.utils.MyEditTextDatePicker;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserNameActivity extends BaseActivity implements IUserNameView {

    @BindView(R.id.userName)
    EditText username;

    @BindView(R.id.logo)
    ImageView logo;

    @BindView(R.id.userPhoneNumber)
    EditText number;

    @BindView(R.id.userGender)
    Spinner Gender;
    //    date picker
    MyEditTextDatePicker datePicker;

    private UserNamePresenter userNamePresenter;

//    getting hash from other activity
    private HashMap<String,String>  userHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);
        ButterKnife.bind(this);
//        setHomeLogo();

//        setting presenter
        DataManager dataManager = ((MvpApp) getApplication()).getDataManager();

//
        userNamePresenter = new UserNamePresenter(dataManager, this);
        userNamePresenter.onAttach(this);

        userHash = (HashMap<String, String>) getIntent().getSerializableExtra("userData");

        datePicker = new MyEditTextDatePicker(this, R.id.userBirthday);

        setSpinner();
    }

    private void setSpinner() {
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_choice, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        Gender.setAdapter(adapter);
    }


    @Override
    public void showUserError(){
        username.setError("Username already exists");
//        Toast.makeText(this, "EditText", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showPhoneError(){
        number.setError("Phone number already exists");
//        Toast.makeText(this, "EditText", Toast.LENGTH_SHORT).show();
    }

    public static Intent getStartIntent(Context context){
        Intent intent = new Intent(context, UserNameActivity.class);
        return intent;
    }


    @Override
    public void setHomeLogo() {
        logo.setImageResource(R.drawable.current_logo);
    }

    @Override
    public void openMainActivity() {
        Intent intent = MainActivity.getStartIntent(this);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        if(userHash.get("userAccountType").toString() == AccountType.GOOGLE.toString()){
//            Log.d("UserAccountType", userHash.get("userAccountType").toString());
//            new MainActivity().googleSignout();
//        }else if(userHash.get("userAccountType").toString() == AccountType.FACEBOOK.toString()){
//            new MainActivity().facebookSignout();
//        }
    }

    @OnClick(R.id.register) void checkUser(View v)  {
        if(CommonUtils.isEmptyForError(username)) {
            CommonUtils.displayAlert(this, "Name err...", "Username can't be empty");
        }else if(CommonUtils.isEmptyForError(number)){
            CommonUtils.displayAlert(this, "Phone number err...", "Phone number can't be empty");
        }else if(CommonUtils.isEmpty(datePicker._editText.getText().toString())) {
            CommonUtils.displayAlert(this,"Birth date err...","Enter a valid birth date...");
        }
        else if(!CommonUtils.isPhoneNumberValid(number.getText().toString())){
            CommonUtils.displayAlert(this, "Phone err..", "Enter number in correct format i.e. +923xxxxxxxxx");
        }else{
            CommonUtils.hideKeyboard(this, v);
            userNamePresenter.setUserName(username.getText().toString(), number.getText().toString(),datePicker._editText.getText().toString(),Gender.getSelectedItem().toString(), userHash);
        }
    }
}
