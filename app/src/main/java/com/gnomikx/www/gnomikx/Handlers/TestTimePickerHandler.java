package com.gnomikx.www.gnomikx.Handlers;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;


import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

/**
 * Class to handle selection of time for the genetic test
 */

public class TestTimePickerHandler implements View.OnTouchListener, TimePickerDialog.OnTimeSetListener {

    private EditText editText;
    private FragmentManager fragmentManager;

    public TestTimePickerHandler(EditText editText, FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        this.editText = editText;
        editText.setOnTouchListener(this);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String selectedTime = hourOfDay + ":" + minute;
        editText.setText(selectedTime);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, 9, 0, 0, true);
            timePickerDialog.enableSeconds(false); //Don't want user to deal in seconds
            timePickerDialog.setVersion(TimePickerDialog.Version.VERSION_2); //newest version of the layout
            timePickerDialog.setMinTime(9, 0, 0); //9:00 am
            timePickerDialog.setMaxTime(17, 0, 0); //5:00 pm
            timePickerDialog.setTimeInterval(1, 10, 60);
            timePickerDialog.show(fragmentManager, "Time picker");
            return true;
        } else return false;
    }
}
