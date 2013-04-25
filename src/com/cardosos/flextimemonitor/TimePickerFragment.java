/**
 * 
 */
package com.cardosos.flextimemonitor;

import java.util.Calendar;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * @author oscar.cardoso
 *
 */
public class TimePickerFragment extends DialogFragment implements
		OnTimeSetListener {

	/**
	 * 
	 */
	public TimePickerFragment() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see android.app.TimePickerDialog.OnTimeSetListener#onTimeSet(android.widget.TimePicker, int, int)
	 */
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// TODO Do something with the time chosen by the user
		Toast.makeText(view.getContext(), "Hour: " + hourOfDay + " Minute: " + minute, Toast.LENGTH_LONG).show();
		Log.i("FTM", "Hour: " + hourOfDay + " Minute: " + minute );
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		
		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
	}
}
