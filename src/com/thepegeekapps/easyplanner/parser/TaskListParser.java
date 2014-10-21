package com.thepegeekapps.easyplanner.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thepegeekapps.easyplanner.api.ApiResponse;
import com.thepegeekapps.easyplanner.model.Task;
import com.thepegeekapps.easyplanner.util.Utilities;

public class TaskListParser extends ApiParser {

	@Override
	public Object readData(InputStream is) {
		String json = Utilities.streamToString(is);
		return readData(json);
	}
	
	@Override
	public Object readData(String json) {
		return readData(json, 0);
	}
	
	public Object readData(String json, long classId) {
		List<Task> tasks = null;
		try {
			checkForError(json);
			if (apiResponse.getStatus() != ApiResponse.STATUS_ERROR) {
				JSONArray jsonArray = new JSONArray(json);
				if (jsonArray != null && jsonArray.length() > 0) {
					tasks = new ArrayList<Task>();
					for (int i=0; i<jsonArray.length(); i++) {
						JSONObject jsonObj = jsonArray.getJSONObject(i);
						Task task = new Task(jsonObj);
						if (task != null && !task.hasClassId() && classId != 0) {
							task.setClassId(classId);
						}
						if (jsonObj.has(Task.CHILDREN)) {
							JSONArray jaChild = jsonObj.optJSONArray(Task.CHILDREN);
							if (jaChild != null && jaChild.length() > 0) {
								List<Task> subtasks = new ArrayList<Task>();
								for (int j=0; j<jaChild.length(); j++) {
									JSONObject joChild = jaChild.optJSONObject(j);
									Task subtask = new Task(joChild);
									if (subtask != null && !subtask.hasClassId() && classId != 0) {
										subtask.setClassId(classId);
									}
									subtasks.add(subtask);
								}
								task.setSubtasks(subtasks);
							}
						}
						tasks.add(task);
					}
				}
			}
		} catch (JSONException e) {
			setStatusError("Invalid response");
			e.printStackTrace();
		}
		return tasks;
	}

}
