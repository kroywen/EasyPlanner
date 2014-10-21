package com.thepegeekapps.easyplanner.screen;

import android.widget.ImageView;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.fragment.ClassesFragment;
import com.thepegeekapps.easyplanner.fragment.TasksFragment;

public class TabletMainScreen extends BaseMainScreen {
	
	private ImageView addTaskBtn;
	
	@Override
	protected void initializeViews() {
		super.initializeViews();
		
		addTaskBtn = (ImageView) findViewById(R.id.addTaskBtn);
		addTaskBtn.setOnClickListener(this);
	}
	
	protected void updateViews() {
		ClassesFragment cf = ClassesFragment.newInstance(timeSelected);
		getSupportFragmentManager().beginTransaction().replace(R.id.classes_content, cf, "ClassesFragment").commit();
		
		TasksFragment tf = TasksFragment.newInstance(timeSelected);
		getSupportFragmentManager().beginTransaction().replace(R.id.tasks_content, tf, "TasksFragment").commit();
	}
	
	@Override
	protected ClassesFragment getClassesFragment() {
		return (ClassesFragment) getSupportFragmentManager().findFragmentById(R.id.classes_content);
	}
	
	protected TasksFragment getTasksFragment() {
		return (TasksFragment) getSupportFragmentManager().findFragmentById(R.id.tasks_content);
	}

}
