package com.thepegeekapps.easyplanner.model;

import java.util.List;

import com.thepegeekapps.easyplanner.util.Utilities;

public class Task {
	
	private long id;
	private long classId;
	private long parentId;
	private String description;
	private long time;
	boolean completed;
	private List<Task> subtasks;
	
	public Task() {}
	
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

}
