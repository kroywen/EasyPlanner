package com.thepegeekapps.easyplanner.screen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.model.Clas;
import com.thepegeekapps.easyplanner.storage.db.DatabaseHelper;
import com.thepegeekapps.easyplanner.storage.db.DatabaseStorage;

public class ClassScreen extends FragmentActivity implements OnClickListener {
	
	private ViewPager pager;
	private PagerAdapter pagerAdapter;	
	
	private DatabaseStorage dbStorage;
	private Clas clas;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.class_screen);
		dbStorage = new DatabaseStorage(this);
		getIntentData();
		initializeViews();
	}
	
	private void getIntentData() {
		Intent intent = getIntent();
		if (intent != null) {
			long id = intent.getLongExtra(DatabaseHelper.FIELD_ID, 0);
			clas = dbStorage.getClassById(id);
		}
	}
	
	private void initializeViews() {
		findViewById(R.id.addClassBtn).setVisibility(View.INVISIBLE);
		
		TextView titleView = (TextView) findViewById(R.id.title);
		titleView.setText(clas.getName());
		
		ImageView menuBtn = (ImageView) findViewById(R.id.menuBtn);
		menuBtn.setImageResource(R.drawable.back_icon);
		menuBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.menuBtn) {
			finish();
		}
	}

}
