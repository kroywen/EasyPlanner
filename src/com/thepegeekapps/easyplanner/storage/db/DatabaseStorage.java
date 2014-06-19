package com.thepegeekapps.easyplanner.storage.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.thepegeekapps.easyplanner.model.Clas;

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

}
