package com.thepegeekapps.easyplanner.screen;

import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.thepegeekapps.easyplanner.api.ApiData;
import com.thepegeekapps.easyplanner.api.ApiResponse;
import com.thepegeekapps.easyplanner.api.ApiService;
import com.thepegeekapps.easyplanner.dialog.InputDialog;
import com.thepegeekapps.easyplanner.dialog.InputDialog.OnInputClickListener;
import com.thepegeekapps.easyplanner.fragment.ClassesFragment;
import com.thepegeekapps.easyplanner.fragment.TasksFragment;
import com.thepegeekapps.easyplanner.lib.slideout.SlideoutActivity;
import com.thepegeekapps.easyplanner.model.Clas;
import com.thepegeekapps.easyplanner.model.Task;
import com.thepegeekapps.easyplanner.util.Utilities;

public class MainScreen extends BaseScreen implements OnClickListener, OnPageChangeListener, OnCheckedChangeListener {
	
	public static final int MENU_REQUEST_CODE = 0;
	
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
	
	private int tabSelected;
	private int timeSelected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);
		initializeViews();
		updateViews();
		
		if (Utilities.isConnectionAvailable(this)) {
			requestClasses();
			requestTasks();
		} else {
			showConnectionErrorDialog();
		}
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
				startActivityForResult(new Intent(MainScreen.this, MenuScreen.class), MENU_REQUEST_CODE);
				overridePendingTransition(0, 0);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MENU_REQUEST_CODE) {
			if (data == null) {
				return;
			}
			boolean logout = data.getBooleanExtra("logout", false);
			if (logout) {
				Intent intent = new Intent(this, LoginScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
		}
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
		final InputDialog dialog = new InputDialog();
		dialog.setTitle(getString(R.string.information));
		dialog.setText(getString(R.string.enter_class_name));
		dialog.setHint(getString(R.string.class_name));
		dialog.setButtons(getString(R.string.add), getString(R.string.cancel), new OnInputClickListener() {
			@Override
			public void onInputOkClick(String inputText) {
				if (TextUtils.isEmpty(inputText)) {
					Toast.makeText(MainScreen.this, R.string.enter_class_name, Toast.LENGTH_SHORT).show();
				} else {
					dialog.hideSoftKeyborad();
					requestAddClass(inputText);
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
	
	public void tryDeleteClass(Clas clas) {
		if (clas == null) {
			return;
		}
		if (Utilities.isConnectionAvailable(this)) {
			requestDeleteClass(clas);
		} else {
			showConnectionErrorDialog();
		}
	}
	
	public void tryMarkTask(Task task, boolean showProgress) {
		if (task == null) {
			return;
		}
		if (Utilities.isConnectionAvailable(this)) {
			requestMarkTask(task, showProgress);
		} else {
			showConnectionErrorDialog();
		}
	}
	
	public void tryDeleteTask(Task task) {
		if (task == null) {
			return;
		}
		if (Utilities.isConnectionAvailable(this)) {
			requestDeleteTask(task);
		} else {
			showConnectionErrorDialog();
		}
	}
	
	public void tryAddTask(Task task) {
		if (task == null) {
			return;
		}
		if (Utilities.isConnectionAvailable(this)) {
			requestAddTask(task);
		} else {
			showConnectionErrorDialog();
		}
	}
	
	private void requestMarkTask(Task task, boolean showProgress) {
		Intent intent = new Intent(this, ApiService.class);
		intent.setData(Uri.parse(ApiData.TASK));
		intent.setAction(ApiData.PUT);
		intent.putExtra(ApiData.TOKEN, settings.getString(ApiData.TOKEN));
		intent.putExtra(ApiData.PARAM_CLASS_ID, task.getClassId());
		intent.putExtra(ApiData.PARAM_TASK_ID, task.getId());
		intent.putExtra(ApiData.PARAM_COMPLETED, task.isCompleted() ? "yes" : "no");
		startService(intent);
		if (showProgress) {
			showProgressDialog(R.string.changing_task_status);
		}
	}
	
	private void requestDeleteTask(Task task) {
		Intent intent = new Intent(this, ApiService.class);
		intent.setData(Uri.parse(ApiData.TASK));
		intent.setAction(ApiData.DELETE);
		intent.putExtra(ApiData.TOKEN, settings.getString(ApiData.TOKEN));
		intent.putExtra(ApiData.PARAM_CLASS_ID, task.getClassId());
		intent.putExtra(ApiData.PARAM_TASK_ID, task.getId());
		startService(intent);
		showProgressDialog(R.string.deleting_task);
	}
	
	private void requestDeleteClass(Clas clas) {
		Intent intent = new Intent(this, ApiService.class);
		intent.setData(Uri.parse(ApiData.CLASSES));
		intent.setAction(ApiData.DELETE);
		intent.putExtra(ApiData.PARAM_ID, clas.getId());
		intent.putExtra(ApiData.TOKEN, settings.getString(ApiData.TOKEN));
		startService(intent);
		showProgressDialog(R.string.deleting_class);
	}
	
	private void requestAddTask(Task task) {
		Intent intent = new Intent(this, ApiService.class);
		intent.setData(Uri.parse(ApiData.TASK));
		intent.setAction(ApiData.POST);
		intent.putExtra(ApiData.TOKEN, settings.getString(ApiData.TOKEN));
		intent.putExtra(ApiData.PARAM_CLASS_ID, task.getClassId());
		intent.putExtra(ApiData.PARAM_TEXT, task.getDescription());
		intent.putExtra(ApiData.PARAM_DATE, Utilities.parseTime(task.getTime(), Utilities.dd_MM_yyyy));
		if (task.hasParentId()) {
			intent.putExtra(ApiData.PARAM_PARENT_ID, task.getParentId());
		}
		startService(intent);
		showProgressDialog(R.string.adding_task);
	}
	
	private void requestClasses() {
		Intent intent = new Intent(this, ApiService.class);
		intent.setData(Uri.parse(ApiData.CLASSES));
		intent.setAction(ApiData.GET);
		intent.putExtra(ApiData.TOKEN, settings.getString(ApiData.TOKEN));
		startService(intent);
		showProgressDialog(R.string.loading_classes);
	}
	
	private void requestAddClass(String className) {
		Intent intent = new Intent(this, ApiService.class);
		intent.setData(Uri.parse(ApiData.CLASSES));
		intent.setAction(ApiData.POST);
		intent.putExtra(ApiData.PARAM_NAME, className);
		intent.putExtra(ApiData.TOKEN, settings.getString(ApiData.TOKEN));
		startService(intent);
		showProgressDialog(R.string.add_class);
	}
	
	private void requestTasks() {
		Intent intent = new Intent(this, ApiService.class);
		intent.setData(Uri.parse(ApiData.TASK));
		intent.setAction(ApiData.GET);
		intent.putExtra(ApiData.TOKEN, settings.getString(ApiData.TOKEN));
		startService(intent);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		hideProgressDialog();
		if (apiStatus == ApiService.API_STATUS_SUCCESS) {
			int status = apiResponse.getStatus();
			String requestName = apiResponse.getRequestName();
			String method = apiResponse.getMethod();
			if (status == ApiResponse.STATUS_SUCCESS) {
				if (ApiData.CLASSES.equalsIgnoreCase(requestName)) {
					if (ApiData.GET.equalsIgnoreCase(method)) {
						List<Clas> classes = (List<Clas>) apiResponse.getData();
						ClassesFragment cf = (ClassesFragment) pagerAdapter.findFragmentByPosition(0);
						cf.setClasses(classes);
					} else if (ApiData.POST.equalsIgnoreCase(method)) {
						requestClasses();
					} else if (ApiData.DELETE.equalsIgnoreCase(method)) {
						requestClasses();
					}
				} else if (ApiData.TASK.equalsIgnoreCase(requestName)) {
					if (ApiData.GET.equalsIgnoreCase(method)) {
						List<Task> tasks = (List<Task>) apiResponse.getData();
						TasksFragment tf = (TasksFragment) pagerAdapter.findFragmentByPosition(1);
						tf.setTasks(tasks);
					} else if (ApiData.PUT.equalsIgnoreCase(method) ||
						ApiData.DELETE.equalsIgnoreCase(method) ||
						ApiData.POST.equalsIgnoreCase(method)) 
					{
						requestTasks();
					}
				}
			} else {
				showInfoDialog(getString(R.string.error), apiResponse.getError());
			}
		} else {
			showInfoDialog(getString(R.string.error), apiResponse.getError());
		}
	}

}
