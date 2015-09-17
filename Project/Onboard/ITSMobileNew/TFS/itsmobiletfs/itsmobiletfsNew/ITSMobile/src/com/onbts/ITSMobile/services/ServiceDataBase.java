package com.onbts.ITSMobile.services;

import android.app.IntentService;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.onbts.ITSMobile.model.DetailedIssue;
import com.onbts.ITSMobile.model.FileModel;
import com.onbts.ITSMobile.model.FilterModel;
import com.onbts.ITSMobile.model.HistoryModel;
import com.onbts.ITSMobile.model.InsertTrackResult;
import com.onbts.ITSMobile.model.IssueClassModel;
import com.onbts.ITSMobile.model.PermissionModel;
import com.onbts.ITSMobile.model.ReturnDateWithActionDialog;
import com.onbts.ITSMobile.model.UserModel;
import com.onbts.ITSMobile.model.base.Model;
import com.onbts.ITSMobile.model.issue.IssueModel;
import com.onbts.ITSMobile.model.issue.UpdateIssueModel;
import com.onbts.ITSMobile.util.RijndaelCrypt;
import com.onbts.ITSMobile.util.UID;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import util.RawQueries;
import util.SPHelper;

/**
 * IntentService for background work with DataBase.
 * Created by tigre on 03.04.14.
 */
public class ServiceDataBase extends IntentService {

    public static final String KEY_REQUEST = "key_request";
    public static final String KEY_REQUEST_MODELS = "key_request_models";
    public static final String BROADCAST_ACTION = "com.onbts.ITSMobile.services.db.callback.action";
    public static final String EXTRA_KEY_UPDATE = "EXTRA_UPDATE";
    private DbService.DatabaseHelper helper;
    private SQLiteDatabase issutraxdb;
    private boolean checkServise = false;

    public ServiceDataBase() {
        super("ServiceDataBase");
    }

    public static void writeFile(byte[] data, File fileName) throws IOException {
        FileOutputStream out = new FileOutputStream(fileName);
        out.write(data);
        out.close();
    }

    private static String getMimeType(String extension) {
        String type = null;
        if (extension != null) {
            extension = extension.replaceAll("\\.", "");
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            DBRequest request =intent.getParcelableExtra(KEY_REQUEST);
            try {


                switch (request.getType()) {
                    case STATUS_SERVICE:
                        break;
                    case FILTER_LIST_DECK:
                        break;
                    case GET_FILTER_PRIORITIES:
                        sendMessageCallBack(request.setModels(getPriorityFilters()));
                        break;
                    case GET_FILTER_LIST_STATUSES:
                        sendMessageCallBack(request.setModels(getStatusFilters()));
                        break;
                    case GET_FILTER_LIST_TYPES:
                        sendMessageCallBack(request.setModels(getTypesFilters()));
                        break;
                    case GET_FILTER_LIST_SECTIONS:
                        sendMessageCallBack(request.setModels(getSectionsFilters()));
                        break;
                    case GET_FILTER_LIST_DECKS:
                        sendMessageCallBack(request.setModels(getDecksFilters()));
                        break;
                    case GET_FILTER_LIST_DEPARTMENTS:
                        sendMessageCallBack(request.setModels(getDepartmentsFilters()));
                        break;
                    case GET_FILTER_LIST_FIREZONES:
                        sendMessageCallBack(request.setModels(getFirezonesFilters()));
                        break;
                    case GET_FILTER_LIST_LOCATIONGROUPS:
                        sendMessageCallBack(request.setModels(getLocationGroupFilters()));
                        break;
                    case GET_FILTER_LIST_LOCATION_ID:
                        sendMessageCallBack(request.setModels(getLocationIDs(intent.getLongExtra("locationGroupID", 0))));
                        break;
                    case LOGIN:
                        sendMessageCallBack(request.setModel(login(intent.getStringExtra("login"),
                                intent.getStringExtra("psw"))));
                        break;
                    case UPDATE_USER:
                        sendMessageCallBack(request.setModel(updateUser(intent.getLongExtra("userID", 0))));
                        break;
                    case USER_CREATE_ISSUE_LIST:
                        sendMessageCallBack(request.setModels(getIssuesUserCreate(intent.getLongExtra("userID",
                                0))));
                        break;
                    case DEPARTMENT_ASSIGNED_ISSUE_LIST:
                        sendMessageCallBack(request.setModels(getIssuesDepartmentAssigned(intent.getLongExtra
                                ("DepartmentID", 0), intent.getLongExtra
                                ("userID", 0))));
                        break;
                    case USER_ASSIGNED_ISSUE_LIST:

                        sendMessageCallBack(request.setModels(getIssuesUserAssigned(intent.getLongExtra("userID",
                                0))));
                        break;
                    case USER_FAVORITE_ISSUE_LIST:
                        sendMessageCallBack(request.setModels(getIssuesUserFavorite(intent.getLongExtra("userID",
                                0))));
                        break;
                    case DEPARTMENT_CREATE_ISSUE_LIST:
                        sendMessageCallBack(request.setModels(getIssuesDepartmentCreate(intent.getLongExtra
                                ("DepartmentID", 0))));
                        break;
                    case DETAILS_ISSUE:
                        sendMessageCallBack(request.setModel(getIssueDetails(intent.getLongExtra("issueID", 0),
                                (UserModel) intent.getParcelableExtra("user"))));
                        break;
//                    case DETAILS_ISSUE_LITE:
//                        sendMessageCallBack(request.setModel(getIssueDetailsLite(intent.getLongExtra("issueID", 0),
//                                (UserModel) intent.getParcelableExtra("user"))));
//                        break;
                    case HISTORY_ISSUE:
                        sendMessageCallBack(request.setModels(getIssueHistory(intent.getLongExtra("issueID", 0),
                                (UserModel) intent.getParcelableExtra("user"))));
                        break;
                    case INSERT_ISSUE_TRACK:
                        ArrayList<ReturnDateWithActionDialog> data = intent.getParcelableArrayListExtra("data");
                        sendMessageCallBack(request.setModel(
                                insertIssueTrack((UserModel) intent.getParcelableExtra("user"),
                                        (DetailedIssue) intent.getParcelableExtra("issue"),
                                        data, intent.getLongExtra("idAction", 0),
                                        intent.getStringExtra("ActionCode"),
                                        intent.getLongExtra("prevActionID", 0),
                                        intent.getLongExtra("nextActionID", 0), intent.getBooleanExtra("keep", false))
                        ));
                        break;

                    case UPDATE_ISSUE:
                        boolean result = updateIssue((UpdateIssueModel) intent.getParcelableExtra("issue_update"));
                        sendMessageCallBack(request);
                        break;
                    case OPEN_FILE:
                        openFile(intent.getLongExtra("fileID", 0));
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
//                Crashlytics.logException(e);
                sendMessageCallBack(request);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        open();
//        Crashlytics.start(this);

        checkServise = true;
        callBackStatusServise(checkServise);
    }

    private void callBackStatusServise(boolean status) {
        Intent intentUpdate = new Intent(BROADCAST_ACTION);
        intentUpdate.putExtra(EXTRA_KEY_UPDATE, status);
        sendBroadcast(intentUpdate);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        callBackStatusServise(checkServise);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        checkServise = false;
        callBackStatusServise(checkServise);
        super.onDestroy();
        close();
    }

    private void close() {
        if (helper != null)
            helper.close();
        issutraxdb = null;
        helper = null;
    }

    /**
     * Login user.
     *
     * @param username - user name
     * @param password - user password
     * @return if (db error, or user not found) - null, if (password incorrect) - UserModel with id = -1,
     * else - UserModel with full data.
     */
    private UserModel login(String username, String password) {
        if (issutraxdb == null)
            return null;
//        Crashlytics.log("start pass: u=" + username + "; p=" + password);
        long timeLast = SPHelper.getLastUpdateTimeForNew(this);
        RijndaelCrypt crypto = new RijndaelCrypt("Onboard@sosftware");

        UserModel user = null;
        Cursor cursor = issutraxdb.query("UsersMobile AS um INNER JOIN Users as u on um.UserID = u.UserID INNER JOIN userDepartments as ud on u.UserID = ud.UserID INNER JOIN departments as d on ud.DepartmentID=d.DepartmentID",
                new String[]{"u.LocationGroupID as LocationGroupID", "u.userid as UserID", "um.password as password", "u.UserDesc as UserDesc", "d.DepartmentDesc as DepartmentDesc", "d.DepartmentID as DepartmentID"},
                "UPPER(um.username) = ?", new String[]{username.toUpperCase()}, null, null, null, "1");
        if (cursor.moveToFirst()) {
            String encrypted = crypto.encrypt(password);
            if (encrypted != null) {
                password = encrypted;
            }
            if (password.equals(cursor.getString(cursor.getColumnIndex("password")))) {
                user = new UserModel(cursor.getLong(cursor.getColumnIndex("LocationGroupID")), cursor.getLong(cursor.getColumnIndex("UserID")),
                        cursor.getString(cursor.getColumnIndex("UserDesc")),
                        cursor.getString(cursor.getColumnIndex("DepartmentDesc")),
                        cursor.getLong(cursor.getColumnIndex("DepartmentID")));
                user.setDeviceId(UID.id(this));

                // password right
            } else {
                // password not right
//                Crashlytics.logException(new Throwable("wrong pass: u=" + username + "; p=" + password));
                user = new UserModel(-1, -1, cursor.getString(cursor.getColumnIndex("UserDesc")), null, -1);
            }
        } else {
            user = null;
        }
        cursor.close();
        if (user != null && user.getId() > 0) {
            cursor = issutraxdb.query("SysParms", new String[]{"SiteID", "WorkflowID",
                    "AllowFileAttachments"}, null, null, null, null, null, "1");
            if (cursor.moveToFirst()) {
                user.setSiteID(cursor.getLong(0));
                ContentValues cv = new ContentValues();
                cv.put("UserID", user.getId());
                cv.put("DeviceID", user.getDeviceId());
                cv.put("SiteID", user.getSiteID());
                cv.put("ToDelete", String.valueOf(false));
                cv.put("Locked", String.valueOf(false));
                cv.put("ReadOnly", String.valueOf(false));
                try {
                    cv.put("AppVersion", UID.getVersion(this));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                cv.put("isDirty", 1);
                issutraxdb.replace("mobiledevice", null, cv);
            }
            cursor.close();
            fillUserCounter(user, timeLast);
        }
        return user;
    }

    private void fillUserCounter(UserModel user, long lastTime) {

        //set permissions

        Cursor cursor = issutraxdb.query("PermissionGroups_Users as pgu INNER JOIN Permissions as p on p.PermissionGroupID" +
                        " = pgu.PermissionGroupID", new String[]{"IssueClassID", "PermissionID",
                        "CanCreateIssue", "CanTransferToPrevDept",
                        "CanTransferToCreatorDept", "CanRequestInfoFromPrevDept", "CanRequestInfoFromCreatorDept", "p.PermissionGroupID as fff"}, "UserID = ?",
                new String[]{String.valueOf(user.getId())}, null, null, null
        );
        user.setIssueClasses(getIssueClassFromCursor(cursor));
        cursor.close();
        if (user.getIssueClasses() == null) {
            cursor = issutraxdb.query("PermissionGroups_Departments as pgd INNER JOIN Permissions as p " +
                            "on p.PermissionGroupID = pgd.PermissionGroupID", new String[]{"IssueClassID",
                            "PermissionID",
                            "CanCreateIssue", "CanTransferToPrevDept", "CanTransferToCreatorDept",
                            "CanRequestInfoFromPrevDept", "CanRequestInfoFromCreatorDept", "p.PermissionGroupID as fff"}, "DepartmentID = ?",
                    new String[]{String.valueOf(user.getDepartmentId())}, null, null, null
            );
            user.setIssueClasses(getIssueClassFromCursor(cursor));
            cursor.close();
        }

        cursor = issutraxdb.rawQuery(RawQueries.USER_ASSIGNED_COUNT, new String[]{
                String.valueOf(lastTime), String.valueOf(user.getId())});
        if (cursor.moveToFirst()) {
            user.setCountAssigned(cursor.getInt(0));
            user.setCountNewAssigned(cursor.getInt(1));
            user.setAlertAssigned(cursor.getInt(2));
            user.setPreAlertAssigned(cursor.getInt(3));
        }
        cursor.close();

        cursor = issutraxdb.rawQuery(RawQueries.CREATE_BY_USER_COUNT, new String[]{String.valueOf(lastTime),
                String.valueOf(user.getId())});
        if (cursor.moveToFirst()) {
            user.setCountCreate(cursor.getInt(0));
            user.setCountNewCreate(cursor.getInt(1));
            user.setAlertCreate(cursor.getInt(2));
            user.setPreAlertCreate(cursor.getInt(3));
        }
        cursor.close();

        cursor = issutraxdb.rawQuery(RawQueries.USER_FAVORITE_COUNT, new String[]{String.valueOf(lastTime)});
        if (cursor.moveToFirst()) {
            user.setCountFavorite(cursor.getInt(0));
            user.setCountNewFavorite(cursor.getInt(1));
            user.setAlertFavorite(cursor.getInt(2));
            user.setPreAlertFavorite(cursor.getInt(3));
        }
        cursor.close();

        cursor = issutraxdb.rawQuery(RawQueries.CREATE_BY_DEPARTMENT_COUNT,
                new String[]{String.valueOf(lastTime), String.valueOf(user.getDepartmentId())
                }
        );
        if (cursor.moveToFirst()) {
            user.setCountDepCreate(cursor.getInt(0));
            user.setCountNewDepCreate(cursor.getInt(1));
            user.setAlertDepCreated(cursor.getInt(2));
            user.setPreAlertDepCreated(cursor.getInt(3));
        }
        cursor.close();

        cursor = issutraxdb.rawQuery(RawQueries.DEPARTMENT_ASSIGNED_COUNT,
                new String[]{String.valueOf(lastTime), String.valueOf(user.getDepartmentId()), String.valueOf(user.getId())}
        );
        if (cursor.moveToFirst()) {
            user.setCountDepAssigned(cursor.getInt(0));
            user.setCountNewDepAssigned(cursor.getInt(1));
            user.setAlertDepAssigned(cursor.getInt(2));
            user.setPreAlertDepAssigned(cursor.getInt(3));
        }
        cursor.close();
    }

    /**
     * Update current user. If user exist update counters. If user not exist - return null.
     *
     * @param userID - current user ID
     * @return UserModel or null if not exist.
     */
    private UserModel updateUser(long userID) {
        if (issutraxdb == null)
            return null;
        long timeLast = SPHelper.getLastUpdateTimeForNew(this);


        UserModel user = null;
        Cursor cursor = issutraxdb.query("UsersMobile AS um INNER JOIN Users as u on um.UserID = u.UserID INNER JOIN userDepartments as ud on u.UserID = ud.UserID INNER JOIN departments as d on ud.DepartmentID=d.DepartmentID",
                new String[]{"u.LocationGroupID as LocationGroupID", "u.userid as UserID", "um.password as password", "u.UserDesc as UserDesc",
                        "d.DepartmentDesc as DepartmentDesc", "d.DepartmentID as DepartmentID"},
                "um.userid = ?", new String[]{String.valueOf(userID)}, null, null,
                null, "1"
        );
        if (cursor.moveToFirst()) {

            user = new UserModel(cursor.getLong(cursor.getColumnIndex("LocationGroupID")), cursor.getLong(cursor.getColumnIndex("UserID")),
                    cursor.getString(cursor.getColumnIndex("UserDesc")),
                    cursor.getString(cursor.getColumnIndex("DepartmentDesc")),
                    cursor.getLong(cursor.getColumnIndex("DepartmentID")));
            user.setDeviceId(UID.id(this));


        } else {
            user = null;
        }
        cursor.close();
        if (user != null && user.getId() > 0) {
            cursor = issutraxdb.query("SysParms", new String[]{"SiteID", "WorkflowID",
                    "AllowFileAttachments"}, null, null, null, null, null, "1");
            if (cursor.moveToFirst()) {
                user.setSiteID(cursor.getLong(0));
            }
            cursor.close();
            fillUserCounter(user, timeLast);
        }
        return user;
    }

    private List<IssueClassModel> getIssueClassFromCursor(Cursor cursor) {
        List<IssueClassModel> issueClasses = null;
        if (cursor.moveToFirst()) {
            issueClasses = new ArrayList<IssueClassModel>();
            do {
                try {
                    issueClasses.add(new IssueClassModel(cursor.getLong(cursor.getColumnIndex("IssueClassID")),
                            new PermissionModel(cursor.getLong(cursor.getColumnIndex("PermissionID")),
                                    cursor.getString(cursor.getColumnIndex("CanCreateIssue")),
                                    cursor.getString(cursor.getColumnIndex("CanTransferToPrevDept")),
                                    cursor.getString(cursor.getColumnIndex("CanTransferToCreatorDept")),
                                    cursor.getString(cursor.getColumnIndex("CanRequestInfoFromPrevDept")),
                                    cursor.getString(cursor.getColumnIndex("CanRequestInfoFromCreatorDept")),
                                    cursor.getInt(cursor.getColumnIndex("fff")))
                    ));
                } catch (NullPointerException e) {
                }

            } while (cursor.moveToNext());
        }
        return issueClasses;
    }

    private ArrayList<IssueModel> getIssuesUserCreate(long id) {
        long time = System.currentTimeMillis();
        ArrayList<IssueModel> issues = null;
        if (issutraxdb == null)
            return null;
        Cursor c = issutraxdb.rawQuery(RawQueries.CREATE_BY_USER_V2, new String[]{String.valueOf(id)});

        if (c.moveToFirst()) {
            issues = new ArrayList<IssueModel>();
            do {
                issues.add(getIssue(c));
            } while (c.moveToNext());
        }
        c.close();
        Log.w("time", "time = " + (System.currentTimeMillis() - time));
        if (issues != null)
            Log.i("time", "count = " + issues.size());
        return issues;
    }

    private ArrayList<IssueModel> getIssuesUserAssigned(long id) {
        Log.i("time", "count = " + id);
        long time = System.currentTimeMillis();
        ArrayList<IssueModel> issues = null;
        if (issutraxdb == null)
            return null;
        Cursor c = issutraxdb.rawQuery(RawQueries.USER_ASSIGNED_V2, new String[]{String.valueOf(id)});
        if (c.moveToFirst()) {
            issues = new ArrayList<IssueModel>();
            do {
                issues.add(getIssue(c));
            } while (c.moveToNext());
        }
        c.close();
        Log.w("time", "time 2 = " + (System.currentTimeMillis() - time));
        if (issues != null)
            Log.i("time", "count = " + issues.size());


        return issues;
    }

    private ArrayList<IssueModel> getIssuesDepartmentCreate(long id) {
        long time = System.currentTimeMillis();
        ArrayList<IssueModel> issues = null;
        if (issutraxdb == null)
            return null;
        Cursor c = issutraxdb.rawQuery(RawQueries.CREATE_BY_DEPARTMENT_V2, new String[]{String.valueOf(id)});

        if (c.moveToFirst()) {
            issues = new ArrayList<IssueModel>();
            do {
                issues.add(getIssue(c));
            } while (c.moveToNext());
        }
        c.close();
        Log.i("time", "time = " + (System.currentTimeMillis() - time));
        if (issues != null)
            Log.i("time", "count = " + issues.size());
        return issues;
    }

    private ArrayList<IssueModel> getIssuesDepartmentAssigned(long idDepartment, long idUser) {
        long time = System.currentTimeMillis();
        ArrayList<IssueModel> issues = null;
        if (issutraxdb == null)
            return null;
        Cursor c = issutraxdb.rawQuery(RawQueries.DEPARTMENT_ASSIGNED_V2, new String[]{String.valueOf(idDepartment),
                String.valueOf(idUser)});
        if (c.moveToFirst()) {
            issues = new ArrayList<IssueModel>();
            do {
                issues.add(getIssue(c));
            } while (c.moveToNext());
        }
        c.close();
        Log.i("time", "time = " + (System.currentTimeMillis() - time));
        if (issues != null)
            Log.i("time", "count = " + issues.size());
        return issues;
    }

    private ArrayList<IssueModel> getIssuesUserFavorite(long id) {
        long time = System.currentTimeMillis();
        ArrayList<IssueModel> issues = null;
        if (issutraxdb == null)
            return null;
        Cursor c = issutraxdb.rawQuery(RawQueries.USER_FAVORITE_V2, new String[]{String.valueOf(true)});
        if (c.moveToFirst()) {
            issues = new ArrayList<IssueModel>();
            do {
                issues.add(getIssue(c));
            } while (c.moveToNext());
        }
        c.close();
        Log.w("time", "time = " + (System.currentTimeMillis() - time));
        if (issues != null)
            Log.i("time", "count = " + issues.size());
        return issues;
    }

    private ArrayList<FilterModel> getPriorityFilters() {
        ArrayList<FilterModel> priorities = new ArrayList<>();
        String query = "select PriorityID, PriorityDesc from priorities;";
        Cursor c = issutraxdb.rawQuery(query, null);
        if (!c.moveToFirst()) {
            Log.e("db", "Null cursor at getPrioritiesFilters");
            c.close();
            return null;
        }
        do {
            priorities.add(new FilterModel(c.getLong(c.getColumnIndex("PriorityID")),
                    c.getString(c.getColumnIndex("PriorityDesc"))));
        } while (c.moveToNext());
        c.close();
        return priorities;
    }

    private ArrayList<FilterModel> getStatusFilters() {
        ArrayList<FilterModel> statuses = new ArrayList<>();
        String query = "select StatusID, StatusDesc from statuses order by StatusDesc Asc;";
        Cursor c = issutraxdb.rawQuery(query, null);
        if (!c.moveToFirst()) {
            Log.e("db", "Null cursor at getStatusFilters");
            c.close();
            return null;
        }
        do {
            statuses.add(new FilterModel(c.getLong(c.getColumnIndex("StatusID")),
                    c.getString(c.getColumnIndex("StatusDesc"))));
        } while (c.moveToNext());
        c.close();
        return statuses;
    }

    private ArrayList<FilterModel> getTypesFilters() {
        ArrayList<FilterModel> types = new ArrayList<>();
        String query = "select IssueTypeID, IssueTypeDesc from issuetypes;";
        Cursor c = issutraxdb.rawQuery(query, null);
        if (!c.moveToFirst()) {
            Log.e("db", "Null cursor at getTypesFilters");
            c.close();
            return null;
        }
        do {
            types.add(new FilterModel(c.getLong(c.getColumnIndex("IssueTypeID")),
                    c.getString(c.getColumnIndex("IssueTypeDesc"))));
        } while (c.moveToNext());
        c.close();
        return types;
    }

    private ArrayList<FilterModel> getSectionsFilters() {
        ArrayList<FilterModel> sections = new ArrayList<>();
        String query = "select ZoneID, ZoneName from zones;";
        Cursor c = issutraxdb.rawQuery(query, null);
        if (!c.moveToFirst()) {
            Log.e("db", "Null cursor at getSectionsFilters");
            c.close();
            return null;
        }
        do {
            sections.add(new FilterModel(c.getLong(c.getColumnIndex("ZoneID")),
                    c.getString(c.getColumnIndex("ZoneName"))));
        } while (c.moveToNext());
        c.close();
        return sections;
    }

    private ArrayList<FilterModel> getDecksFilters() {
        ArrayList<FilterModel> decks = new ArrayList<>();
        String query = "select DeckID, DeckDesc from decks;";
        Cursor c = issutraxdb.rawQuery(query, null);
        if (!c.moveToFirst()) {
            Log.e("db", "Null cursor at getDecksFilters");
            c.close();
            return null;
        }
        do {
            decks.add(new FilterModel(c.getLong(c.getColumnIndex("DeckID")),
                    c.getString(c.getColumnIndex("DeckDesc"))));
        } while (c.moveToNext());
        c.close();
        return decks;
    }

    private ArrayList<FilterModel> getDepartmentsFilters() {
        ArrayList<FilterModel> departments = new ArrayList<>();
        String query = "select DepartmentID, DepartmentDesc from departments;";
        Cursor c = issutraxdb.rawQuery(query, null);
        if (!c.moveToFirst()) {
            Log.e("db", "Null cursor at getDepartmentsFilters");
            c.close();
            return null;
        }
        do {
            departments.add(new FilterModel(c.getLong(c.getColumnIndex("DepartmentID")),
                    c.getString(c.getColumnIndex("DepartmentDesc"))));
        } while (c.moveToNext());
        c.close();
        return departments;
    }

    private ArrayList<FilterModel> getFirezonesFilters() {
        ArrayList<FilterModel> firezones = new ArrayList<>();
        String query = "select FireZoneID, FireZoneName from firezones;";
        Cursor c = issutraxdb.rawQuery(query, null);
        if (!c.moveToFirst()) {
            Log.e("db", "Null cursor at getFirezonesFilters");
            c.close();
            return null;
        }
        do {
            firezones.add(new FilterModel(c.getLong(c.getColumnIndex("FireZoneID")),
                    c.getString(c.getColumnIndex("FireZoneName"))));
        } while (c.moveToNext());
        c.close();
        return firezones;
    }

    private ArrayList<FilterModel> getLocationGroupFilters() {
        ArrayList<FilterModel> locationGroups = new ArrayList<>();
        String query = "select LocationGroupID, LocationGroupDesc from locationgroups WHERE DisplayOnDevice = 'true';";
        Cursor c = issutraxdb.rawQuery(query, null);
        if (!c.moveToFirst()) {
            Log.e("db", "Null cursor at getLocationGroupFilters");
            c.close();
            return null;
        }
        do {
            locationGroups.add(new FilterModel(c.getLong(c.getColumnIndex("LocationGroupID")),
                    c.getString(c.getColumnIndex("LocationGroupDesc"))));
        } while (c.moveToNext());
        c.close();
        return locationGroups;
    }

    private ArrayList<FilterModel> getLocationIDs(long locationGroupID) {
        ArrayList<FilterModel> locationIDs = new ArrayList<>();
        String query = String.format("select b.LocationID as locID, LocationDesc from locations_locationgroups as a LEFT JOIN locations as b ON a.LocationID = b.LocationID WHERE  a.LocationGroupID = %d ;", locationGroupID);
        Cursor c = issutraxdb.rawQuery(query, null);
        if (!c.moveToFirst()) {
            Log.e("db", "Null cursor at getLocationIDFilter");
            c.close();
            return null;
        }
        do {
            locationIDs.add(new FilterModel(c.getLong(c.getColumnIndex("locID")),
                    c.getString(c.getColumnIndex("LocationDesc"))));
        } while (c.moveToNext());
        c.close();
        return locationIDs;
    }

    private IssueModel getIssue(Cursor c) {
        IssueModel issue = new IssueModel(c.getLong(c.getColumnIndex("_id")));
        issue.setTypeDesc(c.getString(c.getColumnIndex("IssueTypeDesc")));
        issue.setTypeID(c.getLong(c.getColumnIndex("IssueTypeID")));
//        issue.setLocationGroupID(c.getLong(c.getColumnIndex("LocationGroupID")));
        issue.setLocationDesc(c.getString(c.getColumnIndex("LocationDesc")));
        issue.setCreateDate(c.getLong(c.getColumnIndex("CreateDate")));
        issue.setStatusDesc(c.getString(c.getColumnIndex("StatusDesc")));
        issue.setDeckID(c.getLong(c.getColumnIndex("DeckID")));
        issue.setStatusID(c.getLong(c.getColumnIndex("StatusID")));
        issue.setPriority(c.getInt(c.getColumnIndex("PriorityID")));
        issue.setZoneID(c.getLong(c.getColumnIndex("ZoneID")));
        issue.setDepartmentID(c.getLong(c.getColumnIndex("DepartmentID")));
        issue.setNotes(c.getString(c.getColumnIndex("Notes")));
        issue.setOpenOnDevice(c.getString(c.getColumnIndex("OpenedOnDevice")));
        issue.setFavorite(c.getString(c.getColumnIndex("DeviceFavorite")));
        issue.setLocationID(c.getLong(c.getColumnIndex("LocationID")));
        issue.setFireZoneID(c.getLong(c.getColumnIndex("FireZoneID")));
        issue.setAlert(c.getString(c.getColumnIndex("AlertDesc")));
        issue.setHasFile(c.getInt(c.getColumnIndex("CountAttach")) > 0);
//        issue.setHasFile(com.onbts.ITSMobile.services.DB.DBRequest.getAttachmentsBoolean(issue.getId(), issutraxdb));
        return issue;

    }

    private DetailedIssue getIssueDetails(long issueID, UserModel user) {
        DetailedIssue issue = null;
        Cursor c = issutraxdb.rawQuery(RawQueries.getIssueDetails, new String[]{String.valueOf(issueID), String.valueOf(issueID)});
        if (c.moveToFirst())
            issue = getIssueDetails(c, user);
        c.close();
        return issue;
    }

//    private DetailsIssueLite getIssueDetailsLite(long issueID, UserModel user) {
//        DetailsIssueLite issue = null;
//        Cursor c = issutraxdb.rawQuery(RawQueries.getIssueDetails, new String[]{String.valueOf(issueID), String.valueOf(issueID)});
//        if (c.moveToFirst())
//            issue = getIssueDetailsLite(c, user);
//        c.close();
//        return issue;
//    }

    private ArrayList<HistoryModel> getIssueHistory(long issueID, UserModel user) {
        return com.onbts.ITSMobile.services.DB.DBRequest.getHistory(issueID, user, issutraxdb, null);
    }

    private DetailedIssue getIssueDetails(Cursor c, UserModel user) {
        long time = System.currentTimeMillis();

        DetailedIssue details = new DetailedIssue();
        details.setIssueType(c.getString(c.getColumnIndex("IssueTypeDesc")));
        details.setLocationDesc(c.getString(c.getColumnIndex("LocationDesc")));
        details.setCreatedDate(c.getString(c.getColumnIndex("CreateDate")));
        details.setNotes(c.getString(c.getColumnIndex("NotesLast")));

        details.setOpenOnDevice(c.getString(c.getColumnIndex("OpenedOnDevice")));
        details.setFavorite(c.getString(c.getColumnIndex("DeviceFavorite")));

        details.setEnteredBy(c.getString(c.getColumnIndex("UserDesc")));
        details.setReportedByCrew(c.getString(c.getColumnIndex("ReportBy")));
        details.setPrior(c.getString(c.getColumnIndex("PriorityLevel")));
        details.setStatus(c.getString(c.getColumnIndex("StatusDesc")));
        details.setOnbehalfofuser(c.getString(c.getColumnIndex("beUserDesc")));
        details.setId(c.getLong(c.getColumnIndex("IssueID")));
        details.setPriorId(c.getLong(c.getColumnIndex("PriorityID")));
        details.setStatusId(c.getInt(c.getColumnIndex("StatusID")));
        details.setCurrentDepartmentId(c.getInt(c.getColumnIndex("CurrentDepartmentID")));
        details.setIssueTypeId(c.getInt(c.getColumnIndex("IssueTypeID")));
        details.setConcurrencyId(c.getInt(c.getColumnIndex("ConcurrencyID")));
        details.setIssueClassID(c.getInt(c.getColumnIndex("IssueClassID")));
        details.setGuestServiceIssueBoolean(c.getString(c.getColumnIndex("GuestServiceIssue")));
        details.setAlert(c.getString(c.getColumnIndex("AlertID")));
/*
        String yOrN = details.isGuestServiceIssueBoolean()
                ? "No"
                : "Yes";
        details.setGuestService(yOrN);
*/
        details.setCrewDepartment(c.getString(c.getColumnIndex("DepartmentDescCrew")));
        details.setCreatorDepartmentDesc(c.getString(c.getColumnIndex("DepartmentDescCreate")));
        details.setCreatorDepartmentId(c.getLong(c.getColumnIndex("CreateByDepartmentID")));
        details.setCrewPosition(c.getString(c.getColumnIndex("PositionName")));
        details.setOnbehalfofuser(c.getString(c.getColumnIndex("beUserDesc")));
        details.setCreatorOfficePhoneNumber(c.getString(c.getColumnIndex("OfficeNumber")));
        details.setCreatorExtension(c.getString(c.getColumnIndex("Extension")));
        details.setCreatorMobile(c.getString(c.getColumnIndex("MobileNumber")));
        details.setCreatorPager(c.getString(c.getColumnIndex("Pager")));

        details.setDefect(c.getString(c.getColumnIndex("DefectName")));
        details.setDeckDesc(c.getString(c.getColumnIndex("DeckDesc")));
        details.setTransverse(c.getString(c.getColumnIndex("TransverseName")));
        details.setSection(c.getString(c.getColumnIndex("ZoneName")));
        details.setFireZone(c.getString(c.getColumnIndex("FireZoneName")));

        details.setRequiresGuestCallbackBoolean(c.getString(c.getColumnIndex("RequiresGuestCallback")));

        details.setGuestFirstName(c.getString(c.getColumnIndex("GuestFirstName")));
        details.setGuestLastName(c.getString(c.getColumnIndex("GuestLastName")));
        details.setDateGuestExp(c.getString(c.getColumnIndex("DateFirstExperienced")));
        details.setCabinNumber(c.getString(c.getColumnIndex("Cabin")));

        details.setReservationNumber(c.getString(c.getColumnIndex("BookingID")));
        details.setDisembarkDate(c.getString(c.getColumnIndex("DebarkationDate")));
        details.setSeverity(c.getString(c.getColumnIndex("ComplaintSeverityDesc")));
        details.setLocationOwnerDepartmentID(c.getLong(c.getColumnIndex("LocationOwnerDepartmentID")));
        details.setCompName(c.getString(c.getColumnIndex("CompName")));
        details.setFuncNo(c.getString(c.getColumnIndex("FuncNo")));
        details.setFuncDescr(c.getString(c.getColumnIndex("FuncDescr")));
        details.setSerialNo(c.getString(c.getColumnIndex("SerialNo")));
        details.setHistoryList(com.onbts.ITSMobile.services.DB.DBRequest.getHistory(details.getId(), user, issutraxdb, null));
        details.setFileList(com.onbts.ITSMobile.services.DB.DBRequest.getAttachments(details.getId(), issutraxdb));
        details.setActionIssues(com.onbts.ITSMobile.services.DB.DBRequest.getActionForIssue(user, details.getId(),
                details.getStatusId(), true, issutraxdb));
//            c.close();
        Log.e("time", "time details = " + (System.currentTimeMillis() - time));
        return details;

    }


    private Model insertIssueTrack(UserModel user, DetailedIssue details, ArrayList<ReturnDateWithActionDialog> data, long idAction,
                                   String actionCode, long prevActionID,
                                   long nextActionID, boolean keep) {
        InsertTrackResult model = null;
        issutraxdb.beginTransaction();
        try {
            model = new InsertTrackResult(com.onbts.ITSMobile.services.DB.DBRequest.insertIssueTrack
                    (user, details, data, idAction, actionCode, prevActionID, nextActionID, keep, issutraxdb, this));
            issutraxdb.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
//            Crashlytics.logException(e);
        }


        issutraxdb.endTransaction();
        return model;
    }

    private boolean updateIssue(UpdateIssueModel model) {
        ContentValues cv = new ContentValues();
        cv.put("DeviceFavorite", String.valueOf(model.favorite));
        cv.put("OpenedOnDevice", String.valueOf(model.open));
        boolean result = issutraxdb.update("mobileissues", cv, "IssueID = ?", new String[]{String.valueOf(model.issueID)})
                > 0;
        return result;
    }

    private void openFile(long id) {
        boolean result = false;
        FileModel model = com.onbts.ITSMobile.services.DB.DBRequest.getAttachment(id, issutraxdb);
        if (model != null) {
            File temp_file = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName()
                    + "/tmp/" + model.getFilename());
            temp_file.getParentFile().mkdirs();
            byte[] fileByte = com.onbts.ITSMobile.services.DB.DBRequest.getImage(String.valueOf(id), issutraxdb);
            if (fileByte != null) {
                try {
                    writeFile(fileByte, temp_file);
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(Uri.fromFile(temp_file), getMimeType(model.getExtension()));
                    startActivity(intent);
                    result = true;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra("result", result);
        sendBroadcast(intent);

    }

    private boolean open() {
        boolean result;
        try {
            File dbFile = new File(DbService.getDbFilePath(this));
            if (!dbFile.exists()) {
                result = false;
            } else {
                if (helper != null && !helper.isOpen())
                    helper = null;
                if (helper == null) {
                    helper = new DbService.DatabaseHelper(this, DbService.getDbFilePath(this), DbService.DB_VER);
                    issutraxdb = helper.getWritableDatabase();
                }
                result = true;
            }

        } catch (Exception e) {
            Log.e("Inspections", "unable to create db", e);
            result = false;
        }
        return result;
    }

    public void sendMessageCallBack(DBRequest request) {
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra(KEY_REQUEST, request);
        intent.putExtra(KEY_REQUEST_MODELS, request.getModels());
        sendBroadcast(intent);

    }

    public boolean getStatusServase() {
        return checkServise;
    }






}
