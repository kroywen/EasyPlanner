package com.thepegeekapps.easyplanner.model;

public class Clas {
	
	private long id;
	private String name;
	private long time;
	
	public Clas() {}
	
	public Clas(long id, String name, long time) {
		this.id = id;
		this.name = name;
		this.time = time;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public long getTime() {
		return time;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
