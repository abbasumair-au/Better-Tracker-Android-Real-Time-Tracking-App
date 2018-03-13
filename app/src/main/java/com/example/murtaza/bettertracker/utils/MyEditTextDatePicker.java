package com.example.murtaza.bettertracker.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by root on 3/1/18.
 */

public class MyEditTextDatePicker  implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    public EditText _editText;
    private int _day;
    private int _month;
    private int _birthYear;
    private Context _context;

    public MyEditTextDatePicker(Context context, int editTextViewID)
    {
        Activity act = (Activity)context;
        this._editText = act.findViewById(editTextViewID);
        this._editText.setOnClickListener(this);
        this._context = context;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        _birthYear = year;
        _month = monthOfYear;
        _day = dayOfMonth;
        updateDisplay();
    }
    @Override
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        DatePickerDialog dialog = new DatePickerDialog(_context, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();

    }

    // updates the date in the birth date EditText
    private void updateDisplay() {



        _editText.setText(new StringBuilder()
                // Month is 0 based so add 1
//                .append(addZero(_day)).append("-").append(addZero(_month + 1)).append("-").append(_birthYear).append(" "));

                .append(_birthYear).append("-").append(addZero(_month + 1)).append("-").append(addZero(_day)).append(" "));
    }

    private String addZero(int number){

        String castedNumber = "";

        if(number < 10){

            castedNumber = "0" + number;
        }else {
            castedNumber  = String.valueOf(number);
        }

        return castedNumber;
    }

}
