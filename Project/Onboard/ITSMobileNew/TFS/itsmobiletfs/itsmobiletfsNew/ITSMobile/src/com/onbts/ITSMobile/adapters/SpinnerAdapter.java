package com.onbts.ITSMobile.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.model.SpinnerNameAndId;

public class SpinnerAdapter extends BaseAdapter {
	private List<SpinnerNameAndId> list;
	LayoutInflater mInflater;
	
	public SpinnerAdapter(Context context, List<SpinnerNameAndId> list) {
		this.list = list;
		mInflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return list.get(position).getId();
	}

	static class ViewHolder {
	    public TextView tvValue;
	}
	 
	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		ViewHolder holder;
		if (view == null) {
			view = mInflater.inflate(R.layout.item_spinner, null);
            holder = new ViewHolder();
            holder.tvValue = (TextView)view.findViewById(R.id.tvItemSpinner);
            view.setTag(holder);
		} else {
            holder = (ViewHolder) view.getTag();
        }
		
        holder.tvValue.setText(list.get(position).getNameNote());
		
		return view;
	}
}
