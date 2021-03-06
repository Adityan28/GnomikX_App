package com.gnomikx.www.gnomikx.Handlers;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Class to handle interaction with date picker to select time for the genetic test android
 */

public class TestDatePickerHandler implements View.OnTouchListener, DatePickerDialog.OnDateSetListener {

    private EditText editText;
    private Calendar myCalendar;
    private Context context;

    public TestDatePickerHandler(EditText editText, Context context) {
        this.editText = editText;
        this.editText.setOnTouchListener(this);
        myCalendar = Calendar.getInstance();
        this.context = context;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String myFormat = "dd MMM yyyy"; //In which you need put here
        SimpleDateFormat sdformat = new SimpleDateFormat(myFormat, Locale.getDefault());
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        editText.setText(sdformat.format(myCalendar.getTime()));

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, this, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(new Date().getTime() + 2 * 24 * 60 * 60 * 1000);
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime() + 7 * 24 * 60 * 60 * 1000);
            datePickerDialog.show();
            return true;
        } else return false;
    }
}

