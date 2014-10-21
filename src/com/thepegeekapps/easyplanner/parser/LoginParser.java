package com.thepegeekapps.easyplanner.parser;

import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.thepegeekapps.easyplanner.api.ApiData;
import com.thepegeekapps.easyplanner.api.ApiResponse;
import com.thepegeekapps.easyplanner.util.Utilities;

public class LoginParser extends ApiParser {

	@Override
	public Object readData(InputStream is) {
		String json = Utilities.streamToString(is);
		return readData(json);
	}
	
	@Override
	public Object readData(String json) {
		String token = null;
		try {
			checkForError(json);
			if (apiResponse.getStatus() != ApiResponse.STATUS_ERROR) {
				JSONObject jsonObj = new JSONObject(json);
				token = jsonObj.optString(ApiData.TOKEN);
			}
		} catch (JSONException e) {
			setStatusError("Invalid response");
			e.printStackTrace();
		}
		return token;
	}

}
