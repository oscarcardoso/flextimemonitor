package com.cardosos.flextimemonitor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
// Vogella SQL tutorial imports
//import android.os.Bundle;
//import android.view.View;

@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends ListActivity {

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
		
		EventAdapter adapter= new EventAdapter(this, R.layout.listview_item_row,(List<Event>) values);
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
					event = datasource.createEvent(System.currentTimeMillis(), Event.CHECK_IN);
					previousEventType = Event.CHECK_IN;
					updatePreviousEventType();
					timeManager.setTodaysTime(getTodaysHours());
					if(!mStartedChrono)
						startTimer();
					Log.i("FTM", "Add Event.CHECK_IN");
				} else {
					b.setText("CHECK IN");
					event = datasource.createEvent(System.currentTimeMillis(), Event.CHECK_OUT);
					previousEventType = Event.CHECK_OUT;
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
			//enable for debug
//			case R.id.add:
//				// Save the new comment to the database
//				if(previousEventType.equals(" ")){
//					event = datasource.createEvent(System.currentTimeMillis(), Event.CHECK_IN);
//					previousEventType = Event.CHECK_IN;
//					updatePreviousEventType();
//					if(!mStartedChrono)
//						startTimer();
//					Log.i("FTM", "previousEventType == \" \". Add Event.CHECK_IN");
//				} else {
//					if(previousEventType.equals(Event.CHECK_IN)){
//						event = datasource.createEvent(System.currentTimeMillis(), Event.CHECK_OUT);
//						previousEventType = Event.CHECK_OUT;
//						updatePreviousEventType();
//						if(mStartedChrono)
//							stopTimer();
//						Log.i("FTM", "previousEventType == Event.CHECK_IN. Add Event.CHECK_OUT");
//					} else {
//						if(previousEventType.equals(Event.CHECK_OUT)){
//							event = datasource.createEvent(System.currentTimeMillis(), Event.CHECK_IN);
//							previousEventType = Event.CHECK_IN;
//							updatePreviousEventType();
//							if(!mStartedChrono)
//								startTimer();
//							Log.i("FTM", "previousEventType == Event.CHECK_OUT. Add Event.CHECK_IN");
//						}
//					}
//				}
//				try{
//					adapter.insert(event, 0);
//				}catch(Exception e){
//					e.printStackTrace();
//				}
//				break;

			//enable for debug
//			case R.id.delete:
//				if(getListAdapter().getCount() > 0){
//					event = (Event) getListAdapter().getItem(0);
//					Log.i("FTM", "Remove " + event.getType());
//					datasource.deleteEvent(event);
//					adapter.remove(event);
//				}
//				break;
		}
		adapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
	    // Get the item that was clicked
		Event event = (Event) this.getListAdapter().getItem(position);
		String keyword = event.toString();
		Toast.makeText(this, "You selected: " + keyword, Toast.LENGTH_SHORT).show();
	
	}
	
	protected void onLongListItemClick(View v, int position, long id){
		Log.i("FTM", "onLongListItemClick id =" + id);
		Toast.makeText(this, "You selected: " + id, Toast.LENGTH_SHORT).show();
		final Event event = (Event) this.getListAdapter().getItem(position);
		final EventAdapter eventAdapter = (EventAdapter)getListAdapter();
		
		AlertDialog dialog;
		final CharSequence[] items = { "Edit Event..", "Delete Event" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Edit event");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int pos) {
				switch (pos) {
				case 0: {
					DialogFragment timeFragment = new TimePickerFragment();
					timeFragment.show(getFragmentManager(), "timePicker");
					
				    DialogFragment dateFragment = new DatePickerFragment();
				    dateFragment.show(getFragmentManager(), "datePicker");
				}
					break;
				case 1: {
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
		mTodayChrono = (TextView)findViewById(R.id.chronometer2);
		mChrono.setText(R.string.empty_time);
		mTodayChrono.setText(R.string.empty_time);
		if(mPauseTime != 0){
			Log.i("FTM", "We've got some saved time, update the timer text");
			mChrono.setText(TimeManager.longToString(mPauseTime));
		}

		datasource.open();
		if(!datasource.isEmpty()){
			updatePreviousEventType();
		}
		
		timeManager.setTodaysTime(getTodaysHours());
		mTodayChrono.setText(TimeManager.longToString(timeManager.getTodaysTime()));

		if(previousEventType.equals(Event.CHECK_IN)){
			Log.i("FTM", "Last check: (" + timeManager.getLastCheckIn() + ") " + DateFormat.format("dd/mm kk:mm:ss", timeManager.getLastCheckIn() ) );
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
				Log.i("FTM", "previousCheckIn: (" + previousCheckIn + ") " + DateFormat.format("dd/mm kk:mm:ss", previousCheckIn));
			} else {
				// This is a CHECK_OUT
				// Add the difference between the last check in and this
				// check out. Only if this is NOT the first event in the list
				if(previousCheckIn != 0){
					timeToSave += thisTime - previousCheckIn;
					Log.i("FTM", "timeToSave: (" + timeToSave + ") " + TimeManager.longToString(timeToSave));
				}
			}
		}
		Log.i("FTM", "So far you have " +  TimeManager.longToString(timeToSave) + " flex time covered");

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
		

		millis = System.currentTimeMillis() - timeManager.getLastCheckIn() + timeManager.getTodaysTime();
		mTodayChrono.setText(TimeManager.longToString(millis));
		if( millis > TimeManager.HOUR * TimeManager.MAX_FLEX_HOURS ){
			mTodayChrono.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
		}
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
		} else {
			Log.w("FTM", "Datasource is not Open");
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
			Log.w("FTM", "Datasource is not Open");
		}		
		Log.w("FTM", "Todays Time is (" + todaysTime + ") " + TimeManager.longToString(todaysTime));
		return todaysTime;
	}
	
	public void showTimePickerDialog(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    newFragment.show(getFragmentManager(), "timePicker");
	}
}
