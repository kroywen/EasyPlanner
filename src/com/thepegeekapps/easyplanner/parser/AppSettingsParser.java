package com.thepegeekapps.easyplanner.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thepegeekapps.easyplanner.api.ApiResponse;
import com.thepegeekapps.easyplanner.model.AppSetting;
import com.thepegeekapps.easyplanner.util.Utilities;

public class AppSettingsParser extends ApiParser {

	@Override
	public Object readData(String json) {
		List<AppSetting> appSettings = null;
		try {
			checkForError(json);
			if (apiResponse.getStatus() != ApiResponse.STATUS_ERROR) {
				JSONArray jsonArray = new JSONArray(json);
				if (jsonArray != null && jsonArray.length() > 0) {
					appSettings = new ArrayList<AppSetting>();
					for (int i=0; i<jsonArray.length(); i++) {
						JSONObject jsonObj = jsonArray.getJSONObject(i);
						AppSetting appSetting = new AppSetting(jsonObj);
						appSettings.add(appSetting);
					}
				}
			}
		} catch (JSONException e) {
			setStatusError("Invalid response");
			e.printStackTrace();
		}
		return appSettings;
	}

	@Override
	public Object readData(InputStream is) {
		String json = Utilities.streamToString(is);
		return readData(json);
	}

}
