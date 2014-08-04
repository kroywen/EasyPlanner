package com.thepegeekapps.easyplanner.fragment;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

import com.thepegeekapps.easyplanner.api.ApiResponse;
import com.thepegeekapps.easyplanner.api.ApiResponseReceiver;
import com.thepegeekapps.easyplanner.api.ApiService;
import com.thepegeekapps.easyplanner.api.OnApiResponseListener;
import com.thepegeekapps.easyplanner.screen.BaseScreen;
import com.thepegeekapps.easyplanner.storage.Settings;
import com.thepegeekapps.easyplanner.storage.db.DatabaseHelper;

public class ClassFragment extends Fragment implements OnApiResponseListener {
	
	protected long time;
	protected ApiResponseReceiver responseReceiver;
	protected long classId;
	protected Settings settings;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		classId = (getArguments() != null) ? getArguments().getLong(DatabaseHelper.FIELD_ID) : 0;
		settings = new Settings(getActivity());
	}
	
	@Override
	public void onResume() {
		super.onResume();
		IntentFilter intentFilter = new IntentFilter(ApiService.ACTION_API_RESULT);
		responseReceiver = new ApiResponseReceiver(this);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
			responseReceiver, intentFilter);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(responseReceiver);
	}

	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		// TODO Auto-generated method stub
		
	}
	
	protected void showProgressDialog(int messageResId) {
		((BaseScreen) getActivity()).showProgressDialog(messageResId);
	}
	
	protected void hideProgressDialog() {
		((BaseScreen) getActivity()).hideProgressDialog();
	}

}
