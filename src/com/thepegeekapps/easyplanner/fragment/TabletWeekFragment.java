package com.thepegeekapps.easyplanner.fragment;

import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.model.Activiti;
import com.thepegeekapps.easyplanner.model.Clas;
import com.thepegeekapps.easyplanner.screen.ClassScreen;
import com.thepegeekapps.easyplanner.ui.view.DateView;
import com.thepegeekapps.easyplanner.util.Utilities;

public class TabletWeekFragment extends BaseWeekFragment implements OnClickListener {
	
	private LinearLayout[] layouts = new LinearLayout[6];
	private int[] layoutIDs = {
		R.id.mondayLayout, R.id.tuesdayLayout, R.id.wednesdayLayout,
		R.id.thursdayLayout, R.id.fridayLayout, R.id.weekendLayout
	};
	
	public static BaseWeekFragment getInstance(long classId) {
		BaseWeekFragment f = new TabletWeekFragment();
		Bundle args = new Bundle();
		args.putLong(Clas.CLASS_ID, classId);
		f.setArguments(args);
		return f;
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tablet_week_fragment, null);
		initializeViews(view);
		return view;
	}
	
	private void initializeViews(View view) {
		dateView = (DateView) view.findViewById(R.id.dateView);
		dateView.setOnDateChangedListener(this);
		
		for (int i=0; i<6; i++) {
			layouts[i] = (LinearLayout) view.findViewById(layoutIDs[i]);
			layouts[i].setOnClickListener(this);
		}
	}
	
	@Override
	protected void updateViews() {
		long currentTime = Utilities.timeToMidnight(dateView.getSelectedTime()).getTimeInMillis();
		for (int i=0; i<6; i++) {
			LinearLayout layout = layouts[i];
			if (layout == null) {
				return;
			}
			
			long[] times = dateItems[i];
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(times[0]);
			
			TextView date1View = (TextView) layout.findViewById(R.id.date1View);
			date1View.setText(Utilities.addLeadingZero(c.get(Calendar.DAY_OF_MONTH)));
			date1View.setBackgroundResource(c.getTimeInMillis() == currentTime ? R.drawable.today_marker : 0);
			date1View.setTextColor(c.getTimeInMillis() == currentTime ? 0xffffffff : 0xff448edb);
			
			TextView day1View = (TextView) layout.findViewById(R.id.day1View);
			day1View.setText(daysOfWeek[c.get(Calendar.DAY_OF_WEEK)]);
			
			if (times[1] != 0) {
				c.setTimeInMillis(times[1]);
			
				TextView date2View = (TextView) layout.findViewById(R.id.date2View);
				date2View.setVisibility(View.VISIBLE);
				date2View.setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
				date2View.setBackgroundResource(c.getTimeInMillis() == currentTime ? R.drawable.today_marker : 0);
				date2View.setTextColor(c.getTimeInMillis() == currentTime ? 0xffffffff : 0xff448edb);
			
				TextView day2View = (TextView) layout.findViewById(R.id.day2View);
				day2View.setVisibility(View.VISIBLE);
				day2View.setText(daysOfWeek[c.get(Calendar.DAY_OF_WEEK)]);
			}
			
			LinearLayout activitiesContent = (LinearLayout) layout.findViewById(R.id.activitiesContent);
			TextView activitiesEmptyView = (TextView) layout.findViewById(R.id.activitiesEmptyView);
			
			List<Activiti> activityList = filterActivities(times);
			if (Utilities.isEmpty(activityList)) {
				activitiesEmptyView.setVisibility(View.VISIBLE);
				activitiesContent.setVisibility(View.GONE);
			} else {
				populateActivitiesContent(activitiesContent, activityList);
				activitiesEmptyView.setVisibility(View.GONE);
				activitiesContent.setVisibility(View.VISIBLE);
			}
		}
	}
	
	@SuppressLint("InflateParams")
	private void populateActivitiesContent(LinearLayout layout, List<Activiti> activityList) {
		layout.removeAllViews();
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i=0; i<activityList.size(); i++) {
			final Activiti activity = activityList.get(i);
			View view = inflater.inflate(R.layout.activity_list_item, null);
			
			TextView description = (TextView) view.findViewById(R.id.description);
			description.setText(activity.getDescription());
			description.setTextColor(0xff458edb);
			
			ImageView removeBtn = (ImageView) view.findViewById(R.id.removeBtn);
			removeBtn.setVisibility(View.INVISIBLE);
			
			layout.addView(view);
		}
	}

	@Override
	public void onClick(View v) {
		long[] times = null;
		for (int i=0; i<6; i++) {
			if (v.getId() == layouts[i].getId()) {
				times = dateItems[i];
				break;
			}
		}
		if (times != null) {
			setTime(times[0]);
			((ClassScreen) getActivity()).checkDayFragment();
		}
	}

}
