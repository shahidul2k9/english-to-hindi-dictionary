package com.shahidul.english.to.hindi.app.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.shahidul.english.to.hindi.app.R;

/**
 * @author Shahidul
 * @since 6/3/2015
 */
public class OptionAdapter extends BaseAdapter {
    private TypedArray optionIcons;
    private String[] optionNames;
    private LayoutInflater layoutInflater;

    public OptionAdapter(Context context, String[] optionNames, TypedArray optionIcons){
        this.optionNames = optionNames;
        this.optionIcons = optionIcons;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return optionNames.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        OptionItemHolder holder;
        if (convertView == null){
            view = layoutInflater.inflate(R.layout.option_item, null);
            holder = new OptionItemHolder();
            holder.optionName = (TextView) view.findViewById(R.id.option_name);
            holder.optionIcon = (ImageView) view.findViewById(R.id.option_icon);
            view.setTag(holder);
        }
        else{
            view = convertView;
            holder = (OptionItemHolder) view.getTag();
        }

        holder.optionName.setText(optionNames[position]);
        holder.optionIcon.setImageDrawable(optionIcons.getDrawable(position));
        return view;
    }

    private static class OptionItemHolder{
        TextView optionName;
        ImageView optionIcon;
    }
}
