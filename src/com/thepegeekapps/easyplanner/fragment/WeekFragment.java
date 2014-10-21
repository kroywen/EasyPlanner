package com.thepegeekapps.easyplanner.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.adapter.DaysListAdapter;
import com.thepegeekapps.easyplanner.model.Clas;
import com.thepegeekapps.easyplanner.screen.ClassScreen;
import com.thepegeekapps.easyplanner.ui.view.DateView;

public class WeekFragment extends BaseWeekFragment implements OnItemClickListener {
	
	private ListView list;
	private DaysListAdapter adapter; 	
	
	public static BaseWeekFragment newInstance(long classId) {
		BaseWeekFragment f = new WeekFragment();
		Bundle args = new Bundle();
		args.putLong(Clas.CLASS_ID, classId);
		f.setArguments(args);
		return f;
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
	protected void updateViews() {
		adapter = new DaysListAdapter(getActivity(), activities, dateItems, dateView.getSelectedTime());
		list.setAdapter(adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		long[] item = adapter.getItem(position);
		setTime(item[0]);
		((ClassScreen) getActivity()).checkDayFragment();
	}

}
