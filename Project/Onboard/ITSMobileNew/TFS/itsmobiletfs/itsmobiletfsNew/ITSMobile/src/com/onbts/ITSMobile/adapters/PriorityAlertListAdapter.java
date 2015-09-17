package com.onbts.ITSMobile.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.model.AlertPriorityChoiceItem;
import com.onbts.ITSMobile.model.FilterModel;

import java.util.ArrayList;

import util.GetColorByPriority;

public class PriorityAlertListAdapter extends BaseAdapter {

    private ArrayList<AlertPriorityChoiceItem> mData;
    private Context mContext;
    private LayoutInflater inflater;
    private OnItemClickListener mOnItemClick = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

        }
    };

    public PriorityAlertListAdapter(ArrayList<AlertPriorityChoiceItem> data, Context context) {
        mData = data;
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    public PriorityAlertListAdapter() {
        mData = new ArrayList<AlertPriorityChoiceItem>();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    public String getCheckedTitle() {
        return mData.get(getCheckedItemPosition()).getFilter().title;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Log.d("list", "getview : " + String.valueOf(position));
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_alert_list, null);
        }
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitleAlertList);
        View v = (View) convertView.findViewById(R.id.vPriorAlertList);
        v.setBackgroundColor(GetColorByPriority.getColor(mData.get(position).getFilter().title, mContext));
        tvTitle.setText(mData.get(position).getFilter().title);
        RadioButton rb = (RadioButton) convertView.findViewById(R.id.rbRadioAlertList);
        rb.setChecked(mData.get(position).isChecked());

        return convertView;
    }

    public ArrayList<AlertPriorityChoiceItem> getData() {
        return mData;
    }

    public void changeRadio(int pos) {
        for (AlertPriorityChoiceItem a : mData) {
            a.setChecked(false);
        }
        mData.get(pos).setChecked(true);
        this.notifyDataSetChanged();
        Log.d("list", "changed");
    }

    public int getCheckedItemPosition() {
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).isChecked() == true) {
                return i;
            }
        }
        return -1;
    }

    public FilterModel getCheckedFilter() {
        return mData.get(getCheckedItemPosition()).getFilter();
    }
    public boolean isAnySelected() {
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).isChecked() == true) {
                return true;
            }
        }
        return false;
    }

    public int getPositionByName(String name) {
        int position = -1;
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getFilter().title.equals(name)) {
                position = i;
                mData.get(i).setChecked(true);
            }
        }
        return position;
    }
}
