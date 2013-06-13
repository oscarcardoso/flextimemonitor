package com.cardosos.flextimemonitor;

import java.util.Calendar;
import java.util.Date;

import android.text.format.DateFormat;

public class TimeManager{

	private long lastCheckIn = 0;
	private long todaysTime = 0;
	public static long MAX_FLEX_HOURS = 9;
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
	
	/**
	 * Get the hour from a long based time.
	 * @param time - the time in long.
	 * @return An int with the amount of hours in the input time.
	 */
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
	
	
	/**
	 * Gets the amount of workdays between <b>startDate</b> and <b>endDate</b>. Imported from <a href="https://gist.github.com/digitalpardoe/1086772#file-calculateduration-java">digitalpardoe</a> gists.
	 * @param startDate
	 * @param endDate
	 * @return An int with the amount of workdays between the dates.
	 */
	public static int calculateDuration(Date startDate, Date endDate){
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(startDate);
		
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(endDate);
		
		int workDays = 0;
		
		if(startCal.getTimeInMillis() > endCal.getTimeInMillis()){
			startCal.setTime(endDate);
			endCal.setTime(startDate);
		}
		
		do
		{
			startCal.add(Calendar.DAY_OF_MONTH, 1);
			if(startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
				workDays++;
			}
		}
		while(startCal.getTimeInMillis() <= endCal.getTimeInMillis());
		
		return workDays;
	}
	
	/**
	 * Gets the date between the <b>startDate</b> and a specific <b>duration</b> in days. Imported from <a href="https://gist.github.com/digitalpardoe/1086772#file-calculateenddate-java">digitalpardoe</a> gists.
	 * @param startDate = The start date in the period of time.
	 * @param duration = The quantity in days to get the date from.
	 * @return a Date with the proper amount of days added from startDate
	 */
	public static Date calculateEndDate(Date startDate, int duration){
		Calendar startCal = Calendar.getInstance();
		
		startCal.setTime(startDate);
		
		for (int i = 1; i < duration; i++){
			startCal.add(Calendar.DAY_OF_MONTH, 1);
			while (startCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || startCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ){
				startCal.add(Calendar.DAY_OF_MONTH, 1);
			}
		}
		
		return startCal.getTime();
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
