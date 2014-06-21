package com.thepegeekapps.easyplanner.storage.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	public static final String DATABASE_NAME = "easy_planner";
	public static final int DATABASE_VERSION = 1;
	
	public static final String TABLE_CLASSES = "classes";
	public static final String TABLE_ACTIVITIES = "activities";
	public static final String TABLE_HOMEWORKS = "homeworks";
	public static final String TABLE_TASKS = "tasks";
	
	public static final String FIELD_ID = "id";
	public static final String FIELD_CLASS_ID = "class_id";
	public static final String FIELD_NAME = "name";
	public static final String FIELD_DESCRIPTION = "description";
	public static final String FIELD_TIME = "time";
	
	public static final String CREATE_TABLE_CLASSES =
		"create table if not exists " + TABLE_CLASSES + " (" +
		FIELD_ID + " integer primary key autoincrement, " +
		FIELD_NAME + " text, " +
		FIELD_TIME + " integer);";
	
	public static final String DROP_TABLE_CLASSES =
		"drop table if exists " + TABLE_CLASSES;
	
	public static final String CREATE_TABLE_ACTIVITIES = 
		"create table if not exists " + TABLE_ACTIVITIES + " (" +
		FIELD_ID + " integer primary key autoincrement, " +
		FIELD_CLASS_ID + " integer, " +
		FIELD_DESCRIPTION + " text, " +
		FIELD_TIME + " integer);";
	
	public static final String DROP_TABLE_ACTIVITIES =
		"drop table if exists " + TABLE_ACTIVITIES;
	
	public static final String CREATE_TABLE_HOMEWORKS =
		"create table if not exists " + TABLE_HOMEWORKS + " (" +
		FIELD_ID + " integer primary key autoincrement, " +
		FIELD_CLASS_ID + " integer, " +
		FIELD_DESCRIPTION + " text, " +
		FIELD_TIME + " integer);";
	
	public static final String DROP_TABLE_HOMEWORKS =
		"drop table if exists " + TABLE_HOMEWORKS;
	
	public static final String CREATE_TABLE_TASKS = 
		"create table if not exists " + TABLE_TASKS + " (" +
		FIELD_ID + " integer primary key autoincrement, " +
		FIELD_CLASS_ID + " integer, " +
		FIELD_DESCRIPTION + " text, " +
		FIELD_TIME + " integer);";
	
	public static final String DROP_TABLE_TASKS =
		"drop table if exists " + TABLE_TASKS;
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_CLASSES);
		db.execSQL(CREATE_TABLE_ACTIVITIES);
		db.execSQL(CREATE_TABLE_HOMEWORKS);
		db.execSQL(CREATE_TABLE_TASKS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DROP_TABLE_CLASSES);
		db.execSQL(DROP_TABLE_ACTIVITIES);
		db.execSQL(DROP_TABLE_HOMEWORKS);
		db.execSQL(DROP_TABLE_TASKS);
		onCreate(db);
	}

}
