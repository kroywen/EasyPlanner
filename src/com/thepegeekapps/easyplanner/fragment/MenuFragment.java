package com.thepegeekapps.easyplanner.fragment;

import android.annotation.SuppressLint;
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
import com.thepegeekapps.easyplanner.storage.Settings;

public class MenuFragment extends Fragment implements OnItemClickListener {
	
	private ListView menuList;
	private MenuAdapter adapter;
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.menu_fragment, null);
		initializeViews(view);		
		return view;
	}
	
	private void initializeViews(View view) {
		menuList = (ListView) view.findViewById(R.id.menuList);
		adapter = new MenuAdapter(getActivity());
		menuList.setAdapter(adapter);
		menuList.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String item = adapter.getItem(position);
		if (item.equalsIgnoreCase("logout")) {
			Settings settings = new Settings(getActivity());
			settings.setString(Settings.TOKEN, null);
			settings.setLong(Settings.TOKEN_EXPIRE_DATE, 0);
			
			((MenuScreen) getActivity()).setLogout(true);
			((MenuScreen) getActivity()).getSlideoutHelper().close();
		}
	}

}
