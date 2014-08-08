package com.thepegeekapps.easyplanner.model;

import java.text.ParseException;

import org.json.JSONObject;

import android.text.TextUtils;

import com.thepegeekapps.easyplanner.util.Utilities;

public class Resource {
	
	public static final String RESOURCE_ID = "resource_id";
	public static final String CLASS_ID = "class_id";
	public static final String TITLE = "title";
	public static final String TYPE = "type";
	public static final String DATE = "date";
	public static final String URL = "url";
	public static final String CREATED = "created";
	
	private long id;
	private long classId;
	private String title;
	private String type;
	private String date;
	private long time;
	private String url;
	private String created;
	
	public Resource() {}
	
	public Resource(JSONObject jsonObj) {
		this.id = jsonObj.optLong(RESOURCE_ID);
		this.classId = jsonObj.optLong(CLASS_ID);
		this.title = jsonObj.optString(TITLE);
		this.type = jsonObj.optString(TYPE);
		this.date = jsonObj.optString(DATE);
		this.url = jsonObj.optString(URL);
		this.created = jsonObj.optString(CREATED);
		parseTime();
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
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
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
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
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
	
	public boolean isFile() {
		return !TextUtils.isEmpty(type) && type.equalsIgnoreCase("file");
	}
	
	public boolean isUrl() {
		return !TextUtils.isEmpty(type) && type.equalsIgnoreCase("url");
	}

}
