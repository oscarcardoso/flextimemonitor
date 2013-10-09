package com.cardosos.flextimemonitor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cardosos.flextimemonitor.DatePickerFragment.DatePickedListener;
import com.cardosos.flextimemonitor.TimePickerFragment.TimePickedListener;
// Vogella SQL tutorial imports
//import android.os.Bundle;
//import android.view.View;

@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends ListActivity implements TimePickedListener, DatePickedListener{

	private EventsDataSource datasource; // Vogella
	private String previousEventType = " ";
	private TextView mChrono;
	private TextView mTodayChrono;
	private boolean mStartedChrono = false;
	private long mPauseTime = 0;
	private long mStartTime = 0;
	private static String FILENAME = "flex_time_data";
	private TimeManager timeManager = new TimeManager();
	private Handler handler = new Handler();
	public static final String TAG = "FTM";

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			updateChrono();
			handler.postDelayed(this, 500);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Vogella start
		datasource = new EventsDataSource(this);
		datasource.open();

		List<Event> values = datasource.getAllEvents();
		
		Collections.reverse(values);

		// Use the SimpleCursorAdapter to show the
		// elements in a ListView
		Log.i("FTM", "Create an Event array with " + values.size() + " events");

//		View header = (View)getLayoutInflater().inflate(R.layout.listview_header_row, null);
//		this.getListView().addHeaderView(header);
		
		// Before attaching the adapter, get the previous day briefs
		List<Event> briefedValues = new ArrayList<Event>();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		
		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		
		for(int k=0 ; k < values.size(); k++){
			if(values.get(k).getDay() == dayOfMonth){
				briefedValues.add(values.get(k));
			}else{
				break;
			}
		}
		
		for(int j = dayOfMonth - 1; j > 0; j--){
			EventGroup group = new EventGroup();
			for(int i=0; i < values.size(); i++){
				if(values.get(i).getDay() < dayOfMonth){
					if(values.get(i).getDay() == j){
						group.addEvent(values.get(i));
						Log.i(TAG, "Add a " + values.get(i).getType() + " event from: " + j);
					}
				}
			}
			if(!group.isEmpty()){
				group.setHours();
				briefedValues.add(group);
				Log.i(TAG, "Add a group: " + group.getDay());
			}
		}
		
		EventAdapter adapter= new EventAdapter(this, R.layout.listview_item_row,(List<Event>) briefedValues);
		//EventAdapter adapter= new EventAdapter(this, R.layout.listview_item_row,(List<Event>) values);
		setListAdapter(adapter);
		//timeManager.setLastCheckIn(0);
		// Vogella end
		if(!values.isEmpty()){
			updatePreviousEventType();
			Log.i("FTM", "Values are not empty");
			Log.i("FTM", (previousEventType.equals(Event.CHECK_IN)) ? "Last Event was a CHECK_IN" : "Last Event was not a CHECK_IN");
			for(int i = 0; i < values.size(); i++){
				Log.i("FTM", "Checking value #" + i);
				if(values.get(i).getType().equals(Event.CHECK_IN)){
					timeManager.setLastCheckIn(values.get(i).getTime());
					Log.i("FTM", "Last Check In: (" + timeManager.getLastCheckIn()+ ") " + DateFormat.format("dd/mm kk:mm:ss", timeManager.getLastCheckIn()));
					break;
				}
			}
		}

		loadEventsInTempFile();

		mChrono = (TextView) findViewById(R.id.chronometer1);
		mTodayChrono = (TextView) findViewById(R.id.chronometer2);
		
		Button checkButton = (Button)findViewById(R.id.checkButton);
		if(previousEventType.equals(Event.CHECK_IN))
			checkButton.setText("CHECK OUT");
		else
			checkButton.setText("CHECK IN");

		mChrono.setText(R.string.empty_time);

		if(mPauseTime != 0){
			Log.i("FTM", "We've got some saved time, update the timer text");
			mChrono.setText(TimeManager.longToString(mPauseTime));
		}

		if(!mStartedChrono && previousEventType.equals(Event.CHECK_IN)){
			startTimer();
			checkButton.setText("CHECK OUT");
		}
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	        // For the main activity, make sure the app icon in the action bar
	        // does not behave as a button
	        ActionBar actionBar = getActionBar();
	        actionBar.setHomeButtonEnabled(false);
	    }
		
		ListView eventList = getListView();
		eventList.setOnItemLongClickListener( new OnItemLongClickListener() {
			
			@Override
		      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		        //Toast.makeText(MainActivity.this, "Item in position " + position + " clicked", Toast.LENGTH_LONG).show();
		        onLongListItemClick(view, position, id);
		        // Return true to consume the click event. In this case the
		        // onListItemClick listener is not called anymore.
		        return true;
		      }
		});
	}
	
	// Vogella start
	// Will be called via the onClick attribute
	// of the buttons in main.xml
	public void onClick(View view){
		EventAdapter adapter = (EventAdapter) getListAdapter();
		Event event = null;
		Button b = (Button) view;
		switch(view.getId()){
			case R.id.checkButton:
				// just check that the button has the proper name
				if(previousEventType.equals(Event.CHECK_OUT)){
					// You are doing a CHECK_IN
					// So, set the button text to CHECK_OUT.
					b.setText("CHECK OUT");
					// Then you create a new event with the current time
					event = datasource.createEvent(System.currentTimeMillis(), Event.CHECK_IN);
					// Then you set the previousEventType to CHECK_IN
					// this is probably unnecesary
					//previousEventType = Event.CHECK_IN;
					// Then you update the previous event type 
					// thru the datasource
					updatePreviousEventType();
					// Then you set the today's time in the timeManager
					// thru the datasource.
					timeManager.setTodaysTime(getTodaysHours());
					// and finally, if the chrono is not started, start the
					// timer.
					if(!mStartedChrono)
						startTimer();
					Log.i("FTM", "Add Event.CHECK_IN");
				} else {
					b.setText("CHECK IN");
					event = datasource.createEvent(System.currentTimeMillis(), Event.CHECK_OUT);
					// this is probably unnecesary
					//previousEventType = Event.CHECK_OUT;
					updatePreviousEventType();
					timeManager.setTodaysTime(getTodaysHours());
					Log.i("FTM", "Add Event.CHECK_OUT");
				}
				try{
					adapter.insert(event, 0);
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
		}
		adapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
	    // Get the item that was clicked
		Event thisEvent = (Event) this.getListAdapter().getItem(position);
		if(thisEvent.getType().equals(Event.DAY_BRIEF)){
			EventGroup eventGroup = (EventGroup) this.getListAdapter().getItem(position);
			if(eventGroup.isViewOpened()){
				//This is when the view is OPENED. Remove the group events!
				for(int i = 0; i < eventGroup.countEvents(); i++){
					((EventAdapter)this.getListAdapter()).remove(eventGroup.getEventList().get(i));
				}
				((EventAdapter)this.getListAdapter()).notifyDataSetChanged();
				eventGroup.setViewOpened(false);
			}else{
				//This is when the view is CLOSED. Insert the group events!
				//Adapter adapter = this.getListAdapter();
				for(int i = 0; i<eventGroup.countEvents(); i++){
					((EventAdapter)this.getListAdapter()).insert(eventGroup.getEventList().get(i), position + i + 1);
				}
				((EventAdapter)this.getListAdapter()).notifyDataSetChanged();
				eventGroup.setViewOpened(true);
			}
		}else{
			//Event event = (Event) this.getListAdapter().getItem(position);
			String keyword = thisEvent.toString();
		}
		//Toast.makeText(this, "You selected: " + keyword, Toast.LENGTH_SHORT).show();
	
	}
	
	protected void onLongListItemClick(View v, int position, long id){
		Log.i("FTM", "onLongListItemClick id =" + id);
		Toast.makeText(this, "You selected: " + id, Toast.LENGTH_SHORT).show();
		final Event event = (Event) this.getListAdapter().getItem(position);
		final EventAdapter eventAdapter = (EventAdapter)getListAdapter();
		final int longListItemClickPosition = position;
		
		AlertDialog dialog;
		final CharSequence[] items = { "Edit Event time..", "Edit Event date...", "Delete Event" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Edit event");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int pos) {
				switch (pos) {
				case 0: {
					// Edit the event time.
					DialogFragment timeFragment = new TimePickerFragment(longListItemClickPosition, event.getDayTimeHours(), event.getDayTimeMinutes());
					timeFragment.show(getFragmentManager(), "timePicker");
				}
				break;
				case 1: {
					// Edit the event date.
				    DialogFragment dateFragment = new DatePickerFragment(longListItemClickPosition, event.getDay(), event.getMonth(), event.getYear());
				    dateFragment.show(getFragmentManager(), "datePicker");
				}
					break;
				case 2: {
						Toast.makeText(MainActivity.this, "Deleted Event on:" + event.toString(), Toast.LENGTH_LONG).show();
						if(eventAdapter.getCount() > 0){
							Log.i("FTM", "Remove " + event.getType());
							datasource.deleteEvent(event);
							eventAdapter.remove(event);
						}
						eventAdapter.notifyDataSetChanged();
					}
					break;
				}
			}
		});
		dialog = builder.create();
		dialog.show();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		// Handle item selection
		switch(item.getItemId()){
		case R.id.menu_about:
			try {
				Toast.makeText(MainActivity.this, "Flex Time Monitor v" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName + "\nmade by: Oscar Julio Cardoso Hernandez", Toast.LENGTH_SHORT).show();
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			return true;
		case R.id.menu_settings:
			Toast.makeText(MainActivity.this, "The settings activity is not yet implemented :P", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.delete_previous_month:
			Toast.makeText(MainActivity.this, "Trying to delete previous months events.", Toast.LENGTH_SHORT).show();
			deletePreviousMonthsEvents(getPreviousMonthEvents());
			Toast.makeText(MainActivity.this, "Previous months events, if any, are now deleted.", Toast.LENGTH_SHORT).show();
			Log.d(TAG, "Refreshing list adapter and stuff");
			((EventAdapter)getListAdapter()).notifyDataSetChanged();
			updatePreviousEventType();
			saveEventsInTempFile();
			loadEventsInTempFile();
			return true;
		case R.id.export:
			Toast.makeText(MainActivity.this, "Exporting database data", Toast.LENGTH_LONG).show();
			backupDatabaseToSD();
			Toast.makeText(MainActivity.this, "Exporting finished!", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.restore:
			Toast.makeText(MainActivity.this, "Importing database", Toast.LENGTH_LONG).show();
			restoreFromBackup();
			Toast.makeText(MainActivity.this, "Importing finished!", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.check_in:
			return true;
		case R.id.check_out:
			return true;
		default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		Log.d(TAG, "onPrepareOptionsMenu");
		MenuItem check_in_item = menu.findItem(R.id.check_in);
		MenuItem check_out_item = menu.findItem(R.id.check_out);
		datasource.open();
		if(!datasource.isEmpty()){
			updatePreviousEventType();
		}
		if(previousEventType.equals(Event.CHECK_IN))
			check_in_item.setVisible(false);
		else
			check_out_item.setVisible(false);
		super.onPrepareOptionsMenu(menu);
		return true;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy(); // Always call the superclass
		// Stop method tracing that the activity started during onCreate()
		android.os.Debug.stopMethodTracing();
	}
	
	@Override
	public void onStop(){
		super.onStop(); // Always call the superclass method first
		Log.i(TAG, "onStop()");
	}

	@Override
	public void onResume(){
		super.onResume();
		Log.i(TAG, "onResume()");
		mChrono = (TextView) findViewById(R.id.chronometer1);
		mTodayChrono = (TextView)findViewById(R.id.chronometer2);
		mChrono.setText(R.string.empty_time);
		mTodayChrono.setText(R.string.empty_time);
		if(mPauseTime != 0){
			Log.i(TAG, "We've got some saved time, update the timer text");
			mChrono.setText(TimeManager.longToString(mPauseTime));
		}

		datasource.open();
		if(!datasource.isEmpty()){
			updatePreviousEventType();
		}
		
		timeManager.setTodaysTime(getTodaysHours());
		mTodayChrono.setText(TimeManager.longToString(timeManager.getTodaysTime()));

		if(previousEventType.equals(Event.CHECK_IN)){
			Log.i(TAG, "Last check: (" + timeManager.getLastCheckIn() + ") " + DateFormat.format("dd/mm kk:mm:ss", timeManager.getLastCheckIn() ) );
			startTimer();
		}
		//updateChrono();
	}

	@Override
	public void onPause(){
		saveEventsInTempFile();
		stopTimer();
		datasource.close();
		super.onPause();
		Log.i(TAG, "onPause()");
	}

	public void updateChrono(){
		if(previousEventType.equals(" ") || previousEventType.equals(Event.CHECK_OUT)){
			return;
		}
		
		long fixedTimeStart = TimeManager.getFixedTimeStart();//Fixed time start (10:00:00hrs) value in long
		
		if( System.currentTimeMillis() < fixedTimeStart //if the current time is lower than 10hrs
			|| System.currentTimeMillis() > ( fixedTimeStart + ( TimeManager.FIXED_TIME_DURATION * TimeManager.HOUR ) ) ){
			//or is higher than 
			if(System.currentTimeMillis() > ( fixedTimeStart + ( TimeManager.FIXED_TIME_DURATION * TimeManager.HOUR ) )){
				//Log.i(TAG, "This is FlexTime!");
				long millis = 0;
				if(mPauseTime > 0){
					// mPauseTime + (fixedTimeStart - timeManager.getLastCheckIn()) + (System.currentTimeMillis() - (fixedTimeStart + (TimeManager.FIXED_TIME_DURATION * TimeManager.HOUR) ) )
					millis = mPauseTime + 
							(fixedTimeStart - timeManager.getLastCheckIn()) + 
							(System.currentTimeMillis() - (fixedTimeStart + (TimeManager.FIXED_TIME_DURATION * TimeManager.HOUR) ) );
				}else{
					millis = (fixedTimeStart - timeManager.getLastCheckIn()) + 
							(System.currentTimeMillis() - (fixedTimeStart + (TimeManager.FIXED_TIME_DURATION * TimeManager.HOUR) ) );
				}
				mChrono.setText(TimeManager.longToString(millis));
		
				//TODO: Possible bug due to getTodaysTime being innacurate.
				millis = timeManager.getTodaysTime() + 
						(fixedTimeStart - timeManager.getLastCheckIn()) + 
						(System.currentTimeMillis() - (fixedTimeStart + (TimeManager.FIXED_TIME_DURATION * TimeManager.HOUR) ) );

				mTodayChrono.setText(TimeManager.longToString(millis));
				if( millis > TimeManager.HOUR * TimeManager.MAX_FLEX_HOURS ){
					mTodayChrono.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
				}
			}else{
				//Log.i(TAG, "This is FlexTime!");
				long millis = 0;
				if(mPauseTime > 0){
					millis = System.currentTimeMillis() - timeManager.getLastCheckIn() + mPauseTime;
				}else{
					millis = System.currentTimeMillis() - timeManager.getLastCheckIn();
				}
				mChrono.setText(TimeManager.longToString(millis));
		
				millis = System.currentTimeMillis() - timeManager.getLastCheckIn() + timeManager.getTodaysTime();
				mTodayChrono.setText(TimeManager.longToString(millis));
				if( millis > TimeManager.HOUR * TimeManager.MAX_FLEX_HOURS ){
					mTodayChrono.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
				}
			}
		}else{
			long millis = 0;
			long todaysTime = timeManager.getTodaysTime();

			//Log.i(TAG, "This sucks, this is FixedTime!");
			//Log.i(TAG, "fixedTimeStart: " + fixedTimeStart);
			//Log.i(TAG, "lastCheckIn: " + timeManager.getLastCheckIn());
			//Log.i(TAG, "mPauseTime: " + mPauseTime);
			//Log.i(TAG, "todaysTime: " + todaysTime);

			if(mPauseTime > 0){
				if(timeManager.getLastCheckIn() < fixedTimeStart)
					millis = mPauseTime + fixedTimeStart - timeManager.getLastCheckIn();
			}else{
				if(timeManager.getLastCheckIn() < fixedTimeStart)
					millis = fixedTimeStart - timeManager.getLastCheckIn();
			}
			mChrono.setText(TimeManager.longToString(millis));
	
			millis = fixedTimeStart - timeManager.getLastCheckIn() + todaysTime;
			mTodayChrono.setText(TimeManager.longToString(millis));
			if( millis > TimeManager.HOUR * TimeManager.MAX_FLEX_HOURS ){
				mTodayChrono.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
			}
		}

//		long millis = 0;
//		if(mPauseTime > 0){
//			millis = System.currentTimeMillis() - timeManager.getLastCheckIn() + mPauseTime;	
//		}else{
//			millis = System.currentTimeMillis() - timeManager.getLastCheckIn();
//		}
//		mChrono.setText(TimeManager.longToString(millis));
//		
//
//		millis = System.currentTimeMillis() - timeManager.getLastCheckIn() + timeManager.getTodaysTime();
//		mTodayChrono.setText(TimeManager.longToString(millis));
//		if( millis > TimeManager.HOUR * TimeManager.MAX_FLEX_HOURS ){
//			mTodayChrono.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
//		}
		//Log.i("FTM","Timer: (" + millis + ") " + TimeManager.longToString(millis));
	}

	public void startTimer(){
		if(!mStartedChrono){
			handler.postDelayed(runnable, 500);
			mStartedChrono = true;
		}
	}

	public void stopTimer(){
		handler.removeCallbacks(runnable);
		mStartedChrono = false;
	}

	public void updatePreviousEventType(){
		if(datasource.isOpen()){
			Event lastEvent = datasource.getLastEvent();
			previousEventType = lastEvent.getType();
			if(previousEventType.equals(Event.CHECK_IN))
				timeManager.setLastCheckIn(lastEvent.getTime());
			else
				Log.i(TAG, "Previous event type is not a CHECK_IN");
		} else {
			Log.w(TAG, "Datasource is not Open");
		}
	}
	
	public long getTodaysHours(){
		long todaysTime = 0;
		long lastCheckIn = 0;
		if(datasource.isOpen()){
			List<Event> todaysEvents = datasource.getAllEvents();
			for(Event e:todaysEvents){
				if(DateUtils.isToday(e.getTime())){
					if(e.getType().equals(Event.CHECK_IN)){
						lastCheckIn = e.getTime();
					} else {
						if(e.getType().equals(Event.CHECK_OUT)){
							if(lastCheckIn > 0){
								todaysTime += e.getTime() - lastCheckIn;
							}
						} 
					}
				}
			}
		} else {
			Log.w(TAG, "Datasource is not Open");
		}		
		Log.w(TAG, "Todays Time is (" + todaysTime + ") " + TimeManager.longToString(todaysTime));
		return todaysTime;
	}
	
	public void showTimePickerDialog(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    newFragment.show(getFragmentManager(), "timePicker");
	}

	@Override
	public void onDatePicked(int id, int day, int month, int year) {
		Log.i(TAG, "Date Picked");
		if(datasource.isOpen()){
			//datasource.updateEvent();
			//datasource.updateEvent(modifiedEvent);
			Event modifiedEvent = (Event)this.getListAdapter().getItem(id);
			modifiedEvent.setDate(day, month, year);
			datasource.updateEvent(modifiedEvent);
			((EventAdapter)getListAdapter()).notifyDataSetChanged();
			if(id == 0)
				updatePreviousEventType();
			saveEventsInTempFile();
			loadEventsInTempFile();
			Log.i(TAG, "Event " + modifiedEvent.getId() + " was modified");
		} else {
			Log.w(TAG, "Datasource is not Open");
		}		
	}

	@Override
	public void onTimePicked(int id, int hour, int minute) {
		Log.i(TAG, "Time Picked");
		if(datasource.isOpen()){
			//datasource.updateEvent();
			Event modifiedEvent = (Event)this.getListAdapter().getItem(id);
			modifiedEvent.setDayTimeHours(hour);
			modifiedEvent.setDayTimeMinutes(minute);
			datasource.updateEvent(modifiedEvent);
			((EventAdapter)getListAdapter()).notifyDataSetChanged();
			if(id == 0)
				updatePreviousEventType();
			saveEventsInTempFile();
			loadEventsInTempFile();
			Log.i(TAG, "Event " + modifiedEvent.getId() + " was modified");
		} else {
			Log.w(TAG, "Datasource is not Open");
		}		
	}
	
	public void backupDatabaseToSD(){
		Log.d(TAG, "Backing up database");
		datasource.backupToFile("events.csv");
		Log.d(TAG, "Database backed up!");
	}
	
	public void restoreFromBackup(){
		try {
			Log.i(TAG, "Restoring database");
			datasource.restoreFromFile("events.csv");
			Log.i(TAG, "Database restored!");
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void saveEventsInTempFile(){
		
		List<Event> values = datasource.getAllEvents();

		long timeToSave = 0;
		long previousCheckIn = 0;
		for(int i=0; i<values.size(); i++){
			long thisTime = values.get(i).getTime();
			String thisType = values.get(i).getType();
			if(thisType.equals(Event.CHECK_IN)){
				// This is a CHECK_IN, save the time to compare it to the
				// next event, a CHECK_OUT
				//timeToSave =+ values.get(i).getTime();
				previousCheckIn = thisTime;
				Log.i(TAG, "previousCheckIn: (" + previousCheckIn + ") " + DateFormat.format("dd/mm kk:mm:ss", previousCheckIn));
			} else {
				// This is a CHECK_OUT
				// Add the difference between the last check in and this
				// check out. Only if this is NOT the first event in the list
				if(previousCheckIn != 0){
					timeToSave += thisTime - previousCheckIn;
					Log.i(TAG, "timeToSave: (" + timeToSave + ") " + TimeManager.longToString(timeToSave));
				}
			}
		}
		Log.i(TAG, "So far you have " +  TimeManager.longToString(timeToSave) + " flex time covered");

		try {
			FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
			fos.write(Long.toString(timeToSave).getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			Log.e(TAG, "File Not Found! Expected file->" + FILENAME);
			e.printStackTrace();
		} catch (IOException e){
			Log.e(TAG, "File Write/Read Error");
			e.printStackTrace();
		}
	}
	
	public void loadEventsInTempFile(){
		// dxd this is the part of the code that reads the file to get
		// the cached last time
		StringBuffer fileContent = new StringBuffer("");
		FileInputStream fis;
		int ch;
				
		try {
			fis = openFileInput(FILENAME);
			try{
				while( (ch = fis.read() ) != -1 ){
					fileContent.append((char)ch);
				}
				fis.close();
			} catch (IOException e){
				Log.e(TAG, "File Read Error");
				e.printStackTrace();
			}
			mPauseTime = Long.parseLong(new String(fileContent));
			Log.i(TAG, "Saved time: (" + mPauseTime + ") " + TimeManager.longToString(mPauseTime));
		} catch (FileNotFoundException e) {
			Log.e(TAG, "File Not Found! Expected file->" + FILENAME);
			e.printStackTrace();
		}
		// dxd file read part end
	}
	
	public List<Event> getPreviousMonthEvents(){
		List<Event> eventList = new ArrayList<Event>();
		if(datasource.isOpen() && !datasource.isEmpty()){
			List<Event> values = datasource.getAllEvents();
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			long thisMonth = cal.getTimeInMillis();
			for(int i=0; i<values.size(); i++){
				if(thisMonth > ((Event)values.get(i)).getTime()){
					eventList.add((Event)values.get(i));
				}
			}
		}
		return eventList;
	}
	
	public void deletePreviousMonthsEvents(List<Event> previousMonthEvents){
		if(!datasource.isOpen() || datasource.isEmpty() || previousMonthEvents.isEmpty()){
			Log.i(TAG,"Datasource is NOT open, or is empty, or there are no previous month events.");
			return;
		}
		for(int i=0; i < previousMonthEvents.size(); i++){
			long id = previousMonthEvents.get(i).getId();
			datasource.deleteEvent(previousMonthEvents.get(i));
			Log.i(TAG,"Deleted event id:" + id);
		}
	}
}

