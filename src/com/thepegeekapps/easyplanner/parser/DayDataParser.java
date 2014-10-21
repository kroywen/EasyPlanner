package com.thepegeekapps.easyplanner.parser;

import java.io.InputStream;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thepegeekapps.easyplanner.api.ApiResponse;
import com.thepegeekapps.easyplanner.model.Activiti;
import com.thepegeekapps.easyplanner.model.Clas;
import com.thepegeekapps.easyplanner.model.DayDataHolder;
import com.thepegeekapps.easyplanner.model.Homework;
import com.thepegeekapps.easyplanner.model.Resource;
import com.thepegeekapps.easyplanner.model.Task;
import com.thepegeekapps.easyplanner.util.Utilities;

public class DayDataParser extends ApiParser {

	@Override
	public Object readData(InputStream is) {
		String json = Utilities.streamToString(is);
		return readData(json);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object readData(String json) {
		DayDataHolder holder = new DayDataHolder();
		try {
			checkForError(json);
			if (apiResponse.getStatus() != ApiResponse.STATUS_ERROR) {
				JSONArray jsonArray = new JSONObject(json).optJSONArray("classes");
				if (jsonArray != null && jsonArray.length() > 0) {
					
					ActivityListParser ap = new ActivityListParser();
					ResourceListParser rp = new ResourceListParser();
					HomeworkListParser hp = new HomeworkListParser();
					TaskListParser tp = new TaskListParser();
					
					for (int i=0; i<jsonArray.length(); i++) {
						JSONObject jsonObj = jsonArray.getJSONObject(i);
						long classId = jsonObj.optLong(Clas.CLASS_ID);
						
						if (jsonObj.has("activities")) {
							JSONArray jsonArray2 = jsonObj.optJSONArray("activities");
							if (jsonArray2 != null && jsonArray2.length() > 0) {
								List<Activiti> activities = (List<Activiti>) ap.readData(jsonArray2.toString(), classId);
								holder.addActivities(activities);
							}
						}
						
						if (jsonObj.has("resources")) {
							JSONArray jsonArray2 = jsonObj.optJSONArray("resources");
							if (jsonArray2 != null && jsonArray2.length() > 0) {
								List<Resource> resources = (List<Resource>) rp.readData(jsonArray2.toString(), classId);
								holder.addResources(resources);
							}
						}
						
						if (jsonObj.has("homework")) {
							JSONArray jsonArray2 = jsonObj.optJSONArray("homework");
							if (jsonArray2 != null && jsonArray2.length() > 0) {
								List<Homework> homeworks = (List<Homework>) hp.readData(jsonArray2.toString(), classId);
								holder.addHomeworks(homeworks);
							}
						}
						
						if (jsonObj.has("tasks")) {
							JSONArray jsonArray2 = jsonObj.optJSONArray("tasks");
							if (jsonArray2 != null && jsonArray2.length() > 0) {
								List<Task> tasks = (List<Task>) tp.readData(jsonArray2.toString(), classId);
								holder.addTasks(tasks);
							}	
						}
						
					}
				}
			}
		} catch (JSONException e) {
			setStatusError("Invalid response");
			e.printStackTrace();
		}
		return holder;
	}

}
