/**
 * 
 */
package com.cardosos.flextimemonitor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.text.format.DateFormat;
import android.util.Log;

/**
 * @author oscar.cardoso
 *
 */
public class EventGroup extends Event {

	protected List<Event> events = new ArrayList<Event>();
	protected int hours;
	protected int minutes;
	public boolean viewOpened = false;
	public static final String TAG = "FTM";
	
	
	/**
	 * 
	 */
	public EventGroup() {
		this.type = DAY_BRIEF;
	}
	
	/**
	 * 
	 */
	public EventGroup(List<Event> events) {
		this.type = DAY_BRIEF;
		this.events = events;
	}
	
	public void addEvent(Event event){
		this.events.add(event);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, event.getDay());
		cal.set(Calendar.MONTH, event.getMonth());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		this.time = cal.getTimeInMillis();
	}

	public int countEvents(){
		return events.size();
	}

	public List<Event> getEventList(){
		return events;
	}

	//public Event getEvent(int index){
		//if( !events.isEmpty() && index < events.size()){
			//return events.get(index);
		//}
	//}
	
	public void removeEvent(Event event){
		this.events.remove((Event) event);
	}
	
	public void removeEvent(long id){
		int index = -1;
		for(int i = 0;i<this.events.size(); i++){
			if(this.events.get(i).getId() == id)
				index = i;
		}
		if(index == -1)
			return;
		else
			this.events.remove(index);
	}
	
	public boolean isEmpty(){
		return this.events.isEmpty();
	}
	
	public int getHours(){
		int hours = 0;
		long tempHours = 0;
		String previousType = CHECK_OUT;
		long previousEvent = 0;
		for(int i = 0; i<this.events.size(); i++){
			Log.i(TAG, "Event " + i + " from group");
			if(this.events.get(i).getType().equals(CHECK_IN)){
				Log.i(TAG, "is a " + CHECK_IN + " type Event");
				if(previousEvent != 0 && previousType.equals(CHECK_OUT)){
					Log.i(TAG, "previous event was a " + CHECK_OUT + " and has some time in it");
					tempHours += previousEvent - this.events.get(i).getTime();
					Log.i(TAG, "We have " + tempHours + " ms in hand.");
				}
				previousEvent = this.events.get(i).getTime();
				previousType = this.events.get(i).getType();
			}else{
				if(this.events.get(i).getType().equals(CHECK_OUT)){
					Log.i(TAG, "is a " + CHECK_OUT + " type Event");
					previousEvent = this.events.get(i).getTime();
					previousType = this.events.get(i).getType();
				}
			}
		}
		Log.i(TAG, "Day " + getDay() + " has " + tempHours + " ms");
		hours = TimeManager.getHourInt(tempHours);
		minutes = TimeManager.getHourMinutesInt(tempHours);
		Log.i(TAG, "Day " + getDay() + " has " + hours + " hrs and " + minutes + " minutes");
		return hours;
	}
	
	public void setHours(){
		this.hours = getHours();
	}
	
	public boolean isViewOpened(){
		return this.viewOpened;
	}

	public void setViewOpened(boolean isViewOpened){
		this.viewOpened = isViewOpened;
	}
	
	@Override
	public String getTitle() {
		this.title = (String) DateFormat.format("dd/MMM", time) + " " + this.hours + ":" + this.minutes;
		return title;
	}
	
	@Override
	public int getIcon() {
		if(this.type.equals(DAY_BRIEF)){
			this.icon = PRESENCE;
		} else {
			this.icon = ABSENCE;
		}
		return icon;
	}

	@Override
	public void setIcon(int icon) {
		this.icon = icon;
	}
	
	@Override
	public int getBriefIcon(){
		switch(hours){
		case 0:
			return HOURS_ZERO;
		case 1:
			return HOURS_ONE;
		case 2:
			return HOURS_TWO;
		case 3:
			return HOURS_THREE;
		case 4:
			return HOURS_FOUR;
		case 5:
			return HOURS_FIVE;
		default:
			return HOURS_FIVE;
		}
	}
	
	@Override
	public void setBriefIcon(int briefsIcon){
		this.briefsIcon = briefsIcon;
	}
}
