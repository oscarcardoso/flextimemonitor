package com.cardosos.flextimemonitor;

public class TimeManager{

	private long lastCheckIn;

	public void TimeManager(){
		
	}

	public void TimeManager(long lastCheckIn){
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

	public void setLastCheckIn(long lastCheckIn){
		this.lastCheckIn = lastCheckIn;
	}

	public long getLastCheckIn(){
		return lastCheckIn;
	}
}
