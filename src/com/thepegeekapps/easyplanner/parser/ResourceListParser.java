package com.thepegeekapps.easyplanner.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.thepegeekapps.easyplanner.api.ApiResponse;
import com.thepegeekapps.easyplanner.model.Resource;
import com.thepegeekapps.easyplanner.util.Utilities;

public class ResourceListParser extends ApiParser {

	@Override
	public Object readData(InputStream is) {
		List<Resource> resources = null;
		String json = Utilities.streamToString(is);
		Log.d("Response", json);
		try {
			checkForError(json);
			if (apiResponse.getStatus() != ApiResponse.STATUS_ERROR) {
				JSONArray jsonArray = new JSONArray(json);
				if (jsonArray != null && jsonArray.length() > 0) {
					resources = new ArrayList<Resource>();
					for (int i=0; i<jsonArray.length(); i++) {
						JSONObject jsonObj = jsonArray.getJSONObject(i);
						Resource resource = new Resource(jsonObj);
						resources.add(resource);
					}
				}
			}
		} catch (JSONException e) {
			setStatusError("Invalid response");
			e.printStackTrace();
		}
		return resources;
	}

}
