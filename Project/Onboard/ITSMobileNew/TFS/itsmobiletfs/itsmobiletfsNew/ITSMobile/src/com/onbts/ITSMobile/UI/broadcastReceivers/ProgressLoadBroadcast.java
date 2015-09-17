package com.onbts.ITSMobile.UI.broadcastReceivers;

import android.R.bool;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.onbts.ITSMobile.services.ServiceSync;
import com.onbts.ITSMobile.services.SyncService.SyncTaskState;

public class ProgressLoadBroadcast extends BroadcastReceiver {

    private ProgressLoadBroadcastListener progressLoadBroadcastListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            SyncTaskState currentState = (SyncTaskState) intent
                    .getSerializableExtra(ServiceSync.KEY_KEY_CURRENT_TASK_STATE);
            String message = intent.getStringExtra(ServiceSync.KEY_KEY_MESSAGE);

            if (currentState != null)
                switch (currentState) {
                    case MSG_BACKGROUND_SYNC_WAITING:
                    case MSG_HARD_SYNC_IN_PROGRESS:
                    case MSG_SYNC_IN_PROGRESS:
                        progressLoadBroadcastListener.showProgress(true);
                        if (message != null)
                            progressLoadBroadcastListener.dialogSetMessage(message);

                        break;
                    case MSG_NO:
                    case MSG_HARD_SYNC_COMPLETED:
                    case MSG_SYNC_COMPLETED:
                        progressLoadBroadcastListener.showProgress(false);
                        break;
                    case MSG_SYNC_FAILED:
                    case MSG_SYNC_CANCELED:
                    case MSG_SYNC_REJECTED:
                        progressLoadBroadcastListener.showProgress(false);
                        if (message != null)
                            progressLoadBroadcastListener.dialogError(true, message);
                        break;
                }
            progressLoadBroadcastListener.currentState(currentState, message);
        }
    }

    public void setProgressLoadBroadcastListener(
            ProgressLoadBroadcastListener progressLoadBroadcastListener2) {
        this.progressLoadBroadcastListener = progressLoadBroadcastListener2;
    }

    public interface ProgressLoadBroadcastListener {
        void showProgress(boolean show);

        void dialogSetMessage(String message);

        void dialogError(boolean show, String message);
                
        void currentState(SyncTaskState currentTask, String message);
    }
}