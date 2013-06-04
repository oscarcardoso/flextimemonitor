/**
 * 
 */
package com.cardosos.flextimemonitor;

import android.app.Activity;
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

	private long id;
	private int hour;
	private int minute;
	private TimePickedListener mListener;
	
	/**
	 * 
	 */
	public TimePickerFragment() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 */
	public TimePickerFragment(int hour, int minute) {
		// TODO Create a timepicker with a specific time
		this.hour = hour;
		this.minute = minute;
	}

	public TimePickerFragment(long id, int hourInt, int minutesInt) {
		// TODO Create a timepicker with a specific time and id
		this.id = id;
		this.hour = hourInt;
		this.minute = minutesInt;
	}

	/* (non-Javadoc)
	 * @see android.app.TimePickerDialog.OnTimeSetListener#onTimeSet(android.widget.TimePicker, int, int)
	 */
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// TODO Do something with the time chosen by the user. It returns the time in an integer.
		Toast.makeText(view.getContext(), "Hour: " + hourOfDay + " Minute: " + minute, Toast.LENGTH_LONG).show();
		Log.i("FTM", "Hour: " + hourOfDay + " Minute: " + minute );
		mListener.onTimePicked(this.id, hourOfDay, minute);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		// Use the current time as the default values for the picker
//		final Calendar c = Calendar.getInstance();
//		int hour = c.get(Calendar.HOUR_OF_DAY);
//		int minute = c.get(Calendar.MINUTE);
				
		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, this.hour, this.minute, DateFormat.is24HourFormat(getActivity()));
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try
		{
			mListener = (TimePickedListener) activity;
		} catch(ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement " + TimePickedListener.class.getName());
		}
	}
	
	public static interface TimePickedListener{
		public void onTimePicked(long id, int hour, int minute);
	}
}
