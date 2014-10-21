package com.thepegeekapps.easyplanner.parser;

import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.thepegeekapps.easyplanner.api.ApiData;
import com.thepegeekapps.easyplanner.api.ApiResponse;

public abstract class ApiParser {
	
	protected ApiResponse apiResponse;
	
	public ApiParser() {
		apiResponse = new ApiResponse();
	}
	
	public ApiResponse getApiResponse() {
		return apiResponse;
	}
	
	public void setApiResponse(ApiResponse apiResponse) {
		this.apiResponse = apiResponse;
	}
	
	public void parse(InputStream is) {
		Object data = readData(is);
		apiResponse.setData(data);
	}
	
	public abstract Object readData(String json);
	
	public abstract Object readData(InputStream is);
	
	protected void checkForError(String json) {
		try {
			JSONObject jsonObj = new JSONObject(json);
			if (jsonObj.has(ApiData.ERROR)) {
				setStatusError(jsonObj.optString(ApiData.ERROR));
			}
		} catch (JSONException e) {
			// possibly we have JSONArray as response, so everything is OK
		}		
	}
	
	protected void setStatusError(String message) {
		apiResponse.setStatus(ApiResponse.STATUS_ERROR);
		apiResponse.setError(message);
	}

}
