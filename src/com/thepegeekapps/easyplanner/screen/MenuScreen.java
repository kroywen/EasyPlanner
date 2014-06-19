package com.thepegeekapps.easyplanner.screen;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.fragment.MenuFragment;
import com.thepegeekapps.easyplanner.lib.slideout.SlideoutHelper;

public class MenuScreen extends FragmentActivity {
	
	private SlideoutHelper mSlideoutHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    mSlideoutHelper = new SlideoutHelper(this);
	    mSlideoutHelper.activate();
	    getSupportFragmentManager().beginTransaction().add(R.id.slideout_placeholder, new MenuFragment(), "menu").commit();
	    mSlideoutHelper.open();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			mSlideoutHelper.close();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	public SlideoutHelper getSlideoutHelper(){
		return mSlideoutHelper;
	}

}
