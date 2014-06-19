package com.thepegeekapps.easyplanner.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.adapter.ClassAdapter;
import com.thepegeekapps.easyplanner.dialog.ConfirmationDialog;
import com.thepegeekapps.easyplanner.model.Clas;
import com.thepegeekapps.easyplanner.storage.db.DatabaseStorage;

public class ClassesFragment extends Fragment {
	
	private View emptyView;
	private ListView classesList;
	
	private List<Clas> classes;
	private DatabaseStorage dbStorage;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbStorage = new DatabaseStorage(getActivity());
	}
	
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
	}
	
	public void updateViews() {
		classes = dbStorage.getClasses();
		if (classes == null || classes.isEmpty()) {
			emptyView.setVisibility(View.VISIBLE);
			classesList.setVisibility(View.INVISIBLE);
		} else {
			emptyView.setVisibility(View.INVISIBLE);
			classesList.setVisibility(View.VISIBLE);
			ClassAdapter adapter = new ClassAdapter(this, classes);
			classesList.setAdapter(adapter);
		}
	}
	
	public void deleteClass(final Clas clas) {
		final ConfirmationDialog dialog = new ConfirmationDialog();
		dialog.setRetainInstance(true);
		dialog.setTitle(getString(R.string.information));
		dialog.setText(getString(R.string.delete_class_confirmation));
		dialog.setOkListener(getString(R.string.delete), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dbStorage.deleteClas(clas);
				updateViews();
				dialog.dismiss();
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

}
