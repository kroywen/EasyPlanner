package com.thepegeekapps.easyplanner.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thepegeekapps.easyplanner.api.ApiResponse;
import com.thepegeekapps.easyplanner.model.Clas;
import com.thepegeekapps.easyplanner.util.Utilities;

public class ClasListParser extends ApiParser {

	@Override
	public Object readData(InputStream is) {
		String json = Utilities.streamToString(is);
		return readData(json);
	}
	
	@Override
	public Object readData(String json) {
		List<Clas> classes = null;
		
		try {
			checkForError(json);
			if (apiResponse.getStatus() != ApiResponse.STATUS_ERROR) {
				JSONArray jsonArray = new JSONArray(json);
				if (jsonArray != null && jsonArray.length() > 0) {
					classes = new ArrayList<Clas>();
					for (int i=0; i<jsonArray.length(); i++) {
						JSONObject jsonObj = jsonArray.getJSONObject(i);
						Clas clas = new Clas(jsonObj);
						if (!clas.isArchived()) {
							classes.add(clas);
						}
					}
				}
			}
		} catch (JSONException e) {
			setStatusError("Invalid response");
			e.printStackTrace();
		}
		return classes;
	}

}
