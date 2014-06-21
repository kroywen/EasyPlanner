package com.thepegeekapps.easyplanner.model;

public class Task {
	
	private long id;
	private long classId;
	private String description;
	private long time;
	
	public Task() {}
	
	public Task(long id, long classId, String description, long time) {
		this.id = id;
		this.classId = classId;
		this.description = description;
		this.time = time;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getClassId() {
		return classId;
	}
	
	public void setClassId(long classId) {
		this.classId = classId;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public long getTime() {
		return time;
	}
	
	public void setTime(long time) {
		this.time = time;
	}

}
