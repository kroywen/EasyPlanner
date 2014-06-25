package com.thepegeekapps.easyplanner.fragment;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
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
import com.thepegeekapps.easyplanner.screen.MainScreen;
import com.thepegeekapps.easyplanner.storage.db.DatabaseHelper;
import com.thepegeekapps.easyplanner.storage.db.DatabaseStorage;
import com.thepegeekapps.easyplanner.util.Utilities;

public class TasksFragment extends Fragment {
	
	private View emptyView;
	private LinearLayout tasksContent;
	
	private List<Task> tasks;
	private DatabaseStorage dbStorage;
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
		dbStorage = new DatabaseStorage(getActivity());
		timeSelected = (getArguments() != null) ? getArguments().getInt("time") : 0;
	}
	
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
		String selection = null;
		if (timeSelected == MainScreen.TIME_TODAY) {
			Calendar calendar = Calendar.getInstance(); 
			long dayStart = Utilities.getDayStart(calendar.getTimeInMillis());
			long dayEnd = Utilities.getDayEnd(calendar.getTimeInMillis());
			selection = DatabaseHelper.FIELD_TIME + " > " + dayStart + " AND " +
				DatabaseHelper.FIELD_TIME + " < " + dayEnd;
		}
		tasks = dbStorage.getTasks(selection);
		
		if (Utilities.isEmpty(tasks)) {
			emptyView.setVisibility(View.VISIBLE);
			tasksContent.setVisibility(View.INVISIBLE);
		} else {
			emptyView.setVisibility(View.INVISIBLE);
			tasksContent.setVisibility(View.VISIBLE);
			populateTasksContent(tasks);
		}
	}
	
	private void populateTasksContent(List<Task> tasks) {
		tasksContent.removeAllViews();
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i=0; i<tasks.size(); i++) {
			final Task task = tasks.get(i);
			View taskView = inflater.inflate(R.layout.task_main_list_item, null);
			
			final CheckBox completedCb = (CheckBox) taskView.findViewById(R.id.completedCb);
			completedCb.setText(Html.fromHtml(task.getDescription() + "<br /><font color=\"#ff0000\">Required in 3 days</font>"));
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
						updateViews();
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
							dbStorage.updateTask(subtask);
							
							task.setCompleted(task.areSubtasksCompleted());
							dbStorage.updateTask(task);
							
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
				dbStorage.deleteTask(task);
				updateViews();
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
					Task parent = dbStorage.getTaskById(parentId);
					Task task = new Task(0, parent.getClassId(), parentId, inputText, System.currentTimeMillis(), false);
					dbStorage.addTask(task);
					updateViews();
				}
			}
			@Override
			public void onInputCancelClick() {}
		});
		dialog.show(getChildFragmentManager(), "add_task");	
	}

}
