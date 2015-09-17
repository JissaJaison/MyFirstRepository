package com.onbts.ITSMobile.services;

import util.SPHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by tigre on 24.03.14.
 */
public class UpdaterReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null) {

            if (Updater.isEnable(context)) {
                Updater.init(context, Updater.isEnable(context), SPHelper.getRefreshTime(context));
            }
        }
    }
}
