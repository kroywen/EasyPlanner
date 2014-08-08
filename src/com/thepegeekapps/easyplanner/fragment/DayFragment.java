package com.thepegeekapps.easyplanner.fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.api.ApiData;
import com.thepegeekapps.easyplanner.api.ApiResponse;
import com.thepegeekapps.easyplanner.api.ApiService;
import com.thepegeekapps.easyplanner.dialog.ConfirmationDialog;
import com.thepegeekapps.easyplanner.dialog.InputDialog;
import com.thepegeekapps.easyplanner.dialog.InputDialog.OnInputClickListener;
import com.thepegeekapps.easyplanner.model.Activiti;
import com.thepegeekapps.easyplanner.model.Homework;
import com.thepegeekapps.easyplanner.model.Resource;
import com.thepegeekapps.easyplanner.model.Task;
import com.thepegeekapps.easyplanner.screen.AddResourceScreen;
import com.thepegeekapps.easyplanner.screen.BaseScreen;
import com.thepegeekapps.easyplanner.screen.ClassScreen;
import com.thepegeekapps.easyplanner.storage.db.DatabaseHelper;
import com.thepegeekapps.easyplanner.ui.view.DateView;
import com.thepegeekapps.easyplanner.ui.view.DateView.OnDateChangedListener;
import com.thepegeekapps.easyplanner.util.Utilities;

public class DayFragment extends ClassFragment implements OnDateChangedListener, OnClickListener {
	
	public static final int ADD_RESOURCE_REQUEST_CODE = 0;
	
	private DateView dateView;
	
	private TextView currentDateView;
	private TextView currentDayView;
	
	private ImageView addActivityBtn;
	private LinearLayout activitiesContent;
	private TextView activitiesEmptyView;
	
	private ImageView addMediaBtn;
	private LinearLayout mediaContent;
	private TextView mediaEmptyView;
	
	private ImageView addHomeworkBtn;
	private LinearLayout homeworksContent;
	private TextView homeworksEmptyView;
	
	private ImageView addTaskBtn;
	private LinearLayout tasksContent;
	private TextView tasksEmptyView;
	
	private String[] daysOfWeek;
	private List<Activiti> activities;
	private List<Resource> resources;
	private List<Homework> homeworks;
	private List<Task> tasks;
	
	public static DayFragment newInstance(long classId) {
		DayFragment f = new DayFragment();
		Bundle args = new Bundle();
		args.putLong(DatabaseHelper.FIELD_ID, classId);
		f.setArguments(args);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		daysOfWeek = getResources().getStringArray(R.array.days_of_week);
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.day_fragment, null);
		initializeViews(view);
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		dateView.setDate(getTime());
	}
	
	private void initializeViews(View view) {
		dateView = (DateView) view.findViewById(R.id.dateView);
		dateView.setOnDateChangedListener(this);
		
		currentDateView = (TextView) view.findViewById(R.id.currentDateView);
		currentDayView = (TextView) view.findViewById(R.id.currentDayView);
		
		addActivityBtn = (ImageView) view.findViewById(R.id.addActivityBtn);
		addActivityBtn.setOnClickListener(this);
		activitiesContent = (LinearLayout) view.findViewById(R.id.activitiesContent);
		activitiesEmptyView = (TextView) view.findViewById(R.id.activitiesEmptyView);
		
		addMediaBtn = (ImageView) view.findViewById(R.id.addMediaBtn);
		addMediaBtn.setOnClickListener(this);
		mediaContent = (LinearLayout) view.findViewById(R.id.mediaContent);
		mediaEmptyView = (TextView) view.findViewById(R.id.mediaEmptyView);
		
		addHomeworkBtn = (ImageView) view.findViewById(R.id.addHomeworkBtn);
		addHomeworkBtn.setOnClickListener(this);
		homeworksContent = (LinearLayout) view.findViewById(R.id.homeworksContent);
		homeworksEmptyView = (TextView) view.findViewById(R.id.homeworksEmptyView);
		
		addTaskBtn = (ImageView) view.findViewById(R.id.addTaskBtn);
		addTaskBtn.setOnClickListener(this);
		tasksContent = (LinearLayout) view.findViewById(R.id.tasksContent);
		tasksEmptyView = (TextView) view.findViewById(R.id.tasksEmptyView);
	}
	
	public void updateViews() {
		updateCurrentDay();
		requestActivities();
		requestResources();
		requestHomeworks();
		requestTasks();
	}
	
	private void updateCurrentDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(getTime());
		currentDateView.setText(Utilities.addLeadingZero(calendar.get(Calendar.DAY_OF_MONTH)));
		currentDayView.setText(daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK)]);
	}
	
	private void requestActivities() {
		long dayStart = Utilities.getDayStart(getTime()) / 1000;
		long dayEnd = Utilities.getDayEnd(getTime()) / 1000;
		
		Intent intent = new Intent(getActivity(), ApiService.class);
		intent.setData(Uri.parse(ApiData.ACTIVITY));
		intent.setAction(ApiData.GET);
		intent.putExtra(ApiData.TOKEN, settings.getString(ApiData.TOKEN));
		intent.putExtra(ApiData.PARAM_CLASS_ID, classId);
		intent.putExtra(ApiData.PARAM_DATE_START, dayStart);
		intent.putExtra(ApiData.PARAM_DATE_END, dayEnd);
		getActivity().startService(intent);
	}
	
	private void updateActivities() {
		if (Utilities.isEmpty(activities)) {
			activitiesEmptyView.setVisibility(View.VISIBLE);
			activitiesContent.setVisibility(View.GONE);
		} else {
			populateActivitiesContent(activities);			
			activitiesEmptyView.setVisibility(View.GONE);
			activitiesContent.setVisibility(View.VISIBLE);
		}
	}
	
	@SuppressLint("InflateParams")
	private void populateActivitiesContent(List<Activiti> activities) {
		activitiesContent.removeAllViews();
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i=0; i<activities.size(); i++) {
			final Activiti activity = activities.get(i);
			View view = inflater.inflate(R.layout.activity_list_item, null);
			view.setBackgroundResource(R.drawable.list_item_selector);
			
			TextView description = (TextView) view.findViewById(R.id.description);
			description.setText(activity.getDescription());
			
			ImageView removeBtn = (ImageView) view.findViewById(R.id.removeBtn);
			removeBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showDeleteActivityDialog(activity);
				}
			});
			
			activitiesContent.addView(view);
		}
	}
	
	private void showAddActivityDialog() {
		InputDialog dialog = new InputDialog();
		dialog.setTitle(getString(R.string.information));
		dialog.setText(getString(R.string.describe_activity));
		dialog.setHint(getString(R.string.activity));
		dialog.setButtons(getString(R.string.add), getString(R.string.cancel), new OnInputClickListener() {
			@Override
			public void onInputOkClick(String inputText) {
				if (TextUtils.isEmpty(inputText)) {
					Toast.makeText(getActivity(), R.string.describe_activity, Toast.LENGTH_SHORT).show();
				} else {
					requestAddActivity(inputText);
				}
			}
			@Override
			public void onInputCancelClick() {}
		});
		dialog.show(getChildFragmentManager(), "AddActivityDialog");
	}
	
	private void requestAddActivity(String description) {
		Intent intent = new Intent(getActivity(), ApiService.class);
		intent.setData(Uri.parse(ApiData.ACTIVITY));
		intent.setAction(ApiData.POST);
		intent.putExtra(ApiData.TOKEN, settings.getString(ApiData.TOKEN));
		intent.putExtra(ApiData.PARAM_CLASS_ID, classId);
		intent.putExtra(ApiData.PARAM_TEXT, description);
		intent.putExtra(ApiData.PARAM_DATE, Utilities.parseTime(dateView.getSelectedTime(), Utilities.dd_MM_yyyy));
		getActivity().startService(intent);
		showProgressDialog(R.string.adding_activity);
	}
	
	private void showDeleteActivityDialog(final Activiti activity) {
		final ConfirmationDialog dialog = new ConfirmationDialog();
		dialog.setTitle(getString(R.string.information));
		dialog.setText(getString(R.string.delete_activity_confirmation));
		dialog.setOkListener(getString(R.string.delete), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				requestDeleteActivity(activity);
				dialog.dismiss();
			}
		});
		dialog.setCancelListener(getString(R.string.cancel), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.show(getChildFragmentManager(), "DeleteActivityDialog");
	}
	
	private void requestDeleteActivity(Activiti activity) {
		Intent intent = new Intent(getActivity(), ApiService.class);
		intent.setData(Uri.parse(ApiData.ACTIVITY));
		intent.setAction(ApiData.DELETE);
		intent.putExtra(ApiData.TOKEN, settings.getString(ApiData.TOKEN));
		intent.putExtra(ApiData.PARAM_CLASS_ID, classId);
		intent.putExtra(ApiData.PARAM_ACTIVITY_ID, activity.getId());
		getActivity().startService(intent);
		((BaseScreen) getActivity()).showProgressDialog(R.string.deleting_activity);
	}
	
	private void requestResources(){
		long dayStart = Utilities.getDayStart(getTime()) / 1000;
		long dayEnd = Utilities.getDayEnd(getTime()) / 1000;
		
		Intent intent = new Intent(getActivity(), ApiService.class);
		intent.setData(Uri.parse(ApiData.RESOURCE));
		intent.setAction(ApiData.GET);
		intent.putExtra(ApiData.TOKEN, settings.getString(ApiData.TOKEN));
		intent.putExtra(ApiData.PARAM_CLASS_ID, classId);
		intent.putExtra(ApiData.PARAM_DATE_START, dayStart);
		intent.putExtra(ApiData.PARAM_DATE_END, dayEnd); 
		getActivity().startService(intent);
	}
	
	private void updateResources() {
		if (Utilities.isEmpty(resources)) {
			mediaEmptyView.setVisibility(View.VISIBLE);
			mediaContent.setVisibility(View.GONE);
		} else {
			populateResourcesContent(resources);
			mediaEmptyView.setVisibility(View.GONE);
			mediaContent.setVisibility(View.VISIBLE);
		}
	}
	
	@SuppressLint("InflateParams")
	private void populateResourcesContent(List<Resource> resources) {
		mediaContent.removeAllViews();
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i=0; i<resources.size(); i++) {
			final Resource resource = resources.get(i);
			View view = inflater.inflate(R.layout.activity_list_item, null);
			view.setBackgroundResource(R.drawable.list_item_selector);
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (resource.isUrl()) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(resource.getUrl()));
						startActivity(intent);
					} else if (resource.isFile()) {
						File docDir = new File(Environment.getExternalStorageDirectory() + "/easyplanner/");
						docDir.mkdirs();
						File file = new File(docDir, resource.getTitle());
						if (file.exists()) {
							try {
								openFile(file);
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							new DownloadResourceTask(resource).execute();
						}
					}
				}
			});
			
			TextView description = (TextView) view.findViewById(R.id.description);
			description.setText(resource.getTitle());
			
			ImageView removeBtn = (ImageView) view.findViewById(R.id.removeBtn);
			removeBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showDeleteResourceDialog(resource);
				}
			});
			
			mediaContent.addView(view);
		}
	}
	
	private void showDeleteResourceDialog(final Resource resource) {
		final ConfirmationDialog dialog = new ConfirmationDialog();
		dialog.setTitle(getString(R.string.information));
		dialog.setText(getString(R.string.delete_resource_confirmation));
		dialog.setOkListener(getString(R.string.delete), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				requestDeleteResource(resource);
				dialog.dismiss();
			}
		});
		dialog.setCancelListener(getString(R.string.cancel), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.show(getChildFragmentManager(), "DeleteResourceDialog");
	}
	
	private void requestDeleteResource(Resource resource) {
		Intent intent = new Intent(getActivity(), ApiService.class);
		intent.setData(Uri.parse(ApiData.RESOURCE));
		intent.setAction(ApiData.DELETE);
		intent.putExtra(ApiData.TOKEN, settings.getString(ApiData.TOKEN));
		intent.putExtra(ApiData.PARAM_CLASS_ID, classId);
		intent.putExtra(ApiData.PARAM_RESOURCE_ID, resource.getId());
		getActivity().startService(intent);
		showProgressDialog(R.string.deleting_resource);
	}
	
	private void requestHomeworks() {
		long dayStart = Utilities.getDayStart(getTime()) / 1000;
		long dayEnd = Utilities.getDayEnd(getTime()) / 1000;
		
		Intent intent = new Intent(getActivity(), ApiService.class);
		intent.setData(Uri.parse(ApiData.HOMEWORK));
		intent.setAction(ApiData.GET);
		intent.putExtra(ApiData.TOKEN, settings.getString(ApiData.TOKEN));
		intent.putExtra(ApiData.PARAM_CLASS_ID, classId);
		intent.putExtra(ApiData.PARAM_DATE_START, dayStart);
		intent.putExtra(ApiData.PARAM_DATE_END, dayEnd);
		getActivity().startService(intent);
	}
	
	private void updateHomeworks() {
		if (Utilities.isEmpty(homeworks)) {
			homeworksEmptyView.setVisibility(View.VISIBLE);
			homeworksContent.setVisibility(View.GONE);
		} else {
			populateHomeworksContent(homeworks);			
			homeworksEmptyView.setVisibility(View.GONE);
			homeworksContent.setVisibility(View.VISIBLE);
		}
	}
	
	@SuppressLint("InflateParams")
	private void populateHomeworksContent(List<Homework> homeworks) {
		homeworksContent.removeAllViews();
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i=0; i<homeworks.size(); i++) {
			final Homework homework = homeworks.get(i);
			View view = inflater.inflate(R.layout.activity_list_item, null);
			view.setBackgroundResource(R.drawable.list_item_selector);
			
			TextView description = (TextView) view.findViewById(R.id.description);
			description.setText(homework.getDescription());
			
			ImageView removeBtn = (ImageView) view.findViewById(R.id.removeBtn);
			removeBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showDeleteHomeworkDialog(homework);
				}
			});
			
			homeworksContent.addView(view);
		}
	}
	
	private void showDeleteHomeworkDialog(final Homework homework) {
		final ConfirmationDialog dialog = new ConfirmationDialog();
		dialog.setTitle(getString(R.string.information));
		dialog.setText(getString(R.string.delete_homework_confirmation));
		dialog.setOkListener(getString(R.string.delete), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				requestDeleteHomework(homework);
				dialog.dismiss();
			}
		});
		dialog.setCancelListener(getString(R.string.cancel), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.show(getChildFragmentManager(), "DeleteHomeworkDialog");
	}
	
	private void requestDeleteHomework(Homework homework) {
		Intent intent = new Intent(getActivity(), ApiService.class);
		intent.setData(Uri.parse(ApiData.HOMEWORK));
		intent.setAction(ApiData.DELETE);
		intent.putExtra(ApiData.TOKEN, settings.getString(ApiData.TOKEN));
		intent.putExtra(ApiData.PARAM_CLASS_ID, classId);
		intent.putExtra(ApiData.PARAM_HOMEWORK_ID, homework.getId());
		getActivity().startService(intent);
		showProgressDialog(R.string.deleting_homework);
	}
	
	private void showAddHomeworkDialog() {
		InputDialog dialog = new InputDialog();
		dialog.setTitle(getString(R.string.information));
		dialog.setText(getString(R.string.describe_homework));
		dialog.setHint(getString(R.string.homework));
		dialog.setButtons(getString(R.string.add), getString(R.string.cancel), new OnInputClickListener() {
			@Override
			public void onInputOkClick(String inputText) {
				if (TextUtils.isEmpty(inputText)) {
					Toast.makeText(getActivity(), R.string.describe_homework, Toast.LENGTH_SHORT).show();
				} else {
					requestAddHomework(inputText);
				}
			}
			@Override
			public void onInputCancelClick() {}
		});
		dialog.show(getChildFragmentManager(), "AddHomeworkDialog");
	}
	
	private void requestAddHomework(String description) {
		Intent intent = new Intent(getActivity(), ApiService.class);
		intent.setData(Uri.parse(ApiData.HOMEWORK));
		intent.setAction(ApiData.POST);
		intent.putExtra(ApiData.TOKEN, settings.getString(ApiData.TOKEN));
		intent.putExtra(ApiData.PARAM_CLASS_ID, classId);
		intent.putExtra(ApiData.PARAM_TEXT, description);
		intent.putExtra(ApiData.PARAM_DATE, Utilities.parseTime(dateView.getSelectedTime(), Utilities.dd_MM_yyyy));
		getActivity().startService(intent);
		showProgressDialog(R.string.adding_homework);
	}
	
	private void requestTasks() {
		long dayStart = Utilities.getDayStart(getTime()) / 1000;
		long dayEnd = Utilities.getDayEnd(getTime()) / 1000;
		
		Intent intent = new Intent(getActivity(), ApiService.class);
		intent.setData(Uri.parse(ApiData.TASK));
		intent.setAction(ApiData.GET);
		intent.putExtra(ApiData.TOKEN, settings.getString(ApiData.TOKEN));
		intent.putExtra(ApiData.PARAM_CLASS_ID, classId);
		intent.putExtra(ApiData.PARAM_DATE_START, dayStart);
		intent.putExtra(ApiData.PARAM_DATE_END, dayEnd);
		getActivity().startService(intent);
	}
	
	private void updateTasks() {
		if (Utilities.isEmpty(tasks)) {
			tasksEmptyView.setVisibility(View.VISIBLE);
			tasksContent.setVisibility(View.GONE);
		} else {
			tasksEmptyView.setVisibility(View.GONE);
			tasksContent.setVisibility(View.VISIBLE);
			populateTasksContent(tasks);			
		}
	}
	
	@SuppressLint("InflateParams")
	private void populateTasksContent(List<Task> tasks) {
		tasksContent.removeAllViews();
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i=0; i<tasks.size(); i++) {
			final Task task = tasks.get(i);
			View taskView = inflater.inflate(R.layout.task_list_item, null);
			taskView.setBackgroundResource(R.drawable.list_item_selector);
			
			final CheckBox completedCb = (CheckBox) taskView.findViewById(R.id.completedCb);
			completedCb.setText(task.getDescription());
			if (task.hasSubtasks()) {
				completedCb.setChecked(task.areSubtasksCompleted());
				completedCb.setEnabled(false);
			} else {
				completedCb.setChecked(task.isCompleted());
				completedCb.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						requestMarkTask(task, !task.isCompleted(), true);
					}
				});
			}
			
			ImageView removeBtn = (ImageView) taskView.findViewById(R.id.removeBtn);
			if (task.isCompleted()) {
				removeBtn.setImageResource(R.drawable.remove_icon);
				removeBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showDeleteTaskDialog(task);
					}
				});
			} else {
				removeBtn.setImageResource(R.drawable.icon_add_light);
				removeBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showAddTaskDialog(task.getId());
					}
				});
			}
			
			tasksContent.addView(taskView);
			
			if (task.hasSubtasks()) {
				List<Task> subtasks = task.getSubtasks();
				for (int j=0; j<subtasks.size(); j++) {
					final Task subtask = subtasks.get(j);
					View subtaskView = inflater.inflate(R.layout.subtask_list_item, null);
					
					final CheckBox completedSubtaskCb = (CheckBox) subtaskView.findViewById(R.id.completedCb);
					completedSubtaskCb.setText(subtask.getDescription());
					completedSubtaskCb.setChecked(subtask.isCompleted());
					completedSubtaskCb.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							subtask.setCompleted(!subtask.isCompleted());
							requestMarkTask(subtask, subtask.isCompleted(), true);
							
							task.setCompleted(task.areSubtasksCompleted());
							requestMarkTask(task, task.isCompleted(), false);
						}
					});
					
					ImageView removeSubtaskBtn = (ImageView) subtaskView.findViewById(R.id.removeBtn);
					removeSubtaskBtn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							showDeleteTaskDialog(subtask);
						}
					});
					
					tasksContent.addView(subtaskView);
				}
			}
		}
	}
	
	private void requestMarkTask(Task task, boolean completed, boolean showProgress) {
		Intent intent = new Intent(getActivity(), ApiService.class);
		intent.setData(Uri.parse(ApiData.TASK));
		intent.setAction(ApiData.PUT);
		intent.putExtra(ApiData.TOKEN, settings.getString(ApiData.TOKEN));
		intent.putExtra(ApiData.PARAM_CLASS_ID, classId);
		intent.putExtra(ApiData.PARAM_TASK_ID, task.getId());
		intent.putExtra(ApiData.PARAM_COMPLETED, completed ? "yes" : "no");
		getActivity().startService(intent);
		if (showProgress) {
			showProgressDialog(R.string.changing_task_status);
		}
	}
	
	private void showDeleteTaskDialog(final Task task) {
		final ConfirmationDialog dialog = new ConfirmationDialog();
		dialog.setTitle(getString(R.string.information));
		dialog.setText(getString(R.string.delete_task_confirmation));
		dialog.setOkListener(getString(R.string.delete), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				requestDeleteTask(task);
				dialog.dismiss();
			}
		});
		dialog.setCancelListener(getString(R.string.cancel), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.show(getChildFragmentManager(), "DeleteTaskDialog");
	}
	
	private void requestDeleteTask(Task task) {
		Intent intent = new Intent(getActivity(), ApiService.class);
		intent.setData(Uri.parse(ApiData.TASK));
		intent.setAction(ApiData.DELETE);
		intent.putExtra(ApiData.TOKEN, settings.getString(ApiData.TOKEN));
		intent.putExtra(ApiData.PARAM_CLASS_ID, classId);
		intent.putExtra(ApiData.PARAM_TASK_ID, task.getId());
		getActivity().startService(intent);
		showProgressDialog(R.string.deleting_task);
	}
	
	private void showAddTaskDialog(final long parentId) {
		InputDialog dialog = new InputDialog();
		dialog.setTitle(getString(R.string.information));
		dialog.setText(getString(R.string.describe_task));
		dialog.setHint(getString(R.string.task));
		dialog.setButtons(getString(R.string.add), getString(R.string.cancel), new OnInputClickListener() {
			@Override
			public void onInputOkClick(String inputText) {
				if (TextUtils.isEmpty(inputText)) {
					Toast.makeText(getActivity(), R.string.describe_task, Toast.LENGTH_SHORT).show();
				} else {
					requestAddTask(inputText, parentId);
				}
			}
			@Override
			public void onInputCancelClick() {}
		});
		dialog.show(getChildFragmentManager(), "AddTaskDialog");	
	}
	
	private void requestAddTask(String description, long parentId) {
		Intent intent = new Intent(getActivity(), ApiService.class);
		intent.setData(Uri.parse(ApiData.TASK));
		intent.setAction(ApiData.POST);
		intent.putExtra(ApiData.TOKEN, settings.getString(ApiData.TOKEN));
		intent.putExtra(ApiData.PARAM_CLASS_ID, classId);
		intent.putExtra(ApiData.PARAM_TEXT, description);
		intent.putExtra(ApiData.PARAM_DATE, Utilities.parseTime(dateView.getSelectedTime(), Utilities.dd_MM_yyyy));
		if (parentId != 0) {
			intent.putExtra(ApiData.PARAM_PARENT_ID, parentId);
		}
		getActivity().startService(intent);
		showProgressDialog(R.string.adding_task);
	}

	@Override
	public void onDateChanged(long time) {
		setTime(time);
		updateViews();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addActivityBtn:
			showAddActivityDialog();
			break;
		case R.id.addMediaBtn:
			showAddResourcesDialog();
			break;
		case R.id.addHomeworkBtn:
			showAddHomeworkDialog();
			break;
		case R.id.addTaskBtn:
			showAddTaskDialog(0);
			break;
		}
	}
	
	private void showAddResourcesDialog() {
		Intent intent = new Intent(getActivity(), AddResourceScreen.class);
		intent.putExtra(ApiData.PARAM_CLASS_ID, classId);
		intent.putExtra(ApiData.PARAM_DATE, getTime());
		startActivityForResult(intent, ADD_RESOURCE_REQUEST_CODE);
	}
	
	public void setCurrentTime(long time) {
		if (dateView != null) {
			dateView.setDate(time, false);
			updateViews();
		}
	}
	
	public long getTime() {
		return ((ClassScreen) getActivity()).getTime();
	}
	
	public void setTime(long time) {
		((ClassScreen) getActivity()).setTime(time);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onApiResponse(int apiStatus, ApiResponse apiResponse) {
		((BaseScreen) getActivity()).hideProgressDialog();
		if (apiStatus == ApiService.API_STATUS_SUCCESS) {
			int status = apiResponse.getStatus();
			String requestName = apiResponse.getRequestName();
			String method = apiResponse.getMethod();
			if (status == ApiResponse.STATUS_SUCCESS) {
				if (ApiData.ACTIVITY.equalsIgnoreCase(requestName)) {
					if (ApiData.GET.equalsIgnoreCase(method)) {
						activities = (List<Activiti>) apiResponse.getData();
						updateActivities();
					} else if (ApiData.DELETE.equalsIgnoreCase(method) ||
						ApiData.POST.equalsIgnoreCase(method)) 
					{
						requestActivities();
					}
				} else if (ApiData.RESOURCE.equalsIgnoreCase(requestName)) {
					if (ApiData.GET.equalsIgnoreCase(method)) {
						resources = (List<Resource>) apiResponse.getData();
						updateResources();
					} else if (ApiData.DELETE.equalsIgnoreCase(method) ||
						ApiData.POST.equalsIgnoreCase(method)) 
					{
						requestResources();
					}
				} else if (ApiData.HOMEWORK.equalsIgnoreCase(requestName)) {
					if (ApiData.GET.equalsIgnoreCase(method)) {
						homeworks = (List<Homework>) apiResponse.getData();
						updateHomeworks();
					} else if (ApiData.DELETE.equalsIgnoreCase(method) ||
						ApiData.POST.equalsIgnoreCase(method))
					{
						requestHomeworks();
					}
				} else if (ApiData.TASK.equalsIgnoreCase(requestName)) {
					if (ApiData.GET.equalsIgnoreCase(method)) {
						tasks = (List<Task>) apiResponse.getData();
						updateTasks();
					} else {
						requestTasks();
					}
				}
			}
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ADD_RESOURCE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			requestResources();
		}
	}
	
	private void openFile(File url) throws IOException {
        File file = url;
        Uri uri = Uri.fromFile(file);
        
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            intent.setDataAndType(uri, "application/msword");
        } else if(url.toString().contains(".pdf")) {
            intent.setDataAndType(uri, "application/pdf");
        } else if(url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if(url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if(url.toString().contains(".zip") || url.toString().contains(".rar")) {
            intent.setDataAndType(uri, "application/zip");
        } else if(url.toString().contains(".rtf")) {
            intent.setDataAndType(uri, "application/rtf");
        } else if(url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            intent.setDataAndType(uri, "audio/x-wav");
        } else if(url.toString().contains(".gif")) {
            intent.setDataAndType(uri, "image/gif");
        } else if(url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
            intent.setDataAndType(uri, "image/jpeg");
        } else if(url.toString().contains(".txt")) {
            intent.setDataAndType(uri, "text/plain");
        } else if(url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            intent.setDataAndType(uri, "video/*");
        } else {
            intent.setDataAndType(uri, "*/*");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
        startActivity(intent);
    }
	
	class DownloadResourceTask extends AsyncTask<Void, Void, String> implements OnCancelListener {
		
		private ProgressDialog dialog;
    	private Resource resource;
    	
    	public DownloadResourceTask(Resource resource) {
    		this.resource = resource;
    		dialog = new ProgressDialog(getActivity());
    		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    		dialog.setMessage(getString(R.string.downloading));
    		dialog.setIndeterminate(true);
    		dialog.setOnCancelListener(this);
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		dialog.show();
    	}

		@Override
		public void onCancel(DialogInterface dialog) {
			cancel(true);
		}

		@Override
		protected String doInBackground(Void... params) {
			InputStream input = null;
	        OutputStream output = null;
	        HttpURLConnection connection = null;
			try {
				URL url = new URL(resource.getUrl());
	            connection = (HttpURLConnection) url.openConnection();
	            connection.connect();

	            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
	            	String error = getString(R.string.error) + " " + connection.getResponseCode() + ": " + connection.getResponseMessage();
	                return error;
	            }

	            input = connection.getInputStream();
	            
	            File docDir = new File(Environment.getExternalStorageDirectory(), "/easyplanner/");
				docDir.mkdirs();
				File file = new File(docDir, resource.getTitle());
	            
	            output = new FileOutputStream(file);

	            byte data[] = new byte[4096];
	            int count;
	            while ((count = input.read(data)) != -1) {
	                if (isCancelled()) {
	                    input.close();
	                    return getString(R.string.canceled);
	                }
	                output.write(data, 0, count);
	            }
			} catch (Exception e) {
				e.printStackTrace();
				return getString(R.string.download_error);
			} finally {
				try {
	                if (output != null)
	                    output.close();
	                if (input != null)
	                    input.close();
	            } catch (IOException ignored) {
	            }
			}
			return null;
		}
		
		@Override
		public void onPostExecute(String error) {
			if (dialog != null && dialog.isShowing())
				dialog.dismiss();
			if (!TextUtils.isEmpty(error)) {
				((BaseScreen) getActivity()).showToast(error);
			} else {
				File docDir = new File(Environment.getExternalStorageDirectory() + "/easyplanner/");
				docDir.mkdirs();
				File file = new File(docDir, resource.getTitle());
				if (file.exists()) {
					try {
						openFile(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}

}
