package com.cardosos.flextimemonitor;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Collections;

import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;

public class TimeManager{

	private long lastCheckIn = 0;
	private long todaysTime = 0;
	private long lunchTime = 0;
	private boolean isInside 	= false;
	private boolean isOutside 	= false;
	private boolean isOvertime 	= false;
	private boolean isAbsent 	= false;
	private boolean isWeekend 	= false;
	private boolean isLunch 	= false;
	private boolean isIvanFix   = false;
	private int flexMode = FLEX_MODE_NORMAL;
	private int daysOff = 0;
	private Day today = new Day(Day.STATE_OUT_IN_TIME);

	public static long MAX_FLEX_HOURS = 5;
	public static long DAY = 86400000;
	public static long HOUR = 3600000;
	public static long MINUTE = 60000;
	public static int FIXED_TIME_START = 10;
	public static int FIXED_TIME_BREAK = 1;
	public static int FIXED_TIME_DURATION = 5;
	public static int FLEX_MODE_NORMAL 		= 0;
	public static int FLEX_MODE_REDUX 		= 1;
	public static int FLEX_MODE_TOTAL 		= 2;
	public static final String TAG = "FTM";


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
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	public static String longToString(long time, boolean hundredHours){
		if(hundredHours){
			int seconds = (int) (time / 1000);
			int minutes = seconds / 60;
			seconds = seconds % 60;
			int hours = minutes / 60;
			minutes = minutes % 60;
			return String.format("%03d:%02d:%02d", hours, minutes, seconds);
		}else{
			return longToString(time);
		}
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
	
	public static int getHourMinutesInt(long time){
		int seconds = (int) (time / 1000);
		int minutes = seconds / 60;
		seconds = seconds % 60;
		int hours = minutes / 60;
		minutes = minutes % 60;
		return minutes;
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
	
	public static Date getMonthDate(boolean startOfMonth){
		Calendar cal = Calendar.getInstance();
		if(startOfMonth){
			cal.set(Calendar.DAY_OF_MONTH, 1);//First day of month is 1
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
		}else{
			int max = cal.getMaximum(Calendar.DAY_OF_MONTH);
			cal.set(Calendar.DAY_OF_MONTH, max);//First day of month is 1
			max = cal.getMaximum(Calendar.HOUR_OF_DAY);
			cal.set(Calendar.HOUR_OF_DAY, max);
			max = cal.getMaximum(Calendar.MINUTE);
			cal.set(Calendar.MINUTE, max);
			max = cal.getMaximum(Calendar.SECOND);
			cal.set(Calendar.SECOND, max);
			max = cal.getMaximum(Calendar.MILLISECOND);
			cal.set(Calendar.MILLISECOND, max);
			Log.i(TAG, "endDate: " +  cal.toString());
		}
		return cal.getTime();
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

	public static long getFixedTimeStart(){
		long fixedTimeStart = 0;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, TimeManager.FIXED_TIME_START);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0 ); // Fix the loop second bug.
		cal.set(Calendar.MILLISECOND, 0);
		fixedTimeStart = cal.getTimeInMillis();
		return fixedTimeStart;
	}

	public static long getFixedTimeEnd(){
		return getFixedTimeStart() + (FIXED_TIME_DURATION * HOUR);
	}

	public static long getFixedTimeStart(Event event){
		long fixedTimeStart = 0;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, TimeManager.FIXED_TIME_START);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0 ); // Fix the loop second bug.
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.DAY_OF_MONTH, event.getDay());
		cal.set(Calendar.MONTH, event.getMonth());
		cal.set(Calendar.YEAR, event.getYear());
		fixedTimeStart = cal.getTimeInMillis();
		return fixedTimeStart;
	}

	public static long getFixedTimeEnd(Event event){
		return getFixedTimeStart(event) + (FIXED_TIME_DURATION * HOUR);
	}

	public static long getTodaysHours(List<Event> todaysEvents, boolean isReversed){
		if(!todaysEvents.isEmpty() && isReversed){
			Collections.reverse(todaysEvents);
			return getTodaysHours(todaysEvents);
		}else{
			Log.e(TAG, "getTodaysHours is either empty or not reversed");
			return getTodaysHours(todaysEvents);
		}
	}

	public static long getTodaysHours(List<Event> todaysEvents){
		long todaysTime = 0;
		long lastCheckIn = 0;
		long lastCheckOut = 0;
		long lunchTime = 0;
		boolean isWeekend = false;
		boolean isAbsent = false;
		boolean isOvertime = false;
		boolean isLunch = false;

		if(!todaysEvents.isEmpty()){
			long fixedTimeStart = getFixedTimeStart(todaysEvents.get(0));

			if(todaysEvents.get(0).isWeekend()){
				isWeekend = true;
				isOvertime = false;
				isLunch = false;

			}else{
				if(todaysEvents.get(0).getTime() > fixedTimeStart){
					isAbsent = true;
				}
			}

			for(Event e:todaysEvents){
				//if(DateUtils.isToday(e.getTime())){
					if(e.getType().equals(Event.CHECK_IN)){
						//Log.w(TAG, "HOURS: CHECK_IN");
						if(lastCheckOut > 0){
							if( !isWeekend && !isAbsent ){
								// Define case 2: Exit before fts and enter after fixedTimeStart 
								if( e.getTime() > fixedTimeStart &&
									e.getTime() < fixedTimeStart + ( FIXED_TIME_DURATION * HOUR ) &&
									lastCheckOut < fixedTimeStart){
									//Log.w(TAG, "CASE 2");
									lunchTime += e.getTime() - fixedTimeStart;
								}
								// Define case 4: Enter after fixedTimeStart and exit before fts+(FTD*HOURS)
								if( lastCheckOut > fixedTimeStart && 
									lastCheckOut < ( fixedTimeStart + (FIXED_TIME_DURATION * HOUR) ) && 
									e.getTime() > lastCheckOut &&
									e.getTime() < ( fixedTimeStart + ( FIXED_TIME_DURATION * HOUR ) ) ){
									//Log.w(TAG, "CASE 4");
									lunchTime += e.getTime() - lastCheckOut;
								}
								// Define case 9: Enter before fte and exit after fte
								if( lastCheckOut > fixedTimeStart && 
									lastCheckOut < ( fixedTimeStart + (FIXED_TIME_DURATION * HOUR) ) &&
									e.getTime() > lastCheckOut &&
									e.getTime() > ( fixedTimeStart + ( FIXED_TIME_DURATION * HOUR ) ) ){
									//Log.w(TAG, "CASE 9");
									lunchTime += ( fixedTimeStart + ( FIXED_TIME_DURATION * HOUR ) ) - lastCheckOut;
								}
							}
						}
						lastCheckOut = 0;
						lastCheckIn = e.getTime();
					} else {
						if(e.getType().equals(Event.CHECK_OUT)){
							//Log.w(TAG, "HOURS: CHECK_OUT");
							if(lastCheckIn > 0){
								// Define case 7: Enter in weekend
								if( isWeekend || isAbsent ){
									todaysTime += e.getTime() - lastCheckIn;
								}else{
									// Define case 1: Enter before fixedTimeStart and exit after fixedTimeStart but before fts+(FTD*HOURS)
									if( lastCheckIn < fixedTimeStart && 
										e.getTime() > fixedTimeStart && 
										e.getTime() < ( fixedTimeStart + (FIXED_TIME_DURATION * HOUR))){
										//Log.w(TAG, "CASE 1");
										todaysTime += fixedTimeStart - lastCheckIn;
										isLunch = true;
										//lastCheckOut = e.getTime();
									}
									// Define case 3: Enter before fixedTimeStart and exit after fts+(FTD*HOURS)
									if( lastCheckIn < fixedTimeStart && 
										e.getTime() > ( fixedTimeStart + FIXED_TIME_DURATION * HOUR ) ){
										//Log.w(TAG, "CASE 3");
										todaysTime += fixedTimeStart - lastCheckIn;
										todaysTime += e.getTime() - fixedTimeStart + (FIXED_TIME_DURATION * HOUR);
										isLunch = false;
									}
									// Define case 5: Enter after fts+(FTD*HOURS) and exit after
									if( lastCheckIn > fixedTimeStart + (FIXED_TIME_DURATION * HOUR) && 
										e.getTime() > fixedTimeStart + (FIXED_TIME_DURATION * HOUR) ){
										//Log.w(TAG, "CASE 5");
										todaysTime += e.getTime() - lastCheckIn;
										isLunch = false;
									}
									// Define case 6: Enter before fts and exit before fts
									if( lastCheckIn < fixedTimeStart &&
										e.getTime() < fixedTimeStart){
										//Log.w(TAG, "CASE 6");
										todaysTime += e.getTime() - lastCheckIn;
										isLunch = false;
									}
									// Define case 7: Enter after fts and before fts+(FTD*HOURS) and exit after
									if( lastCheckIn > fixedTimeStart &&
										lastCheckIn < fixedTimeStart + ( FIXED_TIME_DURATION * HOUR ) &&
										e.getTime() > fixedTimeStart + ( FIXED_TIME_DURATION * HOUR ) ){
										//Log.w(TAG, "CASE 7");
										todaysTime += e.getTime() - (fixedTimeStart + ( FIXED_TIME_DURATION * HOUR )) ;
										isLunch = false;
										}
									// Define case 8: Enter after fts and before fte and exit before fte
									if(lastCheckIn > fixedTimeStart &&
									   lastCheckIn < fixedTimeStart + ( FIXED_TIME_DURATION * HOUR ) &&
									   e.getTime() < fixedTimeStart + ( FIXED_TIME_DURATION * HOUR ) ){
										//Log.w(TAG, "CASE 8");
										isLunch = true;
									}
								}
							}
							lastCheckIn = 0;
							lastCheckOut = e.getTime();
						} 
					}
				//}
			}
		} else {
			Log.w(TAG, "List<Events> is empty.");
			return 0;
		}		

		//TODO: Substract lunchtime when STATE_IN_OVERTIME
		if(lunchTime > (FIXED_TIME_BREAK * HOUR)){
			Log.w(TAG, "LunchTime Exceeded.");
			todaysTime -= lunchTime;
			isOvertime = true;
			//isLunch = false;
		}
		Log.w(TAG, "Todays Time is (" + todaysTime + ") " + longToString(todaysTime));
		Log.w(TAG, "FLAGS: WK,AB,LN,OV,OT,IN");
		Log.w(TAG, "FLAGS: "+((isWeekend)?" 1":" 0")+","+
							((isAbsent)?" 1":" 0")+","+
							((isLunch)?" 1":" 0")+","+
							((isOvertime)?" 1":" 0")+","+
							((lastCheckOut!=0)?" 1":" 0")+","+
							((lastCheckIn!=0)?" 1":" 0"));
		return todaysTime;
	}

	public long getGroupHours(List<Event> groupEvents, boolean isReversed){
		if(!groupEvents.isEmpty() && isReversed){
			Collections.reverse(groupEvents);
			return getGroupHours(groupEvents);
		}else{
			Log.e(TAG, "getGroupHours is either empty or not reversed");
			return getGroupHours(groupEvents);
		}
	}
	
	public long getGroupHours(List<Event> groupEvents){
		long todaysTime = 0;
		long lastCheckIn = 0;
		long lastCheckOut = 0;
		long lunchTime = 0;

		if(!groupEvents.isEmpty()){
			long fixedTimeStart = getFixedTimeStart(groupEvents.get(0));

			if(groupEvents.get(0).isWeekend()){
				this.isWeekend = true;
				this.isOvertime = false;
				this.isLunch = false;

			}else{
				if(groupEvents.get(0).getTime() > fixedTimeStart){
					this.isAbsent = true;
				}
			}

			for(Event e:groupEvents){
				//if(DateUtils.isToday(e.getTime())){
					if(e.getType().equals(Event.CHECK_IN)){
						//Log.w(TAG, "HOURS: CHECK_IN");
						if(lastCheckOut > 0){
							if( !this.isWeekend && !this.isAbsent ){
								// Define case 2: Exit before fts and enter after fixedTimeStart 
								if( e.getTime() > fixedTimeStart &&
									e.getTime() < fixedTimeStart + ( FIXED_TIME_DURATION * HOUR ) &&
									lastCheckOut < fixedTimeStart){
									//Log.w(TAG, "CASE 2");
									lunchTime += e.getTime() - fixedTimeStart;
								}
								// Define case 4: Enter after fixedTimeStart and exit before fts+(FTD*HOURS)
								if( lastCheckOut > fixedTimeStart && 
									lastCheckOut < ( fixedTimeStart + (FIXED_TIME_DURATION * HOUR) ) && 
									e.getTime() > lastCheckOut &&
									e.getTime() < ( fixedTimeStart + ( FIXED_TIME_DURATION * HOUR ) ) ){
									//Log.w(TAG, "CASE 4");
									lunchTime += e.getTime() - lastCheckOut;
								}
								// Define case 9: Enter before fte and exit after fte
								if( lastCheckOut > fixedTimeStart && 
									lastCheckOut < ( fixedTimeStart + (FIXED_TIME_DURATION * HOUR) ) &&
									e.getTime() > lastCheckOut &&
									e.getTime() > ( fixedTimeStart + ( FIXED_TIME_DURATION * HOUR ) ) ){
									//Log.w(TAG, "CASE 9");
									lunchTime += ( fixedTimeStart + ( FIXED_TIME_DURATION * HOUR ) ) - lastCheckOut;
								}
							}
						}
						lastCheckOut = 0;
						lastCheckIn = e.getTime();
						this.isOutside = false;
						this.isInside = true;
					} else {
						if(e.getType().equals(Event.CHECK_OUT)){
							//Log.w(TAG, "HOURS: CHECK_OUT");
							if(lastCheckIn > 0){
								// Define case 7: Enter in weekend
								if( this.isWeekend || this.isAbsent ){
									todaysTime += e.getTime() - lastCheckIn;
								}else{
									// Define case 1: Enter before fixedTimeStart and exit after fixedTimeStart but before fts+(FTD*HOURS)
									if( lastCheckIn < fixedTimeStart && 
										e.getTime() > fixedTimeStart && 
										e.getTime() < ( fixedTimeStart + (FIXED_TIME_DURATION * HOUR))){
										//Log.w(TAG, "CASE 1");
										todaysTime += fixedTimeStart - lastCheckIn;
										this.isLunch = true;
										//lastCheckOut = e.getTime();
									}
									// Define case 3: Enter before fixedTimeStart and exit after fts+(FTD*HOURS)
									if( lastCheckIn < fixedTimeStart && 
										e.getTime() > ( fixedTimeStart + FIXED_TIME_DURATION * HOUR ) ){
										//Log.w(TAG, "CASE 3");
										todaysTime += fixedTimeStart - lastCheckIn;
										todaysTime += e.getTime() - fixedTimeStart + (FIXED_TIME_DURATION * HOUR);
										this.isLunch = false;
									}
									// Define case 5: Enter after fts+(FTD*HOURS) and exit after
									if( lastCheckIn > fixedTimeStart + (FIXED_TIME_DURATION * HOUR) && 
										e.getTime() > fixedTimeStart + (FIXED_TIME_DURATION * HOUR) ){
										//Log.w(TAG, "CASE 5");
										todaysTime += e.getTime() - lastCheckIn;
										this.isLunch = false;
									}
									// Define case 6: Enter before fts and exit before fts
									if( lastCheckIn < fixedTimeStart &&
										e.getTime() < fixedTimeStart){
										//Log.w(TAG, "CASE 6");
										todaysTime += e.getTime() - lastCheckIn;
										this.isLunch = false;
									}
									// Define case 7: Enter after fts and before fts+(FTD*HOURS) and exit after
									if( lastCheckIn > fixedTimeStart &&
										lastCheckIn < fixedTimeStart + ( FIXED_TIME_DURATION * HOUR ) &&
										e.getTime() > fixedTimeStart + ( FIXED_TIME_DURATION * HOUR ) ){
										//Log.w(TAG, "CASE 7");
										todaysTime += e.getTime() - (fixedTimeStart + ( FIXED_TIME_DURATION * HOUR )) ;
										this.isLunch = false;
										}
									// Define case 8: Enter after fts and before fte and exit before fte
									if(lastCheckIn > fixedTimeStart &&
									   lastCheckIn < fixedTimeStart + ( FIXED_TIME_DURATION * HOUR ) &&
									   e.getTime() < fixedTimeStart + ( FIXED_TIME_DURATION * HOUR ) ){
										//Log.w(TAG, "CASE 8");
										this.isLunch = true;
									}
								}
							}
							lastCheckIn = 0;
							lastCheckOut = e.getTime();
							this.isOutside = true;
							this.isInside = false;
						} 
					}
				//}
			}
		} else {
			Log.w(TAG, "List<Events> is empty.");
			return 0;
		}		

		//TODO: Substract lunchtime when STATE_IN_OVERTIME
		if(lunchTime > (FIXED_TIME_BREAK * HOUR)){
			Log.w(TAG, "LunchTime Exceeded.");
			todaysTime -= lunchTime;
			this.isOvertime = true;
			//isLunch = false;
		}
		Log.w(TAG, "Todays Time is (" + todaysTime + ") " + longToString(todaysTime));
		Log.w(TAG, "FLAGS: WK,AB,LN,OV,OT,IN");
		Log.w(TAG, "FLAGS: "+((this.isWeekend)?" 1":" 0")+","+
							((this.isAbsent)?" 1":" 0")+","+
							((this.isLunch)?" 1":" 0")+","+
							((this.isOvertime)?" 1":" 0")+","+
							((lastCheckOut!=0)?" 1":" 0")+","+
							((lastCheckIn!=0)?" 1":" 0"));
		return todaysTime;
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

	public boolean isInside(){
		return this.isInside;
	}

	public void setInside(boolean isInside){
		this.isInside = isInside;
	}

	public boolean isOutside(){
		return this.isOutside;
	}

	public void setOutside(boolean isOutside){
		this.isOutside = isOutside;
	}

	public boolean isOvertime(){
		return this.isOvertime;
	}

	public void setOvertime(boolean isOvertime){
		this.isOvertime = isOvertime;
	}

	public boolean isAbsent(){
		return this.isAbsent;
	}

	public void setAbsent(boolean isAbsent){
		this.isAbsent = isAbsent;
	}

	public boolean isWeekend(){
		return this.isWeekend;
	}

	public void setWeekend(boolean isWeekend){
		this.isWeekend = isWeekend;
	}

	public boolean isLunch(){
		return this.isLunch;
	}

	public void setLunch(boolean isLunch){
		this.isLunch = isLunch;
	}

	public void setLunchTime(long lunchTime){
		this.lunchTime = lunchTime;
	}

	public long getLunchTime(){
		return this.lunchTime;
	}

	public void updateLunchTime(List<Event> events){
		long todaysTime = 0;
		long lastCheckIn = 0;
		long lastCheckOut = 0;
		long lunchTime = 0;
		if(!events.isEmpty()){
			long fixedTimeStart = getFixedTimeStart(events.get(0));

			if(events.get(0).isWeekend()){
				this.isWeekend = true;
				this.isOvertime = false;
				this.isLunch = false;
				this.isAbsent = false;
			}else{
				if(events.get(0).getTime() > fixedTimeStart){
					this.isAbsent = true;
				}
			}

			for(Event e:events){
				//if(DateUtils.isToday(e.getTime())){
					if(e.getType().equals(Event.CHECK_IN)){
						//Log.w(TAG, "HOURS: CHECK_IN");
						if(lastCheckOut > 0){
							if( !this.isWeekend && !this.isAbsent ){
								// Define case 2: Exit before fts and enter after fixedTimeStart 
								if( e.getTime() > fixedTimeStart &&
									e.getTime() < fixedTimeStart + ( FIXED_TIME_DURATION * HOUR ) &&
									lastCheckOut < fixedTimeStart){
									//Log.w(TAG, "CASE 2");
									lunchTime += e.getTime() - fixedTimeStart;
								}
								// Define case 4: Enter after fixedTimeStart and exit before fts+(FTD*HOURS)
								if( lastCheckOut > fixedTimeStart && 
									lastCheckOut < ( fixedTimeStart + (FIXED_TIME_DURATION * HOUR) ) && 
									e.getTime() > lastCheckOut &&
									e.getTime() < ( fixedTimeStart + ( FIXED_TIME_DURATION * HOUR ) ) ){
									//Log.w(TAG, "CASE 4");
									lunchTime += e.getTime() - lastCheckOut;
								}
								// Define case 9: Enter before fte and exit after fte
								if( lastCheckOut > fixedTimeStart && 
									lastCheckOut < ( fixedTimeStart + (FIXED_TIME_DURATION * HOUR) ) &&
									e.getTime() > lastCheckOut &&
									e.getTime() > ( fixedTimeStart + ( FIXED_TIME_DURATION * HOUR ) ) ){
									//Log.w(TAG, "CASE 9");
									lunchTime += ( fixedTimeStart + ( FIXED_TIME_DURATION * HOUR ) ) - lastCheckOut;
								}
							}
						}
						lastCheckOut = 0;
						lastCheckIn = e.getTime();
						this.isOutside = false;
						this.isInside = true;
					} else {
						if(e.getType().equals(Event.CHECK_OUT)){
							//Log.w(TAG, "HOURS: CHECK_OUT");
							if(lastCheckIn > 0){
								// Define case 7: Enter in weekend
								if( this.isWeekend || this.isAbsent ){
									todaysTime += e.getTime() - lastCheckIn;
								}else{
									// Define case 1: Enter before fixedTimeStart and exit after fixedTimeStart but before fts+(FTD*HOURS)
									if( lastCheckIn < fixedTimeStart && 
										e.getTime() > fixedTimeStart && 
										e.getTime() < ( fixedTimeStart + (FIXED_TIME_DURATION * HOUR))){
										//Log.w(TAG, "CASE 1");
										todaysTime += fixedTimeStart - lastCheckIn;
										this.isLunch = true;
										//lastCheckOut = e.getTime();
									}
									// Define case 3: Enter before fixedTimeStart and exit after fts+(FTD*HOURS)
									if( lastCheckIn < fixedTimeStart && 
										e.getTime() > ( fixedTimeStart + FIXED_TIME_DURATION * HOUR ) ){
										//Log.w(TAG, "CASE 3");
										todaysTime += fixedTimeStart - lastCheckIn;
										todaysTime += e.getTime() - fixedTimeStart + (FIXED_TIME_DURATION * HOUR);
										this.isLunch = false;
									}
									// Define case 5: Enter after fts+(FTD*HOURS) and exit after
									if( lastCheckIn > fixedTimeStart + (FIXED_TIME_DURATION * HOUR) && 
										e.getTime() > fixedTimeStart + (FIXED_TIME_DURATION * HOUR) ){
										//Log.w(TAG, "CASE 5");
										todaysTime += e.getTime() - lastCheckIn;
										this.isLunch = false;
									}
									// Define case 6: Enter before fts and exit before fts
									if( lastCheckIn < fixedTimeStart &&
										e.getTime() < fixedTimeStart){
										//Log.w(TAG, "CASE 6");
										todaysTime += e.getTime() - lastCheckIn;
										this.isLunch = false;
									}
									// Define case 7: Enter after fts and before fts+(FTD*HOURS) and exit after
									if( lastCheckIn > fixedTimeStart &&
										lastCheckIn < fixedTimeStart + ( FIXED_TIME_DURATION * HOUR ) &&
										e.getTime() > fixedTimeStart + ( FIXED_TIME_DURATION * HOUR ) ){
											//Log.w(TAG, "CASE 7");
											todaysTime += e.getTime() - (fixedTimeStart + ( FIXED_TIME_DURATION * HOUR )) ;
											this.isLunch = false;
										}
									// Define case 8: Enter after fts and before fte and exit before fte
									if(lastCheckIn > fixedTimeStart &&
									   lastCheckIn < fixedTimeStart + ( FIXED_TIME_DURATION * HOUR ) &&
									   e.getTime() < fixedTimeStart + ( FIXED_TIME_DURATION * HOUR ) ){
										//Log.w(TAG, "CASE 8");
										this.isLunch = true;
									}
								}
							}
							lastCheckIn = 0;
							lastCheckOut = e.getTime();
							this.isOutside = true;
							this.isInside = false;
						} 
					}
				//}
			}
		} else {
			Log.w(TAG, "List<Events> is empty.");
			return;
		}		

		//TODO: Substract lunchtime when STATE_IN_OVERTIME
		if(lunchTime > (FIXED_TIME_BREAK * HOUR)){
			Log.w(TAG, "LunchTime Exceeded.");
			todaysTime -= lunchTime;
			this.isOvertime = true;
		}
		Log.w(TAG, "FLAGS: WK,AB,LN,OV,OT,IN");
		Log.w(TAG, "FLAGS: "+((this.isWeekend)?" 1":" 0")+","+
							((this.isAbsent)?" 1":" 0")+","+
							((this.isLunch)?" 1":" 0")+","+
							((this.isOvertime)?" 1":" 0")+","+
							((lastCheckOut!=0)?" 1":" 0")+","+
							((lastCheckIn!=0)?" 1":" 0"));

		this.lunchTime = lunchTime;
	}

	public void addLunchTime(long addedLunchTime){
		this.lunchTime += addedLunchTime;
	}

	public void clearLunchTime(){
		this.lunchTime = 0;
	}

	public void setDayState(int state){
		this.today.setState(state);
	}

	public int getDayState(){
		return this.today.getState();
	}

	public void setDaysOff(int days){
		this.daysOff = days;
	}

	public int getDaysOff(){
		return this.daysOff;
	}

	public void updateState(){
		//long fixedTimeStart = TimeManager.getFixedTimeStart();
		//long fixedTimeEnd = TimeManager.getFixedTimeEnd();
		//long lunchTime = timeManager.getLunchTime();

		if(this.isWeekend()){
			if(this.isOutside)
				this.setDayState(Day.STATE_OUT_WEEKEND);
			else
				this.setDayState(Day.STATE_IN_WEEKEND);
		}else{
			if(this.isAbsent()){
				if(this.isOutside)
					this.setDayState(Day.STATE_OUT_ABSENT);
				else
					this.setDayState(Day.STATE_IN_ABSENT);
			}else{
				if(this.isLunch()){
					if(this.isOutside)
						this.setDayState(Day.STATE_OUT_LUNCH);
					else
						this.setDayState(Day.STATE_IN_LUNCH);
				}else{
					if(this.isOvertime()){
						if(this.isOutside)
							this.setDayState(Day.STATE_OUT_OVERTIME);
						else
							this.setDayState(Day.STATE_IN_OVERTIME);
					}else{
						if(this.isOutside)
							this.setDayState(Day.STATE_OUT_IN_TIME);
						else
							this.setDayState(Day.STATE_IN_IN_TIME);
					}
				}
			}
		}
		//Log.i("FTM","Timer: (" + millis + ") " + TimeManager.longToString(millis));
		switch(this.getDayState()){
			case Day.STATE_OUT_WEEKEND:
				Log.i("FTM","STATE_OUT_WEEKEND");
				break;
			case Day.STATE_IN_WEEKEND:
				Log.i("FTM","STATE_IN_WEEKEND");
				break;
			case Day.STATE_OUT_ABSENT:
				Log.i("FTM","STATE_OUT_ABSENT");
				break;
			case Day.STATE_IN_ABSENT:
				Log.i("FTM","STATE_IN_ABSENT");
				break;
			case Day.STATE_OUT_LUNCH:
				Log.i("FTM","STATE_OUT_LUNCH");
				break;
			case Day.STATE_IN_LUNCH:
				Log.i("FTM","STATE_IN_LUNCH");
				break;
			case Day.STATE_OUT_OVERTIME:
				Log.i("FTM","STATE_OUT_OVERTIME");
				break;
			case Day.STATE_IN_OVERTIME:
				Log.i("FTM","STATE_IN_OVERTIME");
				break;
			case Day.STATE_OUT_IN_TIME:
				Log.i("FTM","STATE_OUT_IN_TIME");
				break;
			case Day.STATE_IN_IN_TIME:
				Log.i("FTM","STATE_IN_IN_TIME");
				break;
		}
	}
}

