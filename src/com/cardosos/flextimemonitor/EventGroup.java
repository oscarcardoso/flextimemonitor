/**
 * 
 */
package com.cardosos.flextimemonitor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.text.format.DateFormat;

/**
 * @author oscar.cardoso
 *
 */
public class EventGroup extends Event {

	protected List<Event> events = new ArrayList<Event>();
	
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
	
	@Override
	public String getTitle() {
		this.title = (String) DateFormat.format("dd/MMM", time);
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
}
