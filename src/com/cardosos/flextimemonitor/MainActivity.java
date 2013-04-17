package com.cardosos.flextimemonitor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.cardosos.flextimemonitor.R;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

// Vogella SQL tutorial imports
import java.util.List;
import java.util.Random;

import android.app.ListActivity;
//import android.os.Bundle;
//import android.view.View;
import android.widget.ArrayAdapter;

@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends ListActivity {

	private EventsDataSource datasource; // Vogella
	private String previousEventType = " ";
	private TextView mChrono;
	private boolean mStartedChrono = false;
	private long mPauseTime = 0;
	private long mStartTime = 0;
	private Timer timer = new Timer();
	private static String FILENAME = "flex_time_data";
	private TimeManager timeManager = new TimeManager();
	
	final Handler h = new Handler(new Callback(){
		@Override
		public boolean handleMessage(Message msg){
			updateChrono();
			return false;
		}
	});
	
	class firstTask extends TimerTask {
		@Override
		public void run(){
			h.sendEmptyMessage(0);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Vogella start
		datasource = new EventsDataSource(this);
		datasource.open();

		List<Event> values = datasource.getAllEvents();

		// Use the SimpleCursorAdapter to show the
		// elements in a ListView
		ArrayAdapter<Event> adapter= new ArrayAdapter<Event>(this, android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
		// Vogella end
		if(!values.isEmpty()){
			updatePreviousEventType();
			Log.i("FTM", "Values are not empty");
			Log.i("FTM", (previousEventType.equals(Event.CHECK_IN)) ? "Last Event was a CHECK_IN" : "Last Event was not a CHECK_IN");
			for(int i = values.size() - 1; i>=0; i--){
				Log.i("FTM", "Checking value #" + i);
				if(values.get(i).getType().equals(Event.CHECK_IN)){
					timeManager.setLastCheckIn(values.get(i).getTime());
					Log.i("FTM", "Last Check In: (" + timeManager.getLastCheckIn()+ ") " + DateFormat.format("dd/mm kk:hh:mm", timeManager.getLastCheckIn()));
				}
			}
		}

		// dxd this is the part of the code that reads the file to get
		// the cached last time
		// TODO: Make this read before the database check
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
				Log.e("FTM", "File Read Error");
				e.printStackTrace();
			}
			mPauseTime = Long.parseLong(new String(fileContent));
			Log.i("FTM", "Saved time: (" + mPauseTime + ") " + TimeManager.longToString(mPauseTime));
		} catch (FileNotFoundException e) {
			Log.e("FTM", "File Not Found! Expected file->" + FILENAME);
			e.printStackTrace();
		}
		// dxd file read part end
		
		mChrono = (TextView) findViewById(R.id.chronometer1);
		
		Button checkButton = (Button)findViewById(R.id.checkButton);
		if(previousEventType.equals(Event.CHECK_IN))
			checkButton.setText("CHECK OUT");
		else
			checkButton.setText("CHECK IN");

		mChrono.setText("000:00:00");

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
	}
	
	// Vogella start
	// Will be called via the onClick attribute
	// of the buttons in main.xml
	public void onClick(View view){
		@SuppressWarnings("unchecked")
		ArrayAdapter<Event> adapter = (ArrayAdapter<Event>) getListAdapter();
		Event event = null;
		Button b = (Button) view;
		switch(view.getId()){
			case R.id.checkButton:
				// just check that the button has the proper name
				if(previousEventType.equals(Event.CHECK_OUT)){
					// You are doing a CHECK_IN
					// So, set the button text to CHECK_OUT.
					b.setText("CHECK OUT");
					event = datasource.createEvent(System.currentTimeMillis(), Event.CHECK_IN);
					previousEventType = Event.CHECK_IN;
					if(!mStartedChrono)
						startTimer();
					Log.i("FTM", "Add Event.CHECK_IN");
				} else {
					b.setText("CHECK IN");
					event = datasource.createEvent(System.currentTimeMillis(), Event.CHECK_OUT);
					previousEventType = Event.CHECK_OUT;
					Log.i("FTM", "Add Event.CHECK_OUT");
				}
				adapter.add(event);
				break;
			case R.id.add:
				// Save the new comment to the database
				if(previousEventType.equals(" ")){
					event = datasource.createEvent(System.currentTimeMillis(), Event.CHECK_IN);
					previousEventType = Event.CHECK_IN;
					if(!mStartedChrono)
						startTimer();
					Log.i("FTM", "previousEventType == \" \". Add Event.CHECK_IN");
				} else {
					if(previousEventType.equals(Event.CHECK_IN)){
						event = datasource.createEvent(System.currentTimeMillis(), Event.CHECK_OUT);
						previousEventType = Event.CHECK_OUT;
						if(mStartedChrono)
							stopTimer();
						Log.i("FTM", "previousEventType == Event.CHECK_IN. Add Event.CHECK_OUT");
					} else {
						if(previousEventType.equals(Event.CHECK_OUT)){
							event = datasource.createEvent(System.currentTimeMillis(), Event.CHECK_IN);
							previousEventType = Event.CHECK_IN;
							if(!mStartedChrono)
								startTimer();
							Log.i("FTM", "previousEventType == Event.CHECK_OUT. Add Event.CHECK_IN");
						}
					}
				}
				adapter.add(event);
				break;
			case R.id.delete:
				if(getListAdapter().getCount() > 0){
					event = (Event) getListAdapter().getItem(0);
					Log.i("FTM", "Remove " + event.getType());
					datasource.deleteEvent(event);
					adapter.remove(event);
				}
				break;
		}
		adapter.notifyDataSetChanged();
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
	public void onDestroy(){
		super.onDestroy(); // Always call the superclass
		// Stop method tracing that the activity started during onCreate()
		android.os.Debug.stopMethodTracing();
	}
	
	@Override
	public void onStop(){
		super.onStop(); // Always call the superclass method first
		Log.i("FTM", "onStop()");
	}

	@Override
	public void onResume(){
		super.onResume();
		Log.i("FTM", "onResume()");
		mChrono = (TextView) findViewById(R.id.chronometer1);
		mChrono.setText("000:00:00");
		if(mPauseTime != 0){
			Log.i("FTM", "We've got some saved time, update the timer text");
			mChrono.setText(TimeManager.longToString(mPauseTime));
		}

		datasource.open();
		if(!datasource.isEmpty()){
			updatePreviousEventType();
		}

		if(previousEventType.equals(Event.CHECK_IN)){
			startTimer();
		}
		//updateChrono();
	}

	@Override
	public void onPause(){

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
				Log.i("FTM", "previousCheckIn = " + previousCheckIn);
			} else {
				// This is a CHECK_OUT
				// Add the difference between the last check in and this
				// check out. Only if this is NOT the first event in the list
				if(previousCheckIn != 0){
					timeToSave += thisTime - previousCheckIn;
					Log.i("FTM", "timeToSave = " + timeToSave);
				}
			}
			Log.i("FTM", "So far you have " +  TimeManager.longToString(timeToSave) + " flex time covered");
		}

		try {
			FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
			fos.write(Long.toString(timeToSave).getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			Log.e("FTM", "File Not Found! Expected file->" + FILENAME);
			e.printStackTrace();
		} catch (IOException e){
			Log.e("FTM", "File Write/Read Error");
			e.printStackTrace();
		}

		stopTimer();

		datasource.close();
		super.onPause();
		Log.i("FTM", "onPause()");
	}

	public void updateChrono(){
		if(previousEventType.equals(" ") || previousEventType.equals(Event.CHECK_OUT)){
			return;
		}

		long millis = 0;
		if(mPauseTime > 0){
			millis = System.currentTimeMillis() - timeManager.getLastCheckIn() + mPauseTime;	
		}else{
			millis = System.currentTimeMillis() - timeManager.getLastCheckIn();
		}

		mChrono.setText(TimeManager.longToString(millis));
		Log.i("FTM","Timer: (" + millis + ") " + TimeManager.longToString(millis));
	}

	public void startTimer(){
		if(!mStartedChrono){
			timer = new Timer();
			timer.schedule(new firstTask(), 0, 500);
			mStartedChrono = true;
		}
	}

	public void stopTimer(){
		timer.cancel();
		timer.purge();
		mStartedChrono = false;
	}

	public void updatePreviousEventType(){
		if(datasource.isOpen()){
			Event lastEvent = datasource.getLastEvent();
			previousEventType = lastEvent.getType();
			if(previousEventType.equals(Event.CHECK_IN))
				timeManager.setLastCheckIn(lastEvent.getTime());
		} else {
			Log.w("FTM", "Datasource is not Open");
		}
	}
	
}
