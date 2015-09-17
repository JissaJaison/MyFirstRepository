package com.onbts.ITSMobile.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by tigre on 21.04.14.
 */
public class UID {
    public synchronized static String id(Context context) {
        String androidId = Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (androidId == null)
            try {
                androidId = UUID.nameUUIDFromBytes(androidId.getBytes("UTF-8")).toString();
            } catch (UnsupportedEncodingException e) {
                Log.e("error", "Cant get UUID of device");
                e.printStackTrace();
            }
        return androidId;
    }

    public synchronized static String getVersion(Context context) throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        return pInfo.versionName;
    }
}
