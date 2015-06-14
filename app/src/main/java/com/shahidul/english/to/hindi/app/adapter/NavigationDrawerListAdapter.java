package com.shahidul.english.to.hindi.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.shahidul.english.to.hindi.app.R;
import com.shahidul.english.to.hindi.app.model.NavigationDrawerItem;

import java.util.ArrayList;

public class NavigationDrawerListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<NavigationDrawerItem> navDrawerItems;
	
	public NavigationDrawerListAdapter(Context context, ArrayList<NavigationDrawerItem> navDrawerItems){
		this.navDrawerItems = navDrawerItems;
		mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {		
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
        }
         
        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        TextView txtCount = (TextView) convertView.findViewById(R.id.counter);
         
        imgIcon.setImageResource(navDrawerItems.get(position).getIcon());        
        txtTitle.setText(navDrawerItems.get(position).getTitle());

        if(navDrawerItems.get(position).getCounterVisibility()){
        	txtCount.setText(navDrawerItems.get(position).getCount());
        }else{
        	txtCount.setVisibility(View.GONE);
        }
        return convertView;
	}

}
