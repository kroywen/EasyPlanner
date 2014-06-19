package com.thepegeekapps.easyplanner.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thepegeekapps.easyplanner.R;
import com.thepegeekapps.easyplanner.fragment.ClassesFragment;
import com.thepegeekapps.easyplanner.model.Clas;

public class ClassAdapter extends BaseAdapter {
	
	private ClassesFragment fragment;
	private List<Clas> classes;
	
	public ClassAdapter(ClassesFragment fragment, List<Clas> classes) {
		this.fragment = fragment;
		this.classes = classes;
	}

	@Override
	public int getCount() {
		return classes.size();
	}

	@Override
	public Clas getItem(int position) {
		return classes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return classes.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.class_list_item, null);
		}
		
		final Clas clas = classes.get(position);
		
		TextView name = (TextView) convertView.findViewById(R.id.name);
		name.setText(clas.getName());
		
		ImageView removeBtn = (ImageView) convertView.findViewById(R.id.removeBtn);
		removeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fragment.deleteClass(clas);
			}
		});
		
		return convertView;
	}

}
