package com.onbts.ITSMobile.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

//import com.crashlytics.android.Crashlytics;
import com.onbts.ITSMobile.services.SyncService.SyncTask;
import com.onbts.ITSMobile.services.SyncService.SyncTaskState;

import util.SPHelper;

public class ServiceSync extends Service {

    public static final String ACTION_SYNC_SERVICE = "com.onbts.ITSMobile.services.action.ServiceSync";
    public static final String ACTION_SYNC_SERVICE_TASK = "com.onbts.ITSMobile.services.action.ServiceSync.task";
    public static final String KEY_KEY_TASK = "key_task";
    public static final String KEY_KEY_MESSAGE = "key_message";
    public static final String KEY_KEY_CURRENT_TASK_STATE = "key_current_task_state";
    private static final String TAG = "ServiceSync";

    @Override
    public void onCreate() {
        super.onCreate();
//        Crashlytics.start(this);
        SyncService.getInstance().init(this, syncServiceHandler, SPHelper.getUrlServer(this),
                DbService.getDbFilePath(this));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.hasExtra(KEY_KEY_TASK))
                taskSwitch((SyncTask) intent.getSerializableExtra(KEY_KEY_TASK));
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void taskSwitch(SyncTask id) {
        Log.i(TAG, "SyncTask - " + id);
        if (checkState()) {
            return;
        }
        SyncService.getInstance().setSyncServiceUrl(SPHelper.getUrlServer(getApplication()));
        switch (id) {
            case HARD_SYNC_TASK:
                SyncService.getInstance().StartHardSync();
//                getSharedPreferences("server", Context.MODE_MULTI_PROCESS)
//                        .edit().putLong("last_time_update", System.currentTimeMillis()).commit();
                SPHelper.clearLastUpdateTime(this);
//                SPHelper.setLastUpdateTime(this, System.currentTimeMillis());
                sendBroadcast(new Intent(ACTION_SYNC_SERVICE_TASK));
                break;
            case MANUAL_SYNC_TASK:
                SyncService.getInstance().StartManualSync();
//                getSharedPreferences("server", Context.MODE_MULTI_PROCESS)
//                        .edit().putLong("last_time_update", System.currentTimeMillis()).commit();
//                SPHelper.setLastUpdateTime(this, System.currentTimeMillis());
                sendBroadcast(new Intent(ACTION_SYNC_SERVICE_TASK));
                break;
            case STATE_SYNC_TASK:
                checkState();
                break;
            default:
                break;
        }
    }

    private boolean checkState() {
        boolean sync = SyncService.getInstance()._isSyncing.get();
        Log.i(TAG, "checkState - " + sync);
        if (sync)
            sendCallBackMessage(SyncTaskState.MSG_SYNC_IN_PROGRESS,
                    "Sync in progress...");
        else
            sendCallBackMessage(SyncTaskState.MSG_NO, "idle");
        return sync;
    }

    private void sendCallBackMessage(SyncTaskState state, String message) {
        if (state == SyncTaskState.MSG_HARD_SYNC_COMPLETED) {
            DbService.getInstance(getApplicationContext()).init();
        }
        Log.d(TAG, "SyncTaskState - " + state + "; message = " + message);
        Intent intent = new Intent(ACTION_SYNC_SERVICE);
        intent.putExtra(KEY_KEY_CURRENT_TASK_STATE, state);
        intent.putExtra(KEY_KEY_MESSAGE, message);
        sendBroadcast(intent);
//		if (!SyncService.getInstance()._isSyncing.get())
//			stopSelf();
    }

    private Handler syncServiceHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case SyncService.MSG_BACKGROUND_SYNC_WAITING:
                    break;
                case SyncService.MSG_SYNC_IN_PROGRESS:
                case SyncService.MSG_HARD_SYNC_IN_PROGRESS:
                    break;
                case SyncService.MSG_SYNC_COMPLETED:
                case SyncService.MSG_HARD_SYNC_COMPLETED:
                    SPHelper.setLastUpdateTime(ServiceSync.this, System.currentTimeMillis());
                    SPHelper.clearLastUpdateError(ServiceSync.this);
                    //Changes made by Jissa on Issue 10 of mobile changes. need to import update_server.xml
                    //toggle_button1.setEnabled(true);
                    //toggle_button1.setSelected(true);
                    break;
                case SyncService.MSG_SYNC_CANCELED:
                    break;
                case SyncService.MSG_SYNC_FAILED:
                    SPHelper.setLastUpdateError(ServiceSync.this, msg.obj != null ? msg.obj.toString() : "error");
                    break;
                default:
                    break;

            }

            if (msg.obj != null) {
                sendCallBackMessage(SyncTaskState.getValue(msg.what),
                        msg.obj.toString());
            } else {
                sendCallBackMessage(SyncTaskState.getValue(msg.what), null);
            }
        }
    };
}
