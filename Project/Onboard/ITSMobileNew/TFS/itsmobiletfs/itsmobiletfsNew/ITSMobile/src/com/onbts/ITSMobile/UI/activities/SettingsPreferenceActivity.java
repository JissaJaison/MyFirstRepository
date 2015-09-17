package com.onbts.ITSMobile.UI.activities;

import java.util.List;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.UI.broadcastReceivers.ProgressLoadBroadcast;
import com.onbts.ITSMobile.UI.fragments.preference.ConfigurationServerFragment;
import com.onbts.ITSMobile.UI.fragments.preference.UpdateServerFragment;
import com.onbts.ITSMobile.services.ServiceSync;
import com.onbts.ITSMobile.services.SyncService.SyncTaskState;

import util.SPHelper;

public class SettingsPreferenceActivity extends PreferenceActivity implements ProgressLoadBroadcast.ProgressLoadBroadcastListener {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected boolean isValidFragment(String fragmentName) {
        if (ConfigurationServerFragment.class.getName().equals(fragmentName)) {
            return true;
        }
        if (UpdateServerFragment.class.getName().equals(fragmentName)) {
            return true;
        }
        if (super.isValidFragment(fragmentName)) {
            return true;
        }
        return false;
    }

    private ProgressDialog dialog;
    private ProgressLoadBroadcast ServiceListener = new ProgressLoadBroadcast();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ServiceListener.setProgressLoadBroadcastListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ServiceSync.ACTION_SYNC_SERVICE);
        registerReceiver(ServiceListener, filter);
        String error = SPHelper.getLastUpdateError(this);
        if (error!=null && error.length()>0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Error Sync");
            builder.setMessage(error);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();
            SPHelper.clearLastUpdateError(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(ServiceListener);
    }

    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.header_settings_login, target);
    }

    @Override
    public void showProgress(boolean show) {
        if (show) {
            if (dialog == null)
                dialog = new ProgressDialog(this);
            dialog.setCancelable(false);
            if (!dialog.isShowing())
                try {
                    dialog.show();
                } catch (Exception ignore) {
                }

        } else {
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }

    @Override
    public void dialogSetMessage(String message) {
        if (dialog != null)
            dialog.setMessage(message);
    }

    @Override
    public void currentState(SyncTaskState currentTask, String message) {
    }

    @Override
    public void dialogError(boolean show, String message) {
        Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.warning);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.dialog_btn_ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
        SPHelper.clearLastUpdateError(this);
    }
}