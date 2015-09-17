package util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.onbts.ITSMobile.model.UserModel;
import com.onbts.ITSMobile.util.Constants;

/**
 * Created by Rajeesh on 11-08-2015.
 */
public class Util {

    /**
     * Used to change the status of the user login.
     * @param isLoggedIn
     * @param context
     */
    public static synchronized void changeLoginStatus(boolean isLoggedIn, Context context) {
        try {
            SharedPreferences sharedPreferences
                    = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.LOGIN_FLAG, isLoggedIn);
            editor.commit();
        } catch (Exception e) {
            Log.d(Util.class.getName(), "Trouble writing in to shared preference.");
        }
    }

    /**
     * Used to store the logged in user details
     * @param loginData
     * @param context
     */
    public static synchronized void storeLoggedInUserDetails(String loginData, Context context) {
        try {
            SharedPreferences sharedPreferences
                    = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.LOGGED_IN_USERDATA, loginData);
            editor.commit();
        } catch (Exception e) {
            Log.d(Util.class.getName(), "Trouble writing in to shared preference.");
        }
    }

    /**
     * Used to restore the logged in user info in to session
     * @param context
     */
    public static synchronized UserModel getSessionInfo(Context context) {
        String userData = "";
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            userData = sharedPreferences.getString(Constants.LOGGED_IN_USERDATA, "");
        } catch (Exception e) {
            Log.d(Util.class.getName(), "Trouble getting value from shared preference.");
        }
        return new GsonBuilder().create().fromJson(userData, UserModel.class);
    }

    /**
     * Used to get logged in status value from sharedpreference
     * @param context
     * @return true if logged in, else false
     */
    public static synchronized boolean getLoggedInStatus(Context context) {
        boolean isLoggedIn = false;
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            isLoggedIn = sharedPreferences.getBoolean(Constants.LOGIN_FLAG, false);
        } catch (Exception e) {
            Log.d(Util.class.getName(), "Trouble getting value from shared preference.");
        }
        return isLoggedIn;
    }

}

