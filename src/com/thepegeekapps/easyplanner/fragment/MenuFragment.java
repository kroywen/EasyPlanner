package com.thepegeekapps.easyplanner.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
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
		switch (position) {
		case 0:
			contactUs();
			break;
		case 1:
			visitWebsite();
			break;
		case 2:
			reportBug();
			break;
		case 3:
			tellFriend();
			break;
		case 4:
			logout();
			break;
		}
	}
	
	private void contactUs() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"support@thepegeekapps.com"});
		startActivity(Intent.createChooser(intent, getString(R.string.email_app_select)));
	}
	
	private void visitWebsite() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("http://www.thepegeekapps.com"));
		startActivity(intent);
	}
	
	protected void reportBug() {
		String msgText = String.format(getString(R.string.report_bug_template), 
			android.os.Build.BRAND, 
			android.os.Build.MODEL,
			android.os.Build.VERSION.RELEASE);
	
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"support@thepegeekapps.com"});
		intent.putExtra(Intent.EXTRA_TEXT, msgText);
		startActivity(Intent.createChooser(intent, getString(R.string.email_app_select)));
	}
	
	protected void tellFriend() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.tell_prewritten_message));
		startActivity(Intent.createChooser(intent, getString(R.string.email_app_select)));
	}
	
	private void logout() {
		Settings settings = new Settings(getActivity());
		settings.setString(Settings.TOKEN, null);
		settings.setLong(Settings.TOKEN_EXPIRE_DATE, 0);
		
		((MenuScreen) getActivity()).setLogout(true);
		((MenuScreen) getActivity()).getSlideoutHelper().close();
	}

}
