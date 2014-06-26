package com.thepegeekapps.easyplanner.screen;

import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.fragment.DayFragment;
import com.thepegeekapps.easyplanner.fragment.MonthFragment;
import com.thepegeekapps.easyplanner.fragment.OnDataChangeListener;
import com.thepegeekapps.easyplanner.fragment.OnTimeSelectListener;
import com.thepegeekapps.easyplanner.fragment.WeekFragment;
import com.thepegeekapps.easyplanner.model.Clas;
import com.thepegeekapps.easyplanner.storage.db.DatabaseHelper;
import com.thepegeekapps.easyplanner.storage.db.DatabaseStorage;

public class ClassScreen extends FragmentActivity implements OnClickListener, OnCheckedChangeListener, OnPageChangeListener, 
	OnDataChangeListener, OnTimeSelectListener {
	
	private ViewPager pager;
	private ClassFragmentPagerAdapter pagerAdapter;
	private RadioGroup viewSelectorGroup;
	
	private DatabaseStorage dbStorage;
	private long classId;
	private Clas clas;
	
	private Calendar calendar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.class_screen);
		dbStorage = new DatabaseStorage(this);
		calendar = Calendar.getInstance();
		getIntentData();
		initializeViews();
		updateViews();
	}
	
	private void getIntentData() {
		Intent intent = getIntent();
		if (intent != null) {
			classId = intent.getLongExtra(DatabaseHelper.FIELD_ID, 0);
			clas = dbStorage.getClassById(classId);
		}
	}
	
	private void initializeViews() {
		findViewById(R.id.addClassBtn).setVisibility(View.INVISIBLE);
		
		TextView titleView = (TextView) findViewById(R.id.title);
		titleView.setText(clas.getName());
		
		ImageView menuBtn = (ImageView) findViewById(R.id.menuBtn);
		menuBtn.setImageResource(R.drawable.back_icon);
		menuBtn.setOnClickListener(this);
		
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setOnPageChangeListener(this);
		
		viewSelectorGroup = (RadioGroup) findViewById(R.id.viewSelectorGroup);
		viewSelectorGroup.setOnCheckedChangeListener(this);
	}
	
	private void updateViews() {
		pagerAdapter = new ClassFragmentPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(pagerAdapter);
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
		pager.setCurrentItem(position, true);
	}

	@Override
	public void onPageScrollStateChanged(int state) {}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

	@Override
	public void onPageSelected(int position) {
		int id = (position == 0) ? R.id.dayBtn : (position == 1) ? R.id.weekBtn : R.id.monthBtn;
		viewSelectorGroup.setOnCheckedChangeListener(null);
		viewSelectorGroup.check(id);
		viewSelectorGroup.setOnCheckedChangeListener(this);
	}
	
	@Override
	public void onDataChanged() {
		WeekFragment weekFragment = (WeekFragment) pagerAdapter.findFragmentByPosition(1);
		if (weekFragment != null) {
			weekFragment.updateViews();
		}
		MonthFragment monthFragment = (MonthFragment) pagerAdapter.findFragmentByPosition(2);
		if (monthFragment != null) {
			monthFragment.updateViews();
		}
	}
	
	@Override
	public void onTimeSelected(int position, long time) {
		if (position == 0) {
			WeekFragment weekFragment = (WeekFragment) pagerAdapter.findFragmentByPosition(1);
			if (weekFragment != null) {
				weekFragment.setCurrentTime(time);
			}
			MonthFragment monthFragment = (MonthFragment) pagerAdapter.findFragmentByPosition(2);
			if (monthFragment != null) {
				monthFragment.setCurrentTime(time);
			}
		} else if (position == 1) {
			DayFragment dayFragment = (DayFragment) pagerAdapter.findFragmentByPosition(0);
			if (dayFragment != null) {
				dayFragment.setCurrentTime(time);
			}
			MonthFragment monthFragment = (MonthFragment) pagerAdapter.findFragmentByPosition(2);
			if (monthFragment != null) {
				monthFragment.setCurrentTime(time);
			}
		} else {
			DayFragment dayFragment = (DayFragment) pagerAdapter.findFragmentByPosition(0);
			if (dayFragment != null) {
				dayFragment.setCurrentTime(time);
			}
			WeekFragment weekFragment = (WeekFragment) pagerAdapter.findFragmentByPosition(1);
			if (weekFragment != null) {
				weekFragment.setCurrentTime(time);
			}
		}
	}
	
	public long getTime() {
		return calendar.getTimeInMillis();
	}
	
	public void setTime(long time) {
		calendar.setTimeInMillis(time);
	}
	
	private class ClassFragmentPagerAdapter extends FragmentPagerAdapter {
		
		public ClassFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public Fragment getItem(int position) {
			return (position == 0) ? DayFragment.newInstance(classId) : 
				(position == 1) ? WeekFragment.newInstance(classId) :
				MonthFragment.newInstance(classId);
		}
		
		@Override
		public int getCount() {
			return 3;
		}
		
		public Fragment findFragmentByPosition(int position) {
		    return getSupportFragmentManager().findFragmentByTag("android:switcher:" + pager.getId() + ":" + getItemId(position));
		}
		
	}

		

}
