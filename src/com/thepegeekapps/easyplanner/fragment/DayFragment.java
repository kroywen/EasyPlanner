package com.thepegeekapps.easyplanner.fragment;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.thepegeekapps.easyplanner.dialog.ConfirmationDialog;
import com.thepegeekapps.easyplanner.dialog.InputDialog;
import com.thepegeekapps.easyplanner.dialog.InputDialog.OnInputClickListener;
import com.thepegeekapps.easyplanner.model.Activiti;
import com.thepegeekapps.easyplanner.model.Homework;
import com.thepegeekapps.easyplanner.model.Task;
import com.thepegeekapps.easyplanner.screen.ClassScreen;
import com.thepegeekapps.easyplanner.storage.db.DatabaseHelper;
import com.thepegeekapps.easyplanner.storage.db.DatabaseStorage;
import com.thepegeekapps.easyplanner.ui.view.DateView;
import com.thepegeekapps.easyplanner.ui.view.DateView.OnDateChangedListener;
import com.thepegeekapps.easyplanner.util.Utilities;

public class DayFragment extends Fragment implements OnDateChangedListener, OnClickListener {
	
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
	
	private DatabaseStorage dbStorage;
	private long classId;
	private String[] daysOfWeek;
	private List<Activiti> activities;
	private List<Homework> homeworks;
	private List<Task> tasks;
	
	private OnDataChangeListener dataListener;
	private OnTimeSelectListener timeListener;
	
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
		dbStorage = new DatabaseStorage(getActivity());
		classId = (getArguments() != null) ? getArguments().getLong(DatabaseHelper.FIELD_ID) : 0;
		daysOfWeek = getResources().getStringArray(R.array.days_of_week);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.day_fragment, null);
		initializeViews(view);
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			dataListener = (OnDataChangeListener) activity;
			timeListener = (OnTimeSelectListener) activity;
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		updateActivities();
		updateHomeworks();
		updateTasks();
	}
	
	private void updateCurrentDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(getTime());
		currentDateView.setText(Utilities.addLeadingZero(calendar.get(Calendar.DAY_OF_MONTH)));
		currentDayView.setText(daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK)]);
	}
	
	private void updateActivities() {
		long dayStart = Utilities.getDayStart(getTime());
		long dayEnd = Utilities.getDayEnd(getTime());
		String selection = DatabaseHelper.FIELD_CLASS_ID + "=" + classId + " AND " +
				DatabaseHelper.FIELD_TIME + " > " + dayStart + " AND " +
				DatabaseHelper.FIELD_TIME + " < " + dayEnd;
		activities = dbStorage.getActivities(selection);
		if (Utilities.isEmpty(activities)) {
			activitiesEmptyView.setVisibility(View.VISIBLE);
			activitiesContent.setVisibility(View.GONE);
		} else {
			populateActivitiesContent(activities);			
			activitiesEmptyView.setVisibility(View.GONE);
			activitiesContent.setVisibility(View.VISIBLE);
		}
	}
	
	private void populateActivitiesContent(List<Activiti> activities) {
		activitiesContent.removeAllViews();
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i=0; i<activities.size(); i++) {
			final Activiti activity = activities.get(i);
			View view = inflater.inflate(R.layout.activity_list_item, null);
			
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
	
	private void showDeleteActivityDialog(final Activiti activity) {
		final ConfirmationDialog dialog = new ConfirmationDialog();
		dialog.setTitle(getString(R.string.information));
		dialog.setText(getString(R.string.delete_activity_confirmation));
		dialog.setOkListener(getString(R.string.delete), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dbStorage.deleteActivity(activity);
				updateActivities();
				dialog.dismiss();
				if (dataListener != null) {
					dataListener.onDataChanged();
				}
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
	
	private void updateHomeworks() {
		long dayStart = Utilities.getDayStart(getTime());
		long dayEnd = Utilities.getDayEnd(getTime());
		String selection = DatabaseHelper.FIELD_CLASS_ID + "=" + classId + " AND " +
				DatabaseHelper.FIELD_TIME + " > " + dayStart + " AND " +
				DatabaseHelper.FIELD_TIME + " < " + dayEnd;
		homeworks = dbStorage.getHomeworks(selection);
		if (Utilities.isEmpty(homeworks)) {
			homeworksEmptyView.setVisibility(View.VISIBLE);
			homeworksContent.setVisibility(View.GONE);
		} else {
			populateHomeworksContent(homeworks);			
			homeworksEmptyView.setVisibility(View.GONE);
			homeworksContent.setVisibility(View.VISIBLE);
		}
	}
	
	private void populateHomeworksContent(List<Homework> homeworks) {
		homeworksContent.removeAllViews();
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i=0; i<homeworks.size(); i++) {
			final Homework homework = homeworks.get(i);
			View view = inflater.inflate(R.layout.activity_list_item, null);
			
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
				dbStorage.deleteHomework(homework);
				updateHomeworks();
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
	
	private void updateTasks() {
		long dayStart = Utilities.getDayStart(getTime());
		long dayEnd = Utilities.getDayEnd(getTime());
		String selection = DatabaseHelper.FIELD_CLASS_ID + "=" + classId + " AND " +
				DatabaseHelper.FIELD_TIME + " > " + dayStart + " AND " +
				DatabaseHelper.FIELD_TIME + " < " + dayEnd;
		tasks = dbStorage.getTasks(selection);
		if (Utilities.isEmpty(tasks)) {
			tasksEmptyView.setVisibility(View.VISIBLE);
			tasksContent.setVisibility(View.GONE);
		} else {
			tasksEmptyView.setVisibility(View.GONE);
			tasksContent.setVisibility(View.VISIBLE);
			populateTasksContent(tasks);			
		}
	}
	
	private void populateTasksContent(List<Task> tasks) {
		tasksContent.removeAllViews();
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i=0; i<tasks.size(); i++) {
			final Task task = tasks.get(i);
			View taskView = inflater.inflate(R.layout.task_list_item, null);
			
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
						task.setCompleted(!task.isCompleted());
						dbStorage.updateTask(task);
						updateTasks();
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
							dbStorage.updateTask(subtask);
							
							task.setCompleted(task.areSubtasksCompleted());
							dbStorage.updateTask(task);
							
							updateTasks();
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
	
	private void showDeleteTaskDialog(final Task task) {
		final ConfirmationDialog dialog = new ConfirmationDialog();
		dialog.setTitle(getString(R.string.information));
		dialog.setText(getString(R.string.delete_task_confirmation));
		dialog.setOkListener(getString(R.string.delete), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dbStorage.deleteTask(task);
				updateTasks();
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

	@Override
	public void onDateChanged(long time) {
		setTime(time);
		updateViews();
		if (timeListener != null) {
			timeListener.onTimeSelected(0, time);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addActivityBtn:
			showAddActivityDialog();
			break;
		case R.id.addHomeworkBtn:
			showAddHomeworkDialog();
			break;
		case R.id.addTaskBtn:
			showAddTaskDialog(0);
			break;
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
					Activiti activity = new Activiti(0, classId, inputText, dateView.getSelectedTime());
					dbStorage.addActivity(activity);
					updateActivities();
					if (dataListener != null) {
						dataListener.onDataChanged();
					}
				}
			}
			@Override
			public void onInputCancelClick() {}
		});
		dialog.show(getChildFragmentManager(), "add_activity");
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
					Homework homework = new Homework(0, classId, inputText, dateView.getSelectedTime());
					dbStorage.addHomework(homework);
					updateHomeworks();
				}
			}
			@Override
			public void onInputCancelClick() {}
		});
		dialog.show(getChildFragmentManager(), "add_homework");
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
					Task task = new Task(0, classId, parentId, inputText, dateView.getSelectedTime(), false);
					dbStorage.addTask(task);
					updateTasks();
				}
			}
			@Override
			public void onInputCancelClick() {}
		});
		dialog.show(getChildFragmentManager(), "add_task");	
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

}
