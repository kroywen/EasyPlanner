package com.thepegeekapps.easyplanner.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class MonthDateView extends DateView {
	
	public MonthDateView(Context context) {
		this(context, null);
	}
	
	public MonthDateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	@Override
	protected void initializeViews(View view) {
		super.initializeViews(view);
		
		dayOfWeekNext.setVisibility(View.GONE);
		dayOfWeekPrev.setVisibility(View.GONE);
		dayOfWeekView.setVisibility(View.GONE);
		
		dayNext.setVisibility(View.GONE);
		dayPrev.setVisibility(View.GONE);
		dayView.setVisibility(View.GONE);
	}

}
