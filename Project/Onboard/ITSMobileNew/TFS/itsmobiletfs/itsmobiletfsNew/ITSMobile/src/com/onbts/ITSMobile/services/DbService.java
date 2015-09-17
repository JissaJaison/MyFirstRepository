package com.onbts.ITSMobile.services;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.onbts.ITSMobile.model.DetailedIssue;
import com.onbts.ITSMobile.model.HistoryModel;
import com.onbts.ITSMobile.model.IssueClassModel;
import com.onbts.ITSMobile.model.PermissionModel;
import com.onbts.ITSMobile.model.SpinnerNameAndId;
import com.onbts.ITSMobile.model.UserModel;
import com.onbts.ITSMobile.panels.PanelAction;
import com.onbts.ITSMobile.panels.PanelActionWithEditText;
import com.onbts.ITSMobile.panels.PanelActionWithSpinner;
import com.onbts.ITSMobile.panels.PanelActionWithTwoSpinner;
import com.onbts.ITSMobile.panels.PanelFileAttachment;
import com.onbts.ITSMobile.panels.PanelReassign;
import com.onbts.ITSMobile.services.DB.DBRequest;
import com.onbts.ITSMobile.util.Files;
import com.onbts.ITSMobile.util.RijndaelCrypt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import util.GetColorByPriority;
import util.RawQueries;
import util.SPHelper;

public class DbService {
    public static final int DB_VER = 1;
    private static final String DB_NAME = "ITS.db";
    private static final String logId = "Database";
    private static final String ASSIGNED_ACTION_ID = "21";
    private static final String CREATED_ACTION_ID = "1";
    private static Context _context;
    private static DatabaseHelper helper;
    private static DbService db;

    private static SQLiteDatabase issutraxdb;

    public DbService(Context context) {
        _context = context;
        init();
    }

//	SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    public static String getDbFilePath(Context context) {

        return Files.getAppDataDirectory(context) + "/" + DB_NAME;
    }

    public static DbService getInstance(Context context) {

        if (db == null && context != null)
            db = new DbService(context);
        return db;

    }

    public boolean init() {
        boolean result = false;
        try {

            if (!new File(getDbFilePath(_context)).exists()) {
                result = false;
            } else {
                if ((helper != null && helper.isOpen()) && (issutraxdb != null && issutraxdb.isOpen())) {
                    return true;
                }
                helper = new DatabaseHelper(_context, getDbFilePath(_context), DB_VER);
                issutraxdb = helper.getWritableDatabase();
                result = true;
            }

        } catch (Exception e) {
            Log.e("Inspections", "unable to create db", e);
            result = false;
        }
        return result;
    }

   /* public void close() {
        if (helper != null)
            helper.close();
    }*/

    public SQLiteDatabase getIssutraxdb() {
        return issutraxdb;
    }

    public boolean isAvailable(Context context) {
        return new File(getDbFilePath(context)).exists();
    }


    // 0: succussful, 1: no user, 2: password wrong
    @Deprecated
    @SuppressLint("DefaultLocale")
    public int checkLogin(String username, String password) {
        if (issutraxdb == null)
            return 1;
        RijndaelCrypt crypto = new RijndaelCrypt("Onboard@sosftware");
        Cursor cursorUsers = issutraxdb.query("UsersMobile", new String[]{"Count(*)"},
                "UPPER(username) = ?", new String[]{username.toUpperCase()}, null, null, null, null);
        int result = 1;
        try {
            if (cursorUsers.moveToFirst()) {
                if (cursorUsers.getInt(0) == 1) {
                    // password not right
                    result = 2;

                    String encrypted = crypto.encrypt(password);
                    if (encrypted != null) {
                        password = encrypted;
                    }
                    Cursor cursor = issutraxdb.query("UsersMobile", new String[]{"Count(*)"},
                            "UPPER(username) = ? AND password = ?", new String[]{
                                    username.toUpperCase(), password}, null, null, null, null
                    );
                    try {
                        if (cursor.moveToFirst()) {
                            // password right or wrong
                            result = cursor.getInt(0) == 1 ? 0 : 2;
                        }
                    } finally {
                        cursor.close();
                    }

                }
            }
        } finally {
            cursorUsers.close();
        }

        // password not right
        return result;
    }


    @SuppressLint("DefaultLocale")
    public UserModel checkLoginUser(String username, String password) {
        if (issutraxdb == null)
            return null;

        RijndaelCrypt crypto = new RijndaelCrypt("Onboard@sosftware");

        UserModel user = null;
        Cursor cursor = issutraxdb.query("UsersMobile AS um INNER JOIN Users as u on um.UserID = u.UserID INNER JOIN userDepartments as ud on u.UserID = ud.UserID INNER JOIN departments as d on ud.DepartmentID=d.DepartmentID",
                new String[]{"u.userid as UserID", "um.password as password", "u.UserDesc as UserDesc", "d.DepartmentDesc as DepartmentDesc", "d.DepartmentID as DepartmentID", "d.DeviceID as DeviceID"},
                "UPPER(um.username) = ?", new String[]{username.toUpperCase()}, null, null, null, "1");
        if (cursor.moveToFirst()) {
            String encrypted = crypto.encrypt(password);
            if (encrypted != null) {
                password = encrypted;
            }
            if (password.equals(cursor.getString(cursor.getColumnIndex("password")))) {
                user = new UserModel(-1, cursor.getLong(cursor.getColumnIndex("UserID")),
                        cursor.getString(cursor.getColumnIndex("UserDesc")),
                        cursor.getString(cursor.getColumnIndex("DepartmentDesc")),
                        cursor.getLong(cursor.getColumnIndex("DepartmentID")));
                // password right
            } else {
                // password not right
                user = new UserModel(-1, -1, cursor.getString(cursor.getColumnIndex("UserDesc")), null, -1);
            }
        } else {
            user = null;
        }
        cursor.close();
        if (user != null && user.getId() > 0) {
            //set permissions
            cursor = issutraxdb.query("PermissionGroups_Users as pgu INNER JOIN Permissions as p on p.PermissionGroupID" +
                            " = pgu.PermissionGroupID", new String[]{"IssueClassID", "PermissionID",
                            "CanCreateIssue", "CanTransferToPrevDept",
                            "CanTransferToCreatorDept", "CanRequestInfoFromPrevDept", "CanRequestInfoFromCreatorDept"}, "UserID = ?",
                    new String[]{String.valueOf(user.getId())}, null, null, null
            );
            user.setIssueClasses(getIssueClassFromCursor(cursor));
            cursor.close();
            if (user.getIssueClasses() == null) {
                cursor = issutraxdb.query("PermissionGroups_Departments as pgd INNER JOIN Permissions as p " +
                                "on p.PermissionGroupID = pgd.PermissionGroupID", new String[]{"IssueClassID",
                                "PermissionID",
                                "CanCreateIssue", "CanTransferToPrevDept", "CanTransferToCreatorDept",
                                "CanRequestInfoFromPrevDept", "CanRequestInfoFromCreatorDept"}, "DepartmentID = ?",
                        new String[]{String.valueOf(user.getDepartmentId())}, null, null, null
                );
                user.setIssueClasses(getIssueClassFromCursor(cursor));
                cursor.close();
            }
        }
        // password not right
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
                                    cursor.getString(cursor.getColumnIndex("CanCreateIssue")).equals("true"),
                                    cursor.getString(cursor.getColumnIndex("CanTransferToPrevDept")).equals("true"),
                                    cursor.getString(cursor.getColumnIndex("CanTransferToCreatorDept")).equals("true"),
                                    cursor.getString(cursor.getColumnIndex("CanRequestInfoFromPrevDept")).equals("true"),
                                    cursor.getString(cursor.getColumnIndex("CanRequestInfoFromCreatorDept")).equals("true"),
                                    cursor.getInt(cursor.getColumnIndex("fff00")))
                    ));
                } catch (NullPointerException e) {
                }

            } while (cursor.moveToNext());
        }
        return issueClasses;
    }

    @Deprecated
    public Cursor getIssues() {
        return null;
    }

    @Deprecated
    public Cursor getDepartmentAssigned(long departmentId) {

        return null;
    }

    public Cursor getDepartmentAssigned(long departmentId, long userId) {
        Cursor c = issutraxdb.rawQuery(
                RawQueries.DEPARTMENT_ASSIGNED, new String[]{String.valueOf(departmentId),
                        String.valueOf(userId)}
        );
        Log.d("DB", "getDepartmentAssigned:" + RawQueries.DEPARTMENT_ASSIGNED);
        return c;
    }

    public Cursor getUserAssigned(long userId) {
        Cursor c = issutraxdb.rawQuery(RawQueries.USER_ASSIGNED,
                new String[]{String.valueOf(userId)});
        Log.d("DB", "getUserAssigned:" + RawQueries.USER_ASSIGNED);
        return c;
    }

//    @Deprecated
//    public Cursor getCreatedByUser(long userId) {
//        Cursor c = issutraxdb.rawQuery(String.format(RawQueries.createdByMeIssues, userId), null);
//        Log.d("query user created: ", String.format(RawQueries.createdByMeIssues, userId));
//        return c;
//    }

    public Cursor getCreatedByUser(long userId) {
/*
        Cursor c = issutraxdb.rawQuery(RawQueries.CREATE_BY_USER, new String[]{String.valueOf(userId)});
        Log.d("query user created: ", RawQueries.CREATE_BY_USER);
        return c;
*/
        return null;
    }

    @Deprecated
    public Cursor getCreatedByDepartment(long depId) {
        return null;
    }

    public Cursor getFavorites() {

        return null;
    }

    @Deprecated
    public DetailedIssue getIssueDetails(long issueId) {
        long time = System.currentTimeMillis();
        Cursor c = issutraxdb.rawQuery(RawQueries.getIssueDetails, new String[]{String.valueOf(issueId)});
        if (c.moveToFirst()) {
            DetailedIssue details = new DetailedIssue();
            details.setIssueType(c.getString(c.getColumnIndex("IssueTypeDesc")));
            details.setLocationDesc(c.getString(c.getColumnIndex("LocationDesc")));
            details.setCreatedDate(c.getString(c.getColumnIndex("CreateDate")));
            details.setNotes(c.getString(c.getColumnIndex("Notes")));

            details.setEnteredBy(c.getString(c.getColumnIndex("UserDesc")));
            details.setReportedByCrew(c.getString(c.getColumnIndex("ReportBy")));
            details.setPrior(c.getString(c.getColumnIndex("PriorityLevel")));
            details.setStatus(c.getString(c.getColumnIndex("StatusDesc")));

            details.setId(c.getLong(c.getColumnIndex("IssueID")));
            details.setStatusId(c.getInt(c.getColumnIndex("StatusID")));
            details.setCurrentDepartmentId(c.getInt(c.getColumnIndex("CurrentDepartmentID")));
            details.setIssueTypeId(c.getInt(c.getColumnIndex("IssueTypeID")));
            details.setConcurrencyId(c.getInt(c.getColumnIndex("ConcurrencyID")));
            details.setIssueClassID(c.getInt(c.getColumnIndex("IssueClassID")));
            details.setGuestServiceIssueBoolean(c.getString(c.getColumnIndex("GuestServiceIssue")));
/*            String yOrN = details.isGuestServiceIssueBoolean()
                    ? "No"
                    : "Yes";
            details.setGuestService(yOrN);*/
            details.setCrewDepartment(c.getString(c.getColumnIndex("DepartmentDescCrew")));
            details.setCrewPosition(c.getString(c.getColumnIndex("PositionName")));
            details.setOnbehalfofuser(c.getString(c.getColumnIndex("UserDesc")));
            details.setCreatorOfficePhoneNumber(c.getString(c.getColumnIndex("OfficeNumber")));
            details.setCreatorExtension(c.getString(c.getColumnIndex("Extension")));
            details.setCreatorMobile(c.getString(c.getColumnIndex("MobileNumber")));
            details.setCreatorPager(c.getString(c.getColumnIndex("Pager")));

            details.setCreatorPager(c.getString(c.getColumnIndex("DefectName")));

            details.setDeckDesc(c.getString(c.getColumnIndex("DeckDesc")));
            details.setTransverse(c.getString(c.getColumnIndex("TransverseName")));
            details.setSection(c.getString(c.getColumnIndex("ZoneName")));
            details.setFireZone(c.getString(c.getColumnIndex("FireZoneName")));
            details.setLocationOwner(c.getString(c.getColumnIndex("DepartmentDesc")));


//            details.setGuestServiceIssue(c.getString(c.getColumnIndex("GuestServiceIssue")));

//            details.setRequireGuestCallBack(c.getString(c.getColumnIndex("RequiresGuestCallback")));

            details.setRequiresGuestCallbackBoolean(c.getString(c.getColumnIndex("RequiresGuestCallback")));

            details.setGuestFirstName(c.getString(c.getColumnIndex("GuestFirstName")));
            details.setGuestLastName(c.getString(c.getColumnIndex("GuestLastName")));
            details.setDateGuestExp(c.getString(c.getColumnIndex("DateFirstExperienced")));
            details.setCabinNumber(c.getString(c.getColumnIndex("Cabin")));
            details.setReservationNumber(c.getString(c.getColumnIndex("BookingID")));
            details.setDisembarkDate(c.getString(c.getColumnIndex("DebarkationDate")));
            details.setSeverity(c.getString(c.getColumnIndex("ComplaintSeverityDesc")));
            details.setCompName(c.getString(c.getColumnIndex("CompName")));
            details.setFuncNo(c.getString(c.getColumnIndex("FuncNo")));
            details.setFuncDescr(c.getString(c.getColumnIndex("FuncDescr")));
            details.setSerialNo(c.getString(c.getColumnIndex("SerialNo")));
            //details.setHistoryList(DBRequest.getHistory(issueId, USER, getIssutraxdb()));
            details.setFileList(DBRequest.getAttachments(issueId, getIssutraxdb()));

            c.close();
            Log.e("time", "time details = " + (System.currentTimeMillis() - time));
            return details;
        }
        c.close();

        return null;
    }

    @Deprecated
    public Cursor getIssueDetailsCursor(long issueId) {
        return issutraxdb.rawQuery(RawQueries.getIssueDetails, new String[]{String.valueOf(issueId)});
    }

    @Deprecated
    public String getUserDepartmentId(String userId) {
        String q = String.format("select DepartmentID from userdepartments WHERE UserID = '%s';",
                userId);
        Cursor c = issutraxdb.rawQuery(q, null);
        c.moveToFirst();
        Log.d("query depart by user id", q);
        String ans = c.getString(c.getColumnIndex("DepartmentID"));
        return ans;
    }

    @Deprecated
    public String getUserIdByName(String username) {
        String q = String.format("select UserID from usersmobile WHERE UserName = '%s';", username);
        Cursor c = issutraxdb.rawQuery(q, null);
        c.moveToFirst();
        Log.d("query user id by name", q);
        String ans = c.getString(c.getColumnIndex("UserID"));
        return ans;
    }

    @Deprecated
    public String getUserPosition(long userId) {
        String q = String.format("select userDesc from users WHERE UserID = '%d';", userId);
        Cursor c = issutraxdb.rawQuery(q, null);
        c.moveToFirst();
        Log.d("query userDesc", q);
        String ans = c.getString(c.getColumnIndex("UserDesc"));
        return ans;
    }

    @Deprecated
    public String getUserDepartmentDesc(long departmentId) {
        String q = String.format("select DepartmentDesc from departments WHERE DepartmentID = '%d';",
                departmentId);
        Cursor c = issutraxdb.rawQuery(q, null);
        c.moveToFirst();
        Log.d("query department desc", q);
        String ans = c.getString(c.getColumnIndex("DepartmentDesc"));
        return ans;
    }

    @Deprecated
    public Cursor getAllTypesInQuery(String query) {
        /*String tempquery = query.substring(0, query.length() - 1);
        tempquery += " GROUP BY itype.IssueTypeDesc ORDER BY itype.IssueTypeDesc ASC;";
        Cursor c = issutraxdb.rawQuery(tempquery, null);
        Log.d("query get all types: ", tempquery);*/
        //SELECT ALL TYPES TODO
        Cursor c = issutraxdb.rawQuery("select IssueTypeDesc from issuetypes;", null);
        return c;
    }

    @Deprecated
    public void setFavorite(String id, boolean value) {
        String query = String.format("update issues SET DeviceFavorite='%s' WHERE IssueID='%s';", value, id);
        Log.d("query: ", query);
        issutraxdb.execSQL(query);
    }

    @Deprecated
    public boolean isFavorite(String id) {
        String query = String.format("select  DeviceFavorite from issues where IssueID = '%s'", id);
        Cursor c = issutraxdb.rawQuery(query, null);
        c.moveToFirst();
        String ans = c.getString(c.getColumnIndex("DeviceFavorite"));
        c.close();
        return ans.equals("true");
    }

    @Deprecated
    public boolean isViewed(String id) {
        String query = String.format("select  OpenedOnDevice from issues where IssueID = '%s'", id);
        Cursor c = issutraxdb.rawQuery(query, null);
        c.moveToFirst();
        String ans = c.getString(c.getColumnIndex("OpenedOnDevice"));
        Log.d("view", "id  " + id + ans);
        c.close();
        return ans.equals("true");
    }

    public void setViewed(String id, boolean value) {
        String query = String.format("update issues SET OpenedOnDevice='%s' where IssueID = '%s'", value, id);
        //TODO try catch
        issutraxdb.execSQL(query);
    }

    @Deprecated
    public Cursor createCombineQuery(int wasQuery, long userId, long department) {

        return null;
    }

    @Deprecated
    public String addFilters(String query) {
        String patternPriority = (String) (SPHelper.getPriority(_context).equals("OFF")
                ? "LIKE '%'"
                : "= " + GetColorByPriority.getPriorityDbTitle(SPHelper.getPriority(_context)));
        query += " WHERE pr.PriorityLevel " + patternPriority + " ";

        String patternStatus = (String) (SPHelper.getStatus(_context).equals("OFF") ? "LIKE '%" : "= "
                + "'" + SPHelper.getStatus(_context))
                + "' ";
        query += " AND stat.StatusDesc " + patternStatus + " ";

        String patternType = (String) (SPHelper.getType(_context).equals("OFF") ? "LIKE '%" : "= "
                + "'" + SPHelper.getType(_context))
                + "' ";
        query += " AND itype.IssueTypeDesc " + patternType + " ";

        String patternSection = (String) (SPHelper.getSection(_context).equals("OFF")
                ? "LIKE '%"
                : "= " + "'" + getZoneIdByDesc(SPHelper.getSection(_context))) + "' ";
        query += " AND iss.ZoneID " + patternSection + " ";

        String patternDeck = (String) (SPHelper.getDeck(_context).equals("OFF") ? "LIKE '%" : "= "
                + "'" + getDeckIdByName(SPHelper.getDeck(_context)))
                + "' ";
        query += " AND iss.DeckID " + patternDeck + " ";

        String patternDepartment = (String) (SPHelper.getDepartment(_context).equals("OFF")
                ? "LIKE '%"
                : "= " + "'" + getDepartmentIdByName(SPHelper.getDepartment(_context))) + "' ";
        query += " AND iss.CreateByDepartmentID " + patternDepartment + " ";

        query += " GROUP BY _id ";

        query += SPHelper.getSortBy(_context);

        query += " ;";
        return query;
    }

    @Deprecated
    public String getZoneIdByDesc(String name) {
        String q = String.format("select ZoneID from zones WHERE ZoneName = '%s';", name);
        Log.d("query zone", q);
        Cursor c = issutraxdb.rawQuery(q, null);
        c.moveToFirst();
        return c.getString(c.getColumnIndex("ZoneID"));
    }

    @Deprecated
    public String getDeckIdByName(String name) {
        String q = String.format("select DeckID FROM decks WHERE DeckDesc = '%s';", name);
        Log.d("query deck", q);
        Cursor c = issutraxdb.rawQuery(q, null);
        c.moveToFirst();
        return c.getString(c.getColumnIndex("DeckID"));
    }

    @Deprecated
    public String getDepartmentIdByName(String name) {
        String q = String.format(
                "select DepartmentID from departments AS dep WHERE dep.DepartmentDesc = '%s';", name);
        Cursor c = issutraxdb.rawQuery(q, null);
        c.moveToFirst();
        Log.d("query depart", q);
        String ans = c.getString(c.getColumnIndex("DepartmentID"));
        return ans;
    }

    public List<PanelAction> getActionPanels(long id) {
        List<PanelAction> panelses = new ArrayList<PanelAction>();
        Cursor cursor = issutraxdb.rawQuery("SELECT PanelName, ActionPanels.PanelID " +
                "FROM ActionPanels INNER JOIN Panels ON " +
                "ActionPanels.PanelID = Panels.PanelID " +
                "WHERE ActionID =?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst())
            do {
                switch (cursor.getString(0)) {
                    case "PrioritiesPanel": {
                        // PrioritiesPanel
                        PanelActionWithSpinner panelActionWithSpinner = new PanelActionWithSpinner(cursor.getInt(1), cursor.getString(0));
                        panelActionWithSpinner.setData("PriorityDesc", "PriorityID", "Priorities", null, null, null, null, null);
                        panelses.add(panelActionWithSpinner);
                        break;
                    }
                    case "TransferDepartmentPanel": {
                        // TransferDepartmentPanel
                        PanelActionWithSpinner panelActionWithSpinner = new PanelActionWithSpinner(cursor.getInt(1), cursor.getString(0));
                        panelActionWithSpinner.setData("DepartmentDesc", "DepartmentID", "Departments", null, null, null, null, null);
                        panelses.add(panelActionWithSpinner);
                        break;
                    }
                    case "AssignPanel":
                        // AssignPanel
                        PanelActionWithTwoSpinner panelActionWithTwoSpinner = new PanelActionWithTwoSpinner(cursor.getInt(1), cursor.getString(0));
                        panelActionWithTwoSpinner.setData("DepartmentDesc", "DepartmentID", "Departments", "UserDesc", "UserID", "Users", null, null);
                        panelses.add(panelActionWithTwoSpinner);
                        break;
                    case "RequestInfoPanel": {
                        // RequestInfoPanel
                        PanelActionWithSpinner panelActionWithSpinner = new PanelActionWithSpinner(cursor.getInt(1), cursor.getString(0));
                        panelActionWithSpinner.setData("DepartmentDesc", "DepartmentID", "Departments", null, null, null, null, null);
                        panelses.add(panelActionWithSpinner);
                        break;
                    }
                    case "NotesPanel": {
                        // NotesPanel
                        PanelActionWithEditText panelActionWithEditText = new PanelActionWithEditText(cursor.getInt(1), cursor.getString(0));
                        panelses.add(panelActionWithEditText);
                        break;
                    }
                    case "StartTaskPanel": {
                        //StartTaskPanel
                        PanelActionWithEditText panelActionWithEditText = new PanelActionWithEditText(cursor.getInt(1), cursor.getString(0));
                        panelses.add(panelActionWithEditText);
                        break;
                    }
                    case "CausesPanel": {
                        // CausesPanel
                        PanelActionWithSpinner panelActionWithSpinner = new PanelActionWithSpinner(cursor.getInt(1), cursor.getString(0));
                        panelActionWithSpinner.setData("CauseDesc", "CauseID", "Causes", null, null, null, null, null);
                        panelses.add(panelActionWithSpinner);
                        break;
                    }
                    case "FileAttachmentPanel": {
                        // FileAttachmentPanel
                        PanelFileAttachment panelFileAttachment = new PanelFileAttachment(cursor.getInt(1), cursor.getString(0));
                        panelses.add(panelFileAttachment);
                        break;
                    }
                    case "ReassignPanel": {
                        // ReassignPanel
                        PanelReassign panelReassign = new PanelReassign(cursor.getInt(1), cursor.getString(0));
                        panelReassign.setData("DepartmentDesc", "DepartmentID", "Departments", "UserDesc", "UserID", "Users", null, null);
                        panelses.add(panelReassign);
                        break;
                    }

                }
            } while (cursor.moveToNext());
        cursor.close();
        return panelses;
    }

    private void updateCounters() {
        try {
            issutraxdb.beginTransaction();

            Cursor cursorUser = issutraxdb.rawQuery("SELECT UserID FROM usersmobile", null);
            if (cursorUser.moveToFirst()) {
                ContentValues cv = new ContentValues();
                Cursor cursor = null;
                do {
                    String[] idUser = new String[]{String.valueOf(cursorUser.getLong(0))};

                    cursor = issutraxdb.rawQuery("SELECT Count (*)"
                            + " FROM issues AS iss"
                            + " INNER JOIN issuetracks AS itracks ON (iss.IssueID = itracks.IssueID) AND (itracks.ActionID = 1) AND (iss.CreateByUserID = ?)"
                            , idUser);

                    cv.put("IssueCreatedCount", cursor.getInt(0));
                    cursor.close();

                    cursor = issutraxdb.rawQuery("SELECT Count (*) " +
                            "FROM issues AS iss " +
                            "INNER JOIN issuetracks AS itracks ON iss.IssueID = itracks.IssueID AND itracks.AssigneeUserID = ?"
                            , idUser);
                    cv.put("IssueAssignerCount", cursor.getInt(0));
                    cursor.close();
                    issutraxdb.update("usersmobile", cv, "UserID = ?", idUser);
                } while (cursorUser.moveToNext());

            }
            cursorUser.close();

            issutraxdb.setTransactionSuccessful();
        } catch (Exception e) {
        } finally {
            issutraxdb.endTransaction();
        }


    }


    public List<SpinnerNameAndId> getListNameAndId(String columnName, String columnId, String tableName) {
        List<SpinnerNameAndId> list = new ArrayList<SpinnerNameAndId>();
        Cursor cursor = issutraxdb.rawQuery("SELECT " + columnName + ", " + columnId + " FROM " + tableName, null);
        String debug = "SELECT " + columnName + ", " + columnId + " FROM " + tableName;
        while (cursor.moveToNext()) {
            list.add(new SpinnerNameAndId(cursor.getInt(1), cursor.getString(0), tableName));
        }
        cursor.close();
        return list;
    }

    /**
     * Get Departments for Transfer panel
     *
     * @param user      - UserModel for current user
     * @param nameTable - Table name
     * @param details   - details current issue
     * @return
     */
    public List<SpinnerNameAndId> getTransferPositions(UserModel user, String nameTable,
                                                       DetailedIssue details) {
/*
        DECLARE @CurrentAssignedDept int, @PrevAssignedDept int, @UserDepartmentID int, @IssueClassID int,
        @PermissionGroupID int, @CanTransferToPrevDept bit
*/
        long currentAssignedDept = -1;
        Cursor c;
        HistoryModel historyModelPrev = null;
        if (details.getHistoryList() != null) {
            int count = details.getHistoryList().size();
            if (count > 0) {
                HistoryModel model = details.getHistoryList().get(count - 1);
                currentAssignedDept = model.getDepartmentID();
                for (int i = count - 1; i >= 0; i--) {
                    if (details.getHistoryList().get(i).getDepartmentID() != currentAssignedDept) {
                        historyModelPrev = details.getHistoryList().get(i);
                        break;
                    }
                }
            }
        }

//        long permissionGroupID = 0;
        PermissionModel permissionModel = null;
        for (IssueClassModel icm : user.getIssueClasses()) {
            if (icm.id == details.getIssueClassID()) {
//                permissionGroupID = icm.getPermissionses().getPermissionGroupId();
                permissionModel = icm.getPermissionses();
            }
        }
        ArrayList<SpinnerNameAndId> list = new ArrayList<>();
        /*
        -- Get the current and previous departments assigned to this issue
        SET @CurrentAssignedDept = (SELECT TOP 1 DepartmentID FROM IssueTracks WHERE IssueID = @IssueID
	    ORDER BY LastUpdateDate DESC)
         */

//        Cursor c = issutraxdb.rawQuery("SELECT DepartmentID FROM IssueTracks WHERE IssueID = ? " +
//                                               "ORDER BY LastUpdateDate DESC LIMIT 1", new String[]{String.valueOf(details.getId())});
//        if (!c.moveToFirst()) {
//            Log.e("db", "cant get departament id by issueId in getTransferPositions method");
//            return null;
//        }
//        int currentAssignedDept = c.getInt(c.getColumnIndex("DepartmentID"));
//        c.close();

        /*
        SET @PrevAssignedDept = (SELECT TOP 1 DepartmentID FROM IssueTracks WHERE IssueID = @IssueID
	    AND DepartmentID <> @CurrentAssignedDept ORDER BY LastUpdateDate DESC)
         */

      /*  int prevAssignedDept;
        c = issutraxdb.rawQuery("SELECT DepartmentID FROM IssueTracks WHERE IssueID = ? " +
                                        "AND DepartmentID <> ? ORDER BY LastUpdateDate DESC LIMIT 1",
                                new String[]{String.valueOf(details.getId()), String.valueOf(currentAssignedDept)});
        if (!c.moveToFirst()) {
            prevAssignedDept = 0;
        } else {
            prevAssignedDept = c.getInt(c.getColumnIndex("DepartmentID"));
        }
        c.close();*/

        /*
        -- Get the departments that this user can transfer to
        DECLARE @Departments table (DepartmentID int)
        INSERT INTO @Departments
        SELECT DepartmentID
        FROM Permissions_Departments a INNER JOIN [Permissions] b ON a.PermissionID = b.PermissionID
        WHERE b.PermissionGroupID = @PermissionGroupID
        AND IssueClassID = @IssueClassID AND CanTransfer = 1
         */
        String pureQuery = "SELECT d.DepartmentID as DepartmentID, d.DepartmentDesc as DepartmentDesc " +
                "FROM Permissions_Departments as a INNER JOIN Permissions as b ON a.PermissionID = b.PermissionID " +
                "INNER JOIN departments as d ON d.DepartmentID = a.DepartmentID " +
                "WHERE b.PermissionGroupID = ? and d.Active = 'true' " +
                "AND IssueClassID = ? AND CanTransfer = 'true'";
        c = issutraxdb.rawQuery(pureQuery, new String[]{String.valueOf(permissionModel != null ? permissionModel
                .getPermissionGroupId() : -1),
                String.valueOf(details.getIssueClassID())});
        if (!c.moveToFirst()) {
            Log.w("db", "zero request");
        } else {
            do {
                addingWithDuplicateCheck(list, new SpinnerNameAndId(c.getInt(c.getColumnIndex("DepartmentID")),
                        c.getString(c.getColumnIndex("DepartmentDesc")), nameTable));
            } while (c.moveToNext());
        }
        c.close();


        /*
        IF @PrevAssignedDept IS NOT NULL
         BEGIN
        -- Check if the user can transfer an issue to the previously assigned department
        IF EXISTS(SELECT * FROM [Permissions] WHERE PermissionGroupID  = @PermissionGroupID
           AND IssueClassID = @IssueClassID AND CanTransferToPrevDept = 1)
         SET @CanTransferToPrevDept = 1

         //in user -> issue class -> permissions


        -- If the user can transfer to the previous department or the assigned department is the user's department then
        -- include the previous department in the list, if there's one.
         IF @CanTransferToPrevDept = 1 OR (@CurrentAssignedDept = @UserDepartmentID)
            INSERT INTO @Departments SELECT @PrevAssignedDept
         END
         */
        if (historyModelPrev != null) {
            if (permissionModel.canTransferToPrevDepartament || currentAssignedDept == user.getDepartmentId()) {
                addingWithDuplicateCheck(list, new SpinnerNameAndId(historyModelPrev.getDepartmentID(),
                        historyModelPrev.getDepartmentDesc(), nameTable));
            }
        }

        /*
        -- Check if the user can transfer an issue to the creator's department
        IF EXISTS(SELECT * FROM [Permissions] WHERE PermissionGroupID  = @PermissionGroupID
           AND IssueClassID = @IssueClassID AND CanTransferToCreatorDept = 1)
         BEGIN
            INSERT INTO @Departments
            SELECT CreateByDepartmentID FROM Issues WHERE IssueID = @IssueID
         END
         */

        if (permissionModel.canTransferToCreatorDepartament) {
            details.getCreatorDepartmentId();
            addingWithDuplicateCheck(list, new SpinnerNameAndId(details.getCreatorDepartmentId(), details.getCreatorDepartmentDesc(), nameTable));
        }

//        String query2 = "SELECT * FROM Permissions WHERE PermissionGroupID  = ? " +
//                "   AND IssueClassID = ? AND CanTransferToCreatorDept = 'true'";
//        c = issutraxdb.rawQuery(query2, new String[]{String.valueOf(permGroupID), String.valueOf(issueClassID)});
//        if (c.moveToFirst()) {
//            c.close();
//            c = issutraxdb.rawQuery("SELECT CreateByDepartmentID, d.DepartmentDesc as DepartmentDesc FROM Issues " +
//                                            " LEFT JOIN departments as d ON d.DepartmentID = CreateByDepartmentID " +
//                                            " WHERE IssueID = ?", new String[]{String.valueOf(details.getId())});
//            if (c.moveToFirst()) {
//                addingWithDuplicateCheck(list, new SpinnerNameAndId(c.getInt(c.getColumnIndex("CreateByDepartmentID")), c.getString(c.getColumnIndex("DepartmentDesc")), nameTable));
//            }
//        }
//        c.close();

        return list;
    }

    //using to avoid duplicate departments in spinner
    public void addingWithDuplicateCheck(ArrayList<SpinnerNameAndId> list, SpinnerNameAndId newSpin) {
        for (SpinnerNameAndId s : list) {
            if (s.getId() == newSpin.getId()) {
                return;
            }
        }
        list.add(newSpin);
    }


    /**
     * Getting list of departments for request info panel
     * TODO: need refactor
     *
     * @param permGroupID
     * @param issueClassID
     * @param nameTable
     * @param issueID
     * @param userDepartmentID
     * @param details
     * @return
     */
    public List<SpinnerNameAndId> getRequestInfoPositions(long permGroupID, long issueClassID, String nameTable, long issueID, long userDepartmentID, DetailedIssue details) {
        ArrayList<SpinnerNameAndId> list = new ArrayList<SpinnerNameAndId>();
        Cursor c = issutraxdb.rawQuery("SELECT DepartmentID FROM IssueTracks WHERE IssueID = ? " +
                "ORDER BY LastUpdateDate DESC LIMIT 1", new String[]{String.valueOf(issueID)});
        if (!c.moveToFirst()) {
            Log.e("db", "cant get departament id by issueId in getTransferPositions method");
            return null;
        }
        int currentAssignedDept = c.getInt(c.getColumnIndex("DepartmentID"));
        c.close();
        /*
        SET @PrevAssignedDept = (SELECT TOP 1 DepartmentID FROM IssueTracks WHERE IssueID = @IssueID
	    AND DepartmentID <> @CurrentAssignedDept ORDER BY LastUpdateDate DESC)
         */
        int prevAssignedDept;
        c = issutraxdb.rawQuery("SELECT DepartmentID FROM IssueTracks WHERE IssueID = ? " +
                "AND DepartmentID <> ? ORDER BY LastUpdateDate DESC LIMIT 1", new String[]{String.valueOf(issueID), String.valueOf(currentAssignedDept)});
        if (!c.moveToFirst()) {
            prevAssignedDept = 0;
        } else {
            prevAssignedDept = c.getInt(c.getColumnIndex("DepartmentID"));
        }
        c.close();
        /*
        -- Get the departments that this user can request info from
        DECLARE @Departments table (DepartmentID int)
        INSERT INTO @Departments
        SELECT DepartmentID
        FROM Permissions_Departments a INNER JOIN [Permissions] b ON a.PermissionID = b.PermissionID
        WHERE b.PermissionGroupID = @PermissionGroupID
         AND IssueClassID = @IssueClassID AND CanRequestInfo = 1
         */
        String pureQuery = "SELECT d.DepartmentID as DepartmentID, d.DepartmentDesc as DepartmentDesc " +
                "FROM Permissions_Departments as a INNER JOIN Permissions as b ON a.PermissionID = b.PermissionID " +
                "LEFT JOIN departments as d ON d.DepartmentID = a.DepartmentID " +
                "WHERE b.PermissionGroupID = ? and d.Active = 'true' " +
                "AND IssueClassID = ? AND CanRequestInfo = 'true'";
        c = issutraxdb.rawQuery(pureQuery, new String[]{String.valueOf(permGroupID), String.valueOf(issueClassID)});
        if (!c.moveToFirst()) {
            Log.w("db", "zero query");
        } else {
            do {
                addingWithDuplicateCheck(list, new SpinnerNameAndId(c.getInt(c.getColumnIndex("DepartmentID")), c.getString(c.getColumnIndex("DepartmentDesc")), nameTable));
            } while (c.moveToNext());
        }
        c.close();
        /*
       IF @PrevAssignedDept IS NOT NULL
        */
        if (prevAssignedDept > 0) {
            String query1 = "SELECT * FROM [Permissions] WHERE PermissionGroupID  = ? " +
                    "           AND IssueClassID = ? AND CanRequestInfoFromPrevDept = 'true'";
            c = issutraxdb.rawQuery(query1, new String[]{String.valueOf(permGroupID), String.valueOf(issueClassID)});
            /*
            IF EXISTS(SELECT * FROM [Permissions] WHERE PermissionGroupID  = @PermissionGroupID
	        AND IssueClassID = @IssueClassID AND CanRequestInfoFromPrevDept = 1)
             */
            if (c.moveToFirst()) {
                if (details.getHistoryList() != null && details.getHistoryList().size() > 0) {
                    HistoryModel h = details.getHistoryList().get(details.getHistoryList().size() - 1);
                    addingWithDuplicateCheck(list, new SpinnerNameAndId((int) h.getDepartmentID(), h.getDepartmentDesc(), nameTable));
                }
            }
            c.close();
        }

        /*
        -- Check if the user can request info from the creator's department
        IF EXISTS(SELECT * FROM [Permissions] WHERE PermissionGroupID  = @PermissionGroupID
           AND IssueClassID = @IssueClassID AND CanRequestInfoFromCreatorDept = 1)
         BEGIN
            INSERT INTO @Departments
            SELECT CreateByDepartmentID FROM Issues WHERE IssueID = @IssueID
         END
         */
        String query2 = "SELECT * FROM Permissions WHERE PermissionGroupID  = ? " +
                "   AND IssueClassID = ? AND CanRequestInfoFromPrevDept = 'true'";
        c = issutraxdb.rawQuery(query2, new String[]{String.valueOf(permGroupID), String.valueOf(issueClassID)});
        if (c.moveToFirst()) {
            c.close();
            c = issutraxdb.rawQuery("SELECT CreateByDepartmentID, d.DepartmentDesc as DepartmentDesc FROM Issues " +
                    " LEFT JOIN departments as d ON d.DepartmentID = CreateByDepartmentID " +
                    " WHERE IssueID = ?", new String[]{String.valueOf(issueID)});
            if (c.moveToFirst()) {
                addingWithDuplicateCheck(list, new SpinnerNameAndId(c.getInt(c.getColumnIndex("CreateByDepartmentID")), c.getString(c.getColumnIndex("DepartmentDesc")), nameTable));
            }
        }
        c.close();

        return list;
    }

    public void createViews() {
//        try {
//            Crashlytics.log("DROP VIEW IF EXISTS view_action_alert");
//            issutraxdb.execSQL("DROP VIEW IF EXISTS view_action_alert");
//            Crashlytics.log("CREATE VIEW view_action_alert");
//            issutraxdb.execSQL("CREATE  VIEW view_action_alert AS " +
//                    "Select  ActionCode as AlertID, itr.ActionID as ActionID, iss.IssueID as IssueID  FROM issues AS iss LEFT join issuetracks as itr " +
//                    "ON  iss.IssueID = itr.IssueID " +
//                    "INNER JOIN actions on actions.ActionID = itr.ActionID " +
//                    "where ActionCode = 'PreAlert' OR ActionCode = 'Alert' group by (iss.IssueID)");
//
//            Crashlytics.log("DROP VIEW view_issues");
//            issutraxdb.execSQL("DROP VIEW IF EXISTS view_issues");
//
//            Crashlytics.log("CREATE VIEW view_issues");
//            issutraxdb.execSQL("CREATE VIEW view_issues AS " +
//                    "select distinct iss.IssueID as _id,iss.StatusID as StatusID, " +
//                    "iss.ZoneID as ZoneID, iss.IssueTypeID as TypeID, iss.DeckID as DeckID, iss.FireZoneID as FireZoneID,iss.LocationID as LocationID, iss.CreateDate as CreateDate,\n" +
//                    "iss.OpenedOnDevice as OpenedOnDevice, t.Notes as Notes, itype.IssueTypeDesc as IssueTypeDesc, " +
//                    "stat.StatusDesc as StatusDesc, iss.CreateByDepartmentID as DepartmentID, " +
//                    "loc.LocationDesc as LocationDesc, " +
//                    "iss.DeviceFavorite as DeviceFavorite, " +
//                    "pr.PriorityLevel as PriorityLevel, " +
//                    "pr.PriorityID as PriorityID, " +
//                    "AlertID, " +
//                    "itracks.AssigneeUserID as AssigneeUserID " +
//                    "FROM issues AS iss " +
//                    "LEFT JOIN statuses AS stat ON iss.StatusID = stat.StatusID " +
//                    "LEFT JOIN locations AS loc ON iss.LocationID = loc.LocationID " +
//                    "LEFT JOIN issuetypes AS itype ON iss.IssueTypeID = itype.IssueTypeID " +
//                    "INNER JOIN issuetracks AS itracks ON (iss.IssueID = itracks.IssueID) " +
//                    "LEFT join view_action_alert as alert ON  (iss.IssueID = alert.IssueID) " +
//                    "inner join (select issueid, MAX(LastUpdateDate) as lastupdatedate from issuetracks " +
//                    "group by issueid) as t1 on t1.IssueID = itracks.IssueID and " +
//                    "t1.lastupdatedate = itracks.LastUpdateDate " +
//                    "LEFT JOIN priorities AS pr ON itracks.PriorityID = pr.PriorityID " +
//                    "inner join (select   notes, issueid from issuetracks  where actionid = 1) as t " +
//                    "on t.IssueID = iss.issueid");
//            Crashlytics.log("finish CREATE VIEW");
//        } catch (Exception e) {
//            e.printStackTrace();
//            Crashlytics.logException(e);
//        }
    }

    public void deleteCloses() {
        try {
            issutraxdb.delete("fileattachments", "IssueTrackID = (SELECT IssueTrackID from issuetracks " +
                            "where IssueID = (SELECT IssueID from issues where StatusID = (SELECT StatusID from statuses where StatusCode = ?))) AND isDirty = ?",
                    new String[]{"Closed", "0"});
            issutraxdb.delete("issuetracks", "IssueID = (SELECT IssueID from issues where StatusID = (SELECT StatusID from statuses where StatusCode = ?))  AND isDirty = ?",
                    new String[]{"Closed", "0"});
            issutraxdb.delete("issues", "StatusID = (SELECT StatusID from statuses where StatusCode = ?)  AND isDirty = ?", new String[]{"Closed", "0"});
            //TODO remove all close issue*/
        } catch (Exception e) {
            e.printStackTrace();
//            Crashlytics.logException(e);
        }
    }

    public static class DatabaseHelper extends OrmLiteSqliteOpenHelper {


        DatabaseHelper(Context context, String dbFilePath, int dbVersion) {
            super(context, dbFilePath, null, dbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
            try {
            } catch (Exception e) {
                Log.e("Inspections", "Error onCreate: " + e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
            Log.i("Inspections", "onUpgrade from " + oldVersion + " to " + newVersion);
        }

    }
}
