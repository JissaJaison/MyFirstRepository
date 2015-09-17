package util;

import android.content.Context;

import com.onbts.ITSMobile.R;

import java.util.HashMap;
import java.util.Map;

public class GetColorByPriority {
    // maps need because displayed info not does not match info in DB(ID != _id,
    // Low != 1 - Low)
    private static final Map<String, String> sortMap;

    static {
        sortMap = new HashMap<String, String>();
        sortMap.put("Date", "iss.CreateDate");
        sortMap.put("Location", "loc.LocationDesc");
        sortMap.put("Issue ID", "_id");

    }

    private static final Map<String, String> priorityMap;

    static {
        priorityMap = new HashMap<String, String>();
        priorityMap.put("Critical", "5");
        priorityMap.put("High", "4");
        priorityMap.put("Medium", "3");
        priorityMap.put("Low", "2");
        priorityMap.put("Very low", "1");
        priorityMap.put("OFF", "OFF");
    }

    public static int getColor(long priority, Context c) {
        switch ((int)priority) {
            case 5:
                return c.getResources().getColor(R.color.prior5);
            case 4:
                return c.getResources().getColor(R.color.prior4);
            case 3:
                return c.getResources().getColor(R.color.prior3);
            case 2:
                return c.getResources().getColor(R.color.prior2);
            case 1:
                return c.getResources().getColor(R.color.prior1);
            default:
                return c.getResources().getColor(android.R.color.transparent);
        }
    }

    public static int getColor(String priority, Context c) {
        switch (priority) {
            case "1 - Very Low Priority":
                return c.getResources().getColor(R.color.prior1);
            case "2 - Low Priority":
                return c.getResources().getColor(R.color.prior2);
            case "3 - Medium Priority":
                return c.getResources().getColor(R.color.prior3);
            case "4 - High Priority":
                return c.getResources().getColor(R.color.prior4);
            case "5 - Critical Priority":
                return c.getResources().getColor(R.color.prior5);
            default:
                return c.getResources().getColor(android.R.color.transparent);
        }
    }

    public static String getSortDbTitle(String oldName) {
        if (sortMap.containsKey(oldName)) {
            return sortMap.get(oldName);
        } else {
            return null;
        }
    }

    public static String getPriorityDbTitle(String oldName) {
        if (priorityMap.containsKey(oldName)) {
            return priorityMap.get(oldName);
        } else {
            return null;
        }
    }

    //send "Low" -> get 2
    public static String getPriorityNameByNumber(String priority) {
        for (Map.Entry<String, String> entry : priorityMap.entrySet()) {
            if (entry.getValue().equals(priority)) {
                return entry.getKey();
            }
        }
        return null;
    }
@Deprecated
    public static String switchLeftCheckedMenu(int pos, long id, long dep) {

        return "";
    }

}
