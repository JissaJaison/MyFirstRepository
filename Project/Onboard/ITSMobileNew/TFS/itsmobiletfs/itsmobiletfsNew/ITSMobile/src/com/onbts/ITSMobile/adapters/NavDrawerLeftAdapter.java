package com.onbts.ITSMobile.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.model.NavDrawerItemLeft;

import java.util.ArrayList;

public class NavDrawerLeftAdapter extends BaseAdapter {
    ArrayList<NavDrawerItemLeft> mData;
    private Context mContext;

    public NavDrawerLeftAdapter(Context context, ArrayList<NavDrawerItemLeft> navDrawerItems) {
        this.mContext = context;
        this.mData = navDrawerItems;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_nav_drawer_left, parent, false);
        }
        ImageView icon = (ImageView) convertView.findViewById(R.id.vAlert);

        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        TextView tvCounter = (TextView) convertView.findViewById(R.id.tvCounter);
        TextView tvCounterNew = (TextView) convertView.findViewById(R.id.tvNewCounter);
        tvTitle.setText(mData.get(position).getTitle());
        tvCounter.setText(String.valueOf(mData.get(position).getCount()));
        if (mData.get(position).isAlert()) {
            icon.setVisibility(View.VISIBLE);
            icon.setImageResource(R.drawable.icon_alert_b);
        } else if (mData.get(position).isPreAlert()) {
            icon.setVisibility(View.VISIBLE);
            icon.setImageResource(R.drawable.icon_pre_alert_b);
        } else {
            icon.setVisibility(View.GONE);
        }
        if (mData.get(position).isCounterVisible()) {
            tvCounterNew.setText(String.valueOf((mData.get(position).getNewCount())));
            tvCounterNew.setVisibility(View.VISIBLE);
        } else {
            // hide the counter view
            tvCounterNew.setVisibility(View.GONE);
        }
        convertView.setTag(mData.get(position));

        return convertView;
    }

}
