package com.thepegeekapps.easyplanner.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.model.Clas;

public class TabletDayFragment extends BaseDayFragment {
	
	public static BaseDayFragment newInstance(long classId) {
		BaseDayFragment f = new TabletDayFragment();
		Bundle args = new Bundle();
		args.putLong(Clas.CLASS_ID, classId);
		f.setArguments(args);
		return f;
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tablet_day_fragment, null);
		initializeViews(view);
		return view;
	};
	
	

}
