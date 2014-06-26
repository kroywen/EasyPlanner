package com.thepegeekapps.easyplanner.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
import com.thepegeekapps.easyplanner.storage.db.DatabaseHelper;
import com.thepegeekapps.easyplanner.storage.db.DatabaseStorage;
import com.thepegeekapps.easyplanner.util.Utilities;

public class DaysListAdapter extends BaseAdapter {
	
	private Context context;
	private long[][] items;
	private long time;
	private String[] daysOfWeek;
	private long classId;
	private DatabaseStorage dbStorage;
	
	public DaysListAdapter(Context context, long[][] items, long time, long classId) {
		this.context = context;
		this.items = items;
		this.time = Utilities.timeToMidnight(time).getTimeInMillis();
		this.classId = classId;
		daysOfWeek = context.getResources().getStringArray(R.array.days_of_week);	
		dbStorage = new DatabaseStorage(context);
	}

	@Override
	public int getCount() {
		return items.length;
	}

	@Override
	public long[] getItem(int position) {
		return items[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.week_list_item, null);
		}
		
		long[] times = getItem(position);
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(times[0]);
		
		TextView date1View = (TextView) convertView.findViewById(R.id.date1View);
		date1View.setText(Utilities.addLeadingZero(c.get(Calendar.DAY_OF_MONTH)));
		date1View.setBackgroundResource(c.getTimeInMillis() == time ? R.drawable.today_marker : 0);
		date1View.setTextColor(c.getTimeInMillis() == time ? 0xffffffff : 0xff448edb);
		
		TextView day1View = (TextView) convertView.findViewById(R.id.day1View);
		day1View.setText(daysOfWeek[c.get(Calendar.DAY_OF_WEEK)]);
		
		TextView date2View = (TextView) convertView.findViewById(R.id.date2View);
		TextView day2View = (TextView) convertView.findViewById(R.id.day2View);
		
		if (times[1] != 0) {
			c.setTimeInMillis(times[1]);
			
			date2View.setVisibility(View.VISIBLE);
			date2View.setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
			date2View.setBackgroundResource(c.getTimeInMillis() == time ? R.drawable.today_marker : 0);
			date2View.setTextColor(c.getTimeInMillis() == time ? 0xffffffff : 0xff448edb);
			
			day2View.setVisibility(View.VISIBLE);
			day2View.setText(daysOfWeek[c.get(Calendar.DAY_OF_WEEK)]);
		} else {
			date2View.setVisibility(View.GONE);
			day2View.setVisibility(View.GONE);
		}
		
		LinearLayout activitiesContent = (LinearLayout) convertView.findViewById(R.id.activitiesContent);
		List<Activiti> activities = getActivities(times);
		if (Utilities.isEmpty(activities)) {
			activitiesContent.setVisibility(View.GONE);
		} else {
			populateActivitiesContent(activitiesContent, activities);
			activitiesContent.setVisibility(View.VISIBLE);
		}
		
		return convertView;
	}
	
	private List<Activiti> getActivities(long[] times) {
		List<Activiti> activities = new ArrayList<Activiti>();
		if (times[0] != 0) {
			List<Activiti> activities1 = getActivities(times[0]);
			if (!Utilities.isEmpty(activities1)) {
				activities.addAll(activities1);
			}
		}
		if (times[1] != 0) {
			List<Activiti> activities2 = getActivities(times[1]);
			if (!Utilities.isEmpty(activities2)) {
				activities.addAll(activities2);
			}
		}
		return activities;
	}
	
	private List<Activiti> getActivities(long time) {
		long dayStart = Utilities.getDayStart(time);
		long dayEnd = Utilities.getDayEnd(time);
		String selection = DatabaseHelper.FIELD_CLASS_ID + "=" + classId + " AND " +
				DatabaseHelper.FIELD_TIME + " > " + dayStart + " AND " +
				DatabaseHelper.FIELD_TIME + " < " + dayEnd;
		List<Activiti> activities = dbStorage.getActivities(selection);
		return activities;
	}
	
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
				
				ImageView removeBtn = (ImageView) view.findViewById(R.id.removeBtn);
				removeBtn.setVisibility(View.INVISIBLE);
				
				layout.addView(view);
			}
			layout.setVisibility(View.VISIBLE);
		}
	}

}
