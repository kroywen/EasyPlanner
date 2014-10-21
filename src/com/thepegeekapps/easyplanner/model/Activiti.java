package com.thepegeekapps.easyplanner.model;

import java.text.ParseException;

import org.json.JSONObject;

import com.thepegeekapps.easyplanner.util.Utilities;

public class Activiti {
	
	public static final String ACTIVITY_ID = "activity_id";
	public static final String CLASS_ID = "class_id";
	public static final String TEXT = "text";
	public static final String DATE = "date";
	public static final String CREATED = "created";
	
	private long id;
	private long classId;
	private String description;
	private String date;
	private long time;
	private String created;
	
	public Activiti() {}
	
	public Activiti(JSONObject jsonObj) {
		this.id = jsonObj.optLong(ACTIVITY_ID);
		this.classId = jsonObj.optLong(CLASS_ID);
		this.description = jsonObj.optString(TEXT);
		this.date = jsonObj.optString(DATE);
		this.created = jsonObj.optString(CREATED);
		parseTime();
	}
	
	public Activiti(long id, long classId, String description, long time) {
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
	
	public boolean hasClassId() {
		return classId != 0;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public long getTime() {
		return time;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
	public String getCreated() {
		return created;
	}
	
	public void setCreated(String created) {
		this.created = created;
	}
	
	private void parseTime() {
		try {
			time = Utilities.parseTime(date, Utilities.dd_MM_yyyy);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
