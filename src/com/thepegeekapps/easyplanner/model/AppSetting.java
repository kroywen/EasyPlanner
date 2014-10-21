package com.thepegeekapps.easyplanner.model;

import org.json.JSONObject;

public class AppSetting {
	
	public static final String ALLOWED = "allowed";
	public static final String FEE = "fee";
	
	public static final String NAME = "name";
	public static final String VALUE = "value";
	public static final String DESCRIPTION =" description";

	private String name;
	private String value;
	private String description;
	
	public AppSetting() {}
	
	public AppSetting(JSONObject jsonObj) {
		this.name = jsonObj.optString(NAME);
		this.value = jsonObj.optString(VALUE);
		this.description = jsonObj.optString(DESCRIPTION);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

}
