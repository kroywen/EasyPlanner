package com.thepegeekapps.easyplanner.screen;

import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.fragment.ClassFragment;
import com.thepegeekapps.easyplanner.fragment.DayFragment;
import com.thepegeekapps.easyplanner.fragment.MonthFragment;
import com.thepegeekapps.easyplanner.fragment.WeekFragment;
import com.thepegeekapps.easyplanner.storage.db.DatabaseHelper;

public class ClassScreen extends BaseScreen implements OnClickListener, OnCheckedChangeListener {
	
	private RadioGroup viewSelectorGroup;
	private ClassFragment fragment;
	
	private long classId;
	private String className;
	
	private Calendar calendar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.class_screen);
		calendar = Calendar.getInstance();
		getIntentData();
		initializeViews();
		selectItem(0);
	}
	
	private void getIntentData() {
		Intent intent = getIntent();
		if (intent != null) {
			classId = intent.getLongExtra(DatabaseHelper.FIELD_ID, 0);
			className = intent.getStringExtra(DatabaseHelper.FIELD_NAME);
		}
	}
	
	private void initializeViews() {
		findViewById(R.id.addClassBtn).setVisibility(View.INVISIBLE);
		
		TextView titleView = (TextView) findViewById(R.id.title);
		titleView.setText(className);
		
		ImageView menuBtn = (ImageView) findViewById(R.id.menuBtn);
		menuBtn.setImageResource(R.drawable.back_icon);
		menuBtn.setOnClickListener(this);
		
		viewSelectorGroup = (RadioGroup) findViewById(R.id.viewSelectorGroup);
		viewSelectorGroup.setOnCheckedChangeListener(this);
	}
	
	public void selectItem(int position) {
		fragment = getClassFragment(position);
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.content, fragment).commit();
	}
	
	private ClassFragment getClassFragment(int i) {
		return (i == 0) ? DayFragment.newInstance(classId) : 
    		(i == 1) ? WeekFragment.newInstance(classId) : MonthFragment.newInstance(classId);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.menuBtn) {
			finish();
		}
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		int position = (checkedId == R.id.dayBtn) ? 0 :
			(checkedId == R.id.weekBtn) ? 1 : 2;
		selectItem(position);
	}
	
	public void checkDayFragment() {
		viewSelectorGroup.check(R.id.dayBtn);
	}
	
	public long getTime() {
		return calendar.getTimeInMillis();
	}
	
	public void setTime(long time) {
		calendar.setTimeInMillis(time);
	}

}
