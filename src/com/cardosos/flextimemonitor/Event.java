package com.cardosos.flextimemonitor;

public class Event {
	private long id;
	private long time;
	private String type;
	private static final CHECK_IN = "check_in";
	private static final CHECK_OUT = "check_out";

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return type + " in " + time.toString();
	}
}
