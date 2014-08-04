package com.thepegeekapps.easyplanner.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thepegeekapps.easyplanner.api.ApiResponse;
import com.thepegeekapps.easyplanner.model.Activiti;
import com.thepegeekapps.easyplanner.util.Utilities;

public class ActivityListParser extends ApiParser {

	@Override
	public Object readData(InputStream is) {
		List<Activiti> activities = null;
		String json = Utilities.streamToString(is);
		try {
			checkForError(json);
			if (apiResponse.getStatus() != ApiResponse.STATUS_ERROR) {
				JSONArray jsonArray = new JSONArray(json);
				if (jsonArray != null && jsonArray.length() > 0) {
					activities = new ArrayList<Activiti>();
					for (int i=0; i<jsonArray.length(); i++) {
						JSONObject jsonObj = jsonArray.getJSONObject(i);
						Activiti activity = new Activiti(jsonObj);
						activities.add(activity);
					}
				}
			}
		} catch (JSONException e) {
			setStatusError("Invalid response");
			e.printStackTrace();
		}
		return activities;
	}

}
