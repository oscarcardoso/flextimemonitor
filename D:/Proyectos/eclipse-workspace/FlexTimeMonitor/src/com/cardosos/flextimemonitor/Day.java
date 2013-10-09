package com.cardosos.flextimemonitor;
/**
 * Day Class.
 * This class defines the state of the current day.
 * It's used by the TimeManager class to act upon the change of the status of
 * the current day and behave appropiately.
 */
public class Day {

	private int state;

	/**
	 * STATE DEFINITION
	 *
	 */

	public static int STATE_OUT_IN_TIME 	= 0;
	public static int STATE_IN_IN_TIME 	= 1;
	public static int STATE_OUT_ABSENT 	= 2;
	public static int STATE_IN_ABSENT 	= 3;
	public static int STATE_OUT_OVERTIME = 4;
	public static int STATE_IN_OVERTIME 	= 5;
	public static int STATE_OUT_LUNCH 	= 6;
	public static int STATE_IN_LUNCH 	= 7;
	public static int STATE_OUT_WEEKEND = 8;
	public static int STATE_IN_WEEKEND 	= 9;

	public Day(){
	}

	public Day(int state){
		this.state = state;
	}
	
	public void setState(int state) {
		this.state = state;
	}

	public int getState(){
		return this.state;
	}
}
