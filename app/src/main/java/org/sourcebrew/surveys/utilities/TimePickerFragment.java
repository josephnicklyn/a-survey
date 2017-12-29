package org.sourcebrew.surveys.utilities;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by John on 12/11/2017.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    SimpleMessageInterface mSimpleMessageInterface;

    public TimePickerFragment setOnCallback(SimpleMessageInterface listener) {
        mSimpleMessageInterface = listener;
        return this;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Use the current time as the default values for the time picker
        Calendar cal = Calendar.getInstance();

        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);

        //Create and return a new instance of TimePickerDialog
        return new TimePickerDialog(getActivity(),this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    //onTimeSet() callback method
    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
        if (mSimpleMessageInterface != null)
            mSimpleMessageInterface.OnSimpleMessage(String.format("%02d:%02d", hourOfDay, minute));

    }

}
