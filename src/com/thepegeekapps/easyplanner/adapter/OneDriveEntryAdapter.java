package com.thepegeekapps.easyplanner.adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thepegeekapps.easyplanner.R;

public class OneDriveEntryAdapter extends BaseAdapter {
	
	private Context context;
	private JSONArray array;
	
	public OneDriveEntryAdapter(Context context, JSONArray array) {
		this.context = context;
		this.array = array;
	}

	@Override
	public int getCount() {
		return array.length();
	}

	@Override
	public JSONObject getItem(int position) {
		return array.optJSONObject(position);
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
		
		JSONObject obj = getItem(position);
		
		TextView name = (TextView) convertView.findViewById(R.id.name);
		name.setText(obj.optString("name"));
		
		ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
		String type = obj.optString("type");
		int resId = type.equals("folder") || type.equals("album") ? R.drawable.ic_folder : R.drawable.ic_file;
		icon.setImageResource(resId);
				
		return convertView;
	}

}
