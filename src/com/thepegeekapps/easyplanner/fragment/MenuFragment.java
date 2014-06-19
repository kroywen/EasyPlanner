package com.thepegeekapps.easyplanner.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.adapter.MenuAdapter;

public class MenuFragment extends Fragment {
	
	private ListView menuList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.menu_fragment, null);
		initializeViews(view);		
		return view;
	}
	
	private void initializeViews(View view) {
		menuList = (ListView) view.findViewById(R.id.menuList);
		MenuAdapter adapter = new MenuAdapter(getActivity());
		menuList.setAdapter(adapter);
	}

}
