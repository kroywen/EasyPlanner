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

	private ImageView dayOfMonthNext;
	private ImageView dayOfMonthPrev;
	private TextView dayOfMonthView;
	
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
	
	public DateView(Context context) {
		this(context, null);
	}
	
	public DateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.date_view, null);
		initializeViews(view);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		addView(view, params);
		
		calendar = Calendar.getInstance();
		setDate(calendar.getTimeInMillis());
	}
	
	private void initializeViews(View view) {
		dayOfMonthNext = (ImageView) view.findViewById(R.id.dayOfMonthNext);
		dayOfMonthNext.setOnClickListener(this);
		dayOfMonthPrev = (ImageView) view.findViewById(R.id.dayOfMonthPrev);
		dayOfMonthPrev.setOnClickListener(this);
		dayOfMonthView = (TextView) view.findViewById(R.id.dayOfMonthView);
		
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
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dayOfMonthNext:
			break;
		case R.id.dayOfMonthPrev:
			break;
		case R.id.dayNext:
			break;
		case R.id.dayPrev:
			break;
		case R.id.monthNext:
			break;
		case R.id.monthPrev:
			break;
		case R.id.yearNext:
			break;
		case R.id.yearPrev:
			break;
		}
	}

}
