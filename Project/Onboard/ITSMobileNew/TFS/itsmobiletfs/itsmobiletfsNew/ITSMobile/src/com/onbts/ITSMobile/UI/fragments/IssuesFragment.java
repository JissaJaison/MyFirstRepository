package com.onbts.ITSMobile.UI.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.onbts.ITSMobile.Loaders.AssignedDepartmentLoader;
import com.onbts.ITSMobile.Loaders.AssignedUserLoader;
import com.onbts.ITSMobile.Loaders.CombineQueryLoader;
import com.onbts.ITSMobile.Loaders.CreatedByDepartmentLoader;
import com.onbts.ITSMobile.Loaders.CreatedByUserLoader;
import com.onbts.ITSMobile.Loaders.FavoriteLoader;
import com.onbts.ITSMobile.Loaders.IssueCursorLoader;
import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.UI.fragments.base.BaseFragment;
import com.onbts.ITSMobile.adapters.IssueAdapter;
import com.onbts.ITSMobile.adapters.IssueCursorAdapter;
import com.onbts.ITSMobile.adapters.NavDrawerLeftAdapter;
import com.onbts.ITSMobile.adapters.NavDrawerRightAdapter;
import com.onbts.ITSMobile.adapters.PriorityAlertListAdapter;
import com.onbts.ITSMobile.adapters.SimpleAlertListAdapter;
import com.onbts.ITSMobile.interfaces.OnIssueListCallBack;
import com.onbts.ITSMobile.interfaces.OnNavigationChange;
import com.onbts.ITSMobile.interfaces.OnRefreshDrawer;
import com.onbts.ITSMobile.model.AlertPriorityChoiceItem;
import com.onbts.ITSMobile.model.AlertSimpleChoiceItem;
import com.onbts.ITSMobile.model.NavDrawerItemLeft;
import com.onbts.ITSMobile.model.NavDrawerItemRight;
import com.onbts.ITSMobile.model.UserModel;
import com.onbts.ITSMobile.model.issue.IssueModel;
import com.onbts.ITSMobile.services.DBRequest;
import com.onbts.ITSMobile.services.DbService;
import com.onbts.ITSMobile.services.ServiceDataBase;

import java.util.ArrayList;

import util.GetColorByPriority;
import util.SPHelper;

@Deprecated
public class IssuesFragment extends BaseFragment implements LoaderCallbacks<Cursor>, OnRefreshDrawer {

    private static final int LOADER_ID = 0x00000001;
    private static final int GET_ALL_ISSUES = 0;
    // To store now selected value in left menu
    private int leftMenuChecked = GET_ALL_ISSUES;
    private static final int ASSGND_DPRT_ISSUES = 2;
    private static final int ASSGND_USR_ISSUES = 1;
    private static final int CREATED_DPRT_ISSUES = 4;
    private static final int CREATED_USR_ISSUES = 3;
    private static final int COMBINE_QUERY = 5;
    private static final int FAVORITES_QUERY = 6;
    protected OnIssueListCallBack mNavigator;
    // for loader switching
    Bundle bundle = new Bundle();

    private View.OnClickListener mOnClearFiltersClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // Log.d("list", "clicked" + String.valueOf(position));
            SPHelper.clearSPfilter(getActivity());
            getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, bundle,
                                                                  IssuesFragment.this);
            fillRightMenu();
        }
    };
    private IssueAdapter adapter;
    private String[] strBuf;
    private int lastSelectedInSpinnerItem = -1;
    private OnItemClickListener issueClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
/*
            Cursor theCursor = ((IssueCursorAdapter) ((ListView) lvIssues).getAdapter()).getCursor();
            String selection = theCursor.getString(theCursor.getColumnIndex("_id"));
            Log.d("ica", selection + "cursor size: " + theCursor.getCount());
            DbService.getInstance(getActivity()).setViewed(selection, true);
*/
            mNavigator.onStartIssueDetail(id, adapter.getItems(), position);
        }
    };
    private OnItemClickListener mOnPriorityItemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            Log.d("list", "clicked" + String.valueOf(position));
            alertPriorityAdapter.changeRadio(position);

        }
    };
    private OnItemClickListener mOnSimpleItemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            Log.d("list", "clicked" + String.valueOf(position));
            alertSimpleAdapter.changeRadio(position);

        }
    };
    private String titleToShow = "";
    private PriorityAlertListAdapter alertPriorityAdapter;
    private SimpleAlertListAdapter alertSimpleAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private IssueCursorAdapter ica;
    //    private ArrayList<DetailedIssue> listIssues = new ArrayList<DetailedIssue>();
    private String[] navMenuLeftTitles;
    private String[] navMenuRightTitles;
    private View view;
    private ListView lvIssues;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListLeft;
    private ListView mDrawerListRight;
    private ArrayList<NavDrawerItemLeft> leftNavDrawerItems;
    private ArrayList<NavDrawerItemRight> rightNavDrawerItems;
    private NavDrawerLeftAdapter mLeftAdapter;
    private NavDrawerRightAdapter mRightAdapter;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    // filtres
    private ArrayList<AlertPriorityChoiceItem> itemPriorityList;
    private ArrayList<AlertSimpleChoiceItem> itemChoiceList;
    private ArrayAdapter<String> adp;
    private Spinner sp;
    private AlertDialog.Builder builder;
    private View customHeaderView;
    private View customBodyView;
    private int sortOrder = 0; // -1 DESC +1 - ASC
    private UserModel user;
    private TextView tvNowUsedFiltres, tvResetFilters;
    private LinearLayout llFilterLayout;
    // for progress dialog when data loaded
    private View mProgress;
    private boolean mDataShown;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnNavigationChange) {
            mNavigator = (OnIssueListCallBack) activity;
        } else {
            throw new ClassCastException("Activity must implements fragment's callbacks");
        }
        Log.d("life", "onattach created issuesfragment");
    }

    @Override
    public void onDetach() {
        mNavigator = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("life", "issuesFragment onCreateView " + savedInstanceState);
        //modified by Jissa on issue 11 of itsmobiles.
        view = inflater.inflate(R.layout.frag_issues,container,false);
        //view = inflater.inflate(R.layout.frag_issues,null);
        alertPriorityAdapter = new PriorityAlertListAdapter();
        mTitle = mDrawerTitle = getActivity().getTitle();
        Log.d("db", "onCreate IssueFragments");
        // getActivity.getSupportLoaderManager().restartLoader(LOADER_ID,
        // bundle, mLoaderCallback);
        ica = new IssueCursorAdapter(getActivity(), null, this);
//        lvIssues = new ListView(getActivity());
        adapter = new IssueAdapter(null, getActivity(), this);
        navMenuLeftTitles = getResources().getStringArray(R.array.nav_drawer_left_titles);
        navMenuRightTitles = getResources().getStringArray(R.array.nav_drawer_right_titles);

        leftNavDrawerItems = new ArrayList<NavDrawerItemLeft>();

        fillLeftMenu();
        rightNavDrawerItems = new ArrayList<NavDrawerItemRight>();
        fillRightMenu();

        mProgress = view.findViewById(R.id.pbProgress);
        tvNowUsedFiltres = (TextView) view.findViewById(R.id.tvTopFilter);
        tvResetFilters = (TextView) view.findViewById(R.id.tvResetFilters);
        tvResetFilters.setOnClickListener(mOnClearFiltersClick);
        llFilterLayout = (LinearLayout) view.findViewById(R.id.llFiltersLayout);
        lvIssues = (ListView) view.findViewById(android.R.id.list);
        mDrawerListLeft = (ListView) view.findViewById(R.id.lv_left_drawer);
        mDrawerListLeft.setOnItemClickListener(new LeftMenuClickListener());
        mDrawerListRight = (ListView) view.findViewById(R.id.lv_right_drawer);
        mDrawerListRight.setOnItemClickListener(new RightMenuClickListener());

        // mDrawerList.setAdapter(new ArrayAdapter<String>(this,
        // R.layout.drawer_list_item, mPlanetTitles));
//        lvIssues.setAdapter(ica);
        lvIssues.setAdapter(adapter);
        lvIssues.setOnItemClickListener(issueClickListener);

        mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout,
                                                  R.drawable.ic_navigation_drawer, R.string.app_name,
                                                  // accessibility
                                                  R.string.hello_world // nav drawer close - description for
                                                  // accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActivity().getActionBar().setTitle(titleToShow);
                // calling onPrepareOptionsMenu() to show action bar icons
                getActivity().invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActivity().getActionBar().setTitle("IssuTrax");
                // calling onPrepareOptionsMenu() to hide action bar icons
                getActivity().invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        // getActivity().getActionBar().setHomeButtonEnabled(true);
        // getActivity().getActionBar().setDisplayShowHomeEnabled(true);
        // getActivity().getActionBar().setDisplayUseLogoEnabled(true);
        // getActivity().getActionBar().setDisplayShowTitleEnabled(true);
        // getActivity().getActionBar().setDisplayShowCustomEnabled(false);
        mDrawerToggle.syncState();
        mLeftAdapter = new NavDrawerLeftAdapter(getActivity(), leftNavDrawerItems);
        View userInfoHeader = inflater.inflate(R.layout.header_account_info, null);
        ((TextView) userInfoHeader.findViewById(R.id.tvUserPosition)).setText(user.getName());
        ((TextView) userInfoHeader.findViewById(R.id.tvUserDepartment)).setText(user
                                                                                        .getDepartmentName());
        mDrawerListLeft.addHeaderView(userInfoHeader, null, false);
        mDrawerListLeft.setAdapter(mLeftAdapter);
        mRightAdapter = new NavDrawerRightAdapter(inflater, rightNavDrawerItems);
        mDrawerListRight.setAdapter(mRightAdapter);
        llFilterLayout.setVisibility(SPHelper.getFilterTextState(getActivity()));
        if (llFilterLayout.getVisibility() == View.VISIBLE) {
            Log.d("save", SPHelper.getFilterValue(getActivity()));
            //tvNowUsedFiltres.setText(SPHelper.getFilterValue(getActivity())); commented by Jissa on Issue 9 of changes.
        }
        // setListShown(true);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
//        setRetainInstance(true);
        LoaderManager.enableDebugLogging(true);
        Log.d("life", "issuesFragment onCreate");


    }

    @Override
    public void onResume() {
        super.onResume();
        mNavigator.onTitleChange(titleToShow);
    }

//    public void setData(ArrayList<DetailedIssue> listIssues) {
//        this.listIssues = listIssues;
//        // ica = new IssueCursorAdapter(this, cursor);
//    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!bundle.containsKey("id")) {
            bundle.putInt("id", ASSGND_USR_ISSUES);
        }
       /* Loader loader = getActivity().getSupportLoaderManager().getLoader(LOADER_ID);
        if (loader != null && !loader.isReset()) {
            getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, bundle, this);
        } else {
            getActivity().getSupportLoaderManager().initLoader(LOADER_ID, bundle, this);
        }*/
        Log.d("life", "onactivity created issuesfragment" + savedInstanceState);

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("life", "onStop");
        mDrawerToggle.setDrawerIndicatorEnabled(false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("loader", "onCreateLoader" + " id = " + id);
        // issueListCallBack.onShowProgressDialog();
        // TODO only for demo version, use bundle instead
        // issueListCallBack.onShowProgressDialog();
        // setListShown(true);
        switch (args.getInt("id")) {
            case GET_ALL_ISSUES:
                titleToShow = "All issues";
                leftMenuChecked = GET_ALL_ISSUES;
                return new IssueCursorLoader(getActivity(), DbService.getInstance(getActivity()));
            case ASSGND_DPRT_ISSUES:
                titleToShow = "Department assigned";
                leftMenuChecked = ASSGND_DPRT_ISSUES;
                return new AssignedDepartmentLoader(getActivity(),
                                                    DbService.getInstance(getActivity()), user.getDepartmentId(), user.getId());
            case ASSGND_USR_ISSUES:
                titleToShow = "User assigned";
                leftMenuChecked = ASSGND_USR_ISSUES;
                return new AssignedUserLoader(getActivity(), DbService.getInstance(getActivity()),
                                              user.getId());
            case CREATED_USR_ISSUES:
                titleToShow = "Created by user";
                leftMenuChecked = CREATED_USR_ISSUES;
                return new CreatedByUserLoader(getActivity(), DbService.getInstance(getActivity()),
                                               user.getId());
            case CREATED_DPRT_ISSUES:
                leftMenuChecked = CREATED_DPRT_ISSUES;
                titleToShow = "Created by department";
                return new CreatedByDepartmentLoader(getActivity(),
                                                     DbService.getInstance(getActivity()), user.getDepartmentId());
            case COMBINE_QUERY:
                Log.d("combine query:", String.valueOf(mDrawerListLeft.getCheckedItemPosition()));
                return new CombineQueryLoader(getActivity(), DbService.getInstance(getActivity()),
                                              leftMenuChecked, user.getId(), user.getDepartmentId());
            case FAVORITES_QUERY:
                return new FavoriteLoader(getActivity(), DbService.getInstance(getActivity()));
            default:
                break;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.isClosed())
            return;
        //issueListCallBack.onDismissProgressDialog();
        //setListShown(false);

        Log.d("loader",
              "onLoadFinished + " + String.valueOf(data.getCount()) + " id = " + loader.getId());
        ica.swapCursor(data);
        ica.notifyDataSetChanged();
        tvNowUsedFiltres.setText("Issues: " + data.getCount() + " " + getFormattedFilters());
        llFilterLayout.setVisibility(View.VISIBLE);
        String wasTitle = getActivity().getActionBar().getTitle().toString();
        int viewed = 0;
        if (!data.moveToFirst())
            return;
        do {
            if (data.getString(data.getColumnIndex("OpenedOnDevice")).equals("true"))
                viewed++;
        } while (data.moveToNext());

        titleToShow = wasTitle + "(" + data.getCount() + " / " + viewed + ")";
        // getActivity().getActionBar().setTitle(titleToShow);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d("loader", "onLoaderReset" + " id = " + loader.getId());

    }

    ;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.issues_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    // Here we hide our ActioBar items(menu), when drawer is opening;
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.d("db", "onPrepareMENU IssueFragments");

        // if nav drawer is opened, hide the action items
        // if nav drawer not exist yet, return, if it's possible to get NPE
        // other way
        if (mDrawerListLeft == null || mDrawerListRight == null)
            return;
        boolean drawerOpenLeft = mDrawerLayout.isDrawerOpen(mDrawerListLeft);
        boolean drawerOpenRight = mDrawerLayout.isDrawerOpen(mDrawerListRight);

        if (drawerOpenLeft) {
            menu.findItem(R.id.item_overflow).setVisible(!drawerOpenLeft);
            menu.findItem(R.id.item_refresh).setVisible(!drawerOpenLeft);
            menu.findItem(R.id.item_sort).setVisible(!drawerOpenLeft);
        }
        if (drawerOpenRight) {
            menu.findItem(R.id.item_overflow).setVisible(!drawerOpenRight);
            menu.findItem(R.id.item_refresh).setVisible(!drawerOpenRight);
            menu.findItem(R.id.item_sort).setVisible(!drawerOpenRight);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
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
                rbAsc.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        rbDesc.setChecked(!isChecked);
                        rbAsc.setChecked(isChecked);
                        sortOrder = 1;
                    }
                });
                rbDesc.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        rbAsc.setChecked(!isChecked);
                        rbDesc.setChecked(isChecked);
                        sortOrder = -1;
                    }
                });
                //Here we get our sort direction(1 - asc, -1 - desc)
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
                sp.setOnItemSelectedListener(new OnItemSelectedListener() {
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
                        String sortDirection = (sortOrder == -1 ? " DESC" : " ASC");
                        SPHelper.setSortBy(
                                getActivity(),
                                "ORDER BY "
                                        + GetColorByPriority.getSortDbTitle(adp
                                                                                    .getItem(lastSelectedInSpinnerItem)) + sortDirection
                        );

                        SPHelper.setSortLabel(getActivity(), adp.getItem(lastSelectedInSpinnerItem)
                                + "+" + sortDirection.toString());
                        bundle.putInt("id", COMBINE_QUERY);
                        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, bundle,
                                                                              IssuesFragment.this);
                    }
                });
                dialog.show();
                break;
            case R.id.item_refresh:
                // TODO DELETE BREAK MAKE SYNC
                mNavigator.onNeedSync();
                break;
            // leftMenuChecked = GET_ALL_ISSUES;
            // bundle.putInt("id", GET_ALL_ISSUES);
            // getActivity().getSupportLoaderManager().restartLoader(LOADER_ID,
            // bundle,
            // mLoaderCallback);
            case R.id.item_overflow:
                // TODO
                mDrawerLayout.openDrawer(Gravity.RIGHT);
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    public void fillRightMenu() {
        rightNavDrawerItems.clear();
        rightNavDrawerItems.add(new NavDrawerItemRight(navMenuRightTitles[0], SPHelper
                .getPriority(getActivity())));
        rightNavDrawerItems.add(new NavDrawerItemRight(navMenuRightTitles[1], SPHelper
                .getStatus(getActivity())));
        rightNavDrawerItems.add(new NavDrawerItemRight(navMenuRightTitles[2], SPHelper
                .getType(getActivity())));
        rightNavDrawerItems.add(new NavDrawerItemRight(navMenuRightTitles[3], SPHelper
                .getSection(getActivity())));
        rightNavDrawerItems.add(new NavDrawerItemRight(navMenuRightTitles[4], SPHelper
                .getDeck(getActivity())));
        rightNavDrawerItems.add(new NavDrawerItemRight(navMenuRightTitles[5], SPHelper
                .getDepartment(getActivity())));
        if (mRightAdapter != null) {
            mRightAdapter.notifyDataSetChanged();
        }
    }

    public void fillLeftMenu() {
        user = mNavigator.onGetUser();
        leftNavDrawerItems.clear();
        /*if (user != null && false) {
            leftNavDrawerItems.add(new NavDrawerItemLeft(navMenuLeftTitles[0], true, String
                    .valueOf(DbService.getInstance(getActivity()).getUserAssigned(user.getId()).getCount())));
            leftNavDrawerItems.add(new NavDrawerItemLeft(navMenuLeftTitles[1], true, String
                    .valueOf(DbService.getInstance(getActivity())
                                     .getDepartmentAssigned(user.getDepartmentId(), user.getId()).getCount())));
            leftNavDrawerItems.add(new NavDrawerItemLeft(navMenuLeftTitles[2], true,
                                                         String.valueOf(DbService.getInstance(getActivity()).getCreatedByUser(user.getId())
                                                                                .getCount())
            ));
            leftNavDrawerItems.add(new NavDrawerItemLeft(navMenuLeftTitles[3], true, String
                    .valueOf(DbService.getInstance(getActivity())
                                     .getCreatedByDepartment(user.getDepartmentId()).getCount())));
            leftNavDrawerItems.add(new NavDrawerItemLeft(navMenuLeftTitles[4], true, String
                    .valueOf(DbService.getInstance(getActivity()).getFavorites().getCount())));
            if (mLeftAdapter != null) {
                mLeftAdapter.notifyDataSetChanged();
            }
        }*/
    }
    public void buildCustomDialog(BaseAdapter adapter, String headerTitle,
                                  OnItemClickListener listener, DialogInterface.OnClickListener okListener) {
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.item_alert_header, null);
        View body = LayoutInflater.from(getActivity()).inflate(R.layout.alert_list_radio, null, false);
        builder = new AlertDialog.Builder(getActivity());
        ((TextView) header.findViewById(R.id.tvAlertTitle)).setText(headerTitle);
        builder.setCustomTitle(header);
        ListView lv = (ListView) body.findViewById(R.id.lvAlertList);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(listener);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        builder.setView(body);
        builder.setPositiveButton("OK", okListener);
        builder.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (tvNowUsedFiltres != null) {
            outState.putInt("filterText", tvNowUsedFiltres.getVisibility());
            Log.d("save", String.valueOf(tvNowUsedFiltres.getVisibility()));

            SPHelper.setFilterTextState(getActivity(), tvNowUsedFiltres.getVisibility());
            if (tvNowUsedFiltres.getVisibility() == View.VISIBLE) {
                Log.d("life", "onSaveInstanceState issuesfragment " + tvNowUsedFiltres.getText().toString());
                SPHelper.setFilterValue(getActivity(), tvNowUsedFiltres.getText().toString());
            }
        }
    }

    private void leftMenuSelected() {
        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, bundle, this);
        SPHelper.clearSPfilter(getActivity());
        fillRightMenu();
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    public void setListShown(boolean shown, boolean animate) {
        if (mDataShown == shown) {
            return;
        }
        mDataShown = shown;
        if (shown) {
            if (animate) {
                mProgress.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                                                                      android.R.anim.fade_out));
            }
            mProgress.setVisibility(View.GONE);
        } else {
            if (animate) {
                mProgress.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                                                                      android.R.anim.fade_in));

            }
            mProgress.setVisibility(View.VISIBLE);
        }
    }

    public void setListShown(boolean shown) {
        setListShown(shown, true);
    }

    public void setListShownNoAnimation(boolean shown) {
        setListShown(shown, false);
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

  /*  @Override
    public void onLeftMenuRefresh(boolean justFavorite) {
        if (justFavorite) {
            leftNavDrawerItems.remove(leftNavDrawerItems.size() - 1);
          *//*  leftNavDrawerItems.add(new NavDrawerItemLeft(navMenuLeftTitles[4], true, String
                    .valueOf(DbService.getInstance(getActivity()).getFavorites().getCount())));*//*
            if (mLeftAdapter != null) {
                mLeftAdapter.notifyDataSetChanged();
            }
        } else {
            fillLeftMenu();
        }

    }*/



    @Override
    public void onHandelDBMessage(DBRequest request) {
        switch (request.getType()) {
            case DEPARTMENT_ASSIGNED_ISSUE_LIST:
            case DEPARTMENT_CREATE_ISSUE_LIST:
            case USER_ASSIGNED_ISSUE_LIST:
            case USER_CREATE_ISSUE_LIST:
            case USER_FAVORITE_ISSUE_LIST:
                if (adapter != null)
                    adapter.setModels((ArrayList<IssueModel>) request.getModels());

        }
    }

    @Override
    public void updateIssue(long issueId, boolean open, boolean favorite) {

    }

    private class LeftMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // display view for selected nav drawer item
            switch (position) {
                case 1:
                    bundle.putInt("id", ASSGND_USR_ISSUES);
                    leftMenuSelected();
                    break;
                case 2:
                    bundle.putInt("id", ASSGND_DPRT_ISSUES);
                    leftMenuSelected();
                    break;
                case 3:
                    bundle.putInt("id", CREATED_USR_ISSUES);
                    leftMenuSelected();
                    break;
                case 4:
                    bundle.putInt("id", CREATED_DPRT_ISSUES);
                    leftMenuSelected();
                    break;
                // favorites
                case 5:
                    bundle.putInt("id", FAVORITES_QUERY);
                    leftMenuSelected();
                    break;

                default:
                    break;
            }
            Log.d("clickL", String.valueOf(position));
        }
    }

    private class RightMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // display view for selected nav drawer item
            Log.d("clickR", String.valueOf(position));
            /*
            switch (position) {
                case 0:
                    builder = new AlertDialog.Builder(getActivity());
                    customHeaderView = LayoutInflater.from(getActivity()).inflate(
                            R.layout.item_alert_header, null);
                    ((TextView) customHeaderView.findViewById(R.id.tvAlertTitle))
                            .setText("FILTER BY PRIORITY");
                    builder.setCustomTitle(customHeaderView);
                    customBodyView = LayoutInflater.from(getActivity()).inflate(
                            R.layout.alert_list_radio, null, false);
                    ListView listView = (ListView) customBodyView.findViewById(R.id.lvAlertList);

                    itemPriorityList = new ArrayList<AlertPriorityChoiceItem>();
                    itemPriorityList.add(new AlertPriorityChoiceItem(false, "Off", 0));
                    itemPriorityList.add(new AlertPriorityChoiceItem(false, "Critical", 5));
                    itemPriorityList.add(new AlertPriorityChoiceItem(false, "High", 4));
                    itemPriorityList.add(new AlertPriorityChoiceItem(false, "Medium", 3));
                    itemPriorityList.add(new AlertPriorityChoiceItem(false, "Low", 2));
                    itemPriorityList.add(new AlertPriorityChoiceItem(false, "Very low", 1));
                    alertPriorityAdapter = new PriorityAlertListAdapter(itemPriorityList, getActivity());
                    listView.setAdapter(alertPriorityAdapter);
                    listView.setOnItemClickListener(mOnPriorityItemClick);
                    listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    alertPriorityAdapter.getPositionByName(SPHelper.getPriority(getActivity()));
                    builder.setView(customBodyView);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (alertPriorityAdapter.isAnySelected()) {
                                SPHelper.setPriority(getActivity(),
                                                     alertPriorityAdapter.getCheckedTitle());
                            }

                            mDrawerLayout.closeDrawer(Gravity.RIGHT);
                            fillRightMenu();
                            // filter off value
                            Log.d("adapter alert",
                                  String.valueOf(alertPriorityAdapter.getCheckedItemPosition()));
                            bundle.putInt("id", COMBINE_QUERY);
                            getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, bundle,
                                                                                  IssuesFragment.this);

                        }
                    });
                    builder.show();

                    break;
                case 1:
                    String[] strBuf = getResources().getStringArray(R.array.spinner_statuses);
                    itemChoiceList = new ArrayList<AlertSimpleChoiceItem>();
                    for (int i = 0; i < strBuf.length; i++) {
                        itemChoiceList.add(new AlertSimpleChoiceItem(strBuf[i]));
                    }
                    alertSimpleAdapter = new SimpleAlertListAdapter(itemChoiceList, getActivity());
                    alertSimpleAdapter.getPositionByName(SPHelper.getStatus(getActivity()));
                    DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!alertSimpleAdapter.isAnySelected()) {
                                return;
                            }
                            SPHelper.setStatus(getActivity(), alertSimpleAdapter.getCheckedTitle());
                            mDrawerLayout.closeDrawer(Gravity.RIGHT);
                            fillRightMenu();
                            // filter off value
                            Log.d("adapter alert",
                                  String.valueOf(alertSimpleAdapter.getCheckedItemPosition()));
                            bundle.putInt("id", COMBINE_QUERY);
                            getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, bundle,
                                                                                  IssuesFragment.this);

                        }
                    };
                    buildCustomDialog(alertSimpleAdapter, "FILTER BY STATUS", mOnSimpleItemClick,
                                      okListener);
                    break;
                case 2:
                    List<String> listBuf = new ArrayList<String>();
//                    try {
//                        BufferedReader bReader = new BufferedReader(new InputStreamReader(getActivity()
//                                .getAssets().open("types.txt")));
//                        String line = bReader.readLine();
//                        while (line != null) {
//                            listBuf.add(line);
//                            line = bReader.readLine();
//                        }
//                        bReader.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    listBuf.clear();
                    String query = GetColorByPriority.switchLeftCheckedMenu(leftMenuChecked,
                                                                            user.getId(), user.getDepartmentId());
                    Cursor c = DbService.getInstance(getActivity()).getAllTypesInQuery(query);
                    listBuf.add("Off");
                    if (c.getCount() > 0) {
                        while (c.moveToNext()) {
                            listBuf.add(c.getString(c.getColumnIndex("IssueTypeDesc")));
                        }
                    }

                    strBuf = listBuf.toArray(new String[listBuf.size()]);
                    itemChoiceList = new ArrayList<AlertSimpleChoiceItem>();
                    for (int i = 0; i < strBuf.length; i++) {
                        itemChoiceList.add(new AlertSimpleChoiceItem(strBuf[i]));
                    }
                    alertSimpleAdapter = new SimpleAlertListAdapter(itemChoiceList, getActivity());
                    alertSimpleAdapter.getPositionByName(SPHelper.getType(getActivity()));

                    DialogInterface.OnClickListener okListenerType = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (alertSimpleAdapter.isAnySelected()) {
                                SPHelper.setType(getActivity(), alertSimpleAdapter.getCheckedTitle());
                            }
                            mDrawerLayout.closeDrawer(Gravity.RIGHT);
                            fillRightMenu();
                            // filter off value
                            Log.d("adapter alert",
                                  String.valueOf(alertSimpleAdapter.getCheckedItemPosition()));
                            bundle.putInt("id", COMBINE_QUERY);
                            getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, bundle,
                                                                                  IssuesFragment.this);

                        }
                    };
                    buildCustomDialog(alertSimpleAdapter, "FILTER BY TYPE", mOnSimpleItemClick,
                                      okListenerType);
                    break;
                case 3:
                    strBuf = getResources().getStringArray(R.array.spinner_sections);
                    itemChoiceList = new ArrayList<AlertSimpleChoiceItem>();
                    for (int i = 0; i < strBuf.length; i++) {
                        itemChoiceList.add(new AlertSimpleChoiceItem(strBuf[i]));
                    }
                    alertSimpleAdapter = new SimpleAlertListAdapter(itemChoiceList, getActivity());
                    alertSimpleAdapter.getPositionByName(SPHelper.getSection(getActivity()));

                    DialogInterface.OnClickListener okListenerSec = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (alertSimpleAdapter.isAnySelected()) {
                                SPHelper.setSection(getActivity(), alertSimpleAdapter.getCheckedTitle());
                            }
                            mDrawerLayout.closeDrawer(Gravity.RIGHT);
                            fillRightMenu();
                            // filter off value
                            Log.d("adapter alert",
                                  String.valueOf(alertSimpleAdapter.getCheckedItemPosition()));
                            bundle.putInt("id", COMBINE_QUERY);
                            getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, bundle,
                                                                                  IssuesFragment.this);

                        }
                    };
                    buildCustomDialog(alertSimpleAdapter, "FILTER BY SECTION", mOnSimpleItemClick,
                                      okListenerSec);
                    break;
                case 4:
                    strBuf = getResources().getStringArray(R.array.spinner_decks);
                    itemChoiceList = new ArrayList<AlertSimpleChoiceItem>();
                    for (int i = 0; i < strBuf.length; i++) {
                        itemChoiceList.add(new AlertSimpleChoiceItem(strBuf[i]));
                    }
                    alertSimpleAdapter = new SimpleAlertListAdapter(itemChoiceList, getActivity());
                    alertSimpleAdapter.getPositionByName(SPHelper.getDeck(getActivity()));

                    DialogInterface.OnClickListener okListenerDeck = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (alertSimpleAdapter.isAnySelected()) {
                                SPHelper.setDeck(getActivity(), alertSimpleAdapter.getCheckedTitle());
                            }
                            mDrawerLayout.closeDrawer(Gravity.RIGHT);
                            fillRightMenu();
                            // filter off value
                            Log.d("adapter alert",
                                  String.valueOf(alertSimpleAdapter.getCheckedItemPosition()));
                            bundle.putInt("id", COMBINE_QUERY);
                            getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, bundle,
                                                                                  IssuesFragment.this);

                        }
                    };
                    buildCustomDialog(alertSimpleAdapter, "FILTER BY DECK", mOnSimpleItemClick,
                                      okListenerDeck);
                    break;
                case 5:
                    strBuf = getResources().getStringArray(R.array.spinner_departments);
                    itemChoiceList = new ArrayList<AlertSimpleChoiceItem>();
                    for (int i = 0; i < strBuf.length; i++) {
                        itemChoiceList.add(new AlertSimpleChoiceItem(strBuf[i]));
                    }
                    alertSimpleAdapter = new SimpleAlertListAdapter(itemChoiceList, getActivity());
                    alertSimpleAdapter.getPositionByName(SPHelper.getDepartment(getActivity()));

                    DialogInterface.OnClickListener okListenerDep = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (alertSimpleAdapter.isAnySelected()) {
                                SPHelper.setDepartment(getActivity(),
                                                       alertSimpleAdapter.getCheckedTitle());
                            }
                            mDrawerLayout.closeDrawer(Gravity.RIGHT);
                            fillRightMenu();
                            // filter off value
                            Log.d("adapter alert",
                                  String.valueOf(alertSimpleAdapter.getCheckedItemPosition()));
                            bundle.putInt("id", COMBINE_QUERY);
                            getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, bundle,
                                                                                  IssuesFragment.this);

                        }
                    };
                    buildCustomDialog(alertSimpleAdapter, "FILTER BY DEPARTMENT", mOnSimpleItemClick,
                                      okListenerDep);
                    break;
                default:
                    break;
            }*/
        }
    }
}
