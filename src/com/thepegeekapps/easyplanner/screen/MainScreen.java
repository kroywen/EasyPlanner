package com.thepegeekapps.easyplanner.screen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.dialog.InputDialog;
import com.thepegeekapps.easyplanner.dialog.InputDialog.OnInputClickListener;
import com.thepegeekapps.easyplanner.fragment.ClassesFragment;
import com.thepegeekapps.easyplanner.fragment.TasksFragment;
import com.thepegeekapps.easyplanner.lib.slideout.SlideoutActivity;
import com.thepegeekapps.easyplanner.model.Clas;
import com.thepegeekapps.easyplanner.storage.db.DatabaseStorage;

public class MainScreen extends FragmentActivity implements OnClickListener, OnPageChangeListener, OnCheckedChangeListener {
	
	public static final int TIME_ALL = 0;
	public static final int TIME_TODAY = 1;
	
	public static final int TAB_CLASSES = 0;
	public static final int TAB_TASKS = 1;
	
	private ViewPager pager;
	private MainFragmentPagerAdapter pagerAdapter;	
	private View classesBtn;
	private View tasksBtn;
	private View addClassBtn;
	private RadioGroup timeSelectorGroup;
	private DatabaseStorage dbStorage;
	
	private int tabSelected;
	private int timeSelected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);
		dbStorage = new DatabaseStorage(this);
		initializeViews();
		updateViews();
	}
	
	private void initializeViews() {
		pager = (ViewPager) findViewById(R.id.pager);
		
		classesBtn = findViewById(R.id.classesBtn);
		classesBtn.setOnClickListener(this);
		
		tasksBtn = findViewById(R.id.tasksBtn);
		tasksBtn.setOnClickListener(this);
		
		addClassBtn = findViewById(R.id.addClassBtn);
		addClassBtn.setOnClickListener(this);
		
		timeSelectorGroup = (RadioGroup) findViewById(R.id.timeSelectorGroup);
		timeSelectorGroup.setOnCheckedChangeListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		findViewById(R.id.menuBtn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				findViewById(R.id.menuBtn).setOnClickListener(null);
				int width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
				SlideoutActivity.prepare(MainScreen.this, R.id.inner_content, width);
				startActivity(new Intent(MainScreen.this, MenuScreen.class));
				overridePendingTransition(0, 0);
			}
		});
	}
	
	private void updateViews() {
		pagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(pagerAdapter);
		pager.setOnPageChangeListener(this);
		pager.setCurrentItem(tabSelected);
	}

	@Override
	public void onClick(View view) {
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
		case R.id.addClassBtn:
			showAddClassDialog();
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
	
	private void showAddClassDialog() {
		InputDialog dialog = new InputDialog();
		dialog.setTitle(getString(R.string.information));
		dialog.setText(getString(R.string.enter_class_name));
		dialog.setHint(getString(R.string.class_name));
		dialog.setButtons(getString(R.string.add), getString(R.string.cancel), new OnInputClickListener() {
			@Override
			public void onInputOkClick(String inputText) {
				if (TextUtils.isEmpty(inputText)) {
					Toast.makeText(MainScreen.this, R.string.enter_class_name, Toast.LENGTH_SHORT).show();
				} else {
					Clas clas = new Clas(0, inputText, System.currentTimeMillis());
					dbStorage.addClass(clas);
					updateViews();
				}
			}
			@Override
			public void onInputCancelClick() {}
		});
		dialog.show(getSupportFragmentManager(), "add_class");
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		timeSelected = (checkedId == R.id.todayBtn) ? TIME_TODAY : TIME_ALL;
		
		ClassesFragment cf = (ClassesFragment) pagerAdapter.findFragmentByPosition(0);
		cf.setTimeSelected(timeSelected);
		
		TasksFragment tf = (TasksFragment) pagerAdapter.findFragmentByPosition(1);
		tf.setTimeSelected(timeSelected);
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

}
