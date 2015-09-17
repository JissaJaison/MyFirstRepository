package com.onbts.ITSMobile.UI.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.model.base.Model;
import com.onbts.ITSMobile.services.DBRequest;
import com.onbts.ITSMobile.services.ServiceDataBase;
import com.onbts.ITSMobile.services.SyncService.SyncTask;
import com.onbts.ITSMobile.services.SyncService.SyncTaskState;
import com.onbts.ITSMobile.util.Settings;

import java.util.ArrayList;

import util.SPHelper;

public class TestActivity extends
        com.onbts.ITSMobile.UI.activities.base.BaseActivity implements
        OnClickListener {
    private static final String TAG = "TestActivity";
    private TextView titleMessage;
    private EditText txtUrl;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_test);
        findViewById(R.id.button_soft).setOnClickListener(this);
        findViewById(R.id.button_hard).setOnClickListener(this);
        findViewById(R.id.button_save).setOnClickListener(this);
        findViewById(R.id.button_exit).setOnClickListener(this);
        findViewById(R.id.button_start).setOnClickListener(this);
        findViewById(R.id.button_stop).setOnClickListener(this);
        titleMessage = (TextView) findViewById(R.id.title_message);
        txtUrl = (EditText) findViewById(R.id.syncUrl);
        txtUrl.setText(Settings.getInstance(this).getSettingAsString("url"));
    }

    @Override
    protected void onHandelDBMessage(DBRequest request) {

    }

    @Override
    protected void onHandleServiceMessage(SyncTaskState state, String message) {
       Log.d(TAG, "SyncTaskState - " + state + "; message = " + message);
        titleMessage.setText(message != null ? message : "");
    }

    @Override
    protected void onHandleUpdaterTimer(long timeLeft) {
        if (timeLeft>0)
        getActionBar().setTitle("Осталось " + timeLeft/1000 + " cek");
        else getActionBar().setTitle(R.string.app_name);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_hard:
                runServiceSyncTask(SyncTask.HARD_SYNC_TASK);
                break;
            case R.id.button_start:
                startScheduleTask(SPHelper.getRefreshTime(this));
                break;
            case R.id.button_stop:
                stopScheduleTask();
                break;
            case R.id.button_soft:
                runServiceSyncTask(SyncTask.MANUAL_SYNC_TASK);
                break;
            case R.id.button_exit:
                finish();
                break;
            case R.id.button_save:
                Settings.getInstance(this).setSetting("url",
                        txtUrl.getText().toString());
                break;
            default:
                break;
        }

    }

}
