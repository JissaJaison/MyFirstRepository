package com.onbts.ITSMobile.UI.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.UI.broadcastReceivers.ProgressLoadBroadcast;
import com.onbts.ITSMobile.UI.fragments.base.BaseFragment;
import com.onbts.ITSMobile.adapters.IssueAdapter;
import com.onbts.ITSMobile.interfaces.OnFiltered;
import com.onbts.ITSMobile.interfaces.OnIssueListCallBack;
import com.onbts.ITSMobile.interfaces.OnNavigationChange;
import com.onbts.ITSMobile.interfaces.OnRefreshDrawer;
import com.onbts.ITSMobile.model.FilterModel;
import com.onbts.ITSMobile.model.issue.IssueModel;
import com.onbts.ITSMobile.services.DBRequest;
import com.onbts.ITSMobile.services.ServiceDataBase;
import com.onbts.ITSMobile.services.ServiceSync;
import com.onbts.ITSMobile.services.SyncService;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import util.SPHelper;

/**
 * Created by tigre on 16.04.14.
 */
public class IssueListFragment extends BaseFragment implements AdapterView.OnItemClickListener, OnRefreshDrawer, OnRefreshListener {
    protected OnIssueListCallBack issueListCallBack;
    DBRequest request;
    //Pull to refresh layout
    private PullToRefreshLayout mPullToRefreshLayout;
    private IssueAdapter adapter;
    private ArrayList<IssueModel> models;
    private View customBodyView;
    private int sortOrder = 0; // -1 DESC +1 - ASC
    private ArrayAdapter<String> adp;
    private int lastSelectedInSpinnerItem = -1;
    private FilterModel sortModel;
    private OnFiltered filterListener;
    private LinearLayout llFilterLayout;
    private TextView tvNowUsedFiltres, tvResetFilters;
    private View.OnClickListener mOnClearFiltersClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // Log.d("list", "clicked" + String.valueOf(position));
            SPHelper.clearSPfilter(getActivity());

            //Clear filter models in adapter and refresh listview
            clearFilters();

            //Update right menu from shared preferences
            issueListCallBack.refreshRightMenu();
            //update black line
            updateFilterLine();
            /*getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, bundle,
                    IssuesFragment.this);
            fillRightMenu();*/
        }
    };
    private BroadcastReceiver myBroadcastReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d("broad", intent.getSerializableExtra(ServiceSync.KEY_KEY_CURRENT_TASK_STATE).toString());
                    SyncService.SyncTaskState state = (SyncService.SyncTaskState) intent.getSerializableExtra(ServiceSync.KEY_KEY_CURRENT_TASK_STATE);
                    if (state != null) {
                        switch (state) {
                            case MSG_HARD_SYNC_COMPLETED:
                            case MSG_NO:
                            case MSG_SYNC_CANCELED:
                            case MSG_SYNC_FAILED:
                            case MSG_SYNC_REJECTED:
                            case MSG_SYNC_COMPLETED:
                                mPullToRefreshLayout.setRefreshComplete();
                                break;
                        }
                        Log.d("refresh", "completed");
                    }
                }
            };
    private ProgressLoadBroadcast syncBroadCast = new ProgressLoadBroadcast();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnNavigationChange) {
            issueListCallBack = (OnIssueListCallBack) activity;
        } else {
            throw new ClassCastException("Activity must implements fragment's OnIssueListCallBack");
        }

        if (activity instanceof OnFiltered) {
            filterListener = (OnFiltered) activity;
        } else {
            throw new ClassCastException("Activity must implements fragment's OnFiltered");
        }
    }

    @Override
    public void onDetach() {
        issueListCallBack = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_issue, container, false);
        //  find the PullToRefreshLayout to setup
        mPullToRefreshLayout = (PullToRefreshLayout) v.findViewById(R.id.ptr_layout);
        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(getActivity())
                .options(Options.create()
                        // Here we make the refresh scroll distance to 75% of the refreshable view's height
                        .scrollDistance(.45f)


                        .build())
                        // Mark All Children as pullable
                .allChildrenArePullable()
                        // Set a OnRefreshListener
                .listener(this)
                        // Finally commit the setup to our PullToRefreshLayout
                .setup(mPullToRefreshLayout);
        llFilterLayout = (LinearLayout) v.findViewById(R.id.llFiltersLayout);
        tvNowUsedFiltres = (TextView) v.findViewById(R.id.tvTopFilter);
        tvResetFilters = (TextView) v.findViewById(R.id.tvResetFilters);
        tvResetFilters.setOnClickListener(mOnClearFiltersClick);
        ListView list = (ListView) v.findViewById(R.id.list);
        setHasOptionsMenu(true);
        list.setOnItemClickListener(this);
        UpdateLineEvent updateListener = new UpdateLineEvent() {
            @Override
            public void updateline() {
                updateFilterLine();
            }
        };
        adapter = new IssueAdapter(models, getActivity(), this, updateListener);
        list.setAdapter(adapter);
        if (savedInstanceState == null) {
            sortModel = new FilterModel(1, "Issue ID");
            sortOrder = 1;
        } else {
            sortModel = new FilterModel(savedInstanceState.getInt("direction"),
                    savedInstanceState.getString("type_sort"));
        }

        filterListener.setSort(sortModel);
        updateFilterLine();
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("direction", (int) sortModel.id);
        outState.putString("type_sort", sortModel.title);
        Log.d("onSave", "saved state: ORDER = " + sortOrder + "TYPE: " + sortModel.title);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onHandelDBMessage(DBRequest request) {
        switch (request.getType()) {
            case DEPARTMENT_ASSIGNED_ISSUE_LIST:
            case DEPARTMENT_CREATE_ISSUE_LIST:
            case USER_ASSIGNED_ISSUE_LIST:
            case USER_CREATE_ISSUE_LIST:
            case USER_FAVORITE_ISSUE_LIST:
                this.request = request;
                this.models = (ArrayList<IssueModel>) request.getModels();
                if (adapter != null) {
                    adapter.setModels(models);
                    adapter.getFilter().filter("q");
                }
                break;
        }
    }

    public void onSetIssueModels(ArrayList<IssueModel> models) {
        this.models = models;
        if (adapter != null)
            adapter.setModels(models);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        issueListCallBack.onStartIssueDetail(id, adapter.getItems(), position);
    }

    public void addFilter(FilterModel priority, FilterModel status,
                          FilterModel type, FilterModel section, FilterModel deck, FilterModel department, FilterModel firezone, ArrayList<FilterModel> locationIDs, FilterModel alertFilter) {
        adapter.setStatusFilter(priority, status, type, section, deck, department, firezone, locationIDs, alertFilter);
        adapter.setSortFilter(filterListener.getSort());
        adapter.getFilter().filter("qwe");
        updateFilterLine();
    }

    public ArrayList<FilterModel> getFilters() {
        ArrayList<FilterModel> filters = new ArrayList<>();
        filters.add(adapter.getPriorityFilter());
        filters.add(adapter.getStatusFilter());
        filters.add(adapter.getTypeFilter());
        filters.add(adapter.getSectionFilter());
        filters.add(adapter.getDeckFilter());
        filters.add(adapter.getDepartmentFilter());
        filters.add(adapter.getFirezoneFilter());
//        filters.add(adapter.getLocationIDFilter());
        filters.add(adapter.getAlertFilter());
        return filters;
    }

    public ArrayList<FilterModel> getLocationIDsFilter() {
        return adapter.getLocationIDFilter();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.issues_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_sort:
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                View customHeader = LayoutInflater.from(getActivity()).inflate(
                        R.layout.item_alert_header, null);
                ((TextView) customHeader.findViewById(R.id.tvAlertTitle)).setText("SORT ISSUES");
                dialog.setCustomTitle(customHeader);
                View customView = LayoutInflater.from(getActivity()).inflate(R.layout.alert_sort, null,
                        false);
                Spinner sp = (Spinner) customView.findViewById(R.id.spSort);
                final RadioButton rbAsc = (RadioButton) customView.findViewById(R.id.rbAsc);
                final RadioButton rbDesc = (RadioButton) customView.findViewById(R.id.RbDesc);
                rbAsc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        rbDesc.setChecked(!isChecked);
                        rbAsc.setChecked(isChecked);
                        sortOrder = 1;
                    }
                });
                rbDesc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        rbAsc.setChecked(!isChecked);
                        rbDesc.setChecked(isChecked);
                        sortOrder = 0;
                    }
                });
                //Here we get our sort direction(1 - asc, 0 - desc)
                String[] parts = SPHelper.getSortLabel(getActivity()).split("\\+");
                if (parts[1].equals("1"))
                    rbAsc.setChecked(true);
                else
                    rbDesc.setChecked(true);
                adp = new ArrayAdapter<String>(getActivity(), R.layout.item_spinner, getResources()
                        .getStringArray(R.array.spinner_sort));
                int pos = adp.getPosition(parts[0]);
                customBodyView = LayoutInflater.from(getActivity()).inflate(R.layout.alert_spinner,
                        null, false);
                sp.setAdapter(adp);
                Log.v("spinner", " " + pos);
                sp.setSelection(pos);
                sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        lastSelectedInSpinnerItem = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                dialog.setView(customView);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //String sortDirection = (sortOrder == 0 ? " DESC" : " ASC");

                        sortModel = new FilterModel(sortOrder, adp
                                .getItem(lastSelectedInSpinnerItem));
                        SPHelper.setSortLabel(getActivity(), adp.getItem(lastSelectedInSpinnerItem)
                                + "+" + sortOrder);
                        ArrayList<FilterModel> f = getFilters();
                        filterListener.setSort(sortModel);
                        filterListener.setFilter(f.get(0), f.get(1), f.get(2), f.get(3), f.get(4), f.get(5), f.get(6), getLocationIDsFilter(), f.get(7));
                    }
                });
                dialog.show();
                break;
        }
        return false;
    }

    @Override
    public void updateIssue(long issueId, boolean open, boolean favorite) {
        issueListCallBack.updateIssue(issueId, open, favorite);
    }

    public void updateFilterLine() {
        llFilterLayout.setVisibility(View.VISIBLE);
        tvNowUsedFiltres.setText("Issues: " + adapter.getCount() + " " + getFormattedFilters());
        issueListCallBack.updateTitle(adapter.getCount(), adapter.getAllCount());
    }

    public String getFormattedFilters() {
        StringBuilder sb = new StringBuilder();
        String res = "";
        ArrayList<String> filters = new ArrayList<String>();
        filters.add("priority - " + SPHelper.getPriority(getActivity()));
        filters.add("status - " + SPHelper.getStatus(getActivity()));
        filters.add("type - " + SPHelper.getType(getActivity()));
        filters.add("section - " + SPHelper.getSection(getActivity()));
        filters.add("deck - " + SPHelper.getDeck(getActivity()));
        filters.add("department - " + SPHelper.getDepartment(getActivity()));
        filters.add("firezone - " + SPHelper.getFirezone((getActivity())));
        filters.add("location groups - " + SPHelper.getLocationGroup((getActivity())));
        filters.add("alert - " + SPHelper.getAlert((getActivity())));

        for (String s : filters) {
            if (!s.contains("OFF")) {
                sb.append(s + ", ");
            }
        }
        if (sb.length() > 0) {
            res = sb.substring(0, sb.toString().length() - 2);
        }
        return res;
    }

    public void clearFilters() {
        adapter.clearFilters();
        ArrayList<FilterModel> f = getFilters();
        filterListener.setSort(sortModel);
        filterListener.setFilter(f.get(0), f.get(1), f.get(2), f.get(3), f.get(4), f.get(5), f.get(6), getLocationIDsFilter(), f.get(7));
    }

    @Override
    public void onRefreshStarted(View view) {
        issueListCallBack.onNeedSync();
    }

    public IssueAdapter getAdapter() {
        return adapter;
    }

    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ServiceSync.ACTION_SYNC_SERVICE);

        getActivity().registerReceiver(myBroadcastReceiver, filter);
    }

    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(myBroadcastReceiver);
    }

    public interface UpdateLineEvent {
        public void updateline();
    }
}
