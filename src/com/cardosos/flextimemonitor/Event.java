package com.cardosos.flextimemonitor;

import java.util.Calendar;

import android.text.format.DateFormat;

public class Event {
	private long id;
	private long time = 0;
	private String type = " ";
	public static final String CHECK_IN = "check_in";
	public static final String CHECK_OUT = "check_out";
	public static final int CHECK_IN_ICON = R.drawable.ic_check_in;
	public static final int CHECK_OUT_ICON = R.drawable.ic_check_out;
	private int icon;
	private String title;

	public Event() {
		super();
		this.time = 0;
		this.type = " ";
	}
	
	public Event(long time, String type){
		super();
		this.time = time;
		this.type = type;
	}
	
	public Event(long id, long time, String type){
		super();
		this.id = id;
		this.time = time;
		this.type = type;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
	public int getDayTimeHours(){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(this.time);
		int dayTimeHours = cal.get(Calendar.HOUR_OF_DAY);
		return dayTimeHours;
	}
	
	public void setDayTimeHours(int hours){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(this.time);
		cal.set(Calendar.HOUR_OF_DAY, hours);
		this.time = cal.getTimeInMillis();
	}

	public int getDayTimeMinutes(){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(this.time);
		int dayTimeMinutes = cal.get(Calendar.MINUTE);
		return dayTimeMinutes;
	}
	
	public void setDayTimeMinutes(int minutes){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(this.time);
		cal.set(Calendar.MINUTE, minutes);
		this.time = cal.getTimeInMillis();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		//return type + " in " + DateFormat.format("dd/MM kk:mm:ss", time);
		return type + ": (" + Long.toString(time) + ") - " + DateFormat.format("kk:mm:ss dd/MM", time);
	}

	public String getTitle() {
		this.title = (String) DateFormat.format("dd/MM kk:mm:ss", time);
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getIcon() {
		if(this.type.equals(CHECK_IN)){
			this.icon = CHECK_IN_ICON;
		} else {
			if(this.type.equals(CHECK_OUT)){
				this.icon = CHECK_OUT_ICON;	
			}
		}
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}
}
