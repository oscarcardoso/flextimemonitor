package com.cardosos.flextimemonitor;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;

public class EventsDataSource {
	// Database fields

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_TIME, MySQLiteHelper.COLUMN_TYPE};

	public EventsDataSource(Context context){
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close(){
		dbHelper.close();
	}

	public boolean isOpen(){
		return database.isOpen();
	}
	
	public Event createEvent(long time, String type){
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_TIME, time);
		values.put(MySQLiteHelper.COLUMN_TYPE, type);
		long insertId = database.insert(MySQLiteHelper.TABLE_EVENTS, null, values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_EVENTS, allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		Event newEvent = cursorToEvent(cursor);
		cursor.close();
		return newEvent;
	}

	public void deleteEvent(Event event) {
		long id = event.getId();
		System.out.println("Event deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_EVENTS, MySQLiteHelper.COLUMN_ID
			+ " = " + id, null);
	}

	public List<Event> getAllEvents() {
		List<Event> events = new ArrayList<Event>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_EVENTS,
			allColumns, null, null, null, null, null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			Event event = cursorToEvent(cursor);
			events.add(event);
			cursor.moveToNext();
		}

		// Make sure to close the cursor
		cursor.close();
		return events;
	}
	
	public int getEventsCount(){
		String countQuery = "SELECT * FROM " + MySQLiteHelper.TABLE_EVENTS;
		Cursor cursor = database.rawQuery(countQuery, null);
		cursor.close();
		return cursor.getCount();
	}
	
//	public int updateEvent(Event event){
//		ContentValues values = new ContentValues();
//		values.put(KE, value)
//	}

	public Event getLastEvent(){
		List<Event> events = new ArrayList<Event>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_EVENTS,
			allColumns, null, null, null, null, null);
		
		if(cursor.getCount() < 0)
			return new Event();

		cursor.moveToLast();
		Event event = cursorToEvent(cursor);
		// Make sure to close the cursor
		cursor.close();
		return event;
	}
	
	public List<Event> getTodaysEvents(){
		List<Event> events = new ArrayList<Event>();

		Log.i("FTM", "Today is (" + DateUtils.DAY_IN_MILLIS + ") " + DateFormat.format("dd/MM/YYYY kk:mm:ss", DateUtils.DAY_IN_MILLIS ));
		
		Cursor cursor = database.query(MySQLiteHelper.TABLE_EVENTS,
			allColumns, MySQLiteHelper.COLUMN_TIME + "=" + DateUtils.DAY_IN_MILLIS , null, null, null, null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			Event event = cursorToEvent(cursor);
			events.add(event);
			cursor.moveToNext();
		}

		// Make sure to close the cursor
		cursor.close();
		return events;
	}

	private Event cursorToEvent(Cursor cursor) {
		if(cursor == null && cursor.getCount() < 0)
			return null;

		Event event = new Event();
		event.setId(cursor.getLong(0));
		event.setTime(cursor.getLong(1));
		event.setType(cursor.getString(2));
		return event;
	}

	public boolean isEmpty(){
		Cursor cursor = database.query(MySQLiteHelper.TABLE_EVENTS,
			allColumns, null, null, null, null, null);
		boolean isEmpty = true;
		if(cursor != null && cursor.getCount() > 0 )
			isEmpty = false;

		cursor.close();

		return isEmpty;
	}
}
