package com.thepegeekapps.easyplanner.screen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.fragment.ClassesFragment;
import com.thepegeekapps.easyplanner.fragment.TasksFragment;
import com.thepegeekapps.easyplanner.lib.slideout.SlideoutActivity;
import com.thepegeekapps.easyplanner.model.Clas;
import com.thepegeekapps.easyplanner.storage.db.DatabaseStorage;

public class MainScreen extends FragmentActivity implements OnClickListener, OnPageChangeListener {
	
	private ViewPager pager;
	private PagerAdapter pagerAdapter;	
	private View classesBtn;
	private View tasksBtn;
	private View addClassBtn;
	private DatabaseStorage dbStorage;

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
		pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(pagerAdapter);
		pager.setOnPageChangeListener(this);
	}
	
	private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
		
		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public Fragment getItem(int position) {
			return (position == 0) ? new ClassesFragment() : new TasksFragment();
		}
		
		@Override
		public int getCount() {
			return 2;
		}
		
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
	}
	
	private void showAddClassDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.add_class_dialog, null);
		final EditText className = (EditText) view.findViewById(R.id.className);
		builder.setTitle(R.string.add_class)
			.setView(view)
			.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (TextUtils.isEmpty(className.getText().toString())) {
						Toast.makeText(MainScreen.this, R.string.class_name_empty, Toast.LENGTH_SHORT).show();
					} else {
						String classname = className.getText().toString().trim(); 
						Clas clas = new Clas(0, classname, System.currentTimeMillis());
						dbStorage.addClass(clas);
						updateViews();
						dialog.dismiss();
					}
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.create()
			.show();
	}

}
