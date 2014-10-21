package com.thepegeekapps.easyplanner.screen;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.api.ApiResponse;
import com.thepegeekapps.easyplanner.api.ApiResponseReceiver;
import com.thepegeekapps.easyplanner.api.ApiService;
import com.thepegeekapps.easyplanner.api.OnApiResponseListener;
import com.thepegeekapps.easyplanner.dialog.InfoDialog;
import com.thepegeekapps.easyplanner.dialog.ProgressDialog;
import com.thepegeekapps.easyplanner.storage.Settings;
import com.thepegeekapps.easyplanner.util.Utilities;

public class BaseScreen extends FragmentActivity implements OnApiResponseListener {
	
	public static final String APP_TAG = "EASY_PLANNER"; 
	
	protected ApiResponseReceiver responseReceiver;
	protected ProgressDialog progressDialog;
	protected Settings settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(Utilities.isTabletDevice(this) ? ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE : 
			ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		settings = new Settings(this);
	}
	
	public void showProgressDialog(int messageResId) {
		showProgressDialog(getString(messageResId));
	}
	
	public void showProgressDialog(String message) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog();
		}
		progressDialog.setText(message);
		if (!progressDialog.isVisible() && !progressDialog.isAdded()) {
			try {
				progressDialog.show(getSupportFragmentManager(), "ProgressDialog");
			} catch (Exception e) { // IllegalStateException: Can not perform this action after onSaveInstanceState
				e.printStackTrace();
			}
		}
	}
	
	public void hideProgressDialog() {
		if (progressDialog != null) {
			try {
				progressDialog.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter intentFilter = new IntentFilter(ApiService.ACTION_API_RESULT);
		responseReceiver = new ApiResponseReceiver(this);
		LocalBroadcastManager.getInstance(this).registerReceiver(
			responseReceiver, intentFilter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(responseReceiver);
	}
	
	public void showConnectionErrorDialog() {
		showInfoDialog(R.string.information, R.string.no_connection);
	}
	
	protected void showInfoDialog(int titleResId, int messageResId) {
		showInfoDialog(getString(titleResId), getString(messageResId));
	}
	
	protected void showInfoDialog(String title, String message) {
		InfoDialog dialog = new InfoDialog();
		dialog.setTitle(title);
		dialog.setText(message);
		try {
			dialog.show(getSupportFragmentManager(), "InfoDialog");
		} catch (Exception e) { // IllegalStateException: Can not perform this action after onSaveInstanceState
			e.printStackTrace();
		}
	}
	
	public void hideSoftKeyborad() {
		View view = getCurrentFocus();
		if (view == null) {
			return;
		}
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	public void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
	
	public static void log(String tag, String message) {
		Log.d(tag, message);
	}
	
	public static void log(String message) {
		log(APP_TAG, message);
	}
	
	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {}

}
