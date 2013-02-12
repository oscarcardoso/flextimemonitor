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

@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends Activity {

	private TextView mChrono;
	private boolean mStartedChrono;
	private long mPauseTime = 0;
	private long mStartTime = 0;
	private Timer timer = new Timer();
	private static String FILENAME = "flex_time_data";
	
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
			Log.i("FTM", "Saved time: " + mPauseTime);
		} catch (FileNotFoundException e) {
			Log.e("FTM", "File Not Found! Expected file->" + FILENAME);
			e.printStackTrace();
		}
		
		
		mChrono = (TextView) findViewById(R.id.chronometer1);
		
		Button checkButton = (Button)findViewById(R.id.button1);
		checkButton.setText("CHECK IN");
		checkButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Button b = (Button)v;
				if(b.getText().equals("CHECK OUT")){
					stopChrono();
					b.setText("CHECK IN");
				}else{
					startChrono();
					b.setText("CHECK OUT");
				}
				
			}
		});
		/*
		String dateString = (String) DateFormat.format("hh:mm:ss", System.currentTimeMillis());
		Toast.makeText(MainActivity.this, "Start time: " +dateString, Toast.LENGTH_SHORT).show();
		*/
		if(mPauseTime != 0){
			int seconds = (int) (mPauseTime / 1000);
			int minutes = seconds / 60;
			seconds = seconds % 60;
			int hours = minutes / 60;
			minutes = minutes % 60;
			mChrono.setText(String.format("%03d:%02d:%02d", hours, minutes, seconds));
		}else{
			mChrono.setText("000:00:00");
		}
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	        // For the main activity, make sure the app icon in the action bar
	        // does not behave as a button
	        ActionBar actionBar = getActionBar();
	        actionBar.setHomeButtonEnabled(false);
	    }
	}
	
	public void updateChrono(){
		long millis = 0;
		if(mPauseTime != 0){
			millis = System.currentTimeMillis() - mStartTime + mPauseTime;	
		}else{
			millis = System.currentTimeMillis() - mStartTime;
		}
		int seconds = (int) (millis / 1000);
		int minutes = seconds / 60;
		seconds = seconds % 60;
		int hours = minutes / 60;
		mChrono.setText(String.format("%03d:%02d:%02d", hours, minutes, seconds));
	}
	
	public void stopChrono(){
		mPauseTime = System.currentTimeMillis() - mStartTime + mPauseTime;
		timer.cancel();
		timer.purge();
		mStartedChrono = false;
		Log.i("FTM", "Stopped Chrono at: " + DateFormat.format("dd/MM kk:mm:ss", mPauseTime));
	}
	
	public void startChrono(){
		mStartTime = System.currentTimeMillis();
		timer = new Timer();
		timer.schedule(new firstTask(), 0,500);
		mStartedChrono = true;
		Log.i("FTM", "Started Chrono at: " + DateFormat.format("dd/MM kk:mm:ss", mStartTime));
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
		// Save the timer paused time so that the progress is not lost
		String time_data = "";
		
		if(mStartedChrono){
			stopChrono();
		}
		time_data = String.valueOf(mPauseTime);
		
		try {
			FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
			fos.write(time_data.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			Log.e("FTM", "File Not Found! Expected file->" + FILENAME);
			e.printStackTrace();
		} catch (IOException e){
			Log.e("FTM", "File Write/Read Error");
			e.printStackTrace();
		}
		
	}
	
}