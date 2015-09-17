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
import com.onbts.ITSMobile.model.AlertSimpleChoiceItem;
import com.onbts.ITSMobile.model.FilterModel;

import java.util.ArrayList;

public class SimpleAlertListAdapter extends BaseAdapter {

    ArrayList<AlertSimpleChoiceItem> mData;
    Context mContext;
    LayoutInflater inflater;
    private OnItemClickListener mOnItemClick = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

        }
    };

    public SimpleAlertListAdapter(ArrayList<AlertSimpleChoiceItem> data, Context context) {
        mData = data;
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    public SimpleAlertListAdapter() {
        mData = new ArrayList<AlertSimpleChoiceItem>();
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
        if (getCheckedItemPosition() > 0) {
            return mData.get(getCheckedItemPosition()).getFilter().title;
        } else {
            return "OFF";
        }
    }

    public String getCheckedTitle(int pos) {
        if (pos>0 && mData.size()>pos) {
            return mData.get(pos).getFilter().title;
        } else {
            return "OFF";
        }
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
            convertView = mInflater.inflate(R.layout.item_alert_simple_choice, null);
        }
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitleAlertList);
        tvTitle.setText(mData.get(position).getFilter().title);
        RadioButton rb = (RadioButton) convertView.findViewById(R.id.rbRadioAlertList);
        rb.setChecked(mData.get(position).isChecked());

        return convertView;
    }

    public ArrayList<AlertSimpleChoiceItem> getData() {
        return mData;
    }

    public void changeRadio(int pos) {
        for (AlertSimpleChoiceItem a : mData) {
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

    public int isAnySelected() {
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).isChecked() == true) {
                return i;
            }
        }
        return 0;
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

    public FilterModel getCheckedFilter() {
        int pos = getCheckedItemPosition();
        if (pos >= 0 && pos < mData.size())
            return mData.get(pos).getFilter();
        return null;
    }
}