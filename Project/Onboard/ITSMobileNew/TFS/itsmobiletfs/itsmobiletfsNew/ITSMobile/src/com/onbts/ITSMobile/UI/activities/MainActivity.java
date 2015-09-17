package com.onbts.ITSMobile.UI.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.UI.activities.base.BaseActivity;
import com.onbts.ITSMobile.UI.fragments.IssuesFragment;
import com.onbts.ITSMobile.interfaces.OnNavigationChange;
import com.onbts.ITSMobile.model.DetailedIssue;
import com.onbts.ITSMobile.model.ReturnDateWithActionDialog;
import com.onbts.ITSMobile.model.UserModel;
import com.onbts.ITSMobile.services.DBRequest;
import com.onbts.ITSMobile.services.ServiceDataBase;
import com.onbts.ITSMobile.services.SyncService;
import com.onbts.ITSMobile.services.SyncService.SyncTask;
import com.onbts.ITSMobile.services.SyncService.SyncTaskState;

import java.util.ArrayList;

import util.SPHelper;

@Deprecated
public class MainActivity extends BaseActivity implements OnNavigationChange {
    private ProgressDialog mDialog;
    //    private Fragment currentFragment;
    private UserModel user;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d("life", "MA onPostResume" + this);
    }

    /**
     * Save all appropriate fragment state.
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("user", user);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDialog = new ProgressDialog(this);
        // this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (savedInstanceState != null) {
            user = savedInstanceState.getParcelable("user");
            setContentView(R.layout.ac_main);
            return;
        }
        user = getIntent().getParcelableExtra("user");
        if (user != null) {
//            DbService.getInstance(getApplicationContext()).init();
            setContentView(R.layout.ac_main);

            IssuesFragment issueList = new IssuesFragment();
            changeFragment(issueList, false);
        }
    }

    public void onClickBtn(View v) {
        Toast.makeText(this, "Clicked on Button", Toast.LENGTH_LONG).show();
        SyncService.getInstance().StartHardSync();

    }

    @Override
    protected void onResume() {
        super.onResume();
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
        IssuesFragment issueList = new IssuesFragment();
        changeFragment(issueList, true);
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

    }

    @Override
    protected void onHandleServiceMessage(SyncTaskState currentTask,
                                          String message) {
    }

    @Override
    protected void onHandleUpdaterTimer(long timeLeft) {

    }

    @Override
    public void onNeedSync() {
        runServiceSyncTask(SyncTask.MANUAL_SYNC_TASK);
    }

    @Override
    public void onAddActions(DetailedIssue details, ArrayList<ReturnDateWithActionDialog> data, long idAction, long prevActionID, long nextActionID, boolean keep) {

    }

    @Override
    public void onLoadDetaileIssue(long issueId) {

    }

    @Override
    public void updateIssue(long issueId, boolean open, boolean favorite) {

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
