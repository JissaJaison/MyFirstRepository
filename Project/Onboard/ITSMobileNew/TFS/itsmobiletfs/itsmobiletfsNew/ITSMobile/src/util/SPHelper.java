package util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SPHelper {

    private final static String FILTER_PREF = "com.onbts.ITSMobile.filter";

    private final static String PRIORITY_FILTER = "Priority";
    private final static String STATUS_FILTER = "Status";
    private final static String TYPE_FILTER = "Type";
    private final static String SECTION_FILTER = "Section";
    private final static String DECK_FILTER = "Deck";
    private final static String DEPARTMENT_FILTER = "Department";
    private final static String FIRE_ZONE_FILTER = "Firezone";
    private final static String LOCATION_GROUP = "Location_group";
    private final static String ALERT_FILTER = "alert_group";

    private final static String SORT_BY = "SortBy";
    private final static String SORT_LABEL = "SortLabel";
    private final static String FILTER_TEXT_STATE = "filter_text_state";
    private final static String FILTER_TEXT_VALUE = "filter_text_value";
    private final static String USER_ID = "user_id";
    private final static String USER_DEP = "user_dep";
    private final static String URL_SERVER = "urlServer";
    private final static String DEFAULT_URL_SERVER = "http://192.168.125.181/ITSMobileSync/ITSMobileSyncService.svc";
    public final static String UPDATE_TIME = "period";
    public final static String LAST_UPDATE_TIME = "last_time_update";
    public final static String LAST_UPDATE_ERROR = "last_error_update";
    public final static String LAST_UPDATE_TIME_FOR_NEW = "last_time_update_for_new";
    private final static String AUTO_SYNC = "autoSynchronization";

    public static int getFilterTextState(Context context) {
        if (context == null) return 0;
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(context.getPackageName(), Context.MODE_MULTI_PROCESS);
        int state = 0;
        try {
            state = sharedPrefs.getInt(FILTER_TEXT_STATE, 0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("shared", "SP: state: " + state);

        return state;
    }

    public static boolean setFilterTextState(Context context, int state) {
        if (context == null) return false;
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(context.getPackageName(), Context.MODE_MULTI_PROCESS);

        Editor editor = sharedPrefs.edit();
        try {
            editor.putInt(FILTER_TEXT_STATE, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return editor.commit();
    }

    public static String getFilterValue(Context context) {
        if (context == null) return "";
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(context.getPackageName(), Context.MODE_MULTI_PROCESS);
        //-1 - Not show, 0 - null result when filtred
        String value = "";
        try {
            value = sharedPrefs.getString(FILTER_TEXT_VALUE, "");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("shared", "SP: value: " + value);

        return value;
    }

    public static boolean setFilterValue(Context context, String value) {
        if (context == null) return false;
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(context.getPackageName(), Context.MODE_MULTI_PROCESS);
        Editor editor = sharedPrefs.edit();
        try {
            editor.putString(FILTER_TEXT_VALUE, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return editor.commit();
    }

    public static String getPriority(Context context) {
        if (context == null) return "";
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(FILTER_PREF, Context.MODE_MULTI_PROCESS);
        String priority = "";
        try {
            priority = sharedPrefs.getString(PRIORITY_FILTER, "OFF");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("shared", "SP: priority: " + priority);

        return priority;
    }

    public static boolean setPriority(Context context, String priority) {
        if (context == null) return false;
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(FILTER_PREF, Context.MODE_MULTI_PROCESS);
        Editor editor = sharedPrefs.edit();
        try {
            editor.putString(PRIORITY_FILTER, priority);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return editor.commit();
    }

    public static String getType(Context context) {
        if (context == null) return "";
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(FILTER_PREF, Context.MODE_MULTI_PROCESS);
        String type = "";
        try {
            type = sharedPrefs.getString(TYPE_FILTER, "OFF");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("shared", "SP: priority: " + type);

        return type;
    }

    public static boolean setType(Context context, String priority) {
        if (context == null) return false;
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(FILTER_PREF, Context.MODE_MULTI_PROCESS);
        Editor editor = sharedPrefs.edit();
        try {
            editor.putString(TYPE_FILTER, priority);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return editor.commit();
    }

    public static String getStatus(Context context) {
        if (context == null) return "";
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(FILTER_PREF, Context.MODE_MULTI_PROCESS);
        String status = "";
        try {
            status = sharedPrefs.getString(STATUS_FILTER, "OFF");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("shared", "SP: status: " + status);

        return status;
    }

    public static boolean setStatus(Context context, String status) {
        if (context == null) return false;
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(FILTER_PREF, Context.MODE_MULTI_PROCESS);
        Editor editor = sharedPrefs.edit();
        try {
            editor.putString(STATUS_FILTER, status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return editor.commit();
    }

    public static String getSection(Context context) {
        if (context == null) return "";
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(FILTER_PREF, Context.MODE_MULTI_PROCESS);
        String section = "";
        try {
            section = sharedPrefs.getString(SECTION_FILTER, "OFF");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("shared", "SP: section: " + section);

        return section;
    }

    public static boolean setSection(Context context, String section) {
        if (context == null) return false;
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(FILTER_PREF, Context.MODE_MULTI_PROCESS);
        Editor editor = sharedPrefs.edit();
        try {
            editor.putString(SECTION_FILTER, section);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return editor.commit();
    }

    public static String getDeck(Context context) {
        if (context == null) return "";
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(FILTER_PREF, Context.MODE_MULTI_PROCESS);
        String deck = "";
        try {
            deck = sharedPrefs.getString(DECK_FILTER, "OFF");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("shared", "SP: section: " + deck);

        return deck;
    }

    public static boolean setDeck(Context context, String deck) {
        if (context == null) return false;
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(FILTER_PREF, Context.MODE_MULTI_PROCESS);
        Editor editor = sharedPrefs.edit();
        try {
            editor.putString(DECK_FILTER, deck);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return editor.commit();
    }

    public static String getDepartment(Context context) {
        if (context == null) return "";
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(FILTER_PREF, Context.MODE_MULTI_PROCESS);
        String department = "";
        try {
            department = sharedPrefs.getString(DEPARTMENT_FILTER, "OFF");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("shared", "SP: section: " + department);

        return department;
    }

    public static boolean setDepartment(Context context, String department) {
        if (context == null) return false;
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(FILTER_PREF, Context.MODE_MULTI_PROCESS);
        Editor editor = sharedPrefs.edit();
        try {
            editor.putString(DEPARTMENT_FILTER, department);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return editor.commit();
    }

    public static String getLocationGroup(Context context) {
        if (context == null) return "";
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(FILTER_PREF, Context.MODE_MULTI_PROCESS);
        String locationGroup = "";
        try {
            locationGroup = sharedPrefs.getString(LOCATION_GROUP, "OFF");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("shared", "SP: section: " + locationGroup);

        return locationGroup;
    }

    public static boolean setLocationGroup(Context context, String locationGroup) {
        if (context == null) return false;
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(FILTER_PREF, Context.MODE_MULTI_PROCESS);
        Editor editor = sharedPrefs.edit();
        try {
            editor.putString(LOCATION_GROUP, locationGroup);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return editor.commit();
    }

    public static String getAlert(Context context) {
        if (context == null) return "";
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(FILTER_PREF, Context.MODE_MULTI_PROCESS);
        String locationGroup = "";
        try {
            locationGroup = sharedPrefs.getString(ALERT_FILTER, "OFF");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("shared", "SP: section: " + locationGroup);

        return locationGroup;
    }

    public static boolean setAlert(Context context, String alert) {
        if (context == null) return false;
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(FILTER_PREF, Context.MODE_MULTI_PROCESS);
        Editor editor = sharedPrefs.edit();
        try {
            editor.putString(ALERT_FILTER, alert);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return editor.commit();
    }

    public static String getFirezone(Context context) {
        if (context == null) return "";
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(FILTER_PREF, Context.MODE_MULTI_PROCESS);
        String firezone = "";
        try {
            firezone = sharedPrefs.getString(FIRE_ZONE_FILTER, "OFF");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("shared", "SP: section: " + firezone);

        return firezone;
    }

    public static boolean setFirezone(Context context, String firezone) {
        if (context == null) return false;
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(FILTER_PREF, Context.MODE_MULTI_PROCESS);
        Editor editor = sharedPrefs.edit();
        try {
            editor.putString(FIRE_ZONE_FILTER, firezone);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return editor.commit();
    }

    public static String getSortBy(Context context) {
        if (context == null) return "";
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(FILTER_PREF, Context.MODE_MULTI_PROCESS);
        String sortby = "";
        try {
            sortby = sharedPrefs.getString(SORT_BY, "");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("shared", "SP: sortby: " + sortby);

        return sortby;
    }

    public static boolean setSortBy(Context context, String sortby) {
        if (context == null) return false;
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(FILTER_PREF, Context.MODE_MULTI_PROCESS);
        Editor editor = sharedPrefs.edit();
        try {
            editor.putString(SORT_BY, sortby);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return editor.commit();
    }

    public static String getSortLabel(Context context) {
        if (context == null) return "";
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(FILTER_PREF, Context.MODE_MULTI_PROCESS);
        String sortLabel = "";
        try {
            sortLabel = sharedPrefs.getString(SORT_LABEL, "Issue ID+1");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("shared", "SP: sortLabel: " + sortLabel);
        return sortLabel;
    }

    public static boolean setSortLabel(Context context, String sortLabel) {
        if (context == null) return false;
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(FILTER_PREF, Context.MODE_MULTI_PROCESS);
        Editor editor = sharedPrefs.edit();
        try {
            editor.putString(SORT_LABEL, sortLabel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return editor.commit();
    }

    public static void clearSP(Context context) {
        if (context == null) return;
        Log.d("SP", "shared preferences deleted");
        context.getSharedPreferences(context.getPackageName(), Context.MODE_MULTI_PROCESS).edit().clear().commit();
        context.getSharedPreferences(FILTER_PREF, Context.MODE_MULTI_PROCESS).edit().clear().commit();
    }

    public static void clearSPfilter(Context context) {
        if (context == null) return;
        Log.d("SP", "shared preferences deleted");
        context.getSharedPreferences(FILTER_PREF, Context.MODE_MULTI_PROCESS).edit().clear().commit();
    }

    public static String getUrlServer(Context context) {
        if (context == null) return "";
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(context.getPackageName() + "_preferences",
                        Context.MODE_MULTI_PROCESS);
        String urlServer = "";
        urlServer = sharedPrefs.getString(URL_SERVER, DEFAULT_URL_SERVER);
        Log.i("shared", "urlServer: " + urlServer);
        return urlServer;
    }

    public static long getRefreshTime(Context context) {
        if (context == null) return 0;
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(context.getPackageName() + "_preferences",
                        Context.MODE_MULTI_PROCESS);
        String timeUpdate = "";
        timeUpdate = sharedPrefs.getString(UPDATE_TIME, "1800000");
        long delta = 0;
        try {
            delta = Long.valueOf(timeUpdate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("WorkTimer", "delta = " + delta);
        return delta;
    }

    public static boolean setLastUpdateTime(Context context, long lastUpdateTime) {
        if (context == null) return false;
        setLastUpdateTimeForNew(context);
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(context.getPackageName() + "_preferences",
                        Context.MODE_MULTI_PROCESS);
        Log.i("WorkTimer", "setLastUpdateTime = " + lastUpdateTime);
        return sharedPrefs.edit().putLong(LAST_UPDATE_TIME, lastUpdateTime).commit();
    }

    public static boolean setLastUpdateError(Context context, String lastError) {
        if (context == null) return false;
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(context.getPackageName() + "_preferences",
                        Context.MODE_MULTI_PROCESS);
        return sharedPrefs.edit().putString(LAST_UPDATE_ERROR, lastError).commit();
    }

    public static boolean clearLastUpdateError(Context context) {
        if (context == null) return false;
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(context.getPackageName() + "_preferences",
                        Context.MODE_MULTI_PROCESS);
        return sharedPrefs.edit().remove(LAST_UPDATE_ERROR).commit();
    }

    public static String getLastUpdateError(Context context) {
        if (context == null) return "";
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(context.getPackageName() + "_preferences",
                        Context.MODE_MULTI_PROCESS);
        return sharedPrefs.getString(LAST_UPDATE_ERROR, "");
    }

    public static boolean setLastUpdateTimeForNew(Context context) {
        if (context == null) return false;
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(context.getPackageName() + "_preferences",
                        Context.MODE_MULTI_PROCESS);
        long lastUpdateTime = getLastUpdateTime(context);
        return sharedPrefs.edit().putLong(LAST_UPDATE_TIME_FOR_NEW, lastUpdateTime).commit();
    }

    public static long getLastUpdateTime(Context context) {
        if (context == null) return 0;
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(context.getPackageName() + "_preferences",
                        Context.MODE_MULTI_PROCESS);
        return sharedPrefs.getLong(LAST_UPDATE_TIME, 0);
    }

    public static long getLastUpdateTimeForNew(Context context) {
        if (context == null) return 0;
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(context.getPackageName() + "_preferences",
                        Context.MODE_MULTI_PROCESS);
        return sharedPrefs.getLong(LAST_UPDATE_TIME_FOR_NEW, 0);
    }

    public static boolean isUpdaterEnable(Context context) {
        return context.getSharedPreferences(context.getPackageName() + "_preferences",
                Context.MODE_MULTI_PROCESS).getBoolean(AUTO_SYNC, false);
    }

    public static void clearLastUpdateTime(Context context) {
        SharedPreferences sharedPrefs = context
                .getSharedPreferences(context.getPackageName() + "_preferences",
                        Context.MODE_MULTI_PROCESS);
        sharedPrefs.edit().remove(LAST_UPDATE_TIME_FOR_NEW).remove(LAST_UPDATE_TIME).commit();
    }
}
