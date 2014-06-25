package com.thepegeekapps.easyplanner.storage.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.thepegeekapps.easyplanner.model.Activiti;
import com.thepegeekapps.easyplanner.model.Clas;
import com.thepegeekapps.easyplanner.model.Homework;
import com.thepegeekapps.easyplanner.model.Task;

public class DatabaseStorage {
	
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	
	public DatabaseStorage(Context context) {
		dbHelper = new DatabaseHelper(context);
		db = dbHelper.getWritableDatabase();
	}
	
	public List<Clas> getClasses(String selection) {
		List<Clas> classes = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_CLASSES, null, selection, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				classes = new ArrayList<Clas>();
				do {
					Clas clas = new Clas(
						c.getLong(c.getColumnIndex(DatabaseHelper.FIELD_ID)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
						c.getLong(c.getColumnIndex(DatabaseHelper.FIELD_TIME))
					);
					classes.add(clas);
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed()) {
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classes;
	}
	
	public Clas getClassById(long id) {
		Clas clas = null;
		try {
			String selection = DatabaseHelper.FIELD_ID + "=" + id;
			Cursor c = db.query(DatabaseHelper.TABLE_CLASSES, null, selection, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				clas = new Clas(
					c.getLong(c.getColumnIndex(DatabaseHelper.FIELD_ID)),
					c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
					c.getLong(c.getColumnIndex(DatabaseHelper.FIELD_TIME))
				);
			}
			if (c != null && !c.isClosed()) {
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clas;
	}
	
	public void addClass(Clas clas) {
		if (clas == null) {
			return;
		}
		ContentValues values = prepareClassContentValues(clas);
		db.insert(DatabaseHelper.TABLE_CLASSES, null, values);	 
	}
	
	public void updateClass(Clas clas) {
		if (clas == null) {
			return;
		}
		ContentValues values = prepareClassContentValues(clas);
		String whereClause = DatabaseHelper.FIELD_ID + "=" + clas.getId();
		db.update(DatabaseHelper.TABLE_CLASSES, values, whereClause, null);
	}
	
	private ContentValues prepareClassContentValues(Clas clas) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_NAME, clas.getName());
		values.put(DatabaseHelper.FIELD_TIME, clas.getTime());
		return values;
	}
	
	public void deleteClas(Clas clas) {
		if (clas == null) {
			return;
		}
		String whereClause = DatabaseHelper.FIELD_ID + "=" + clas.getId();
		db.delete(DatabaseHelper.TABLE_CLASSES, whereClause, null);
	}
	
	public List<Activiti> getActivities(String selection) {
		List<Activiti> activities = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_ACTIVITIES, null, selection, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				activities = new ArrayList<Activiti>();
				do {
					Activiti activity = new Activiti(
						c.getLong(c.getColumnIndex(DatabaseHelper.FIELD_ID)),
						c.getLong(c.getColumnIndex(DatabaseHelper.FIELD_CLASS_ID)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_DESCRIPTION)),
						c.getLong(c.getColumnIndex(DatabaseHelper.FIELD_TIME))
					);
					activities.add(activity);
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed()) {
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return activities;
	}
	
	public void addActivity(Activiti activity) {
		if (activity == null) {
			return;
		}
		ContentValues values = prepareActivityContentValues(activity);
		db.insert(DatabaseHelper.TABLE_ACTIVITIES, null, values);	 
	}
	
	public void updateActivity(Activiti activity) {
		if (activity == null) {
			return;
		}
		ContentValues values = prepareActivityContentValues(activity);
		String whereClause = DatabaseHelper.FIELD_ID + "=" + activity.getId();
		db.update(DatabaseHelper.TABLE_ACTIVITIES, values, whereClause, null);
	}
	
	private ContentValues prepareActivityContentValues(Activiti activity) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_CLASS_ID, activity.getClassId());
		values.put(DatabaseHelper.FIELD_DESCRIPTION, activity.getDescription());
		values.put(DatabaseHelper.FIELD_TIME, activity.getTime());
		return values;
	}
	
	public void deleteActivity(Activiti activity) {
		if (activity == null) {
			return;
		}
		String whereClause = DatabaseHelper.FIELD_ID + "=" + activity.getId();
		db.delete(DatabaseHelper.TABLE_ACTIVITIES, whereClause, null);
	}
	
	public List<Homework> getHomeworks(String selection) {
		List<Homework> homeworks = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_HOMEWORKS, null, selection, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				homeworks = new ArrayList<Homework>();
				do {
					Homework homework = new Homework(
						c.getLong(c.getColumnIndex(DatabaseHelper.FIELD_ID)),
						c.getLong(c.getColumnIndex(DatabaseHelper.FIELD_CLASS_ID)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_DESCRIPTION)),
						c.getLong(c.getColumnIndex(DatabaseHelper.FIELD_TIME))
					);
					homeworks.add(homework);
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed()) {
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return homeworks;
	}
	
	public void addHomework(Homework homework) {
		if (homework == null) {
			return;
		}
		ContentValues values = prepareHomewrokContentValues(homework);
		db.insert(DatabaseHelper.TABLE_HOMEWORKS, null, values);	 
	}
	
	public void updateHomewrok(Homework homework) {
		if (homework == null) {
			return;
		}
		ContentValues values = prepareHomewrokContentValues(homework);
		String whereClause = DatabaseHelper.FIELD_ID + "=" + homework.getId();
		db.update(DatabaseHelper.TABLE_HOMEWORKS, values, whereClause, null);
	}
	
	private ContentValues prepareHomewrokContentValues(Homework homework) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_CLASS_ID, homework.getClassId());
		values.put(DatabaseHelper.FIELD_DESCRIPTION, homework.getDescription());
		values.put(DatabaseHelper.FIELD_TIME, homework.getTime());
		return values;
	}
	
	public void deleteHomework(Homework homework) {
		if (homework == null) {
			return;
		}
		String whereClause = DatabaseHelper.FIELD_ID + "=" + homework.getId();
		db.delete(DatabaseHelper.TABLE_HOMEWORKS, whereClause, null);
	}
	
	public List<Task> getTasks(String selection) {
		String selection1 = (selection == null) ? "" : selection + " AND ";
		selection1 += DatabaseHelper.FIELD_PARENT_ID + "=" + 0;
		List<Task> tasks = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_TASKS, null, selection1, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				tasks = new ArrayList<Task>();
				do {
					Task task = new Task(
						c.getLong(c.getColumnIndex(DatabaseHelper.FIELD_ID)),
						c.getLong(c.getColumnIndex(DatabaseHelper.FIELD_CLASS_ID)),
						c.getLong(c.getColumnIndex(DatabaseHelper.FIELD_PARENT_ID)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_DESCRIPTION)),
						c.getLong(c.getColumnIndex(DatabaseHelper.FIELD_TIME)),
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_COMPLETED)) == 1
					);
					List<Task> subtasks = getSubtasks(selection, task.getId());
					task.setSubtasks(subtasks);
					tasks.add(task);
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed()) {
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tasks;
	}
	
	public List<Task> getSubtasks(String selection, long parentId) {
		selection = (selection == null) ? "" : selection + " AND ";
		selection += DatabaseHelper.FIELD_PARENT_ID + "=" + parentId;
		List<Task> subtasks = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_TASKS, null, selection, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				subtasks = new ArrayList<Task>();
				do {
					Task task = new Task(
						c.getLong(c.getColumnIndex(DatabaseHelper.FIELD_ID)),
						c.getLong(c.getColumnIndex(DatabaseHelper.FIELD_CLASS_ID)),
						c.getLong(c.getColumnIndex(DatabaseHelper.FIELD_PARENT_ID)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_DESCRIPTION)),
						c.getLong(c.getColumnIndex(DatabaseHelper.FIELD_TIME)),
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_COMPLETED)) == 1
					);
					subtasks.add(task);
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed()) {
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return subtasks;
	}
	
	public Task getTaskById(long id) {
		Task task = null;
		try {
			String selection = DatabaseHelper.FIELD_ID + "=" + id;
			Cursor c = db.query(DatabaseHelper.TABLE_TASKS, null, selection, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				task = new Task(
					c.getLong(c.getColumnIndex(DatabaseHelper.FIELD_ID)),
					c.getLong(c.getColumnIndex(DatabaseHelper.FIELD_CLASS_ID)),
					c.getLong(c.getColumnIndex(DatabaseHelper.FIELD_PARENT_ID)),
					c.getString(c.getColumnIndex(DatabaseHelper.FIELD_DESCRIPTION)),
					c.getLong(c.getColumnIndex(DatabaseHelper.FIELD_TIME)),
					c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_COMPLETED)) == 1
				);
				List<Task> subtasks = getSubtasks(selection, id);
				task.setSubtasks(subtasks);
			}
			if (c != null && !c.isClosed()) {
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return task;
	}
	
	public void addTask(Task task) {
		if (task == null) {
			return;
		}
		ContentValues values = prepareTaskContentValues(task);
		db.insert(DatabaseHelper.TABLE_TASKS, null, values);
	}
	
	public void updateTask(Task task) {
		if (task == null) {
			return;
		}
		ContentValues values = prepareTaskContentValues(task);
		String whereClause = DatabaseHelper.FIELD_ID + "=" + task.getId();
		db.update(DatabaseHelper.TABLE_TASKS, values, whereClause, null);
	}
	
	private ContentValues prepareTaskContentValues(Task task) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_CLASS_ID, task.getClassId());
		values.put(DatabaseHelper.FIELD_PARENT_ID, task.getParentId());
		values.put(DatabaseHelper.FIELD_DESCRIPTION, task.getDescription());
		values.put(DatabaseHelper.FIELD_TIME, task.getTime());
		values.put(DatabaseHelper.FIELD_COMPLETED, task.isCompleted() ? 1 : 0);
		return values;
	}
	
	public void deleteTask(Task task) {
		if (task == null) {
			return;
		}
		String whereClause = DatabaseHelper.FIELD_ID + "=" + task.getId();
		db.delete(DatabaseHelper.TABLE_TASKS, whereClause, null);
		
		if (task.hasSubtasks()) {
			for (Task subtask : task.getSubtasks()) {
				deleteTask(subtask);
			}
		}
	}

}
