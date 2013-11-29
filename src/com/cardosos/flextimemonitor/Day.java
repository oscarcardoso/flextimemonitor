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

	public static final int STATE_OUT_IN_TIME 		= 0;
	public static final int STATE_IN_IN_TIME 		= 1;
	public static final int STATE_OUT_ABSENT 		= 2;
	public static final int STATE_IN_ABSENT 		= 3;
	public static final int STATE_OUT_OVERTIME 		= 4;
	public static final int STATE_IN_OVERTIME 		= 5;
	public static final int STATE_OUT_LUNCH 		= 6;
	public static final int STATE_IN_LUNCH 			= 7;
	public static final int STATE_OUT_WEEKEND 		= 8;
	public static final int STATE_IN_WEEKEND 		= 9;
	public static final int STATE_OUT_DAYOFF    	= 10;//counting time in a dayoff
	public static final int STATE_IN_DAYOFF			= 11;
	public static final int STATE_OUT_REPOSITION	= 12;//counting time in a reposition day such as saturday
	public static final int STATE_IN_REPOSITION		= 13;
	public static final int STATE_OUT_DELAY			= 14;//counting time properly in a delayed arrival day
	public static final int STATE_IN_DELAY			= 15;

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
