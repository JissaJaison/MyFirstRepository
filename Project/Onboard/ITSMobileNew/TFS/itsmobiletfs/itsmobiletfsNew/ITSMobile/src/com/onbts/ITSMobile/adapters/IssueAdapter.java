package com.onbts.ITSMobile.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.UI.fragments.IssueListFragment;
import com.onbts.ITSMobile.interfaces.OnRefreshDrawer;
import com.onbts.ITSMobile.model.FilterModel;
import com.onbts.ITSMobile.model.issue.IssueModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import util.GetColorByPriority;

/**
 * Created by tigre on 15.04.14.
 */
public class IssueAdapter extends BaseAdapter implements Filterable {
    public static long DAY_THRESHOLD = 86400000; //24*1000*60 ms


    private ArrayList<IssueModel> backupModels;
    private ArrayList<IssueModel> models;
    private LayoutInflater inflater;
    private Context mContext;
    private OnRefreshDrawer listener;

    //Filtering models
    private FilterModel priorityFilter;
    private FilterModel alertFilter;
    private FilterModel statusFilter;
    private FilterModel sectionFilter;
    private FilterModel typeFilter;
    private FilterModel deckFilter;
    private FilterModel departmentFilter;
    private FilterModel sortModel;
    private FilterModel firezoneFilter;


    private  ArrayList<FilterModel>  locationID;

    IssueListFragment.UpdateLineEvent updateListener;

    public IssueAdapter(ArrayList<IssueModel> models, Context context, OnRefreshDrawer refreshListener) {
        super();
        this.models = models;
        backupModels = models;
        this.inflater = LayoutInflater.from(context);
        mContext = context;
        listener = refreshListener;
        //default sort
        sortModel = new FilterModel(1, "id");
    }

    public IssueAdapter(ArrayList<IssueModel> models, Context context, OnRefreshDrawer refreshListener, IssueListFragment.UpdateLineEvent updateListener) {
        super();
        this.models = models;
        backupModels = models;
        this.inflater = LayoutInflater.from(context);
        mContext = context;
        listener = refreshListener;
        //default sort
        sortModel = new FilterModel(1, "id");
        this.updateListener = updateListener;

    }

    public void setModels(ArrayList<IssueModel> models) {
        this.models = models;
        backupModels = models;
        notifyDataSetChanged();
    }

    public String timestampToDate(long time) {
        Calendar cal = Calendar.getInstance();
        long nowStamp = System.currentTimeMillis();
        long diff = nowStamp - time;
        if (diff < DAY_THRESHOLD) {
            long hours = TimeUnit.MILLISECONDS.toHours(diff);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            long seconds = TimeUnit.MILLISECONDS.toMinutes(diff);

            if (hours > 0) {
                return String.valueOf(hours) + " hour(s) ago.";
            }
            if (minutes > 0) {
                return String.valueOf(minutes) + " minute(s) ago.";
            }
            if (seconds > 0) {
                return String.valueOf(seconds) + " second(s) ago.";
            }
        }
        // Log.d("time", String.valueOf(nowStamp) + "  " + timestamp + " diff: " + diff);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US); // the format of your date
        cal.setTimeInMillis(time); // *1000 is to convert seconds to milliseconds
        Date date = cal.getTime();
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return models != null ? models.size() : 0;
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return models != null && models.size() > position ? models.get(position) : null;
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return models != null && models.size() > position ? models.get(position).getId() : -1;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_issue, parent, false);
            holder.tvIssueStatus = (TextView) convertView.findViewById(R.id.tvIssueStatus);
            holder.tvIssueType = (TextView) convertView.findViewById(R.id.tvIssueType);
            holder.tvIssueDesc = (TextView) convertView.findViewById(R.id.tvIssueDesc);
            holder.tvIssueDate = (TextView) convertView.findViewById(R.id.tvIssueDate);
            holder.tvIssueId = (TextView) convertView.findViewById(R.id.tvIssueId);
            holder.tvLocationDesc = (TextView) convertView.findViewById(R.id.tvIssueLocation);
            holder.v = convertView.findViewById(R.id.vPrior);
            holder.imgStar = (ImageView) convertView.findViewById(R.id.imgFavorite);
            holder.llIssueBackground = (LinearLayout) convertView.findViewById(R.id.llIssueLayout);
            holder.attach = convertView.findViewById(R.id.imgAttach);
            holder.vAlert = (ImageView) convertView.findViewById(R.id.vAlert);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();


        final IssueModel model = models.get(position);
        holder.v.setBackgroundColor(GetColorByPriority.getColor(model.getPriority(), mContext));
        holder.tvIssueStatus.setText(model.getStatusDesc());
        holder.tvIssueType.setText(model.getTypeDesc());
        holder.tvIssueDate.setText(timestampToDate(model.getCreateDate()));
        if (holder.tvIssueDesc != null) {
            holder.tvIssueDesc.setText(model.getNotes());
        }
        holder.attach.setVisibility(model.isHasFile() ? View.VISIBLE : View.INVISIBLE);
        holder.tvIssueId.setText(String.valueOf(model.getId()));
        holder.tvLocationDesc.setText(model.getLocationDesc());
        if (!model.isFavorite()) {
            holder.imgStar.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_star));
        } else {
            holder.imgStar.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_star_pressed));
        }
        holder.imgStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.setFavorite(!model.isFavorite());
                listener.updateIssue(model.getId(), model.isOpenOnDevice(), model.isFavorite());
                notifyDataSetChanged();
            }
        });
        if (model.isOpenOnDevice()) {
            holder.llIssueBackground.setBackgroundColor(mContext.getResources().getColor(R.color.viewed));
            holder.tvIssueType.setTypeface(null, Typeface.NORMAL);
        } else {
            holder.llIssueBackground.setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
            holder.tvIssueType.setTypeface(null, Typeface.BOLD);
        }
        switch (model.getAlert()) {
            case 1:
                holder.vAlert.setImageResource(R.drawable.icon_pre_alert);
                holder.vAlert.setVisibility(View.VISIBLE);
                break;
            case 2:
                holder.vAlert.setImageResource(R.drawable.icon_alert);
                holder.vAlert.setVisibility(View.VISIBLE);
                break;
            default:
                holder.vAlert.setVisibility(View.GONE);
                break;
        }
        return convertView;
    }

    public ArrayList<IssueModel> getItems() {
        return models;
    }

    @Override
    public Filter getFilter() {
        final Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                if (constraint == null || backupModels == null) {
                    results.values = backupModels;
                    return results;
                }
                ArrayList<IssueModel> tempResults = new ArrayList<>();
//                ArrayList<IssueModel> afterFilters = new ArrayList<>();
                //After sequence splite we get two Strings:first - filter name, second - filter value
                for (IssueModel model : backupModels) {
                    boolean isAdd = checkFilter(priorityFilter, model.getPriority());
                    isAdd = isAdd && checkFilter(statusFilter, model.getStatusID());
                    isAdd = isAdd && checkFilter(typeFilter, model.getTypeID());
                    isAdd = isAdd && checkFilter(sectionFilter, model.getZoneID());
                    isAdd = isAdd && checkFilter(deckFilter, model.getDeckID());
                    isAdd = isAdd && checkFilter(departmentFilter, model.getDepartmentID());
                    isAdd = isAdd && checkFilter(firezoneFilter, model.getFireZoneID());
                    isAdd = isAdd && checkFilter(alertFilter, model.getAlert());
//                    isAdd = isAdd &&  checkFilter(locationID, model.getLocationGroupID());
                    isAdd = isAdd && checkArrayFilter(locationID, model.getLocationID());
                    if (isAdd)
                        tempResults.add(model);
                }
                //HERE WE START SORTING
                switch (sortModel.title) {
                    case "Issue ID":
                        if (sortModel.id > 0)
                            Collections.sort(tempResults, new IssueModel.ascIdComparator());
                        else
                            Collections.sort(tempResults, new IssueModel.descIdComparator());
                        break;
                    case "Date":
                        if (sortModel.id > 0)
                            Collections.sort(tempResults, new IssueModel.ascDateComparator());
                        else
                            Collections.sort(tempResults, new IssueModel.descDateComparator());
                        break;
                    case "Location":
                        if (sortModel.id == 0)
                            Collections.sort(tempResults, new IssueModel.ascLocationComparator());
                        else
                            Collections.sort(tempResults, new IssueModel.descLocationComparator());
                        break;
                    case "Priority":
                        if (sortModel.id > 0)
                            Collections.sort(tempResults, new IssueModel.ascPriorityComparator());
                        else
                            Collections.sort(tempResults, new IssueModel.descPriorityComparator());
                        break;

                }
                results.values = tempResults;
                return results;
            }

            public boolean checkFilter(FilterModel filter, long idModel) {
                if ((filter != null && (filter.id <= 0 || idModel == filter.id))
                        || (filter == null))
                    return true;
                else
                    return false;
            }

            public boolean checkArrayFilter(ArrayList<FilterModel> filter, long idModel) {
                if ((filter != null && (filter.size() <= 0 || parseFilterArray(filter, idModel)))
                        || (filter == null))
                    return true;
                else
                    return false;
            }

            private boolean parseFilterArray(ArrayList<FilterModel> a, long id) {
                for (FilterModel fm : a) {
                    if (fm.id == id)
                        return true;
                }
                return false;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                models = (ArrayList<IssueModel>) results.values;
                notifyDataSetChanged();
                if (updateListener != null) {
                    updateListener.updateline();
                }
            }
        };
        return filter;
    }

    public void setStatusFilter(FilterModel priority, FilterModel status, FilterModel type, FilterModel section, FilterModel deck,
                                FilterModel department, FilterModel firezone, ArrayList<FilterModel> locationID, FilterModel alertFilter) {
        this.priorityFilter = priority;
        this.statusFilter = status;
        this.typeFilter = type;
        this.sectionFilter = section;
        this.deckFilter = deck;
        this.departmentFilter = department;
        this.firezoneFilter = firezone;
        this.locationID = locationID;
        this.alertFilter = alertFilter;
    }

    public ArrayList<IssueModel> getBackupModels() {
        return backupModels;
    }

    public void setSortFilter(FilterModel sort) {
        sortModel = sort;
    }

    public ArrayList<IssueModel> getItemsAll() {
        return backupModels;
    }

    public FilterModel getStatusFilter() {
        return statusFilter;
    }

    public FilterModel getPriorityFilter() {
        return priorityFilter;
    }

    public FilterModel getSectionFilter() {
        return sectionFilter;
    }

    public FilterModel getTypeFilter() {
        return typeFilter;
    }

    public FilterModel getDeckFilter() {
        return deckFilter;
    }

    public FilterModel getDepartmentFilter() {
        return departmentFilter;
    }

    public FilterModel getFirezoneFilter() {
        return firezoneFilter;
    }

    public ArrayList<FilterModel> getLocationIDFilter() {
        return locationID;
    }

    public int getAllCount() {
        return backupModels != null ? backupModels.size() : 0;
    }

    public FilterModel getAlertFilter() {
        return alertFilter;
    }

    private static class ViewHolder {
        TextView tvIssueStatus;
        TextView tvIssueType;
        TextView tvIssueDesc;
        TextView tvIssueDate;
        TextView tvIssueId;
        TextView tvLocationDesc;
        ImageView imgStar;
        View attach;
        LinearLayout llIssueBackground;
        View v;
        ImageView vAlert;
    }

    public void clearFilters() {
        priorityFilter = null;
        statusFilter = null;
        typeFilter = null;
        deckFilter = null;
        departmentFilter = null;
        sectionFilter = null;
        firezoneFilter = null;
        locationID = null;
        alertFilter = null;
        getFilter().filter("q");
    }
}
