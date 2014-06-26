package com.thepegeekapps.easyplanner.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.adapter.DaysListAdapter;
import com.thepegeekapps.easyplanner.screen.ClassScreen;
import com.thepegeekapps.easyplanner.storage.db.DatabaseHelper;
import com.thepegeekapps.easyplanner.ui.view.DateView;
import com.thepegeekapps.easyplanner.ui.view.DateView.OnDateChangedListener;
import com.thepegeekapps.easyplanner.util.Utilities;

public class MonthFragment extends Fragment implements OnDateChangedListener {
	
	private DateView dateView;
	private ListView list;
	
	private long classId;
	private OnTimeSelectListener timeListener;
	
	public static MonthFragment newInstance(long classId) {
		MonthFragment f = new MonthFragment();
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
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.month_fragment, null);
		initializeViews(view);
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			timeListener = (OnTimeSelectListener) activity;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initializeViews(View view) {
		dateView = (DateView) view.findViewById(R.id.dateView);
		dateView.setOnDateChangedListener(this);
		
		list = (ListView) view.findViewById(R.id.list);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		dateView.setDate(getTime());
	}
	
	@Override
	public void onDateChanged(long time) {
		setTime(time);
		updateViews();
		if (timeListener != null) {
			timeListener.onTimeSelected(1, time);
		}
	}
	
	public void updateViews() {
		long[][] items = Utilities.generateMonthItems(dateView.getSelectedTime()); 
		DaysListAdapter adapter = new DaysListAdapter(getActivity(), items, dateView.getSelectedTime(), classId);
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

}
