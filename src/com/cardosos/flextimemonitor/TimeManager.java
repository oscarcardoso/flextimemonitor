package com.cardosos.flextimemonitor;

import android.text.format.DateFormat;

public class TimeManager{

	private long lastCheckIn = 0;
	private long todaysTime = 0;
	public static long MAX_FLEX_HOURS = 10;
	public static long DAY = 86400000;
	public static long HOUR = 3600000;
	public static long MINUTE = 60000;

	public TimeManager(){
	}

	public TimeManager(long lastCheckIn){
		this.lastCheckIn = lastCheckIn;
	}

	public static String longToString(long time){
		int seconds = (int) (time / 1000);
		int minutes = seconds / 60;
		seconds = seconds % 60;
		int hours = minutes / 60;
		minutes = minutes % 60;
		return String.format("%03d:%02d:%02d", hours, minutes, seconds);
	}
	
	public static String getHourString(long time){
		int seconds = (int) (time / 1000);
		int minutes = seconds / 60;
		seconds = seconds % 60;
		int hours = minutes / 60;
		minutes = minutes % 60;
		return String.format("%03d", hours);	
	}
	
	public static int getHourInt(long time){
		int seconds = (int) (time / 1000);
		int minutes = seconds / 60;
		seconds = seconds % 60;
		int hours = minutes / 60;
		minutes = minutes % 60;
		return hours;	
	}

	public static int getMinutesInt(long time){
		int seconds = (int) (time / 1000);
		int minutes = seconds / 60;
		seconds = seconds % 60;
		minutes = minutes % 60;
		return minutes;	
	}

	public static int getDayInt(long time){
		return Integer.parseInt((String) DateFormat.format("dd", time));
	}
	
	public static int getMonthInt(long time){
		return Integer.parseInt((String) DateFormat.format("MM", time)) - 1;
	}
	
	public static int getYearInt(long time){
		return Integer.parseInt((String) DateFormat.format("yyyy", time));
	}

	public void setLastCheckIn(long lastCheckIn){
		this.lastCheckIn = lastCheckIn;
	}

	public long getLastCheckIn(){
		return lastCheckIn;
	}

	public long getTodaysTime() {
		return todaysTime;
	}

	public void setTodaysTime(long todaysTime) {
		this.todaysTime = todaysTime;
	}
}
