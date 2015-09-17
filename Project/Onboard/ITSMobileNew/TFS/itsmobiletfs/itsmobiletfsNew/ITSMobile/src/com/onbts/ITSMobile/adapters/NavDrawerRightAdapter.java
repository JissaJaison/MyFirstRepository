package com.onbts.ITSMobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.model.NavDrawerItemRight;

import java.util.ArrayList;

public class NavDrawerRightAdapter extends BaseAdapter {
    ArrayList<NavDrawerItemRight> mData;
    private LayoutInflater inflater;

    public NavDrawerRightAdapter(LayoutInflater inflater, ArrayList<NavDrawerItemRight> navDrawerItems) {
        this.inflater = inflater;
        this.mData = navDrawerItems;
    }

    @Override
    public int getCount() {
        return mData!=null?mData.size():0;
    }

    @Override
    public Object getItem(int position) {
        return mData!=null && mData.size()>position? mData.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_nav_drawer_right, null);
        }
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvFilterName);
        TextView tvValue = (TextView) convertView.findViewById(R.id.tvFilterValue);
        tvTitle.setText(mData.get(position).getFilterName());
        tvValue.setText(mData.get(position).getFilterValue());
        return convertView;
    }

}
