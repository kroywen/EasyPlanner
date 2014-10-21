package com.thepegeekapps.easyplanner.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thepegeekapps.easyplanner.api.ApiResponse;
import com.thepegeekapps.easyplanner.model.Resource;
import com.thepegeekapps.easyplanner.util.Utilities;

public class ResourceListParser extends ApiParser {

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
		List<Resource> resources = null;
		try {
			checkForError(json);
			if (apiResponse.getStatus() != ApiResponse.STATUS_ERROR) {
				JSONArray jsonArray = new JSONArray(json);
				if (jsonArray != null && jsonArray.length() > 0) {
					resources = new ArrayList<Resource>();
					for (int i=0; i<jsonArray.length(); i++) {
						JSONObject jsonObj = jsonArray.getJSONObject(i);
						Resource resource = new Resource(jsonObj);
						if (resource != null && !resource.hasClassId() && classId != 0) {
							resource.setClassId(classId);
						}
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
