package com.thepegeekapps.easyplanner.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thepegeekapps.easyplanner.R;

public class TasksFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tasks_fragment, null);
		initializeViews(view);		
		return view;
	}
	
	private void initializeViews(View view) {
		
	}

}
