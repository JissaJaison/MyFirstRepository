package com.onbts.ITSMobile.UI.activities.base;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

//import com.crashlytics.android.Crashlytics;
import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.UI.broadcastReceivers.ProgressLoadBroadcast;
import com.onbts.ITSMobile.model.base.Model;
import com.onbts.ITSMobile.services.DBRequest;
import com.onbts.ITSMobile.services.DbService;
import com.onbts.ITSMobile.services.ServiceDataBase;
import com.onbts.ITSMobile.services.ServiceSync;
import com.onbts.ITSMobile.services.SyncService.SyncTask;
import com.onbts.ITSMobile.services.SyncService.SyncTaskState;
import com.onbts.ITSMobile.services.Updater;

import util.SPHelper;

public abstract class BaseActivity extends FragmentActivity implements ProgressLoadBroadcast.ProgressLoadBroadcastListener {

    private ProgressLoadBroadcast ServiceListener = new ProgressLoadBroadcast();
    private ProgressDialog dialog, dialogUpdate;
    private BroadcastReceiver dbListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                DBRequest request = intent
                        .getParcelableExtra(ServiceDataBase.KEY_REQUEST);

                if (request != null) {
                    request.setModels(intent.<Model>getParcelableArrayListExtra(ServiceDataBase.KEY_REQUEST_MODELS));
                    onHandelDBMessage(request);
//                    Toast.makeText(BaseActivity.this, "finish " + (request.getModels() != null ? request.getModels().size() : " null "), 0).show();
                }
                if (intent.hasExtra("result")) {
                    if (intent.getBooleanExtra("result", false)) {

                    } else {
                        Toast.makeText(BaseActivity.this, "App Not Found for this file", Toast.LENGTH_LONG).show();
                    }
                }
                if (intent.hasExtra(ServiceDataBase.EXTRA_KEY_UPDATE)) {
                    onUpdateProgress(intent.getBooleanExtra(ServiceDataBase.EXTRA_KEY_UPDATE, false));
                }
            } else {
//                Toast.makeText(BaseActivity.this, "finish null", 0).show();
            }


        }
    };
    private BroadcastReceiver timerListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (Updater.isEnable(BaseActivity.this)) {
                    startTimer(Updater.getTimeLeft(BaseActivity.this));
                }
            }

        }
    };

    private CountDownTimer timer = null;

    protected abstract void onHandelDBMessage(DBRequest request);

    protected abstract void onHandleServiceMessage(SyncTaskState currentTask,
                                                   String message);

    protected abstract void onHandleUpdaterTimer(long timeLeft);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Crashlytics.start(this);
        if (savedInstanceState != null)
            Log.i("BaseActivity", "onCreate " + BaseActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent updateIntent = new Intent(BaseActivity.this, ServiceDataBase.class);
        updateIntent.putExtra(ServiceDataBase.KEY_REQUEST, new DBRequest(DBRequest.DBRequestType.STATUS_SERVICE));
        startService(updateIntent);

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
    protected void onPause() {
        super.onPause();
        onUpdateProgress(false);
    }

    protected void onUpdateProgress(boolean showProgress) {

        if (showProgress) {
            if (dialogUpdate == null) {
                dialogUpdate = new ProgressDialog(BaseActivity.this);
            }
            dialogUpdate.setCancelable(false);
            dialogUpdate.setMessage("Loading data...");
            if (!dialogUpdate.isShowing())
                try {
                    dialogUpdate.show();
                } catch (Exception ignore) {
                }
        } else {
            if (dialogUpdate != null)
                dialogUpdate.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        runServiceSyncTask(SyncTask.STATE_SYNC_TASK);
        DbService.getInstance(getApplicationContext()).init();
        IntentFilter filter = new IntentFilter(ServiceSync.ACTION_SYNC_SERVICE);
        registerReceiver(ServiceListener, filter);
        ServiceListener.setProgressLoadBroadcastListener(this);
        IntentFilter filterDB = new IntentFilter(ServiceDataBase.BROADCAST_ACTION);
        registerReceiver(dbListener, filterDB);
        IntentFilter filterTimer = new IntentFilter(ServiceSync.ACTION_SYNC_SERVICE_TASK);
        registerReceiver(timerListener, filterTimer);
        if (Updater.isEnable(this)) {
            startTimer(Updater.getTimeLeft(this));
        }
    }

    @Override
    public void showProgress(boolean show) {
        if (show) {
            if (dialog == null)
                dialog = new ProgressDialog(BaseActivity.this);
            dialog.setCancelable(false);
            if (!dialog.isShowing())
                try {
                    dialog.show();
                } catch (Exception ignore) {
                }

        } else {
            if (dialog != null)
                dialog.dismiss();
        }
    }

    @Override
    public void dialogSetMessage(String message) {
        if (dialog != null)
            dialog.setMessage(message);
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

    @Override
    public void currentState(SyncTaskState currentTask, String message) {
        onHandleServiceMessage(currentTask, message);
    }

    private void startTimer(long time) {
        Log.i("WorkTimer", "startTimer - " + time);
        if (timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {
                BaseActivity.this.onHandleUpdaterTimer(millisUntilFinished);
            }

            public void onFinish() {
                stopTimer();
            }
        }.start();

    }

    private void stopTimer() {
        BaseActivity.this.onHandleUpdaterTimer(-1);
        if (timer != null)
            timer.cancel();
        timer = null;
    }


    @Override
    protected void onStop() {
        unregisterReceiver(ServiceListener);
        unregisterReceiver(dbListener);
        unregisterReceiver(timerListener);
        stopTimer();
        super.onStop();
    }

    protected void runServiceSyncTask(SyncTask task) {
        Intent intent = new Intent(this, ServiceSync.class);
        intent.putExtra(ServiceSync.KEY_KEY_TASK, task);
        startService(intent);
    }

    protected void startScheduleTask(long delta) {
        startTimer(Updater.init(this, true, delta));

    }

    protected void stopScheduleTask() {
        Updater.init(this, false, -1);
        stopTimer();
    }

    protected void changeFragment(Fragment frag, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.replace(R.id.fragment_container, frag);
        ft.commitAllowingStateLoss();
    }

    /**
     * Возвращает Intent для отправки сообщения сервису, который работает с базой данных.
     *
     * @return
     */
    protected Intent getDB() {
        return new Intent(this, ServiceDataBase.class);
    }

    /**
     * Отправка Intent сервиса.
     *
     * @param intent
     */
    protected void sendDBRequest(Intent intent) {
        startService(intent);
    }

    ;
}
