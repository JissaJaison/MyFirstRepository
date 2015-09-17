package com.onbts.ITSMobile.UI.activities;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.UI.activities.base.BaseIssueActivity;
import com.onbts.ITSMobile.UI.fragments.IssueListFragment;
import com.onbts.ITSMobile.UI.fragments.menu.LeftMenuFragment;
import com.onbts.ITSMobile.UI.fragments.menu.RightMenuFragment;
import com.onbts.ITSMobile.interfaces.OnDrawerMove;
import com.onbts.ITSMobile.interfaces.OnFiltered;
import com.onbts.ITSMobile.interfaces.OnIssueListCallBack;
import com.onbts.ITSMobile.interfaces.OnLeftMenuCallBack;
import com.onbts.ITSMobile.model.DetailedIssue;
import com.onbts.ITSMobile.model.FilterModel;
import com.onbts.ITSMobile.model.NavDrawerItemLeft;
import com.onbts.ITSMobile.model.ReturnDateWithActionDialog;
import com.onbts.ITSMobile.model.UserModel;
import com.onbts.ITSMobile.model.issue.IssueModel;
import com.onbts.ITSMobile.model.issue.UpdateIssueModel;
import com.onbts.ITSMobile.services.DBRequest;
import com.onbts.ITSMobile.services.DbService;
import com.onbts.ITSMobile.services.ServiceDataBase;
import com.onbts.ITSMobile.services.SyncService;
import com.onbts.ITSMobile.util.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import util.SPHelper;

/**
 * Created by tigre on 07.04.14.
 */
public class IssueListActivityIts extends BaseIssueActivity implements
        OnIssueListCallBack, OnFiltered, OnDrawerMove, OnLeftMenuCallBack {
    NavDrawerItemLeft.NavDrawerItemLeftType type;
    private IssueListFragment issueList;
    private LeftMenuFragment leftMenu;
    private RightMenuFragment rightMenu;
    private DrawerLayout mDrawerLayout;
    private ArrayList<FilterModel> priorityList;
    private ArrayList<FilterModel> statusList;
    private ArrayList<FilterModel> sectionsList;
    private ArrayList<FilterModel> typesList;
    private ArrayList<FilterModel> decksList;
    private ArrayList<FilterModel> departmentsList;
    private ArrayList<FilterModel> firezonesList;
    private ArrayList<FilterModel> locationGroupsList;

    private FilterModel sortModel;
    private DBRequest current;
    private ActionBarDrawerToggle mDrawerToggle;
    private String titleToShow;
    //OnboarD Added 11/10
    private Boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DbService.getInstance(getApplicationContext()).init();
        setContentView(R.layout.ac_list_issue);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        issueList = (IssueListFragment) getSupportFragmentManager().findFragmentById(R.id.issue_list_fragment);
        leftMenu = (LeftMenuFragment) getSupportFragmentManager().findFragmentById(R.id.menu_left);
        rightMenu = (RightMenuFragment) getSupportFragmentManager().findFragmentById(R.id.menu_right);

        Log.e("onCreate", "onCreate = " + user.getId());

        FloatingActionButton fabButton = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.add_button))
                .withButtonColor(Color.parseColor("#5FBDFC"))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16)
                .create();

        fabButton.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             if(isNetworkConnected()){
                                                 openIssueCreateApp();
                                             } else {
                                                 showToast("Please ensure that you are connected to the internet");
                                             }
                                         }
                                     }
        );

        if (savedInstanceState == null) {
//            onCategorySelected(leftMenu.getLeftNavDraverPositions(0));
            current = new DBRequest(DBRequest.DBRequestType.USER_ASSIGNED_ISSUE_LIST);
            sendRequestToDB(new DBRequest(DBRequest.DBRequestType.GET_FILTER_PRIORITIES));
            sendRequestToDB(new DBRequest(DBRequest.DBRequestType.GET_FILTER_LIST_STATUSES));
            sendRequestToDB(new DBRequest(DBRequest.DBRequestType.GET_FILTER_LIST_TYPES));
            sendRequestToDB(new DBRequest(DBRequest.DBRequestType.GET_FILTER_LIST_SECTIONS));
            sendRequestToDB(new DBRequest(DBRequest.DBRequestType.GET_FILTER_LIST_DECKS));
            sendRequestToDB(new DBRequest(DBRequest.DBRequestType.GET_FILTER_LIST_DEPARTMENTS));
            sendRequestToDB(new DBRequest(DBRequest.DBRequestType.GET_FILTER_LIST_FIREZONES));
            sendRequestToDB(new DBRequest(DBRequest.DBRequestType.GET_FILTER_LIST_LOCATIONGROUPS));

        } else {

//            NavDrawerItemLeft.NavDrawerItemLeftType type = (NavDrawerItemLeft.NavDrawerItemLeftType) savedInstanceState.get("type");
            current = new DBRequest(DBRequest.DBRequestType.valueOf(savedInstanceState.getString("type")));
            priorityList = savedInstanceState.getParcelableArrayList("priorityList");
            statusList = savedInstanceState.getParcelableArrayList("statusList");
            sectionsList = savedInstanceState.getParcelableArrayList("sectionsList");
            typesList = savedInstanceState.getParcelableArrayList("typesList");
            decksList = savedInstanceState.getParcelableArrayList("decksList");
            departmentsList = savedInstanceState.getParcelableArrayList("departmentsList");
            firezonesList = savedInstanceState.getParcelableArrayList("firezonesList");
            locationGroupsList = savedInstanceState.getParcelableArrayList("locationGroupsList");
            titleToShow = savedInstanceState.getString("title");
            getActionBar().setTitle(titleToShow);
            updateTitle();
//            updateAll();
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_navigation_drawer, R.string.app_name,
                // accessibility
                R.string.hello_world // nav drawer close - description for
                // accessibility
        ) {
            public void onDrawerClosed(View view) {
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    getActionBar().setTitle("Filters");
//                    try {
//                        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT))
//                            mDrawerLayout.closeDrawer(Gravity.LEFT);
//                    } catch (Exception e) {
//                    }
//
                } else if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    getActionBar().setTitle("Folders");
//                    try {
//
//                    } catch (Exception e) {
//                        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT))
//                            mDrawerLayout.closeDrawer(Gravity.RIGHT);
//                    }
//
                } else
                    getActionBar().setTitle(titleToShow);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
//
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    getActionBar().setTitle("Filters");
//                    try {
//                        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT))
//                            mDrawerLayout.closeDrawer(Gravity.LEFT);
//                    } catch (Exception e) {
//                    }
//
                } else if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    getActionBar().setTitle("Folders");
//                    try {
//
//                    } catch (Exception e) {
//                        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT))
//                            mDrawerLayout.closeDrawer(Gravity.RIGHT);
//                    }
//
                }
//                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        // getActivity().getActionBar().setHomeButtonEnabled(true);
        // getActivity().getActionBar().setDisplayShowHomeEnabled(true);
        // getActivity().getActionBar().setDisplayUseLogoEnabled(true);
        // getActivity().getActionBar().setDisplayShowTitleEnabled(true);
        // getActivity().getActionBar().setDisplayShowCustomEnabled(false);
    }

    private void openIssueCreateApp() {
        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.onbts.ITSMobile.createIssue");
        if (LaunchIntent != null) {
            Intent intent = new Intent(Intent.ACTION_DEFAULT);
            intent.putExtra("CREATE_ISSUE_USER_ID", user.getId());
            intent.putExtra("FROM_OTHER_APP","102");
            intent.setComponent(new ComponentName("com.onbts.ITSMobile.createIssue","com.onbts.ITSMobile.createIssue.UI.IssueClassesActivity"));
          //  intent.setComponent(new ComponentName("com.onbts.ITSMobile.createIssue","com.onbts.ITSMobile.createIssue.UI.LoginActivity"));
            startActivity(intent);

            /*LaunchIntent.putExtra("CREATE_ISSUE_USER_ID", user.getId());
            LaunchIntent.setAction(Intent.ACTION_SEND);
            LaunchIntent.setType("text/plain");
            startActivity(LaunchIntent);*/
        } else {
           showToast("Application not installed");
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            SPHelper.clearSP(this);
        }
    }

    @Override
    public void onOpenFile(long id) {

    }

    @Override
    public void onStartIssueList() {
//        IssuesFragment issueList = new IssuesFragment();
//        changeFragment(issueList, true);
    }

    @Override
    public void onTitleChange(String title) {
        getActionBar().setTitle(title);
    }

    @Override
    public void updateIssue(long issueId, boolean open, boolean favorite) {
        if (issueId > 0) {
            Intent intent = getDB();
            intent.putExtra("issue_update", new UpdateIssueModel(open, favorite, issueId));
            intent.putExtra(ServiceDataBase.KEY_REQUEST, new DBRequest(DBRequest.DBRequestType.UPDATE_ISSUE));
            sendDBRequest(intent);
        }
    }

    @Override
    public void refreshRightMenu() {
        rightMenu.fillRightMenu();
    }

    @Override
    public void onStartIssueDetail(long issueId, ArrayList<IssueModel> models, int position) {
        updateIssue(issueId, true, models.get(position).isFavorite());
        Intent intent = new Intent(this, IssueDetailsActivityIts.class);
        intent.putExtra("user", user);
        intent.putExtra("position", position);
        intent.putExtra("issueID", issueId);
        if (models != null) {
//            intent.putParcelableArrayListExtra("models", models);
            long[] ids = new long[models.size()];
            for (int i = 0; i < models.size(); i++) {
                ids[i] = models.get(i).getId();
            }
            intent.putExtra("ids", ids);
        }
        startActivity(intent);
    }

    @Override
    @Deprecated
    public void onShowActionBar() {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("type", current.getType().name());
        outState.putParcelableArrayList("priorityList", priorityList);
        outState.putParcelableArrayList("statusList", statusList);
        outState.putParcelableArrayList("sectionsList", sectionsList);
        outState.putParcelableArrayList("typesList", typesList);
        outState.putParcelableArrayList("decksList", decksList);
        outState.putParcelableArrayList("departmentsList", departmentsList);
        outState.putParcelableArrayList("firezonesList", firezonesList);
        outState.putParcelableArrayList("locationGroupsList", locationGroupsList);
        outState.putString("title", titleToShow);
        super.onSaveInstanceState(outState);
    }

    @Override
    @Deprecated
    public void onHideActionBar() {


    }

    @Override
    public void onShowProgressDialog() {
        mDialog.show(this, "Wait", "Loading");
    }

    @Override
    public void onDismissProgressDialog() {
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onSetUser(UserModel user) {
        this.user = user;
    }

    @Override
    public UserModel onGetUser() {
        return user;
    }

    @Override
    @Deprecated
    public void updateTitle(int count, int allCount) {
        if (type != null)
//            titleToShow = getString(type.getTitle()) + " (" + count + "/" + allCount + ")";
            if (mDrawerLayout != null && !mDrawerLayout.isDrawerOpen(Gravity.LEFT) && !mDrawerLayout.isDrawerOpen(Gravity.RIGHT))
                getActionBar().setTitle(titleToShow);
    }

    public void updateTitle() {
        if (type != null)
            titleToShow = getString(type.getTitle()) + " (" + type.getCount() + ")";
        if (mDrawerLayout != null && !mDrawerLayout.isDrawerOpen(Gravity.LEFT) && !mDrawerLayout.isDrawerOpen(Gravity.RIGHT))
            getActionBar().setTitle(titleToShow);
    }

    @Override
    public void onCategorySelected(NavDrawerItemLeft item) {
        type = item.getType();
        updateTitle();
        switch (item.getType()) {
            case DEPARTMENT_ASSIGNED:
                current = new DBRequest(DBRequest.DBRequestType.DEPARTMENT_ASSIGNED_ISSUE_LIST);
                break;
            case DEPARTMENT_CREATED:
                current = new DBRequest(DBRequest.DBRequestType.DEPARTMENT_CREATE_ISSUE_LIST);
                break;
            case USER_ASSIGNED:
                current = new DBRequest(DBRequest.DBRequestType.USER_ASSIGNED_ISSUE_LIST);
                break;
            case USER_CREATED:
                current = new DBRequest(DBRequest.DBRequestType.USER_CREATE_ISSUE_LIST);
                break;
            case USER_FAVORITE:
                current = new DBRequest(DBRequest.DBRequestType.USER_FAVORITE_ISSUE_LIST);
                break;
        }
        Log.i("IssueListActivityIts", "onCategorySelected = " + item);
        onUpdateIssues();
        if (mDrawerLayout != null)
            mDrawerLayout.closeDrawers();
    }

    @Override
    protected void onUserUpdate() {
        leftMenu.onUserUpdate(user);
    }

    @Override
    public void onSetDefaultLocation() {
        if (SPHelper.getLocationGroup(this).equals("OFF"))
            for (FilterModel model : locationGroupsList) {
                if (model.id == user.getLocationGroupID()) {
                    SPHelper.setLocationGroup(this,
                            model.title);
                    rightMenu.setFilterLocationFilter(model);
                    rightMenu.onGetFilter();
                    break;
                }
            }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onHandelDBMessage(DBRequest request) {
        super.onHandelDBMessage(request);
        switch (request.getType()) {
            case GET_FILTER_PRIORITIES:
                priorityList = new ArrayList<>();
                priorityList.add(0, new FilterModel(0, "OFF"));
                priorityList.addAll((ArrayList<FilterModel>) request.getModels());
                break;
            case GET_FILTER_LIST_STATUSES:
                statusList = new ArrayList<>();
                statusList.add(0, new FilterModel(0, "OFF"));
                statusList.addAll((ArrayList<FilterModel>) request.getModels());
                break;
            case GET_FILTER_LIST_TYPES:
                typesList = new ArrayList<>();
                typesList.add(0, new FilterModel(0, "OFF"));
                typesList.addAll((ArrayList<FilterModel>) request.getModels());
                break;
            case GET_FILTER_LIST_SECTIONS:
                sectionsList = new ArrayList<>();
                sectionsList.add(0, new FilterModel(0, "OFF"));
                sectionsList.addAll((ArrayList<FilterModel>) request.getModels());
                break;
            case GET_FILTER_LIST_DECKS:
                decksList = new ArrayList<>();
                decksList.add(0, new FilterModel(0, "OFF"));
                decksList.addAll((ArrayList<FilterModel>) request.getModels());
                break;
            case GET_FILTER_LIST_DEPARTMENTS:
                departmentsList = new ArrayList<>();
                departmentsList.add(0, new FilterModel(0, "OFF"));
                departmentsList.addAll((ArrayList<FilterModel>) request.getModels());
                break;
            case GET_FILTER_LIST_FIREZONES:
                firezonesList = new ArrayList<>();
                firezonesList.add(0, new FilterModel(0, "OFF"));
                firezonesList.addAll((ArrayList<FilterModel>) request.getModels());
                break;
            case GET_FILTER_LIST_LOCATIONGROUPS:
                locationGroupsList = new ArrayList<>();
                locationGroupsList.add(0, new FilterModel(0, "OFF"));
                locationGroupsList.addAll((ArrayList<FilterModel>) request.getModels());
                onSetDefaultLocation();
                break;
            case GET_FILTER_LIST_LOCATION_ID:
                rightMenu.setLocationIDFilter((ArrayList<FilterModel>) request.getModels());
                break;
            case UPDATE_ISSUE:
                updateUser();
                break;
            default:
                issueList.onHandelDBMessage(request);
                return;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(Gravity.LEFT))
                    mDrawerLayout.closeDrawers();
                else {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                }
                return true;
            case R.id.item_filter:
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT))
                    mDrawerLayout.closeDrawers();
                else {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mDrawerLayout == null) return super.onPrepareOptionsMenu(menu);
        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            menu.findItem(R.id.item_filter).setVisible(true);
            menu.findItem(R.id.item_refresh).setVisible(false);
            menu.findItem(R.id.item_sort).setVisible(false);
            return true;
        }
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            menu.findItem(R.id.item_filter).setVisible(false);
            menu.findItem(R.id.item_refresh).setVisible(false);
            menu.findItem(R.id.item_sort).setVisible(false);
            return true;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.issue_filter, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onHandleServiceMessage(SyncService.SyncTaskState currentTask,
                                          String message) {
        super.onHandleServiceMessage(currentTask, message);
        if (currentTask == SyncService.SyncTaskState.MSG_SYNC_COMPLETED) {
            updateAll();
        }
    }

    private void updateAll() {
        onUpdateIssues();
        updateUser();
    }

    private void onUpdateIssues() {
        updateTitle();
        if (rightMenu != null)
            rightMenu.onGetFilter();
        Log.i("IssueListActivityIts", "onUpdateIssues");
        Intent intent = getDB();
        intent.putExtra(ServiceDataBase.KEY_REQUEST, current);
        intent.putExtra("DepartmentID", user.getDepartmentId());
        intent.putExtra("userID", user.getId());
        sendDBRequest(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        mDrawerToggle.syncState();
        updateAll();

    }

    @Override
    public void onNeedSync() {
        runServiceSyncTask(SyncService.SyncTask.MANUAL_SYNC_TASK);
    }

    @Override
    public void onLoadDetaileIssue(long issueId) {

    }

    @Override
    public void onAddActions(DetailedIssue details, ArrayList<ReturnDateWithActionDialog> data, long idAction, long prevActionID, long nextActionID, boolean keep) {

    }

    @Override
    public void setFilter(FilterModel priority, FilterModel status,
                          FilterModel type, FilterModel section, FilterModel deck, FilterModel department, FilterModel firezone, ArrayList<FilterModel> locationIDs, FilterModel alertFilter) {
        rightMenu.setFilters(priority, status, type, section, deck, department, firezone, locationIDs, alertFilter);
        issueList.addFilter(priority, status, type, section, deck, department, firezone, locationIDs, alertFilter);
        issueList.updateFilterLine();
        closeDrawer(Gravity.RIGHT);
    }

    @Override
    public ArrayList<FilterModel> getFilters(DBRequest request) {
        switch (request.getType()) {
            case GET_FILTER_PRIORITIES:
                return priorityList;
            case GET_FILTER_LIST_STATUSES:
                return statusList;
            case GET_FILTER_LIST_TYPES:
                return typesList;
            case GET_FILTER_LIST_SECTIONS:
                return sectionsList;
            case GET_FILTER_LIST_DECKS:
                return decksList;
            case GET_FILTER_LIST_DEPARTMENTS:
                return departmentsList;
            case GET_FILTER_LIST_FIREZONES:
                return firezonesList;
            case GET_FILTER_LIST_LOCATIONGROUPS:
                return locationGroupsList;
        }
        return null;
    }

    @Override
    public FilterModel getSort() {
        return sortModel;
    }

    @Override
    public void setSort(FilterModel sort) {
        this.sortModel = sort;
    }

    @Override
    public void clearAllFilters() {
        issueList.clearFilters();
    }

    @Override
    public ArrayList<FilterModel> getListOfActualFilters(String filter) {
        ArrayList<FilterModel> flist = new ArrayList<>();

        ArrayList<IssueModel> issues = issueList.getAdapter().getItems();
        if (issues == null) {
            flist.add(new FilterModel(-1, "OFF"));
            return flist;
        }
        switch (filter) {
            case "type":
                for (IssueModel im : issues) {
                    addingWithDuplicateCheck(flist, new FilterModel(im.getTypeID(), im.getTypeDesc()));
                }
                Collections.sort(flist, new Comparator<FilterModel>() {
                    @Override
                    public int compare(FilterModel lhs, FilterModel rhs) {
                        return lhs.title.compareTo(rhs.title);
                    }
                });
                flist.add(0, new FilterModel(-1, "OFF"));
                break;
            case "sections":
                // not used
                break;

        }
        return flist;
    }

    public void addingWithDuplicateCheck(ArrayList<FilterModel> list, FilterModel im) {
//        if (!list.contains(im))

        for (FilterModel s : list) {
            if (s.id == im.id) {
                return;
            }
        }
        list.add(im);
    }

    public void sendRequestToDB(DBRequest request) {
        sendDBRequest(getDB().putExtra(ServiceDataBase.KEY_REQUEST, request));
    }

    //TODO Tigre 11/12: more better
/*
    private long timeExitPressed;
    private final static long TIME_TO_EXIT = 3*1000;
*/

    @Override
    public void onBackPressed() {

        //TODO Tigre 11/12: more better
/*
        if (System.currentTimeMillis() - timeExitPressed < TIME_TO_EXIT){
            finish();
        }else{
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            timeExitPressed = System.currentTimeMillis();
        }
*/

        if (exit)
            this.finish();
        else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }

    @Override
    public void closeDrawer(int gravity) {
        mDrawerLayout.closeDrawer(gravity);
    }

    @Override
    public Intent onGetDB() {
        return getDB();
    }

    @Override
    public void onSendDBRequest(Intent i) {
        sendDBRequest(i);
    }

    @Override
    public void onShowHistory(long id) {

    }

    @Override
    public void onActionMoreClock(DetailedIssue details) {

    }

    @Override
    public void onShowDetails(long id) {

    }


}
