package com.onbts.ITSMobile.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.model.wrapper.ExpandableInfoWrapper;
import com.onbts.ITSMobile.model.wrapper.TwoStringsWrapper;

import java.util.ArrayList;
import java.util.Iterator;

public class ExpIssueAdapter extends BaseExpandableListAdapter {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    //    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<ExpandableInfoWrapper> mData;
    //stored for using in detailedPage, history group always expanded for no arrow
    private int historyPosition = -1;
    private boolean showHistory = false;

    public ExpIssueAdapter(Context context, ArrayList<ExpandableInfoWrapper> mData) {
        inflater = LayoutInflater.from(context);
        this.mData = mData;
    }

    @Override
    public int getGroupCount() {
        if (!showHistory)
            return this.mData.size() - getHistoryCount();
        return this.mData.size();
    }

    public int getHistoryCount() {
        int temp = 0;
        Iterator<ExpandableInfoWrapper> iter = mData.iterator();

        while (iter.hasNext()) {
            ExpandableInfoWrapper item = iter.next();
            if (item.isHistory())
                temp++;
        }
        return temp;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.mData.get(groupPosition).getChildsList() == null ? 0 : this.mData.get(groupPosition).getChildsList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        //return this.mListDataHeader.get(groupPosition);
        return this.mData.get(groupPosition).getHeadTitle();
    }

    public boolean isHistory(int groupPosition) {
        return this.mData.get(groupPosition).isHistory();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (childPosition > -1 && groupPosition > -1) {
            //return this.mListDataChild.get(this.mListDataHeader.get(groupPosition)).get(childPosition);
            return this.mData.get(groupPosition).getChildsList().get(childPosition);
        }
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        switch (getGroupType(groupPosition)) {
            case TYPE_ITEM:
               ExpandableInfoWrapper data =  this.mData.get(groupPosition);
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_group_in_elv, parent, false);
                }
                TextView lblListHeader = (TextView) convertView.findViewById(R.id.tvIssueElvCategory);
                TextView lblListHeaderValue = (TextView) convertView.findViewById(R.id.tvIssueElvCategoryValue);
                ImageView lblListHeaderIndicator = (ImageView) convertView.findViewById(R.id.group_indicator);
                if (lblListHeader != null) {
                    lblListHeaderValue.setText(data.getHeadValue());
                    lblListHeader.setText(data.getHeadTitle());
                    lblListHeaderIndicator.setImageResource(isExpanded ? R.drawable.arrow_up : R.drawable.arrow);
                }
                break;
            case TYPE_SEPARATOR:
                String headerTitleSeparator = (String) getGroup(groupPosition);
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_group_separator, parent, false);
                }
                TextView lblListHeaderSeparator = (TextView) convertView.findViewById(R.id.tvIssueElvCategory2);
                lblListHeaderSeparator.setTypeface(null, Typeface.BOLD);
                lblListHeaderSeparator.setText(headerTitleSeparator);
                break;
            default:
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        final TwoStringsWrapper childText = (TwoStringsWrapper) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_child_in_elv, parent, false);
        }
        if (childText.getLabel().equals("q")) {
            return childText.getLayout();
        }
        TextView tvValueText = (TextView) convertView.findViewById(R.id.tvExpandValue);
        TextView tvLabelText = (TextView) convertView.findViewById(R.id.tvExpandLabel);
        tvValueText.setText(childText.getValue());
        tvLabelText.setText(childText.getLabel());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void setData(ArrayList<ExpandableInfoWrapper> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupTypeCount() {
        return TYPE_SEPARATOR + 1;
    }

    @Override
    public int getGroupType(int groupPosition) {
        String headerTitle = (String) getGroup(groupPosition);
        switch (headerTitle) {
            case "History":
                return TYPE_SEPARATOR;
            case "Details":
                return TYPE_SEPARATOR;
            default:
                return TYPE_ITEM;
        }
    }

    public int getHistoryPosition() {
        int position = -1;
        for (int i = 0; i < mData.size(); i++) {
            String headerTitle = mData.get(i).getHeadTitle();
            if (headerTitle.equals("History")) {
                position = i;
                break;
            }
        }
        return position;
    }

    public String getGroupTitle(int i) {
        return mData != null && mData.size() > i ? mData.get(i).getHeadTitle() : "";
    }

    public int getDetailsPosition() {
        int position = -1;
        for (int i = 0; i < mData.size(); i++) {
            String headerTitle = mData.get(i).getHeadTitle();
            if (headerTitle.equals("Details")) {
                position = i;
                break;
            }
        }
        return position;
    }

    public void switchHistoryDisplay() {
        showHistory = !showHistory;
        notifyDataSetChanged();
    }
}