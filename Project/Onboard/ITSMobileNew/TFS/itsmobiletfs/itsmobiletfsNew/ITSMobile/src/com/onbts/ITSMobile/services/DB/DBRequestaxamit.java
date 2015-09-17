package com.onbts.ITSMobile.services.DB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.onbts.ITSMobile.model.ActionIssue;
import com.onbts.ITSMobile.model.DetailedIssue;
import com.onbts.ITSMobile.model.UserModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
@Deprecated
public class DBRequestaxamit {
    /**
     * See GetActionsForIssue.sql
     *
     * @param user
     * @param issueId
     * @param statusId    - status of issue
     * @param allowAttach - boolean flag allowAttach (remove from request to top level)
     * @param db
     * @return
     */
    public static List<ActionIssue> getActionForIssue(UserModel user, String issueId, long statusId, boolean allowAttach, SQLiteDatabase db) {
        long time = System.currentTimeMillis();
        Log.e("DBRequest", "userId = " + user.getId()
                + " issueId=" + issueId + "; statusId = " + statusId);
        if (db == null || !db.isOpen())
            return null;
        long actionId, prevActionId, nextActionId;
        boolean visibleToCreatorDepartment, visibleToAssignee, actionVisible;
        long lastIssueTrackID, issueTrackIDForPrevAction = -1, issueTrackIDForNextAction = -1, issueTrackIDForThisAction = -1, assignedDepartmentID, assigneeUserID;
        boolean assigneeAnyUser;
        long issueClassID;
        boolean canManage = false;
        // SELECT * FROM IssueTracks WHERE IssueID = 53086 ORDER BY
        // LastUpdateDate DESC LIMIT 1;
        if (db == null || !db.isOpen())
            return null;
        Cursor c = db.query(
                "IssueTracks",
                new String[]{"DepartmentID", "AssigneeUserID",
                        "AssigneeAnyUser"}, "IssueID = ?",
                new String[]{issueId}, null, null,
                "LastUpdateDate DESC", "1"
        );
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }

        assignedDepartmentID = c.getLong(c.getColumnIndex("DepartmentID"));
        assigneeUserID = c.getLong(c.getColumnIndex("AssigneeUserID"));
        assigneeAnyUser = c.getString(c.getColumnIndex("AssigneeAnyUser")).equals("true");
        c.close();
        Log.e("DBRequest", "assignedDepartmentID = " + assignedDepartmentID
                + " assigneeUserID=" + assigneeUserID + "; assigneeAnyUser = "
                + assigneeAnyUser);
        Log.e("DBRequest", "userDepartmentID = " + user.getDepartmentId());

        // SELECT IssueClassID FROM IssueGroups
        // WHERE IssueGroupID = (SELECT IssueGroupID FROM IssueTypes WHERE
        // IssueTypeID = (SELECT IssueTypeID FROM Issues WHERE IssueID =
        // 53086));
        if (db == null || !db.isOpen())
            return null;
        c = db
                .rawQuery(
                        "SELECT IssueClassID FROM IssueGroups WHERE IssueGroupID = "
                                + "(SELECT IssueGroupID FROM IssueTypes WHERE IssueTypeID = "
                                + "(SELECT IssueTypeID FROM Issues WHERE IssueID = ?))",
                        new String[]{issueId}
                );
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }
        issueClassID = c.getInt(0);
        c.close();
        Log.e("DBRequest", "issueClassID = " + issueClassID);

 /*       IF @AssignedDepartmentID <> @UserDepartmentID

*/
        if (assignedDepartmentID != user.getDepartmentId()) {
 /*           BEGIN
                -- Check if this user has permission to manage this issue
            IF EXISTS(SELECT DepartmentID
            FROM Permissions_Departments a INNER JOIN [Permissions] b ON a.PermissionID = b.PermissionID
            WHERE b.PermissionGroupID = dbo.GetUserPermissionGroup(@UserID)
            AND DepartmentID = @AssignedDepartmentID AND IssueClassID = @IssueClassID AND CanManage = 1)
            SET @CanManage = 1
        END*/
//            -- Check if the user has a permission group assigned to it.
            if (db == null || !db.isOpen())
                return null;
            c = db
                    .rawQuery(
                            "SELECT PermissionGroupID FROM PermissionGroups_Users WHERE UserID = ?",
                            new String[]{String.valueOf(user.getId())});
            if (c.getCount() > 0)
                canManage = true;
            c.close();
        }

        // Corrected by onboard
        /*
//            -- Get the permission group for the user's dept.
            if (!canManage) {
                if (db == null || !db.isOpen())
                    return null;
                c = db
                        .rawQuery(
                                " SELECT PermissionGroupID FROM PermissionGroups_Departments\n" +
                                        "            WHERE DepartmentID = ?",
                                new String[]{String.valueOf(user.getDepartmentId())}
                        );
                if (c.getCount() > 0)
                    canManage = true;
                c.close();
            }

        } else {
            //nothing
        }
        */
        Log.e("DBRequest", "canManage = " + canManage);

        if (db == null || !db.isOpen())
            return null;
        c = db
                .rawQuery(
                        "SELECT DISTINCT ActionsByStatus.ActionID, PreviousActionID, NextActionID, VisibleToCreatorDepartment, VisibleToAssignee, ActionDesc, KeepAssignee" +
                                " FROM Actions INNER JOIN ActionsByStatus ON Actions.ActionID = ActionsByStatus.ActionID INNER JOIN ActionWorkflows ON ActionWorkflows.ActionID = ActionsByStatus.ActionID INNER JOIN WorkflowAssociations ON WorkflowAssociations.ActionWorkflowID = ActionWorkflows.ActionWorkflowID"
                                + " WHERE ActionsByStatus.StatusID = ? AND Actions.Active = 'true' AND Actions.InternalToSystem = 'false' AND ParentID = 0 AND WorkflowAssociations.WorkflowID = (SELECT WorkflowID FROM SysParms)/* corrected by onboard */ order by actions.Displayorder",
                        new String[]{String.valueOf(statusId)}
                );
        Log.e("DBRequest", "DISTINCT = " + c.getCount());
        List<ActionIssue> issues = null;
        if (c.moveToFirst()) {
            issues = new ArrayList<ActionIssue>();
            do {
                actionId = c.getLong(c.getColumnIndex("ActionID"));
                prevActionId = c.getLong(c.getColumnIndex("PreviousActionID"));
                nextActionId = c.getLong(c.getColumnIndex("NextActionID"));
                visibleToAssignee = c.getString(c.getColumnIndex("VisibleToAssignee")).equals("true");
                visibleToCreatorDepartment = c.getString(c.getColumnIndex("VisibleToCreatorDepartment")).equals("true");
                actionVisible = false;
                if (db == null || !db.isOpen())
                    return null;
                Cursor ca = db.rawQuery("SELECT IssueTrackID FROM IssueTracks WHERE IssueID = ? AND ActionID = ? ORDER BY LastUpdateDate DESC LIMIT 1", new String[]{issueId, String.valueOf(prevActionId)});
                if (ca.moveToFirst()) {
                    issueTrackIDForPrevAction = ca.getLong(0);
                } else {
                    issueTrackIDForPrevAction = 0;
                }
                ca.close();
                if (db == null || !db.isOpen())
                    return null;
                ca = db.rawQuery("SELECT IssueTrackID FROM IssueTracks WHERE IssueID = ? AND ActionID = ? ORDER BY LastUpdateDate DESC LIMIT 1", new String[]{issueId, String.valueOf(nextActionId)});
                if (ca.moveToFirst()) {
                    issueTrackIDForNextAction = ca.getLong(0);
                }
                ca.close();
                if (db == null || !db.isOpen())
                    return null;
                ca = db.rawQuery("SELECT IssueTrackID FROM IssueTracks WHERE IssueID = ? AND ActionID = ? ORDER BY LastUpdateDate DESC LIMIT 1", new String[]{issueId, String.valueOf(actionId)});
                if (ca.moveToFirst()) {
                    issueTrackIDForThisAction = ca.getLong(0);
                } else {
                    issueTrackIDForThisAction = 0;
                }
                ca.close();

                if (issueTrackIDForPrevAction > 0) {
//                    -- Check if this prev action had a next action. If it did, then if that next action
//                            -- happened afterwards, this prev action is not considered since it can now be selected again
                    long tempIssueTrackID = 0;
                    if (db == null || !db.isOpen())
                        return null;
                    ca = db.rawQuery("SELECT IssueTrackID FROM IssueTracks WHERE IssueID = ? AND ActionID = (SELECT NextActionID FROM Actions WHERE ActionID = ?) ORDER BY LastUpdateDate DESC LIMIT 1", new String[]{issueId, String.valueOf(prevActionId)});
                    if (ca.moveToFirst()) {
                        tempIssueTrackID = ca.getLong(0);
                        issueTrackIDForPrevAction = issueTrackIDForPrevAction < tempIssueTrackID ? 0 : issueTrackIDForPrevAction;
                    } else {

                    }
                    ca.close();

                }


                if (visibleToAssignee)
                    if (canManage)
                        actionVisible = true;
                    else if (assigneeUserID == user.getId()) {
                        actionVisible = true;
                    }
                    //Corrected by OnboarD
                    else if (assigneeAnyUser) {
                        if (assignedDepartmentID == user.getDepartmentId())
                            actionVisible = true;
                        //Corrected by OnboarD
                    }

                /*BEGIN
                IF @CanManage = 1
                SET @ActionVisible = 1
                        -- Check that this user is the assignee
                ELSE IF @AssigneeUserID = @UserID
                SET @ActionVisible = 1
                        -- If AssigneeAnyUser = 1, check that this user belongs to the assigned department
                ELSE IF @AssigneeAnyUser = 1
                BEGIN
                IF @AssignedDepartmentID = @UserDepartmentID
                SET @ActionVisible = 1
                END
                END*/

                if (!actionVisible) {
                    if (db == null || !db.isOpen())
                        return null;
                    ca = db.rawQuery("SELECT * FROM ActionVisibilityByGeneralGroup INNER JOIN GeneralGroups_Users " +
                            "ON ActionVisibilityByGeneralGroup.GeneralGroupID = GeneralGroups_Users.GeneralGroupID " +
                            "WHERE ActionID = ? AND UserID = ? " +
                            "UNION SELECT * FROM ActionVisibilityByGeneralGroup INNER JOIN GeneralGroups_Departments " +
                            "ON ActionVisibilityByGeneralGroup.GeneralGroupID = GeneralGroups_Departments.GeneralGroupID " +
                            "WHERE ActionID = ? AND DepartmentID = ?;", new String[]{String.valueOf(actionId), String.valueOf(user.getId()),
                            String.valueOf(actionId), String.valueOf(user.getDepartmentId())});
                    if (ca.moveToFirst()) {
                        if (assignedDepartmentID == user.getDepartmentId() || canManage)
                            actionVisible = true;
                    }
                    ca.close();
                }
/*
                IF @ActionVisible = 0
                BEGIN
                        -- Check if this user is part of the general groups that this action is visible to.
                IF EXISTS(SELECT * FROM ActionVisibilityByGeneralGroup INNER JOIN GeneralGroups_Users
                ON ActionVisibilityByGeneralGroup.GeneralGroupID = GeneralGroups_Users.GeneralGroupID
                WHERE ActionID = @ActionID AND UserID = @UserID
                        UNION SELECT * FROM ActionVisibilityByGeneralGroup INNER JOIN GeneralGroups_Departments
                ON ActionVisibilityByGeneralGroup.GeneralGroupID = GeneralGroups_Departments.GeneralGroupID
                WHERE ActionID = @ActionID AND DepartmentID = @UserDepartmentID)
                BEGIN
                        -- Check that this user belongs to the assigned department
                IF @AssignedDepartmentID = @UserDepartmentID OR @CanManage = 1
                SET @ActionVisible = 1
                END
                 END
*/


                if (!actionVisible) {
                    if (db == null || !db.isOpen())
                        return null;
                    ca = db.rawQuery("SELECT VisibleToUserGroupThatExecutedPreviousAction FROM Actions WHERE ActionID = ?", new String[]{String.valueOf(actionId)});
                    if (ca.moveToFirst() && ca.getString(0).equals("true") && prevActionId > 0) {
//                        -- Check that this previous action was in this issue
//                        -- and that this action in the cursor did not happen afterwards
//                        -- and that the next action did not happen after this action
                        if (issueTrackIDForPrevAction > 0 && (issueTrackIDForThisAction == 0 || issueTrackIDForPrevAction < issueTrackIDForThisAction) &&
                                (issueTrackIDForNextAction <= 0 || issueTrackIDForThisAction > issueTrackIDForNextAction)) {
//                            -- Check if this user is part of the general groups that executed the previous action.
                            if (db == null || !db.isOpen())
                                return null;
                            Cursor cb = db.rawQuery("SELECT * FROM ActionVisibilityByGeneralGroup INNER JOIN GeneralGroups_Users " +
                                    "ON ActionVisibilityByGeneralGroup.GeneralGroupID = GeneralGroups_Users.GeneralGroupID " +
                                    "WHERE ActionID = ? AND UserID = ? " +
                                    "UNION SELECT * FROM ActionVisibilityByGeneralGroup INNER JOIN GeneralGroups_Departments " +
                                    "ON ActionVisibilityByGeneralGroup.GeneralGroupID = GeneralGroups_Departments.GeneralGroupID " +
                                    "WHERE ActionID = ? AND DepartmentID = ?", new String[]{String.valueOf(actionId),
                                    String.valueOf(user.getId()), String.valueOf(prevActionId), String.valueOf(user.getDepartmentId())});
                            if (cb.moveToFirst())
                                actionVisible = true;
                            cb.close();
                        }

                    }
                    ca.close();
                }
/*
                IF @ActionVisible = 0
                BEGIN
                        -- Check if this action is visible to the user group that executed the previous action
                IF (SELECT VisibleToUserGroupThatExecutedPreviousAction FROM Actions WHERE ActionID = @ActionID) = 1
                AND @PreviousActionID IS NOT NULL
                BEGIN
                        -- Check that this previous action was in this issue
                        -- and that this action in the cursor did not happen afterwards
                -- and that the next action did not happen after this action
                IF @IssueTrackIDForPrevAction IS NOT NULL
                AND (@IssueTrackIDForThisAction = 0 OR @IssueTrackIDForPrevAction < @IssueTrackIDForThisAction)
                AND (@IssueTrackIDForNextAction IS NULL OR @IssueTrackIDForThisAction > @IssueTrackIDForNextAction)
                BEGIN
                        -- Check if this user is part of the general groups that executed the previous action.
                IF EXISTS(SELECT * FROM ActionVisibilityByGeneralGroup INNER JOIN GeneralGroups_Users
                ON ActionVisibilityByGeneralGroup.GeneralGroupID = GeneralGroups_Users.GeneralGroupID
                WHERE ActionID = @ActionID AND UserID = @UserID
                        UNION SELECT * FROM ActionVisibilityByGeneralGroup INNER JOIN GeneralGroups_Departments
                ON ActionVisibilityByGeneralGroup.GeneralGroupID = GeneralGroups_Departments.GeneralGroupID
                WHERE ActionID = @PreviousActionID AND DepartmentID = @UserDepartmentID)
                SET @ActionVisible = 1
                END
                        END
                END
*/

                //Corrected By OnboarD
                if (!actionVisible) {

                    if (db == null || !db.isOpen())
                        return null;
                    ca = db.rawQuery("SELECT * FROM ActionVisibilityByDepartment INNER JOIN UserDepartments " +
                            "ON ActionVisibilityByDepartment.DepartmentID = UserDepartments.DepartmentID " +
                            "WHERE ActionID = ? AND UserDepartments.UserID = ?", new String[]{String.valueOf(actionId), String.valueOf(user.getId())});
                    if (ca.moveToFirst()) {
//                        -- Check if any of those departments has the VisibleOnlyIfCurrentlyAssigned field to false
                        if (db == null || !db.isOpen())
                            return null;
                        Cursor cb = db.rawQuery("SELECT * FROM ActionVisibilityByDepartment INNER JOIN UserDepartments " +
                                "ON ActionVisibilityByDepartment.DepartmentID = UserDepartments.DepartmentID " +
                                "WHERE ActionID = ? AND UserDepartments.UserID = ? AND " +
                                "ActionVisibilityByDepartment.VisibleOnlyIfCurrentlyAssigned = 0", new String[]{String.valueOf(actionId), String.valueOf(user.getId())});
                        if (cb.moveToFirst())
                            actionVisible = true;
                        else {

                            if (assignedDepartmentID == user.getDepartmentId() || canManage)
                                actionVisible = true;
                        }
                        cb.close();
                    }
                    ca.close();
                }

                //Corrected By OnboarD
/*
 * 
                IF @ActionVisible = 0
                BEGIN
                        -- Check if this user is part of a department that this action is visible to.
                IF EXISTS(SELECT * FROM ActionVisibilityByDepartment INNER JOIN UserDepartments
                ON ActionVisibilityByDepartment.DepartmentID = UserDepartments.DepartmentID
                WHERE ActionID = @ActionID AND UserDepartments.UserID = @UserID)
                BEGIN
                        -- Check if any of those departments has the VisibleOnlyIfCurrentlyAssigned field to false
                IF EXISTS(SELECT * FROM ActionVisibilityByDepartment INNER JOIN UserDepartments
                ON ActionVisibilityByDepartment.DepartmentID = UserDepartments.DepartmentID
                WHERE ActionID = @ActionID AND UserDepartments.UserID = @UserID AND
                ActionVisibilityByDepartment.VisibleOnlyIfCurrentlyAssigned = 0)
                SET @ActionVisible = 1
                ELSE
                        BEGIN
                -- Check that this user belongs to the assigned department
                IF @AssignedDepartmentID = @UserDepartmentID OR @CanManage = 1
                SET @ActionVisible = 1
                END
                        END
                END
*/


                if (!actionVisible) {
                    if (visibleToCreatorDepartment) {
//                        -- Check that this user belongs to the creator's department
                        if (db == null || !db.isOpen())
                            return null;
                        ca = db.rawQuery("SELECT * FROM UserDepartments WHERE UserID = ? " +
                                "AND (DepartmentID = (SELECT CreateByDepartmentID FROM Issues WHERE IssueID = ?) " +
                                "OR DepartmentID IN (SELECT DepartmentID FROM UserDepartments WHERE UserID = " +
                                "(SELECT CreateByUserID FROM Issues WHERE IssueID = ?)))", new String[]{String.valueOf(user.getId()), issueId, issueId});
                        if (ca.moveToFirst())
                            actionVisible = true;
                        ca.close();
                    }
                }
/*
                IF @ActionVisible = 0
                BEGIN
                IF @VisibleToCreatorDepartment = 1
                BEGIN
                        -- Check that this user belongs to the creator's department
                IF EXISTS(SELECT * FROM UserDepartments WHERE UserID = @UserID
                        AND (DepartmentID = (SELECT CreateByDepartmentID FROM Issues WHERE IssueID = @IssueID)
                OR DepartmentID IN (SELECT DepartmentID FROM UserDepartments WHERE UserID =
                        (SELECT CreateByUserID FROM Issues WHERE IssueID = @IssueID))))
                SET @ActionVisible = 1
                END
                        END
*/
                if (!actionVisible) {
//                    -- Check if this user is part of the users that this action is visible to.
                    if (db == null || !db.isOpen())
                        return null;
                    ca = db.rawQuery("SELECT * FROM ActionVisibilityByUser WHERE ActionID = ? AND UserID = ?", new String[]{String.valueOf(actionId), String.valueOf(user.getId())});
                    if (ca.moveToFirst())
                        actionVisible = true;
                    ca.close();
                }
                /*
                IF @ActionVisible = 0
                        -- Check if this user is part of the users that this action is visible to.
                BEGIN
                IF EXISTS(SELECT * FROM ActionVisibilityByUser WHERE ActionID = @ActionID AND UserID = @UserID)
                SET @ActionVisible = 1
                END*/

                if (actionVisible) {
//                    -- Get the ActionIDs for Assign and Reassign
//                    DECLARE @AssignActionID int, @ReassignActionID int
//                    SELECT @AssignActionID = ActionID FROM Actions WHERE ActionCode = 'Assign'
//                    SELECT @ReassignActionID = ActionID FROM Actions WHERE ActionCode = 'Reassign'
                    long assignActionID = 0, reassignActionID = 0;
                    if (db == null || !db.isOpen())
                        return null;
                    ca = db.rawQuery("SELECT ActionID FROM Actions WHERE ActionCode = ?", new String[]{"Assign"});
                    if (ca.moveToFirst())
                        assignActionID = ca.getLong(0);
                    ca.close();
                    if (db == null || !db.isOpen())
                        return null;
                    ca = db.rawQuery("SELECT ActionID FROM Actions WHERE ActionCode = ?", new String[]{"Reassign"});
                    if (ca.moveToFirst())
                        reassignActionID = ca.getLong(0);
                    ca.close();

                    if (actionId == assignActionID || actionId == reassignActionID) {
/*                        IF @ActionID = @AssignActionID
                                BEGIN
                                -- If a user has been assigned then don't show the Assign action
                        IF @AssigneeUserID IS NOT NULL OR @AssigneeAnyUser = 1
                        SET @ActionVisible = 0
                        END
                        ELSE IF @ActionID = @ReassignActionID
                                BEGIN
                                -- If no user has been assigned then don't show the Reassign action
                        IF @AssigneeUserID IS NULL AND @AssigneeAnyUser = 0
                        SET @ActionVisible = 0
                        ELSE
                                -- If the user doesn't belong to the assigned department then don't show the Reassign action
                                BEGIN
                        IF @AssignedDepartmentID <> @UserDepartmentID AND @CanManage = 0
                        SET @ActionVisible = 0
                        END
                                END*/

                        if (actionId == assignActionID) {
//                            -- If a user has been assigned then don't show the Assign action
                            if (assigneeAnyUser || assigneeUserID > 0) {
                                actionVisible = false;
                            }
                        } else if (actionId == reassignActionID) {
//                            -- If no user has been assigned then don't show the Reassign action
                            if (!assigneeAnyUser || assigneeUserID <= 0) {
                                actionVisible = false;
                            }
                        } else {
//                            -- If the user doesn't belong to the assigned department then don't show the Reassign action
                            if (assignedDepartmentID != user.getDepartmentId() && canManage) {
                                actionVisible = false;
                            }
                        }

                    } else {
/*                        BEGIN
                                --- Check if there needs to be a Previous ActionID for this action
                        IF NOT @PreviousActionID IS NULL
                        BEGIN
                                -- Check if this previous action happened
                        IF @IssueTrackIDForPrevAction IS NULL
                        SET @ActionVisible = 0
                        END

                                --- Check if there is a Next ActionID for this action
                        IF @ActionVisible = 1 AND NOT @NextActionID IS NULL
                        BEGIN
                                -- Check if the next action happened after this action
                        IF NOT @IssueTrackIDForNextAction IS NULL
                        BEGIN
                        IF  @IssueTrackIDForThisAction > @IssueTrackIDForNextAction
                        SET @ActionVisible = 0
                        END
                                ELSE
                        BEGIN
                        IF @IssueTrackIDForThisAction <> 0
                        SET @ActionVisible = 0
                        END
                                END
                        END*/

//                        --- Check if there needs to be a Previous ActionID for this action
                        if (prevActionId > 0) {
//                            -- Check if this previous action happened
                            if (issueTrackIDForPrevAction <= 0) {
                                actionVisible = false;
                            }
//                            --- Check if there is a Next ActionID for this action
                            if (actionVisible && nextActionId <= 0) {
//                                -- Check if the next action happened after this action
                                if (issueTrackIDForNextAction <= 0) {
                                    if (issueTrackIDForThisAction > issueTrackIDForPrevAction) {
                                        actionVisible = false;
                                    }
                                } else {
                                    if (issueTrackIDForThisAction != 0) {
                                        actionVisible = false;
                                    }
                                }
                            }
                        }
                    }
                }
//                if (actionVisible)
//                    issues.add(new ActionIssue(c.getString(c.getColumnIndex("ActionDesc")),
//                            c.getLong(c.getColumnIndex("ActionID")),
//                            prevActionID, nextActionID, c.getString(c.getColumnIndex("KeepAssignee")).equals("true")));
            } while (c.moveToNext());
        }
        c.close();

        ActionIssue issue = null;
        for (ActionIssue actionIssue : issues) {
            if (db == null || !db.isOpen())
                return null;
            Cursor cursor = db.rawQuery("SELECT ActionID FROM Actions WHERE ActionID = ? AND SetsOtherActionsToNotVisible = ? LIMIT 1", new String[]{String.valueOf(actionIssue.getId()), "true"});
            if (cursor.moveToFirst()) {
                issue = actionIssue;
                cursor.close();
                break;
            }
            cursor.close();
        }


        Iterator<ActionIssue> iterator = issues.iterator();
        while (iterator.hasNext()) {
            ActionIssue issue1 = iterator.next();
            if ((issue1.getId() == 41 && !allowAttach) || (issue != null && issue1.getId() != issue.getId())) {
                iterator.remove();
            }
        }
/*        DECLARE @ExclusiveActionID int
        SET @ExclusiveActionID = (SELECT TOP 1 a.ActionID FROM #TempActions a INNER JOIN Actions b ON a.ActionID = b.ActionID WHERE SetsOtherActionsToNotVisible = 1)
        IF @ExclusiveActionID IS NOT NULL
        DELETE #TempActions WHERE ActionID <> @ExclusiveActionID

        DECLARE @AttachFileActionID int
        SELECT @AttachFileActionID = a.ActionID FROM #TempActions a INNER JOIN Actions b ON a.ActionID = b.ActionID WHERE ActionCode = 'AttachFile'
        IF @AttachFileActionID IS NOT NULL
        BEGIN
	    -- Check if file attachments are allowed
	        IF (SELECT AllowFileAttachments FROM SysParms) = 0
		    DELETE #TempActions WHERE ActionID = @AttachFileActionID
 END


        */

        Log.e("time", "time = " + (System.currentTimeMillis() - time) + " id = " + issueId + "; size = " + (issues != null ? issues.size() : ""));
        return issues;
    }

    /**
     * @param detailedIssue
     * @param actionID
     * @param concurrencyID
     * @param priorityID
     * @param assignedDepartmentID
     * @param assigneeUserID       - not necessary
     * @param assigneeAnyUser      - not necessary
     * @param notes
     * @param hoursWorked          - not necessary
     * @param billable             - not necessary
     * @param invoiceNumber        - not necessary
     * @param compensationID       - not necessary
     * @param monetaryValue        - not necessary
     * @param monetaryValueByUser  - not necessary
     * @param causeID              - not necessary
     * @param partsOrderID         - not necessary
     * @param lastUpdateUserID
     * @param UID                  - not necessary
     * @param updateByIVR          - not necessary
     * @return - if succes return true
     */
    public static boolean insertIssueTrack(UserModel user, DetailedIssue detailedIssue, int actionID, int concurrencyID, int priorityID, int assignedDepartmentID,
                                           int assigneeUserID, boolean assigneeAnyUser, String notes, int hoursWorked,
                                           boolean billable, String invoiceNumber, int compensationID,
                                           float monetaryValue, boolean monetaryValueByUser, int causeID, String partsOrderID,
                                           long lastUpdateUserID, String UID, boolean updateByIVR, SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put("IssueID", detailedIssue.getId());
        cv.put("ActionID", actionID);
        cv.put("PriorityID", priorityID);
        cv.put("DepartmentID", "");
        cv.put("AssigneeUserID", "");
        cv.put("AssigneeAnyUser", "");
        cv.put("PrevAssigneeUserID", "");
        cv.put("Notes", notes);
        cv.put("HoursWorked", hoursWorked);
        cv.put("Billable", "");
        cv.put("InvoiceNumber", "");
        cv.put("CompensationID", "");
        cv.put("MonetaryValue", "");
        cv.put("MonetaryValueByUser", "");
        cv.put("CauseID", "");
        cv.put("PartsOrderID", "");
        cv.put("EmployeeFirstName", "");
        cv.put("EmployeeLastName", "");
        cv.put("CorpEmployeeID", "");
        cv.put("UpdateByIVR", "");
        cv.put("LastUpdateDate", System.currentTimeMillis());
        cv.put("LastUpdateUserID", "");
        cv.put("UpdateBySystem", "");


//        INSERT INTO IssueTracks (IssueID, ActionID, PriorityID, DepartmentID, AssigneeUserID, AssigneeAnyUser, PrevAssigneeUserID, Notes, HoursWorked, Billable,
//                InvoiceNumber, CompensationID, MonetaryValue, MonetaryValueByUser, CauseID, PartsOrderID, EmployeeFirstName, EmployeeLastName,
//                CorpEmployeeID, UpdateByIVR, LastUpdateDate, LastUpdateUserID, UpdateBySystem)
//        VALUES (@IssueID, @ActionID, @PriorityID, @AssignedDepartmentID, @AssigneeUserID, @AssigneeAnyUser, @AssigneeUserIDToKeep, @Notes, @HoursWorked, @Billable,
//        @InvoiceNumber, @CompensationID, @MonetaryValue, @MonetaryValueByUser, @CauseID, @PartsOrderID, @EmployeeFirstName, @EmployeeLastName,
//        @CorpEmployeeID, @UpdateByIVR, @CurrentDate, @LastUpdateUserID, @UpdateBySystem)

        long id = db.insert("IssueTracks", null, cv);
        return id > 0;
    }
}

