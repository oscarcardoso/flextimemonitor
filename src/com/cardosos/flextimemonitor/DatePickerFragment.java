/**
 * 
 */
package com.cardosos.flextimemonitor;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.Toast;

import android.support.v4.app.DialogFragment;
//import android.app.DialogFragment;

/**
 * @author oscar.cardoso
 *
 */
public class DatePickerFragment extends DialogFragment implements
		OnDateSetListener {
	
	private int id;
	private int day;
	private int month; // this is zero based
	private int year;
	private DatePickedListener mListener;

	/**
	 * 
	 */
	public DatePickerFragment() {
	}
	
	/**
	 * 
	 */
	public DatePickerFragment(int day, int month, int year) {
		this.day = day;
		this.month = month;
		this.year = year;
	}

	public DatePickerFragment(int id, int dayInt, int monthInt, int yearInt) {
		this.id = id;
		this.day = dayInt;
		this.month = monthInt;
		this.year = yearInt;
		
	}

	/* (non-Javadoc)
	 * @see android.app.DatePickerDialog.OnDateSetListener#onDateSet(android.widget.DatePicker, int, int, int)
	 */
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// Do something with the date chosen by the user. It returns the day of month, the month of year (zero based)
		//		and the year.
		Toast.makeText(view.getContext(), "Date: " + dayOfMonth + "/" + monthOfYear + "/" + year, Toast.LENGTH_LONG).show();
		Log.i("FTM", "Date: " + dayOfMonth + "/" + monthOfYear + "/" + year);
		mListener.onDatePicked(this.id, dayOfMonth, monthOfYear, year);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		// Use the current date as the default date in the picker
//		final Calendar c = Calendar.getInstance();
//		int year = c.get(Calendar.YEAR);
//		int month = c.get(Calendar.MONTH);
//		int day = c.get(Calendar.DAY_OF_MONTH);
		
		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, this.year, this.month, this.day);		
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try{
			mListener = (DatePickedListener) activity;
		} catch(ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement " + DatePickedListener.class.getName());
		}
	}

	public static interface DatePickedListener{
		public void onDatePicked(int id, int day, int month, int year);
	}
}
