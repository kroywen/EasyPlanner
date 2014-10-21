package com.thepegeekapps.easyplanner.fragment;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.api.ApiData;
import com.thepegeekapps.easyplanner.api.ApiResponse;
import com.thepegeekapps.easyplanner.api.ApiService;
import com.thepegeekapps.easyplanner.model.Activiti;
import com.thepegeekapps.easyplanner.screen.BaseScreen;
import com.thepegeekapps.easyplanner.ui.view.DateView;
import com.thepegeekapps.easyplanner.ui.view.DateView.OnDateChangedListener;

public class BaseMonthFragment extends ClassFragment implements OnDateChangedListener, OnItemClickListener {

	protected DateView dateView;
	protected List<Activiti> activities;
	protected String[] daysOfWeek;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		daysOfWeek = getResources().getStringArray(R.array.days_of_week);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if (dateView != null) {
			dateView.setDate(getTime());
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		BaseScreen screen = (BaseScreen) getActivity();
		if (screen != null) {
			screen.hideProgressDialog();
		}
		if (apiStatus == ApiService.API_STATUS_SUCCESS) {
			int status = apiResponse.getStatus();
			String requestName = apiResponse.getRequestName();
			String method = apiResponse.getMethod();
			if (status == ApiResponse.STATUS_SUCCESS) {
				if (ApiData.ACTIVITY.equalsIgnoreCase(requestName)) {
					if (ApiData.GET.equalsIgnoreCase(method)) {
						activities = (List<Activiti>) apiResponse.getData();
						updateViews();
					}
				}
			}
		}
	}
	
	protected void updateViews() {
		// override in subclasses
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// override in subclasses
	}

	@Override
	public void onDateChanged(long time) {
		// override in subclasses
	}

}
