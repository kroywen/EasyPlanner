package com.thepegeekapps.easyplanner.ui.view;

import java.util.Calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thepegeekapps.easyplanner.R;

public class DateView extends RelativeLayout implements OnClickListener {
	
	public interface OnDateChangedListener {
		void onDateChanged(long time);
	}

	private ImageView dayOfWeekNext;
	private ImageView dayOfWeekPrev;
	private TextView dayOfWeekView;
	
	private ImageView dayNext;
	private ImageView dayPrev;
	private TextView dayView;
	
	private ImageView monthNext;
	private ImageView monthPrev;
	private TextView monthView;
	
	private ImageView yearNext;
	private ImageView yearPrev;
	private TextView yearView;
	
	private Calendar calendar;
	private String[] daysOfWeek;
	private String[] months;
	
	private OnDateChangedListener listener;
	
	public DateView(Context context) {
		this(context, null);
	}
	
	public DateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		daysOfWeek = getContext().getResources().getStringArray(R.array.days_of_week);
		months = getContext().getResources().getStringArray(R.array.months);
		
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.date_view, null);
		initializeViews(view);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		addView(view, params);
		
		calendar = Calendar.getInstance();
	}
	
	private void initializeViews(View view) {
		dayOfWeekNext = (ImageView) view.findViewById(R.id.dayOfWeekNext);
		dayOfWeekNext.setOnClickListener(this);
		dayOfWeekPrev = (ImageView) view.findViewById(R.id.dayOfWeekPrev);
		dayOfWeekPrev.setOnClickListener(this);
		dayOfWeekView = (TextView) view.findViewById(R.id.dayOfWeekView);
		
		dayNext = (ImageView) view.findViewById(R.id.dayNext);
		dayNext.setOnClickListener(this);
		dayPrev = (ImageView) view.findViewById(R.id.dayPrev);
		dayPrev.setOnClickListener(this);
		dayView = (TextView) view.findViewById(R.id.dayView);
		
		monthNext = (ImageView) view.findViewById(R.id.monthNext);
		monthNext.setOnClickListener(this);
		monthPrev = (ImageView) view.findViewById(R.id.monthPrev);
		monthPrev.setOnClickListener(this);
		monthView = (TextView) view.findViewById(R.id.monthView);
		
		yearNext = (ImageView) view.findViewById(R.id.yearNext);
		yearNext.setOnClickListener(this);
		yearPrev = (ImageView) view.findViewById(R.id.yearPrev);
		yearPrev.setOnClickListener(this);
		yearView = (TextView) view.findViewById(R.id.yearView);
	}
	
	public void setDate(long time) {
		calendar.setTimeInMillis(time);
		dayOfWeekView.setText(daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK)]);
		dayView.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
		monthView.setText(months[calendar.get(Calendar.MONTH)]);
		yearView.setText(String.valueOf(calendar.get(Calendar.YEAR)));
		
		if (listener != null) {
			listener.onDateChanged(time);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dayOfWeekNext:
			calendar.add(Calendar.DAY_OF_WEEK, 1);
			break;
		case R.id.dayOfWeekPrev:
			calendar.add(Calendar.DAY_OF_WEEK, -1);
			break;
		case R.id.dayNext:
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			break;
		case R.id.dayPrev:
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			break;
		case R.id.monthNext:
			calendar.add(Calendar.MONTH, 1);
			break;
		case R.id.monthPrev:
			calendar.add(Calendar.MONTH, -1);
			break;
		case R.id.yearNext:
			calendar.add(Calendar.YEAR, 1);
			break;
		case R.id.yearPrev:
			calendar.add(Calendar.YEAR, -1);
			break;
		}
		setDate(calendar.getTimeInMillis());
	}
	
	public void setOnDateChangedListener(OnDateChangedListener listener) {
		this.listener = listener;
	}

}
