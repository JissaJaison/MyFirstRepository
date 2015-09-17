package com.onbts.ITSMobile.UI.activities.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.interfaces.OnNavigationChange;
import com.onbts.ITSMobile.model.UserModel;
import com.onbts.ITSMobile.services.DBRequest;
import com.onbts.ITSMobile.services.DbService;
import com.onbts.ITSMobile.services.ServiceDataBase;
import com.onbts.ITSMobile.services.SyncService;
import com.onbts.ITSMobile.services.SyncService.SyncTask;

/**
 * Created by tigre on 16.04.14.
 */
public abstract class BaseIssueActivity extends BaseActivity implements OnNavigationChange {
    protected ProgressDialog mDialog;
    protected UserModel user;
    private TextView timeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DbService.getInstance(getApplicationContext()).init();
        mDialog = new ProgressDialog(this);
        if (savedInstanceState != null) {
            user = savedInstanceState.getParcelable("user");
            return;
        }
        user = getIntent().getParcelableExtra("user");
        if (user == null)
            finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.base_issue, menu);
        MenuItem menuItem = menu.findItem(R.id.item_refresh);
        timeRefresh = (TextView) menuItem.getActionView().findViewById(R.id.time_refresh);
        timeRefresh.setTextSize(10);
        menuItem.getActionView().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                runServiceSyncTask(SyncTask.MANUAL_SYNC_TASK);
            }
        });
        return true;
    }

    @Override
    protected void onHandleUpdaterTimer(long timeLeft) {

        String time = null;
        long timeLong = 0;
        if (timeLeft > 3600000) {
            timeLong = timeLeft / 3600000;
            time = String.format("%dh", timeLong);
        } else if (timeLeft > 60000) {
            timeLong = timeLeft / 60000;
            time = String.format("%dm", timeLong);
        } else if (timeLeft >= 0) {
            time = "<1m";
        } else
            time = "";

        if (timeRefresh != null)
            timeRefresh.setText(time);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("user", user);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onHandelDBMessage(DBRequest request) {
        if (request!=null){
           if ( request.getType() == DBRequest.DBRequestType.UPDATE_USER){
               if (request.getModel()!=null && request.getModel() instanceof  UserModel){
                   user = (UserModel) request.getModel();
                   if (user.getId() <=0){
                       Toast.makeText(this, "User is not valid", Toast.LENGTH_LONG).show();
                       finish();
                   }else{
                       onUserUpdate();
                   }
               }else{
                   Toast.makeText(this, "User is not valid", Toast.LENGTH_LONG).show();
                   finish();
               }
           }
        }
    }

    protected abstract void onUserUpdate();


    @Override
    protected void onHandleServiceMessage(SyncService.SyncTaskState currentTask, String message) {
        if (currentTask == SyncService.SyncTaskState.MSG_SYNC_COMPLETED) {
            updateUser();
        }
    }

    protected void updateUser(){
        Intent intent = getDB();
        intent.putExtra(ServiceDataBase.KEY_REQUEST, new DBRequest(DBRequest.DBRequestType.UPDATE_USER));
        intent.putExtra("userID", user.getId());
        sendDBRequest(intent);
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
}
