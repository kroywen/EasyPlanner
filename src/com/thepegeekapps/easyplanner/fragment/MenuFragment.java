package com.thepegeekapps.easyplanner.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.adapter.MenuAdapter;
import com.thepegeekapps.easyplanner.screen.MenuScreen;

public class MenuFragment extends Fragment implements OnItemClickListener {
	
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
		menuList.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		menuList.setOnItemClickListener(null);
		((MenuScreen) getActivity()).getSlideoutHelper().close(); 
	}

}
