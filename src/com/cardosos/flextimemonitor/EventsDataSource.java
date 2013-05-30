package com.cardosos.flextimemonitor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;

public class EventsDataSource {
	// Database fields

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_TIME, MySQLiteHelper.COLUMN_TYPE};
	private String fil;

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
	
	public boolean insertEvent(long id, long time, String type){
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_ID, id);
		values.put(MySQLiteHelper.COLUMN_TIME, time);
		values.put(MySQLiteHelper.COLUMN_TYPE, type);
		long err = database.insert(MySQLiteHelper.TABLE_EVENTS, null, values);
		if(err == -1)
			return false;
		else
			return true;
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
	
	public int updateEvent(Event event){
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_TIME, event.getTime());
		values.put(MySQLiteHelper.COLUMN_TYPE, event.getType());
		
		// updating row
	    return database.update(MySQLiteHelper.TABLE_EVENTS, values, MySQLiteHelper.COLUMN_ID + " = ?", new String[] { String.valueOf(event.getId()) });
	}

	public Event getLastEvent(){
		List<Event> events = new ArrayList<Event>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_EVENTS,
			allColumns, null, null, null, null, null);
		
		if(cursor.getCount() < 0)
			return new Event();

		cursor.moveToLast();
		Event event = cursorToEvent(cursor);
		Log.i(MainActivity.TAG, "Last event: " + event.getId() + ", " + event.getTime() + ", " + event.getType());
		// Make sure to close the cursor
		cursor.close();
		return event;
	}
	
	public List<Event> getTodaysEvents(){
		List<Event> events = new ArrayList<Event>();

		Log.i(MainActivity.TAG, "Today is (" + DateUtils.DAY_IN_MILLIS + ") " + DateFormat.format("dd/MM/YYYY kk:mm:ss", DateUtils.DAY_IN_MILLIS ));
		
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
	
	public boolean backupToFile(String filename){
		Log.d(MainActivity.TAG, "backupToFile");
		Boolean returnCode = false;
		int i = 0;
		String csvHeader = "";
		String csvValues = "";
		
		// Do I need to save the header? Nope!
//		for (i = 0; i < MySQLiteHelper.COLUMN_NAMES.length; i++){
//			if (csvHeader.length() > 0){
//				csvHeader += ",";
//			}
//			csvHeader += "\"" + MySQLiteHelper.COLUMN_NAMES[i] + "\"";
//		}
//		
//		csvHeader += "\n";
//		Log.d(MainActivity.TAG, "header=" + csvHeader);
		
		try {
			File sd = Environment.getExternalStorageDirectory();
			if (sd.canWrite()){
				String backupDBPath = filename;
				File backupDB = new File(sd, backupDBPath);
				FileWriter fileWriter = new FileWriter(backupDB);
				BufferedWriter out = new BufferedWriter(fileWriter);
				Cursor cursor = database.query(MySQLiteHelper.TABLE_EVENTS,
					allColumns, null, null, null, null, null);
				if(cursor != null){
					out.write(csvHeader);
					while (cursor.moveToNext()){
						csvValues = Long.toString(cursor.getLong(0)) + ",";
						csvValues += Long.toString(cursor.getLong(1)) + ",";
						csvValues += cursor.getString(2) + "\n";
						out.write(csvValues);
						Log.d(MainActivity.TAG, "values=" + csvValues);
					}
					cursor.close();
				}
				out.close();
				returnCode = true;
			}
		} catch(IOException e){
			returnCode = false;
			e.printStackTrace();
		}
		return returnCode;
	}
	
	public boolean restoreFromFile(String filename){
		try{
			File sdcard = Environment.getExternalStorageDirectory();
			File backupFile = new File(sdcard, filename);
			if(backupFile.exists()){
				BufferedReader reader = new BufferedReader(new FileReader(backupFile));
				String line;
				while ((line = reader.readLine() ) != null){
					line = line.replaceAll("\"", "");
					String[] row = line.split(",");
					String id = row[0];
					String time = row[1];
					String type = row[2];
					insertEvent(Long.parseLong(id), Long.parseLong(time), type);
				}
				reader.close();
			}else{
				return false;
			}
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void deleteAllEvents(){
		database.delete(MySQLiteHelper.TABLE_EVENTS, null, null);
	}
}
