package com.thepegeekapps.easyplanner.screen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.storage.Settings;
import com.thepegeekapps.easyplanner.util.Utilities;

public class SplashScreen extends BaseScreen {
	
	public static final int STOPSPLASH = 0;
	public static final long SPLASHTIME = 2000;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		
		Message msg = new Message();  
        msg.what = STOPSPLASH;  
        splashHandler.sendMessageDelayed(msg, SPLASHTIME);
	}
	
	@Override
	public void onBackPressed() {}
	
	private Handler splashHandler = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {
        	if (msg.what == STOPSPLASH) {
        		String token = settings.getString(Settings.TOKEN);
        		long expireDate = settings.getLong(Settings.TOKEN_EXPIRE_DATE);
        		boolean isExpired = System.currentTimeMillis() > expireDate;
        		if (TextUtils.isEmpty(token) || isExpired) {
        			startLoginScreen();
        		} else {
        			startMainScreen();
        		}
        	}
        	super.handleMessage(msg);
        }
    }; 
    
    private void startLoginScreen() {
    	Intent intent = new Intent(this, LoginScreen.class);
		startActivity(intent);
		finish();
    }
    
    private void startMainScreen() {
    	Intent intent = new Intent(this, Utilities.isTabletDevice(this) ? TabletMainScreen.class : MainScreen.class);
		startActivity(intent);
		finish();
    }

}
