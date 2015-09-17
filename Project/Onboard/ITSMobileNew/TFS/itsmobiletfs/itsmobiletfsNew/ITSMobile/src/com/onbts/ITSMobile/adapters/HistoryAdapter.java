package com.onbts.ITSMobile.adapters;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.model.HistoryModel;
import com.onbts.ITSMobile.model.wrapper.ExpandableInfoWrapper;
import com.onbts.ITSMobile.model.wrapper.TwoStringsWrapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by tigre on 01.06.14.
 */
public class HistoryAdapter extends BaseExpandableListAdapter {
    private final LayoutInflater inflater;
    private ArrayList<HistoryModel> models;
    private ArrayList<ExpandableInfoWrapper> mData = new ArrayList<>();
    private HashMap<String, List<TwoStringsWrapper>> listDataChild = new HashMap<>();
    private List<String> listDataHeader = new ArrayList<>();

    public HistoryAdapter(ArrayList<HistoryModel> models, LayoutInflater inflater) {
        this.models = models;
        this.inflater = inflater;
        updateData();
    }

    public String differenceBetweenStamps(long timestamp) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US); // the format of your date
        cal.setTimeInMillis(timestamp); // *1000 is to convert seconds to milliseconds
        Date date = cal.getTime();
        return sdf.format(date);
    }

    private void updateData() {
        mData.clear();
        listDataChild.clear();
        listDataHeader.clear();
        if (models != null) {
//        mData.add(new ExpandableInfoWrapper("History", null));
            int count = models.size();
            for (int i = count - 1; i >= 0; i--) {
                ArrayList<TwoStringsWrapper> h = new ArrayList<>();
                HistoryModel model = models.get(i);
//            History - Labels should be "Priority", "Assigned Department", "Assigned User", Time Since Last Update", "Update By"
                addDetailsItem(h, "Priority: ", model.getPriorityDesc());
                addDetailsItem(h, "Assigned Department: ", model.getDepartmentDesc());
                addDetailsItem(h, "Assigned User: ", model.getAssignedUser());
                if (i > 0) {
                    long prevStamp = models.get(i - 1).getLastUpdateDate();
                    long nowStamp = model.getLastUpdateDate();
                    addDetailsItem(h, "Time Since Last Update: ", differenceBetweenStamps(nowStamp - prevStamp));

                }
//            addDetailsItem(h, "Update Date: ", timestampToDate(model.getLastUpdateDate()));
                addDetailsItem(h, "Update By: ", model.getUpdatedByUser());
                addDetailsItem(h, "Notes: ", model.getNotes());

                addDetailsHeader(listDataChild, model.getActionDesc(), h, true);

            }
        }
        notifyDataSetChanged();
    }

//    public void addDetailsHeader(HashMap<String, List<TwoStringsWrapper>> map, String key, List<TwoStringsWrapper> list) {
//        if (list.size() > 0) {
//            listDataHeader.add(key);
//            map.put(key, list);
//            mData.add(new ExpandableInfoWrapper(key, list));
//        }
//    }

    public void addDetailsItem(ArrayList<TwoStringsWrapper> list, String title, String value) {
        if (value != null && value.length() > 0) {
            list.add(new TwoStringsWrapper(title, value));
        }
    }

    public void addDetailsHeader(HashMap<String, List<TwoStringsWrapper>> map, String key, List<TwoStringsWrapper> list, boolean isHistory) {
        if (list.size() > 0) {
            listDataHeader.add(key);
            map.put(key, list);
            mData.add(new ExpandableInfoWrapper(key, "(!)", list, true));
        }
    }

    public void setModels(ArrayList<HistoryModel> models) {
        this.models = models;
        updateData();
    }

    @Override
    public int getGroupCount() {
        return this.mData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.mData.get(groupPosition).getChildsList() == null ? 0 : this.mData.get(groupPosition).getChildsList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mData.get(groupPosition).getHeadTitle();
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

//        switch (getGroupType(groupPosition)) {
//            case TYPE_ITEM:
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.item_group_in_elv, parent, false);
        }
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.tvIssueElvCategory);
        ImageView lblListHeaderIndicator = (ImageView) convertView.findViewById(R.id.group_indicator);
        if (lblListHeader != null) {
//            lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(headerTitle);
            lblListHeaderIndicator.setImageResource(isExpanded ? R.drawable.arrow_up : R.drawable.arrow);
        }
//                break;
//            case TYPE_SEPARATOR:
//                String headerTitleSeparator = (String) getGroup(groupPosition);
//                if (convertView == null) {
//
//                    convertView = inflater.inflate(R.layout.item_group_separator, null);
//                }
//                TextView lblListHeaderSeparator = (TextView) convertView.findViewById(R.id.tvIssueElvCategory2);
//                lblListHeaderSeparator.setTypeface(null, Typeface.BOLD);
//                lblListHeaderSeparator.setText(headerTitleSeparator);
//                break;
//            default:
//
//        }


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

}
