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

public class DatabaseStorage {
	
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	
	public DatabaseStorage(Context context) {
		dbHelper = new DatabaseHelper(context);
		db = dbHelper.getWritableDatabase();
	}
	
	public List<Clas> getClasses() {
		List<Clas> classes = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_CLASSES, null, null, null, null, null, null);
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

}
