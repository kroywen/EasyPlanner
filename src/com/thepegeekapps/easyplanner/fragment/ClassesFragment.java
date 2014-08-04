package com.thepegeekapps.easyplanner.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.adapter.ClassAdapter;
import com.thepegeekapps.easyplanner.dialog.ConfirmationDialog;
import com.thepegeekapps.easyplanner.model.Clas;
import com.thepegeekapps.easyplanner.screen.ClassScreen;
import com.thepegeekapps.easyplanner.screen.MainScreen;
import com.thepegeekapps.easyplanner.storage.db.DatabaseHelper;
import com.thepegeekapps.easyplanner.util.Utilities;

public class ClassesFragment extends Fragment implements OnItemClickListener {
	
	private View emptyView;
	private ListView classesList;
	
	private List<Clas> classes;
	private ClassAdapter adapter;
	private int timeSelected;
	
	public static ClassesFragment newInstance(int timeSelected) {
		ClassesFragment f = new ClassesFragment();
		Bundle args = new Bundle();
		args.putInt("time", timeSelected);
		f.setArguments(args);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		timeSelected = (getArguments() != null) ? getArguments().getInt("time") : 0;
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.classes_fragment, null);
		initializeViews(view);
		updateViews();
		return view;
	}
	
	private void initializeViews(View view) {
		emptyView = view.findViewById(R.id.emptyView);
		classesList = (ListView) view.findViewById(R.id.classesList);
		classesList.setOnItemClickListener(this);
	}
	
	public void updateViews() {
		List<Clas> filtered = (timeSelected == MainScreen.TIME_TODAY) ?
			getTodayClasses() : classes;
		
		if (Utilities.isEmpty(filtered)) {
			emptyView.setVisibility(View.VISIBLE);
			classesList.setVisibility(View.INVISIBLE);
		} else {
			emptyView.setVisibility(View.INVISIBLE);
			classesList.setVisibility(View.VISIBLE);
			adapter = new ClassAdapter(this, filtered);
			classesList.setAdapter(adapter);
		}
	}
	
	private List<Clas> getTodayClasses() {
		Calendar calendar = Calendar.getInstance(); 
		long dayStart = Utilities.getDayStart(calendar.getTimeInMillis());
		long dayEnd = Utilities.getDayEnd(calendar.getTimeInMillis());
		List<Clas> filtered = null;
		if (!Utilities.isEmpty(classes)) {
			filtered = new ArrayList<Clas>();
			for (Clas clas : classes) {
				if (clas.getTime() > dayStart && clas.getTime() < dayEnd) {
					filtered.add(clas);
				}
			}
		}
		return filtered;
	}
	
	public void deleteClass(final Clas clas) {
		final ConfirmationDialog dialog = new ConfirmationDialog();
		dialog.setTitle(getString(R.string.information));
		dialog.setText(getString(R.string.delete_class_confirmation));
		dialog.setOkListener(getString(R.string.delete), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				((MainScreen) getActivity()).tryDeleteClass(clas);
				
			}
		});
		dialog.setCancelListener(getString(R.string.cancel), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.show(getChildFragmentManager(), "DeleteClassDialog");
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Clas clas = adapter.getItem(position);
		Intent intent = new Intent(getActivity(), ClassScreen.class);
		intent.putExtra(DatabaseHelper.FIELD_ID, clas.getId());
		intent.putExtra(DatabaseHelper.FIELD_NAME, clas.getName());
		startActivity(intent);
	}
	
	public void setTimeSelected(int timeSelected) {
		this.timeSelected = timeSelected;
		updateViews();
	}
	
	public void setClasses(List<Clas> classes) {
		this.classes = classes;
		updateViews();
	}

}
