package com.thepegeekapps.easyplanner.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.model.Activiti;
import com.thepegeekapps.easyplanner.util.Utilities;

public class MonthAdapter extends BaseAdapter {
	
	private Context context;
	private long[] items;
	private List<List<Activiti>> activities;
	private long time;
	private String[] daysOfWeek;
	private int month;
	
	public MonthAdapter(Context context, List<Activiti> list, long[] items, long time) {
		this.context = context;
		this.items = items;
		this.time = Utilities.timeToMidnight(time).getTimeInMillis();
		daysOfWeek = context.getResources().getStringArray(R.array.days_of_week);	
		generateActivitiyList(list);
		
		long medium = items[items.length/2];
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(medium);
		month = c.get(Calendar.MONTH);
	}

	@Override
	public int getCount() {
		return items.length;
	}

	@Override
	public Long getItem(int position) {
		return items[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.month_list_item, null);
		}
		
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(getItem(position));
		
		TextView dateView = (TextView) convertView.findViewById(R.id.dateView);
		dateView.setText(Utilities.addLeadingZero(c.get(Calendar.DAY_OF_MONTH)));
		dateView.setBackgroundResource(c.getTimeInMillis() == time ? R.drawable.today_marker_small : 0);
		dateView.setTextColor(c.getTimeInMillis() == time ? 0xffffffff : 
			c.get(Calendar.MONTH) == month ? 0xff448edb : 0xffdcdfe3);
		
		TextView dayView = (TextView) convertView.findViewById(R.id.dayView);
		if (position >= 0 && position < 7) {
			dayView.setText(daysOfWeek[c.get(Calendar.DAY_OF_WEEK)]);
			dayView.setVisibility(View.VISIBLE);
		} else {
			dayView.setVisibility(View.INVISIBLE);
		}
		
		LinearLayout activitiesContent = (LinearLayout) convertView.findViewById(R.id.activitiesContent);
		if (!Utilities.isEmpty(this.activities)) {
			List<Activiti> activities = this.activities.get(position);
			if (!Utilities.isEmpty(activities)) {
				populateActivitiesContent(activitiesContent, activities);
				activitiesContent.setVisibility(View.VISIBLE);
			}
		}
		
		return convertView;
	}

	private void generateActivitiyList(List<Activiti> list) {
		if (Utilities.isEmpty(list)) {
			return;
		}
		activities = new ArrayList<List<Activiti>>();
		for (long item : items) {
			List<Activiti> l = getActivities(item, list);
			activities.add(l);
		}
	}
	
	private List<Activiti> getActivities(long time, List<Activiti> list) {
		List<Activiti> activities = new ArrayList<Activiti>();
		if (!Utilities.isEmpty(list)) {
			long dayStart = Utilities.getDayStart(time);
			long dayEnd = Utilities.getDayEnd(time);
			for (Activiti activity : list) {
				if (activity.getTime() >= dayStart && activity.getTime() <= dayEnd) {
					activities.add(activity);
				}
			}
		}
		return activities;
	}
	
	@SuppressLint("InflateParams")
	private void populateActivitiesContent(LinearLayout layout, List<Activiti> activities) {
		layout.removeAllViews();
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (Utilities.isEmpty(activities)) {
			layout.setVisibility(View.GONE);
		} else {
			for (int i=0; i<activities.size(); i++) {
				final Activiti activity = activities.get(i);
				View view = inflater.inflate(R.layout.activity_list_item, null);
				
				TextView description = (TextView) view.findViewById(R.id.description);
				description.setText(activity.getDescription());
//				description.setTextColor(0xff0c3c6d);
				description.setTextColor(0xff458edb);
				
				ImageView removeBtn = (ImageView) view.findViewById(R.id.removeBtn);
				removeBtn.setVisibility(View.INVISIBLE);
				
				layout.addView(view);
			}
			layout.setVisibility(View.VISIBLE);
		}
	}

}
