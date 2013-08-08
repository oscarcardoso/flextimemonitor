package com.cardosos.flextimemonitor;

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
	public static int STATE_OUT_SATURDAY = 8;
	public static int STATE_IN_SATURDAY 	= 9;

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
