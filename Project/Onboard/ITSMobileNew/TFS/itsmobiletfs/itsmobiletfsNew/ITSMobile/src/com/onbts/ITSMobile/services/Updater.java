package com.onbts.ITSMobile.services;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.onbts.ITSMobile.services.SyncService.SyncTask;

import util.SPHelper;

public class Updater {

    public static long init(Context context, boolean run, long delta) {
        run = run && delta > 1000;
        Intent intent = new Intent(context, ServiceSync.class);
        intent.putExtra(ServiceSync.KEY_KEY_TASK, SyncTask.MANUAL_SYNC_TASK);
        intent.putExtra(SPHelper.UPDATE_TIME, delta);
        return init(context, run, intent, delta);
    }

    private static long init(Context context, boolean run, Intent intent, long delta) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent sender = PendingIntent.getService(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(sender);
        if (!run) {
            return 0;
        } else {
            long lastTime = SPHelper.getLastUpdateTime(context);
            long dt = lastTime + delta;
            if (dt < 0)
                dt = 0;
            am.setRepeating(AlarmManager.RTC_WAKEUP, dt, delta, sender);
            Log.i("WorkTimer", "init dt -" + dt);
            return dt - System.currentTimeMillis();
        }
    }

    public static boolean isEnable(Context context) {
        return SPHelper.isUpdaterEnable(context);
    }

    public static long getTimeLeft(Context context) {
        long lastTime = SPHelper.getLastUpdateTime(context);
        long delta = SPHelper.getRefreshTime(context);
        long dt = lastTime + delta;
        Log.i("WorkTimer", "left dt =" + dt);
        dt -= System.currentTimeMillis();
        if (dt < 0)
            dt = 0;
        Log.i("WorkTimer", "left dt =" + dt);
        return dt;

    }
}