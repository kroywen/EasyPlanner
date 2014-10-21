package com.thepegeekapps.easyplanner.screen;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.fragment.ClassesFragment;
import com.thepegeekapps.easyplanner.fragment.TasksFragment;

public class MainScreen extends BaseMainScreen implements OnPageChangeListener {
	
	private View classesBtn;
	private View tasksBtn;
	private ViewPager pager;
	private MainFragmentPagerAdapter pagerAdapter;
	private int tabSelected;
	
	@Override
	protected void initializeViews() {
		super.initializeViews();
		
		pager = (ViewPager) findViewById(R.id.pager);
		
		classesBtn = findViewById(R.id.classesBtn);
		classesBtn.setOnClickListener(this);
		
		tasksBtn = findViewById(R.id.tasksBtn);
		tasksBtn.setOnClickListener(this);
	}
	
	@Override
	protected void updateViews() {
		pagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(pagerAdapter);
		pager.setOnPageChangeListener(this);
		pager.setCurrentItem(tabSelected);
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.classesBtn:
			if (pager.getCurrentItem() != 0) {
				pager.setCurrentItem(0, true);
			}
			break;
		case R.id.tasksBtn:
			if (pager.getCurrentItem() != 1) {
				pager.setCurrentItem(1, true);
			}
			break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

	@Override
	public void onPageSelected(int position) {
		addClassBtn.setVisibility(position == 0 ? View.VISIBLE : View.INVISIBLE);
		tabSelected = position;
	}
	
	class MainFragmentPagerAdapter extends FragmentPagerAdapter {
		
		public MainFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public Fragment getItem(int position) {
			return (position == 0) ? ClassesFragment.newInstance(timeSelected) : TasksFragment.newInstance(timeSelected);
		}
		
		@Override
		public int getCount() {
			return 2;
		}
		
		public Fragment findFragmentByPosition(int position) {
		    return getSupportFragmentManager().findFragmentByTag("android:switcher:" + pager.getId() + ":" + getItemId(position));
		}
		
	}
	
	protected ClassesFragment getClassesFragment() {
		return (ClassesFragment) pagerAdapter.findFragmentByPosition(0);
	}
	
	protected TasksFragment getTasksFragment() {
		return (TasksFragment) pagerAdapter.findFragmentByPosition(1);
	}

}
