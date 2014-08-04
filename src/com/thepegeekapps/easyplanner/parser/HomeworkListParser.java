package com.thepegeekapps.easyplanner.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thepegeekapps.easyplanner.api.ApiResponse;
import com.thepegeekapps.easyplanner.model.Homework;
import com.thepegeekapps.easyplanner.util.Utilities;

public class HomeworkListParser extends ApiParser {

	@Override
	public Object readData(InputStream is) {
		List<Homework> homeworks = null;
		String json = Utilities.streamToString(is);
		try {
			checkForError(json);
			if (apiResponse.getStatus() != ApiResponse.STATUS_ERROR) {
				JSONArray jsonArray = new JSONArray(json);
				if (jsonArray != null && jsonArray.length() > 0) {
					homeworks = new ArrayList<Homework>();
					for (int i=0; i<jsonArray.length(); i++) {
						JSONObject jsonObj = jsonArray.getJSONObject(i);
						Homework homework = new Homework(jsonObj);
						homeworks.add(homework);
					}
				}
			}
		} catch (JSONException e) {
			setStatusError("Invalid response");
			e.printStackTrace();
		}
		return homeworks;
	}

}
