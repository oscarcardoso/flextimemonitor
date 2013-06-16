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
	public static final int HOURS_FIVE = R.drawable.ic_hours_five;
	public static final int HOURS_FOUR = R.drawable.ic_hours_four;
	public static final int HOURS_THREE = R.drawable.ic_hours_three;
	public static final int HOURS_TWO = R.drawable.ic_hours_two;
	public static final int HOURS_ONE = R.drawable.ic_hours_one;
	public static final int HOURS_ZERO = R.drawable.ic_hours_zero;
	public static final int ABSENCE = R.drawable.ic_absence;
	public static final int PRESENCE = R.drawable.ic_presence;
	public static final int OVERTIME_AWAY = R.drawable.ic_overtime_away;
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
		
	public int getDay(){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(this.time);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		return day;
	}
		
	public int getMonth(){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(this.time);
		int month = cal.get(Calendar.MONTH);
		return month;
	}
	
	public int getYear(){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(this.time);
		int year = cal.get(Calendar.YEAR);
		return year;
	}
	
	public void setDay(int day) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(this.time);
		cal.set(Calendar.DAY_OF_MONTH, day);
		this.time = cal.getTimeInMillis();
	}
	
	public void setMonth(int month) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(this.time);
		cal.set(Calendar.MONTH, month);
		this.time = cal.getTimeInMillis();
	}
	
	public void setYear(int year) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(this.time);
		cal.set(Calendar.YEAR, year);
		this.time = cal.getTimeInMillis();
	}
	
	public void setDate(int day, int month, int year) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(this.time);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		this.time = cal.getTimeInMillis();
	}
}
