package com.thepegeekapps.easyplanner.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.thepegeekapps.easyplanner.util.Utilities;

public class DayDataHolder {

	private List<Activiti> activities;
	private List<Resource> resources;
	private List<Homework> homeworks;
	private List<Task> tasks;
	
	public DayDataHolder() {}
	
	public List<Activiti> getActivities() {
		return activities;
	}
	
	public void addActivity(Activiti activity) {
		if (activity == null) return;
		if (activities == null) activities = new ArrayList<Activiti>();
		activities.add(activity);
	}
	
	public void addActivities(List<Activiti> activities) {
		if (Utilities.isEmpty(activities)) return;
		if (this.activities == null) this.activities = new ArrayList<Activiti>();
		this.activities.addAll(activities);
	}
	
	public boolean hasActivities() {
		return !Utilities.isEmpty(activities);
	}
	
	public List<Resource> getResources() {
		return resources;
	}
	
	public void addResource(Resource resource) {
		if (resource == null) return;
		if (resources == null) resources = new ArrayList<Resource>();
		resources.add(resource);
	}
	
	public void addResources(List<Resource> resources) {
		if (Utilities.isEmpty(resources)) return;
		if (this.resources == null) this.resources = new ArrayList<Resource>();
		this.resources.addAll(resources);
	}
	
	public boolean hasResources() {
		return !Utilities.isEmpty(resources);
	}
	
	public List<Homework> getHomeworks() {
		return homeworks;
	}
	
	public void addHomework(Homework homework) {
		if (homework == null) return;
		if (homeworks == null) homeworks = new ArrayList<Homework>();
		homeworks.add(homework);
	}
	
	public void addHomeworks(List<Homework> homeworks) {
		if (Utilities.isEmpty(homeworks)) return;
		if (this.homeworks == null) this.homeworks = new ArrayList<Homework>();
		this.homeworks.addAll(homeworks);
	}
	
	public boolean hasHomeworks() {
		return !Utilities.isEmpty(homeworks);
	}
	
	public List<Task> getTasks() {
		return tasks;
	}
	
	public void addTask(Task task) {
		if (task == null) return;
		if (tasks == null) tasks = new ArrayList<Task>();
		tasks.add(task);
	}
	
	public void addTasks(List<Task> tasks) {
		if (Utilities.isEmpty(tasks)) return;
		if (this.tasks == null) this.tasks = new ArrayList<Task>();
		this.tasks.addAll(tasks);
	}
	
	public boolean hasTasks() {
		return !Utilities.isEmpty(tasks);
	}
	
	public void filter(long classId) {
		if (classId <= 0) {
			return;
		}
		if (!Utilities.isEmpty(activities)) {
			Iterator<Activiti> i = activities.iterator();
			while (i.hasNext()) {
				Activiti activiti = i.next();
				if (activiti.getClassId() != classId) {
					i.remove();
				}
			}
		}
		if (!Utilities.isEmpty(resources)) {
			Iterator<Resource> i = resources.iterator();
			while (i.hasNext()) {
				Resource resource = i.next();
				if (resource.getClassId() != classId) {
					i.remove();
				}
			}
		}
		if (!Utilities.isEmpty(homeworks)) {
			Iterator<Homework> i = homeworks.iterator();
			while (i.hasNext()) {
				Homework homework = i.next();
				if (homework.getClassId() != classId) {
					i.remove();
				}
			}
		}
		if (!Utilities.isEmpty(tasks)) {
			Iterator<Task> i = tasks.iterator();
			while (i.hasNext()) {
				Task task = i.next();
				if (task.getClassId() != classId) {
					i.remove();
				}
			}
		}
	}

}
