package com.thepegeekapps.easyplanner.fragment;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.adapter.DaysListAdapter;
import com.thepegeekapps.easyplanner.api.ApiData;
import com.thepegeekapps.easyplanner.api.ApiResponse;
import com.thepegeekapps.easyplanner.api.ApiService;
import com.thepegeekapps.easyplanner.model.Activiti;
import com.thepegeekapps.easyplanner.screen.BaseScreen;
import com.thepegeekapps.easyplanner.screen.ClassScreen;
import com.thepegeekapps.easyplanner.storage.db.DatabaseHelper;
import com.thepegeekapps.easyplanner.ui.view.DateView;
import com.thepegeekapps.easyplanner.ui.view.DateView.OnDateChangedListener;
import com.thepegeekapps.easyplanner.util.Utilities;

public class WeekFragment extends ClassFragment implements OnDateChangedListener, OnItemClickListener {
	
	private DateView dateView;
	private ListView list;
	private List<Activiti> activities;
	private DaysListAdapter adapter; 
	private long[][] dateItems;
	
	public static WeekFragment newInstance(long classId) {
		WeekFragment f = new WeekFragment();
		Bundle args = new Bundle();
		args.putLong(DatabaseHelper.FIELD_ID, classId);
		f.setArguments(args);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		classId = (getArguments() != null) ? getArguments().getLong(DatabaseHelper.FIELD_ID) : 0;
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.week_fragment, null);
		initializeViews(view);
		return view;
	}
	
	private void initializeViews(View view) {
		dateView = (DateView) view.findViewById(R.id.dateView);
		dateView.setOnDateChangedListener(this);
		
		list = (ListView) view.findViewById(R.id.list);
		list.setOnItemClickListener(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		dateView.setDate(getTime());
	}

	@Override
	public void onDateChanged(long time) {
		setTime(time);
		dateItems = Utilities.generateWeekItems(dateView.getSelectedTime()); 
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
		getActivity().startService(intent);
		showProgressDialog(R.string.loading_activities);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		((BaseScreen) getActivity()).hideProgressDialog();
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
	
	public void updateViews() {
		adapter = new DaysListAdapter(getActivity(), activities, dateItems, dateView.getSelectedTime(), classId);
		list.setAdapter(adapter);
	}
	
	public void setCurrentTime(long time) {
		if (dateView != null) {
			dateView.setDate(time, false);
			updateViews();
		}
	}
	
	public long getTime() {
		return ((ClassScreen) getActivity()).getTime();
	}
	
	public void setTime(long time) {
		((ClassScreen) getActivity()).setTime(time);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		long[] item = adapter.getItem(position);
		setTime(item[0]);
		((ClassScreen) getActivity()).checkDayFragment();
	}

}
