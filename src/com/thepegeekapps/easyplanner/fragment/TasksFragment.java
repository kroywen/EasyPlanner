package com.thepegeekapps.easyplanner.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.dialog.ConfirmationDialog;
import com.thepegeekapps.easyplanner.dialog.InputDialog;
import com.thepegeekapps.easyplanner.dialog.InputDialog.OnInputClickListener;
import com.thepegeekapps.easyplanner.model.Task;
import com.thepegeekapps.easyplanner.screen.BaseMainScreen;
import com.thepegeekapps.easyplanner.util.Utilities;

public class TasksFragment extends Fragment {
	
	private View emptyView;
	private LinearLayout tasksContent;
	
	private List<Task> tasks;
	private int timeSelected;
	
	public static TasksFragment newInstance(int timeSelected) {
		TasksFragment f = new TasksFragment();
		Bundle args = new Bundle();
		args.putInt("time", timeSelected);
		f.setArguments(args);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		timeSelected = (getArguments() != null) ? getArguments().getInt("time") : 0;
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tasks_fragment, null);
		initializeViews(view);	
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		updateViews();
	}
	
	private void initializeViews(View view) {
		emptyView = view.findViewById(R.id.emptyView);
		tasksContent = (LinearLayout) view.findViewById(R.id.tasksContent);
	}
	
	public void setTimeSelected(int timeSelected) {
		this.timeSelected = timeSelected;
		updateViews();
	}
	
	public void updateViews() {
		List<Task> filtered = (timeSelected == BaseMainScreen.TIME_TODAY) ?
			getTodayTasks() : tasks;
		
		if (Utilities.isEmpty(filtered)) {
			emptyView.setVisibility(View.VISIBLE);
			tasksContent.setVisibility(View.INVISIBLE);
		} else {
			emptyView.setVisibility(View.INVISIBLE);
			tasksContent.setVisibility(View.VISIBLE);
			populateTasksContent(filtered);
		}
	}
	
	private List<Task> getTodayTasks() {
		Calendar calendar = Calendar.getInstance(); 
		long dayStart = Utilities.getDayStart(calendar.getTimeInMillis());
		long dayEnd = Utilities.getDayEnd(calendar.getTimeInMillis());
		List<Task> filtered = null;
		if (!Utilities.isEmpty(tasks)) {
			filtered = new ArrayList<Task>();
			for (Task task : tasks) {
				if (task.getTime() >= dayStart && task.getTime() < dayEnd) {
					filtered.add(task);
				}
			}
		}
		return filtered;
	}
	
	@SuppressLint("InflateParams")
	private void populateTasksContent(List<Task> tasks) {
		tasksContent.removeAllViews();
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i=0; i<tasks.size(); i++) {
			final Task task = tasks.get(i);
			View taskView = inflater.inflate(R.layout.task_main_list_item, null);
			
			long now = System.currentTimeMillis();
			String daysStr = null;
			if (now > task.getTime()) {
				if (Utilities.isSameDay(now, task.getTime())) {
					daysStr = getString(R.string.required_today);
				} else {
					daysStr = getString(R.string.overdue);
				}
			} else {
				int daysCount = Utilities.getDaysCount(now, task.getTime());
				daysStr = (daysCount == 0) ? getString(R.string.today) :
					getResources().getQuantityString(R.plurals.daysCount, daysCount, daysCount);
			}
			String timeStr = Utilities.parseTime(task.getTime(), Utilities.EEE_dd_LLL_yyyy);
			String requiredText = getString(R.string.required_days, daysStr, timeStr);
			
			int requiredColor = 0;
			if (now > task.getTime()) {
				if (Utilities.isSameDay(now, task.getTime())) {
					requiredColor = 0xff2c2c2c;
				} else {
					requiredColor = 0xffd21317;
				}
			} else {
				requiredColor = 0xff2c2c2c;
			}
			String cbText = task.getDescription() + "\n" + requiredText;
			SpannableString ss = new SpannableString(cbText);
			ss.setSpan(new RelativeSizeSpan(1.2f), 0, task.getDescription().length(), 0);
			ss.setSpan(new ForegroundColorSpan(0xff448edb), 0, task.getDescription().length(), 0);
			ss.setSpan(new ForegroundColorSpan(requiredColor), task.getDescription().length()+1, cbText.length(), 0);
			ss.setSpan(new RelativeSizeSpan(.8f), task.getDescription().length()+1, cbText.length(), 0);
			
			final CheckBox completedCb = (CheckBox) taskView.findViewById(R.id.completedCb);
			completedCb.setText(ss);
			if (task.hasSubtasks()) {
				completedCb.setChecked(task.areSubtasksCompleted());
				completedCb.setEnabled(false);
			} else {
				completedCb.setChecked(task.isCompleted());
				completedCb.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						task.setCompleted(!task.isCompleted());
						((BaseMainScreen) getActivity()).tryMarkTask(task, true);
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
			tasksContent.addView(createSeparatorView());
			
			if (task.hasSubtasks()) {
				List<Task> subtasks = task.getSubtasks();
				for (int j=0; j<subtasks.size(); j++) {
					final Task subtask = subtasks.get(j);
					View subtaskView = inflater.inflate(R.layout.subtask_main_list_item, null);
					
					final CheckBox completedSubtaskCb = (CheckBox) subtaskView.findViewById(R.id.completedCb);
					completedSubtaskCb.setText(subtask.getDescription());
					completedSubtaskCb.setChecked(subtask.isCompleted());
					completedSubtaskCb.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							subtask.setCompleted(!subtask.isCompleted());
							((BaseMainScreen) getActivity()).tryMarkTask(subtask, true);
							
							task.setCompleted(task.areSubtasksCompleted());
							((BaseMainScreen) getActivity()).tryMarkTask(task, false);
							
							updateViews();
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
					tasksContent.addView(createSeparatorView());
				}
			}
		}
	}
	
	private View createSeparatorView() {
		View view = new View(getActivity());
		view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1));
		view.setBackgroundColor(0xffe2f2ff);
		return view;
	}
	
	private void showDeleteTaskDialog(final Task task) {
		final ConfirmationDialog dialog = new ConfirmationDialog();
		dialog.setTitle(getString(R.string.information));
		dialog.setText(getString(R.string.delete_task_confirmation));
		dialog.setOkListener(getString(R.string.delete), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				((BaseMainScreen) getActivity()).tryDeleteTask(task);
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
	
	public void showAddTaskDialog(final long parentId) {
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
					Task parent = getTaskById(parentId);
					Task task = new Task(0, parent.getClassId(), parentId, inputText, System.currentTimeMillis(), false);
					((BaseMainScreen) getActivity()).tryAddTask(task); 
				}
			}
			@Override
			public void onInputCancelClick() {}
		});
		dialog.show(getChildFragmentManager(), "AddTaskDialog");	
	}
	
	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
		updateViews();
	}
	
	private Task getTaskById(long id) {
		if (Utilities.isEmpty(tasks)) {
			return null;
		}
		for (Task task : tasks) {
			if (task.getId() == id) {
				return task;
			}
		}
		return null;
	}

}
