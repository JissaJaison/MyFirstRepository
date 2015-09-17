package com.onbts.ITSMobile.UI.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.UI.activities.base.BaseIssueActivity;
import com.onbts.ITSMobile.UI.dialogs.action.ActionDialog;
import com.onbts.ITSMobile.UI.fragments.DetailedPage;
import com.onbts.ITSMobile.adapters.IssueDetailedPageAdapter;
import com.onbts.ITSMobile.model.ActionIssue;
import com.onbts.ITSMobile.model.DetailedIssue;

import com.onbts.ITSMobile.model.InsertTrackResult;
import com.onbts.ITSMobile.model.ReturnDateWithActionDialog;
import com.onbts.ITSMobile.model.UserModel;
import com.onbts.ITSMobile.model.issue.UpdateIssueModel;
import com.onbts.ITSMobile.services.DBRequest;
import com.onbts.ITSMobile.services.DbService;
import com.onbts.ITSMobile.services.ServiceDataBase;
import com.onbts.ITSMobile.services.SyncService;

import java.util.ArrayList;

/**
 * Created by tigre on 08.04.14.
 */
public class IssueDetailsActivityIts extends BaseIssueActivity implements ViewPager.OnPageChangeListener, ActionDialog.ActionDialogListener {
    private long[] modelsID;
    private ViewPager viewPager;
    private IssueDetailedPageAdapter pagerAdapter;
    private int selectedPage;
    private long issueId;
    private Menu menu;
    private MenuItem menuItemStart;
    private MenuItem menuItemFavorite;

    //    @Override
//    public void onStartIssueDetail(long issueId, ArrayList<IssueModel> models, int position) {
//        Intent intent = new Intent(this, IssueDetailsActivityIts.class);
//        intent.putExtra("user", user);
//        intent.putExtra("position", position);
//        intent.putExtra("issueID", issueId);
//        intent.putParcelableArrayListExtra("models", models);
//        startActivity(intent);
//    }
    private DetailedIssue detailedIssue;
    private ActionDialog dialog;
    private MenuItem menuItemNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        DbService.getInstance(getApplicationContext()).init();
        setContentView(R.layout.ac_issue);
        viewPager = (ViewPager) findViewById(R.id.vpDetailedIssue);
//        viewPager.setPageTransformer(false, new ZoomOutPageTransformer());

//        IssueDetailFragment issueDetailFragment = new IssueDetailFragment();
        Intent intent = getIntent();
        modelsID = intent.getLongArrayExtra("ids");

        pagerAdapter = new IssueDetailedPageAdapter(getSupportFragmentManager(), modelsID, this);
        viewPager.setAdapter(pagerAdapter);
        int index = intent.getIntExtra("position", -1);
        viewPager.setOnPageChangeListener(this);
        getActionBar().setTitle("ISSUE " + (modelsID.length > index && index >= 0 ? modelsID[index] : ""));
        if (savedInstanceState != null) {
            dialog = (ActionDialog) getSupportFragmentManager().findFragmentByTag("dialog_action");
            if (dialog != null)
                dialog.setActionDialogListener(this);
            viewPager.setCurrentItem(savedInstanceState.getInt("position_page"));
            detailedIssue = savedInstanceState.getParcelable("detailedIssue");
        } else {
            viewPager.setCurrentItem(index + 1);
        }
//        }
    }

    @Override
    protected void onUpdateProgress(boolean showProgress) {
        showProgress = false;
        super.onUpdateProgress(showProgress);
    }

    private void onUpdateIssue(long issueID) {
        onLoadDetaileIssue(issueID);
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
    public void onShowActionBar() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onHideActionBar() {
        // TODO Auto-generated method stub

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
    protected void onHandelDBMessage(DBRequest request) {
        super.onHandelDBMessage(request);
        switch (request.getType()) {
            case DETAILS_ISSUE:
                DetailedIssue issue = (DetailedIssue) request.getModel();
                if (issue != null && issueId == issue.getId()) {
                    detailedIssue = issue;
                    updateUI();
                }
                break;
            case INSERT_ISSUE_TRACK:
                InsertTrackResult result = (InsertTrackResult) request.getModel();
                if (result != null && result.result) {
                    Toast.makeText(this, "Action added successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Adding an action failed!", Toast.LENGTH_LONG).show();
                }
                onUpdateIssue(issueId);
                break;
        }
//        if (request == ServiceDataBase.DBRequest.DETAILS_ISSUE) {
//            DetailedIssue issue = (DetailedIssue) request.getModel();
//            if (issue != null && issueId == issue.getId()) {
//                detailedIssue = issue;
//                updateUI();
//            }
//        }
    }

    @Override
    protected void onUserUpdate() {

    }

    @Override
    protected void onHandleServiceMessage(SyncService.SyncTaskState currentTask,
                                          String message) {
        super.onHandleServiceMessage(currentTask, message);
        if (currentTask == SyncService.SyncTaskState.MSG_SYNC_COMPLETED) {
            onUpdateIssue(issueId);
        }
    }

    @Override
    public void onNeedSync() {
        runServiceSyncTask(SyncService.SyncTask.MANUAL_SYNC_TASK);
    }

    @Override
    public void onAddActions(DetailedIssue details, ArrayList<ReturnDateWithActionDialog> data, long idAction, long prevActionID, long nextActionID, boolean keep) {

    }


    public void onAddActions(DetailedIssue details, ArrayList<ReturnDateWithActionDialog> data, long idAction,
                             String actionCode, long prevActionID,
                             long nextActionID, boolean keep) {
        Intent intent = getDB();
        intent.putExtra("user", user);
        intent.putExtra("issue", details);
        intent.putParcelableArrayListExtra("data", data);
        intent.putExtra("idAction", idAction);
        intent.putExtra("ActionCode", actionCode);
        intent.putExtra("nextActionID", nextActionID);
        intent.putExtra("prevActionID", prevActionID);
        intent.putExtra("keep", keep);
        intent.putExtra(ServiceDataBase.KEY_REQUEST, new DBRequest(DBRequest.DBRequestType.INSERT_ISSUE_TRACK));
        sendDBRequest(intent);

//        String message = DBRequest.insertIssueTrack(user, details, data, idAction, prevActionID, nextActionID, keep,
//                                                    DbService.getInstance(this).getIssutraxdb(), this);
//        if (message == null) {
//            Toast.makeText(this, "УРА", 0).show();
//            onUpdateIssue(details.getId());
//        } else {
//            Toast.makeText(this, "NOT УРА =" + message, 0).show();
//        }

    }

    @Override
    public void onLoadDetaileIssue(long issueId) {
        Intent intent = getDB();
        DBRequest request =new DBRequest(DBRequest.DBRequestType.DETAILS_ISSUE);
        request.setId(issueId);
        intent.putExtra(ServiceDataBase.KEY_REQUEST, request);
        intent.putExtra("issueID", issueId);
        intent.putExtra("user", user);
        sendDBRequest(intent);
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


    /**
     * This method will be invoked when the current page is scrolled, either as part
     * of a programmatically initiated smooth scroll or a user initiated touch scroll.
     *
     * @param position             Position index of the first page currently being displayed.
     *                             Page position+1 will be visible if positionOffset is nonzero.
     * @param positionOffset       Value from [0, 1) indicating the offset from the page at position.
     * @param positionOffsetPixels Value in pixels indicating the offset from position.
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * This method will be invoked when a new page becomes selected. Animation is not
     * necessarily complete.
     *
     * @param position Position index of the new selected page.
     */
    @Override
    public void onPageSelected(int position) {
        selectedPage = position;
        int index = position - 1;
        if (position == 0) {
            index = pagerAdapter.getCount() - 1;
        } else if (position == pagerAdapter.getCount() - 1) {
            index = 0;
        }
        getActionBar().setTitle("ISSUE " + (modelsID.length > index && index >= 0 ? modelsID[index] : ""));
        updateMenuRemove();
        updateIssueId(selectedPage);
    }

    private void updateIssueId(int selectedPage) {
        DetailedPage page = pagerAdapter.getRegisteredFragment(selectedPage);
        if (page != null) {
            issueId = page.getIssueId();
            if (page.getDetails() != null) {
                detailedIssue = page.getDetails();
                updateUI();
                updateIssue(detailedIssue.getId(), true, detailedIssue.isFavorite());
            }
        }
    }

    private void updateMenuRemove() {
        if (menu != null) {
            menu.removeGroup(R.id.item_overflow);
            if (menuItemStart != null)
                menuItemStart.setVisible(false);
            if (menuItemFavorite != null)
                menuItemFavorite.setVisible(false);
        }
    }

    private void updateUI() {
        if (menu != null) {
            if (menuItemFavorite != null) {
                if (detailedIssue.isFavorite())
                    menuItemFavorite.setIcon(R.drawable.ic_star_actionbar_pressed);
                else
                    menuItemFavorite.setIcon(R.drawable.ic_star_actionbar);
                menuItemFavorite.setVisible(true);
            }
/*
            updateMenuRemove();
            if (issueId > 0 && detailedIssue != null) {
                boolean hasStart = false;
                if (menuItemNo != null)
                    menuItemNo.setVisible(detailedIssue.getActionIssues().size() == 0);
                for (ActionIssue issue : detailedIssue.getActionIssues()) {
                    if (issue.getCode().equals("StartTask")) {
                        hasStart = true;
                    } else
                        menu.add(R.id.item_overflow, (int) issue.getId(), 0, issue.getName()).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                }
                if (menuItemStart != null)
                    menuItemStart.setVisible(hasStart);


            }
*/
        }
    }

    /**
     * Called when the scroll state changes. Useful for discovering when the user
     * begins dragging, when the pager is automatically settling to the current page,
     * or when it is fully stopped/idle.
     *
     * @param state The new scroll state.
     * @see android.support.v4.view.ViewPager#SCROLL_STATE_IDLE
     * @see android.support.v4.view.ViewPager#SCROLL_STATE_DRAGGING
     * @see android.support.v4.view.ViewPager#SCROLL_STATE_SETTLING
     */
    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            int trueIndex = viewPager.getCurrentItem();
            int curr = viewPager.getCurrentItem();
            int lastReal = viewPager.getAdapter().getCount() - 2;

            Log.v("scroll", "curr: " + curr + "lastReal: " + lastReal + "TRUE INDEX  = " + trueIndex);
            if (curr == 0) {
                trueIndex = lastReal;
            } else if (curr == lastReal + 1) {
                trueIndex = 1;
            }
            viewPager.setCurrentItem(trueIndex, false);
        }
    }

    private int countIndex(int position) {
        int index = position - 1;
        if (position == 0) {
            index = position;
        } else if (position == pagerAdapter.getCount() - 1) {
            index = 0;
        }
        return index;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detailed, menu);
        this.menu = menu;
        menuItemFavorite = menu.findItem(R.id.item_star);
        menuItemFavorite.setVisible(false);
        menuItemStart = menu.findItem(R.id.item_play);
        menuItemNo = menu.findItem(R.id.item_no_action);
        if (pagerAdapter != null)
            updateIssueId(viewPager.getCurrentItem());
        if (detailedIssue != null)
            updateUI();
        return true;
    }

    @Override
    public void onActionDataConfirm(ArrayList<ReturnDateWithActionDialog> data, long idAction, String actionCode, long prevActionID, long nextActionID, boolean keep) {
        if (data == null || data.size() == 0 || detailedIssue == null) {
            Toast.makeText(this, "Something is wrong!", Toast.LENGTH_LONG).show();
            return;
        }
        onAddActions(detailedIssue, data, idAction, actionCode, prevActionID, nextActionID, keep);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            //case R.id.
            case R.id.item_star:
                detailedIssue.setFavorite(!detailedIssue.isFavorite());
                updateIssue(detailedIssue.getId(), true, detailedIssue.isFavorite());
                updateUI();
                return true;
            case R.id.item_no_action:
                Toast.makeText(this, "No Action", Toast.LENGTH_LONG).show();
                return true;
            default: {
                if (detailedIssue != null)
                    for (ActionIssue issue : detailedIssue.getActionIssues()) {
                        if (item.getItemId() == issue.getId() || (issue.getId() == 22
                                && item.getItemId() == R.id.item_play)) {
                            dialog = new ActionDialog();
                            dialog.setActionDialogListener(this);
                            dialog.setIssue(issue);
                            dialog.setUser(user);
                            dialog.setDetails(detailedIssue);
                            dialog.show(getSupportFragmentManager(), "dialog_action");
                            return true;
                        }
                    }
            }
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActionMoreClock(DetailedIssue details) {
        Intent intent = new Intent(this, IssueDetailsSimpleActivity.class);
        intent.setAction("actionsMore");
        intent.putExtra("user", user);
        intent.putExtra("details", details);
        startActivity(intent);
    }

    @Override
    public void onShowDetails(long id) {
        Intent intent = new Intent(this, IssueDetailsSimpleActivity.class);
        intent.setAction("details");
        intent.putExtra("user", user);
        intent.putExtra("issueID", id);
        startActivity(intent);
    }



    @Override
    public void onClose() {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("position_page", viewPager.getCurrentItem());
        outState.putParcelable("detailedIssue", detailedIssue);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onShowHistory(long id) {
        Intent intent = new Intent(this, IssueDetailsSimpleActivity.class);
        intent.setAction("history");
        intent.putExtra("user", user);
        intent.putExtra("issueID", id);
        startActivity(intent);
//        HistoryFragment historyFragment = new HistoryFragment();
//        historyFragment.setIssueId(id);
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, historyFragment).addToBackStack(null).commit();
    }

    @Override
    public void onOpenFile(long id) {
        Intent intent = getDB();
        intent.putExtra(ServiceDataBase.KEY_REQUEST, new DBRequest(DBRequest.DBRequestType.OPEN_FILE));
        intent.putExtra("fileID", id);
        sendDBRequest(intent);
    }

    @Override
    public Intent onGetDB() {
        return getDB();
    }

    @Override
    public void onSendDBRequest(Intent i) {
        sendDBRequest(i);
    }

//    @Override
//    public void onActionDataConfirm(ArrayList<ReturnDateWithActionDialog> data, long idAction, long prevActionID,
//                                    long nextActionID, boolean keep) {
//        if (data == null || data.size() == 0 || detailedIssue == null) {
//            Toast.makeText(this, "Something is wrong!", 1).show();
//            return;
//        }
//        onAddActions(detailedIssue, data, idAction, prevActionID, nextActionID, keep);
//    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}
