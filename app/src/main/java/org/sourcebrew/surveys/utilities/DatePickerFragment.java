package org.sourcebrew.surveys.utilities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by John on 12/11/2017.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    SimpleMessageInterface mSimpleMessageInterface;

    public DatePickerFragment() {
        super();
    }

    public DatePickerFragment setOnCallback(SimpleMessageInterface listener) {
        mSimpleMessageInterface = listener;
        return  this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Use the current time as the default values for the time picker
        Calendar cal = Calendar.getInstance();

        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);

        //Create and return a new instance of TimePickerDialog
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (mSimpleMessageInterface != null) {
            mSimpleMessageInterface.OnSimpleMessage(String.format("%04d/%02d/%02d", year, month, dayOfMonth));
        }
    }
}