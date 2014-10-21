package com.thepegeekapps.easyplanner.model;

import java.text.ParseException;
import java.util.List;

import org.json.JSONObject;

import com.thepegeekapps.easyplanner.util.Utilities;

public class Task {
	
	public static final String TASK_ID = "task_id";
	public static final String CLASS_ID = "class_id";
	public static final String TEXT = "text";
	public static final String DATE = "date";
	public static final String CREATED = "created";
	public static final String PARENT_ID = "parent_id";
	public static final String COMPLETED = "completed";
	public static final String CHILDREN = "children";
	
	private long id;
	private long classId;
	private long parentId;
	private String description;
	private String date;
	private String created;
	private long time;
	boolean completed;
	private List<Task> subtasks;
	
	public Task() {}
	
	public Task(JSONObject jsonObj) {
		this.id = jsonObj.optLong(TASK_ID);
		this.classId = jsonObj.optLong(CLASS_ID);
		this.parentId = jsonObj.optLong(PARENT_ID);
		this.description = jsonObj.optString(TEXT);
		this.date = jsonObj.optString(DATE);
		this.completed = jsonObj.optString(COMPLETED).equalsIgnoreCase("yes");
		parseTime();
	}
	
	public Task(long id, long classId, long parentId, String description, long time, boolean completed) {
		this.id = id;
		this.classId = classId;
		this.parentId = parentId;
		this.description = description;
		this.time = time;
		this.completed = completed;
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
	
	public long getParentId() {
		return parentId;
	}
	
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	public boolean hasParentId() {
		return parentId != 0;
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
	
	public String getCreated() {
		return created;
	}
	
	public void setCreated(String created) {
		this.created = created;
	}
	
	public long getTime() {
		return time;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
	public boolean isCompleted() {
		return completed;
	}
	
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	
	public List<Task> getSubtasks() {
		return subtasks;
	}
	
	public void setSubtasks(List<Task> subtasks) {
		this.subtasks = subtasks;
	}
	
	public boolean hasSubtasks() {
		return !Utilities.isEmpty(subtasks);
	}
	
	public boolean areSubtasksCompleted() {
		if (Utilities.isEmpty(subtasks)) {
			return true;
		}
		for (Task task : subtasks) {
			if (!task.isCompleted()) {
				return false;
			}
		}
		return true;
	}
	
	private void parseTime() {
		try {
			time = Utilities.parseTime(date, Utilities.dd_MM_yyyy);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
