package com.thepegeekapps.easyplanner.fragment;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.model.Activiti;
import com.thepegeekapps.easyplanner.model.Homework;
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
	private Calendar calendar;
	private long classId;
	private String[] daysOfWeek;
	private List<Activiti> activities;
	private List<Homework> homeworks;
	
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
		calendar = Calendar.getInstance();
		daysOfWeek = getResources().getStringArray(R.array.days_of_week);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.day_fragment, null);
		initializeViews(view);
		dateView.setDate(calendar.getTimeInMillis());
		return view;
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
	
	private void updateContents() {
		updateCurrentDay();
		updateActivities();
		updateHomeworks();
	}
	
	private void updateCurrentDay() {
		currentDateView.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
		currentDayView.setText(daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK)]);
	}
	
	private void updateActivities() {
		long dayStart = Utilities.getDayStart(calendar.getTimeInMillis());
		long dayEnd = Utilities.getDayEnd(calendar.getTimeInMillis());
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
					dbStorage.deleteActivity(activity);
					updateActivities();
				}
			});
			
			activitiesContent.addView(view);
		}
	}
	
	private void updateHomeworks() {
		long dayStart = Utilities.getDayStart(calendar.getTimeInMillis());
		long dayEnd = Utilities.getDayEnd(calendar.getTimeInMillis());
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
					dbStorage.deleteHomework(homework);
					updateHomeworks();
				}
			});
			
			homeworksContent.addView(view);
		}
	}

	@Override
	public void onDateChanged(long time) {
		calendar.setTimeInMillis(time);
		updateContents();
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
		}
	}
	
	private void showAddActivityDialog() {
		// TODO
		dbStorage.addActivity(new Activiti(0, classId, "New activity", System.currentTimeMillis()));
		updateActivities();
	}
	
	private void showAddHomeworkDialog() {
		// TODO
		dbStorage.addHomework(new Homework(0, classId, "New homework", System.currentTimeMillis()));
		updateHomeworks();
	}

}
