package com.example.murtaza.bettertracker.ui.register;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.murtaza.bettertracker.MvpApp;
import com.example.murtaza.bettertracker.R;
import com.example.murtaza.bettertracker.data.DataManager;
import com.example.murtaza.bettertracker.network.CheckNetwork;
import com.example.murtaza.bettertracker.ui.FetchPath;
import com.example.murtaza.bettertracker.ui.base.BaseActivity;
import com.example.murtaza.bettertracker.ui.login.AccountType;
import com.example.murtaza.bettertracker.ui.login.LoginActivity;
import com.example.murtaza.bettertracker.ui.main.MainActivity;
import com.example.murtaza.bettertracker.utils.CommonUtils;
import com.example.murtaza.bettertracker.utils.MyEditTextDatePicker;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;

//import com.example.murtaza.bettertracker.databinding.ActivityLoginBinding;

public class RegisterActivity extends BaseActivity implements IRegisterView {

    private static final int IMG_REQUEST =  1;
    //    ActivityLoginBinding binding;
    private RegisterPresenter presenter;

//    Dialog Boxes
    private ProgressDialog pd;
    private AlertDialog.Builder builder;

//    UI Fields

    @BindView(R.id.profile)
    ImageView profile;

    @BindView(R.id.userName)
    EditText userName;

    @BindView(R.id.userPassword)
    EditText userPassword;

    @BindView(R.id.userEmail)
    EditText userEmail;

    @BindView(R.id.userFirstName)
    EditText userFirstName;

    @BindView(R.id.userLastName)
    EditText userLastName;

    @BindView(R.id.register)
    Button register;

    @BindView(R.id.userPhoneNumber)
    EditText phonenumber;

    @BindView(R.id.userGender)
    Spinner Gender;
//    date picker
    MyEditTextDatePicker datePicker;



//      flag for validation error
    private boolean valid = false;
    private Bitmap bitmap;
    private String profileImgPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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

        presenter = new RegisterPresenter(dataManager, getApplicationContext());
        presenter.onAttach(this);

//        setting up dialog
        pd = new ProgressDialog(RegisterActivity.this);
        builder = new AlertDialog.Builder(RegisterActivity.this);

//       setting progress dialog
        pd.setCancelable(false);
        pd.setMessage("Signing in..");

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

    public static Intent getStartIntent(Context context) {

        Intent intent = new Intent(context, RegisterActivity.class);
        return intent;
    }

    @OnClick(R.id.profile) void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData();
            profileImgPath = FetchPath.getPath(this, path);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                profile.setImageBitmap(getCroppedBitmap(bitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    @Override
    public void openMainActivity() {
        Intent intent = MainActivity.getStartIntent(this);
        startActivity(intent);
        finish();
    }

    @Override
    public void openLoginActivity() {
        Intent intent = LoginActivity.getStartIntent(this);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.register) void RegisterClick(View view){

        onRegisterButtonClick(view);
    }

    @Override
    public void onRegisterButtonClick(View view) {
        if (CheckNetwork.isInternetAvailable(this)) {
            View focusedView = getCurrentFocus();
        if(focusedView!=null){
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
         showLoadingBar();

            String name = userName.getText().toString().trim();
            String firstName = userFirstName.getText().toString().trim();
            String lastName = userLastName.getText().toString().trim();
            String password = userPassword.getText().toString().trim();
            String email = userEmail.getText().toString().trim().toLowerCase();
            String number = phonenumber.getText().toString().trim();
            String gender = Gender.getSelectedItem().toString().trim();
            String date = datePicker._editText.getText().toString().trim();

            Log.d("DatePicker", date);

        if(checkEmpty(name,firstName, lastName, password, email, number, gender, date)){
//            Toast.makeText(RegisterActivity.this, "Login Success!", Toast.LENGTH_LONG).show();
//            Toast.makeText(this, "TestToast", Toast.LENGTH_SHORT).show();
              presenter.validateLoginWithEmailOrID(name, firstName, lastName, password, email, AccountType.EMAIL.toString(), profileImgPath, number, gender, date);

        }else{
            hideLoadingBar();
        }
        }else {
            Toast.makeText(this, "No Internet Available", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean checkEmpty(String name, String firstName, String lastName, String password, String email, String phonenumber, String gender, String date){
        builder.setTitle("Something went wrong");
        if(CommonUtils.isEmpty(name)) {
            displayAlert("Enter a valid username...");
            return false;
        }else if(!CommonUtils.isUserNameValid(userName.getText().toString())){
            displayAlert("Username should contain at least 4 characters");
            return false;
        }else if(CommonUtils.isEmpty(phonenumber)) {
            displayAlert("Please enter correct phone number");
            return false;
        }else if(CommonUtils.isEmpty(firstName)){
            displayAlert("Enter a valid first name...");
            return false;
        }else if(CommonUtils.isEmpty(lastName)) {
            displayAlert("Enter a valid last name...");
            return false;
        }else if(CommonUtils.isEmpty(email)) {
            displayAlert("Enter a valid email...");
            return false;
        }else if(CommonUtils.isEmpty(date)) {
            displayAlert("Enter a valid birth date...");
            return false;
        }else if(CommonUtils.isEmpty(password)) {
            displayAlert("Enter a valid password...");
            return false;
        }else if(!CommonUtils.isPasswordValid(userPassword.getText().toString())){
            displayAlert("Password must be at least 4 characters, no more than 8 characters, and must include at least one upper case letter," +
                    " one lower case letter, and one numeric digit");
            return false;
        }else if(CommonUtils.isEmpty(gender)) {
            displayAlert("Enter a valid gender...");
            return false;
        }else if(!CommonUtils.isEmailValid(userEmail.getText().toString())) {
            displayAlert("Please follow email pattern");
            return false;
        }else if(!CommonUtils.isPhoneNumberValid(phonenumber)){
            displayAlert("Please write correct phonenumber");
            return false;
        }

        return true;
    }

    @Override
    public void displayAlert(String message) {

        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                userName.setText("");
//                userPassword.setText("");
//                userFirstName.setText("");
//                userLastName.setText("");
//                userEmail.setText("");
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void displayAlert(String title, String message) {

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void setHomeLogo() {
        //logo.setImageResource(R.drawable.current_logo);
    }

    @Override
    public void showLoadingBar() {
        pd.show();
    }

    @Override
    public void hideLoadingBar() {
        pd.dismiss();
    }

    @OnFocusChange(R.id.userName) void seeFocusChangeUserName(View v, boolean hasFocus) {
        if(!hasFocus) {
            if(CommonUtils.isEmptyForError(userName)) {
                userName.setError("Username can't be empty");
            }else if(!CommonUtils.isUserNameValid(userName.getText().toString())){
                userName.setError("Username should contain at least 4 characters");
            }
        }
    }

    @OnFocusChange(R.id.userFirstName) void seeFocusChangeFirstName(View v, boolean hasFocus) {
        if(!hasFocus) {
            if(CommonUtils.isEmptyForError(userFirstName)){
                userFirstName.setError("First name can't be empty");
            }else if(!CommonUtils.isFirstNameValid(userFirstName.getText().toString())){
                userFirstName.setError("Username should contain at least 4 characters");
            }
        }
    }

    @OnFocusChange(R.id.userLastName) void seeFocusChangeLastName(View v, boolean hasFocus) {
        if(!hasFocus) {
            if(CommonUtils.isEmptyForError(userLastName)) {
                userLastName.setError("Last name can't be empty");
            }else if(!CommonUtils.isLastNameValid(userName.getText().toString())){
                userLastName.setError("Username should contain at least 4 characters");
            }
        }
    }

    @OnFocusChange(R.id.userPassword) void seeFocusChangeUserPassword(View v, boolean hasFocus) {
        if(!hasFocus) {
            if(CommonUtils.isEmptyForError(userPassword)) {
                userPassword.setError("Password can't be empty");
            }else if(!CommonUtils.isPasswordValid(userPassword.getText().toString())){
                userPassword.setError("Password must be at least 4 characters, no more than 8 characters, and must include at least one upper case letter," +
                        " one lower case letter, and one numeric digit");
            }
        }
    }

    @OnFocusChange(R.id.userEmail) void seeFocusChangeUserEmail(View v, boolean hasFocus) {
        if(!hasFocus) {
            if(CommonUtils.isEmptyForError(userEmail)) {
                userEmail.setError("Email can't be empty");
            }else if(!CommonUtils.isEmailValid(userEmail.getText().toString())) {
                Toast.makeText(this, "In Else If", Toast.LENGTH_SHORT).show();
                userEmail.setError("Please follow email pattern");
            }
        }
    }

    @OnFocusChange(R.id.userPhoneNumber) void seeFocusChangeUserPhoneNumber(View v, boolean hasFocus) {
        if(!hasFocus) {
            if(CommonUtils.isEmptyForError(phonenumber)) {
                phonenumber.setError("Phone can't be empty");
            }else if(!CommonUtils.isPhoneNumberValid(phonenumber.getText().toString())) {
//                Toast.makeText(this, "In Else If", Toast.LENGTH_SHORT).show();
                phonenumber.setError("Please follow phone pattern i.e. +923xx-xxxxxxx");
            }
        }
    }
    @OnFocusChange(R.id.userBirthday) void seeFocusChangeUserBirthday(View v, boolean hasFocus) {
        if(!hasFocus) {
            if(CommonUtils.isEmptyForError(datePicker._editText)) {
                phonenumber.setError("Birth date can't be empty");
            }
        }
    }






    /**
     * Will do it once I have time. We are checking onFocus and onBlur here
     * @param hasFocus
     * @return bool
     */

//    @OnFocusChange(R.id.userName) void seeFocusChange(View v, boolean hasFocus) {
//        if(!hasFocus) {
//            Toast.makeText(this, "in focus", Toast.LENGTH_LONG).show();
//        }else{
////            Toast.makeText(this, "out focus", Toast.LENGTH_LONG).show();
//        }
//        View.OnFocusChangeListener onfocuschange = new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                switch (view.getId()) {
//                    case R.id.userName:
//                        if(!hasFocus){
////            if(CommonUtils.isEmptyForError(userName)) {
//                            userName.setError("Username can't be empty");
////            }
//                        }else {
//                            userName.setError("Username can't be empty");
//                        }
//                        break;
//
//                }
//            }
//        };
//    }


}
