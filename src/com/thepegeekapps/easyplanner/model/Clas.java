package com.thepegeekapps.easyplanner.model;

import java.text.ParseException;

import org.json.JSONObject;

import com.thepegeekapps.easyplanner.util.Utilities;

public class Clas {
	
	public static final String CLASS_ID = "class_id";
	public static final String NAME = "name";
	public static final String CREATED = "created";
	public static final String PRIORITY = "priority";
	public static final String ARCHIVED = "archived";
	
	private long id;
	private String name;
	private long time;
	private String created;
	private int priority;
	private boolean archived;
	
	public Clas() {}
	
	public Clas(JSONObject jsonObj) {
		this.id = jsonObj.optLong(CLASS_ID);
		this.name = jsonObj.optString(NAME);
		this.created = jsonObj.optString(CREATED);
		this.priority = jsonObj.optInt(PRIORITY);
		this.archived = jsonObj.optInt(ARCHIVED) == 1;
		parseTime();
	}
	
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
	
	public String getCreated() {
		return created;
	}
	
	public void setCreated(String created) {
		this.created = created;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public boolean isArchived() {
		return archived;
	}
	
	public void setArchived(boolean archived) {
		this.archived = archived;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	private void parseTime() {
		try {
			time = Utilities.parseTime(created, Utilities.EEE_dd_LLL_yyyy_kk_mm_ss_Z);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
