package com.thepegeekapps.easyplanner.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thepegeekapps.easyplanner.R;

public class MenuAdapter extends BaseAdapter {
	
	private Context context;
	private String[] items;
	private int[] images = new int[] {
		R.drawable.contact_icon,
		R.drawable.visit_site_icon,
		R.drawable.report_bug_icon,
		R.drawable.tell_friend_icon,
		0
	};
	
	public MenuAdapter(Context context) {
		this.context = context;
		items = context.getResources().getStringArray(R.array.menu_items);
	}

	@Override
	public int getCount() {
		return items.length;
	}

	@Override
	public String getItem(int position) {
		return items[position];
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
			convertView = inflater.inflate(R.layout.menu_list_item, null);
		}
		
		ImageView image = (ImageView) convertView.findViewById(R.id.image);
		image.setVisibility(items[position].equalsIgnoreCase("logout") ? View.GONE : View.VISIBLE);
		image.setImageResource(images[position]);
		
		TextView text = (TextView) convertView.findViewById(R.id.text);
		text.setText(items[position]);
		
		return convertView;
	}

}
