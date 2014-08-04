package com.thepegeekapps.easyplanner.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI.Entry;
import com.thepegeekapps.easyplanner.R;

public class DropboxEntryAdapter extends BaseAdapter {
	
	private Context context;
	private List<Entry> contents;
	
	public DropboxEntryAdapter(Context context, List<Entry> contents) {
		this.context = context;
		this.contents = contents;
	}

	@Override
	public int getCount() {
		return contents.size();
	}

	@Override
	public Entry getItem(int position) {
		return contents.get(position);
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
			convertView = inflater.inflate(R.layout.entry_list_item, null);
		}
		
		Entry entry = getItem(position);
		
		ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
		icon.setImageResource(entry.isDir ? R.drawable.ic_folder : R.drawable.ic_file);
		
		TextView name = (TextView) convertView.findViewById(R.id.name);
		name.setText(entry.fileName());
		
		return convertView;
	}

}
