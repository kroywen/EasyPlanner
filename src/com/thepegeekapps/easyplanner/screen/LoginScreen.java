package com.thepegeekapps.easyplanner.screen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.api.ApiData;
import com.thepegeekapps.easyplanner.api.ApiResponse;
import com.thepegeekapps.easyplanner.api.ApiService;
import com.thepegeekapps.easyplanner.storage.Settings;
import com.thepegeekapps.easyplanner.util.Utilities;

public class LoginScreen extends BaseScreen implements OnClickListener {
	
	private EditText email;
	private EditText password;
	private Button loginBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);
		initializeViews();
	}
	
	private void initializeViews() {
		email = (EditText) findViewById(R.id.email);
		email.setText(settings.getString(Settings.EMAIL));
		password = (EditText) findViewById(R.id.password);
		password.setText(settings.getString(Settings.PASSWORD));
		loginBtn = (Button) findViewById(R.id.loginBtn);
		loginBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loginBtn:
			if (Utilities.isConnectionAvailable(this)) {
				hideSoftKeyborad();
				login();
			} else {
				showConnectionErrorDialog();
			}
			break;
		}
	}
	
	private void login() {
		Intent intent = new Intent(this, ApiService.class);
		intent.setData(Uri.parse(ApiData.AUTHENTICATE));
		intent.setAction(ApiData.POST);
		intent.putExtra(ApiData.PARAM_EMAIL, email.getText().toString().trim());
		intent.putExtra(ApiData.PARAM_PASSWORD, password.getText().toString().trim());
		intent.putExtra(ApiData.PARAM_APIKEY, ApiData.APIKEY);
		startService(intent);
		showProgressDialog(R.string.authenticating);
	}
	
	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		hideProgressDialog();
		if (apiStatus == ApiService.API_STATUS_SUCCESS) {
			int status = apiResponse.getStatus();
			if (status == ApiResponse.STATUS_SUCCESS) {
				if (ApiData.AUTHENTICATE.equalsIgnoreCase(apiResponse.getRequestName())) {
					settings.setString(Settings.EMAIL, email.getText().toString().trim());
					settings.setString(Settings.PASSWORD, password.getText().toString().trim());
					String token = (String) apiResponse.getData();
					settings.setString(Settings.TOKEN, token);
					settings.setLong(Settings.TOKEN_EXPIRE_DATE, System.currentTimeMillis() + 30*60*1000);
					startMainScreen();
				}
			} else {
				showInfoDialog(getString(R.string.error), apiResponse.getError());
			}
		} else {
			showInfoDialog(getString(R.string.error), apiResponse.getError());
		}
	}
	
	private void startMainScreen() {
		Intent intent = new Intent(this, MainScreen.class);
		startActivity(intent);
		finish();
	}

}
