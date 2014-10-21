package com.thepegeekapps.easyplanner.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.adapter.MonthAdapter;
import com.thepegeekapps.easyplanner.api.ApiData;
import com.thepegeekapps.easyplanner.api.ApiService;
import com.thepegeekapps.easyplanner.model.Clas;
import com.thepegeekapps.easyplanner.screen.ClassScreen;
import com.thepegeekapps.easyplanner.ui.view.DateView;
import com.thepegeekapps.easyplanner.util.Utilities;

public class TabletMonthFragment extends BaseMonthFragment {
	
	private GridView gridView;
	private long[] dateItems;
	private MonthAdapter adapter;

	public static BaseMonthFragment getInstance(long classId) {
		BaseMonthFragment f = new TabletMonthFragment();
		Bundle args = new Bundle();
		args.putLong(Clas.CLASS_ID, classId);
		f.setArguments(args);
		return f;
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tablet_month_fragment, null);
		initializeViews(view);
		return view;
	}
	
	private void initializeViews(View view) {
		dateView = (DateView) view.findViewById(R.id.dateView);
		dateView.setOnDateChangedListener(this);
		
		gridView = (GridView) view.findViewById(R.id.gridView);
		gridView.setOnItemClickListener(this);
	}
	
	@Override
	public void onDateChanged(long time) {
		setTime(time);
		dateItems = Utilities.generateTabletMonthItems(dateView.getSelectedTime());
		updateViews();
		requestActivities();
	}
	
	private void requestActivities() {
		try {
			long dayStart = Utilities.getDayStart(dateItems[0]) / 1000;
			long dayEnd = Utilities.getDayEnd(dateItems[dateItems.length-1] == 0 ? 
				dateItems[dateItems.length-1] : dateItems[dateItems.length-1]) / 1000;
			
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateViews() {
		adapter = new MonthAdapter(getActivity(), activities, dateItems, dateView.getSelectedTime());
		gridView.setAdapter(adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		long item = adapter.getItem(position);
		setTime(item);
		((ClassScreen) getActivity()).checkDayFragment();
	}

}