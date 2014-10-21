package com.thepegeekapps.easyplanner.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.api.ApiData;
import com.thepegeekapps.easyplanner.api.ApiResponse;
import com.thepegeekapps.easyplanner.api.ApiService;
import com.thepegeekapps.easyplanner.model.Activiti;
import com.thepegeekapps.easyplanner.screen.BaseScreen;
import com.thepegeekapps.easyplanner.ui.view.DateView;
import com.thepegeekapps.easyplanner.ui.view.DateView.OnDateChangedListener;
import com.thepegeekapps.easyplanner.util.Utilities;

public class BaseWeekFragment extends ClassFragment implements OnDateChangedListener {
	
	protected DateView dateView;
	protected long[][] dateItems;
	protected String[] daysOfWeek;
	protected List<Activiti> activities;

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
	
	@Override
	public void onDateChanged(long time) {
		setTime(time);
		dateItems = Utilities.generateWeekItems(dateView.getSelectedTime());
		updateViews();
		requestActivities();
	}
	
	private void requestActivities() {
		long dayStart = Utilities.getDayStart(dateItems[0][0]) / 1000;
		long dayEnd = Utilities.getDayEnd(dateItems[dateItems.length-1][1] == 0 ? 
			dateItems[dateItems.length-1][0] : dateItems[dateItems.length-1][1]) / 1000;
		
		Intent intent = new Intent(getActivity(), ApiService.class);
		intent.setData(Uri.parse(ApiData.ACTIVITY));
		intent.setAction(ApiData.GET);
		intent.putExtra(ApiData.TOKEN, settings.getString(ApiData.TOKEN));
		intent.putExtra(ApiData.PARAM_CLASS_ID, classId);
		intent.putExtra(ApiData.PARAM_DATE_START, dayStart);
		intent.putExtra(ApiData.PARAM_DATE_END, dayEnd);
		
		if (dataTask != null && !dataTask.isCancelled()) {
			dataTask.cancel(true);
		}
		dataTask = new RequestDataTask(intent);
		dataTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
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
	
	protected List<Activiti> filterActivities(long[] time) {
		if (Utilities.isEmpty(activities)) {
			return null;
		}
		List<Activiti> activityList = new ArrayList<Activiti>();
		for (Activiti activity : activities) {
			long activityTime = Utilities.timeToMidnight(activity.getTime()).getTimeInMillis();
			if (activityTime == time[0] || activityTime == time[1]) {
				activityList.add(activity);
			}
		}
		return activityList;
	}

}
