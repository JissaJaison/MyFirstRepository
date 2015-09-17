package com.onbts.ITSMobile.services.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.onbts.ITSMobile.model.ActionIssue;
import com.onbts.ITSMobile.model.DetailedIssue;
import com.onbts.ITSMobile.model.FileModel;
import com.onbts.ITSMobile.model.HistoryModel;
import com.onbts.ITSMobile.model.ReturnDateWithActionDialog;
import com.onbts.ITSMobile.model.UserModel;
import com.onbts.ITSMobile.util.Files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class DBRequest {
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
    public static List<ActionIssue> getActionForIssue(UserModel user, long issueId, long statusId, boolean allowAttach, SQLiteDatabase db) {
        long time = System.currentTimeMillis();
        if (db == null || !db.isOpen()) return null;
        long actionId, prevActionId, nextActionId;
        boolean visibleToCreatorDepartment, visibleToAssignee, actionVisible;
        long lastIssueTrackID, issueTrackIDForPrevAction = -1, issueTrackIDForNextAction = -1, issueTrackIDForThisAction = -1, assignedDepartmentID, assigneeUserID;
        boolean assigneeAnyUser;
        long issueClassID;
        boolean canManage = false;
        // SELECT * FROM IssueTracks WHERE IssueID = 53086 ORDER BY
        // LastUpdateDate DESC LIMIT 1;
        if (db == null || !db.isOpen()) return null;
        Cursor c = db.query("IssueTracks", new String[]{"DepartmentID", "AssigneeUserID", "AssigneeAnyUser"},
                "IssueID = ?", new String[]{String.valueOf(issueId)}, null, null, "LastUpdateDate DESC", "1");
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }

        assignedDepartmentID = c.getLong(c.getColumnIndex("DepartmentID"));
        assigneeUserID = c.getLong(c.getColumnIndex("AssigneeUserID"));
        assigneeAnyUser = c.getString(c.getColumnIndex("AssigneeAnyUser")).equals("true");
        c.close();

        // SELECT IssueClassID FROM IssueGroups
        // WHERE IssueGroupID = (SELECT IssueGroupID FROM IssueTypes WHERE
        // IssueTypeID = (SELECT IssueTypeID FROM Issues WHERE IssueID =
        // 53086));
        if (db == null || !db.isOpen()) return null;
        c = db.rawQuery("SELECT IssueClassID FROM IssueGroups WHERE IssueGroupID = " + "(SELECT IssueGroupID FROM IssueTypes WHERE IssueTypeID = " + "(SELECT IssueTypeID FROM Issues WHERE IssueID = ?))", new String[]{String.valueOf(issueId)});
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }
        issueClassID = c.getInt(0);
        c.close();

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
            if (db == null || !db.isOpen()) return null;
            c = db.rawQuery("SELECT PermissionGroupID FROM PermissionGroups_Users WHERE UserID = ?", new String[]{String.valueOf(user.getId())});
            if (c.getCount() > 0) canManage = true;
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

        if (db == null || !db.isOpen()) return null;
        c = db.rawQuery("SELECT DISTINCT ActionsByStatus.ActionID, PreviousActionID, NextActionID, " +
                        "VisibleToCreatorDepartment, VisibleToAssignee, ActionDesc, ActionCode, KeepAssignee" +
                        " FROM Actions INNER JOIN ActionsByStatus " +
                        "ON Actions.ActionID = ActionsByStatus.ActionID " +
                        "INNER JOIN ActionWorkflows ON ActionWorkflows.ActionID = ActionsByStatus.ActionID " +
                        "INNER JOIN WorkflowAssociations " +
                        "ON WorkflowAssociations.ActionWorkflowID = ActionWorkflows.ActionWorkflowID " +
                        "WHERE ActionsByStatus.StatusID = ? " +
                        "AND Actions.Active = 'true' " +
                        "AND Actions.VisibleOnITSMobile = 'true' " +
                        "AND Actions.InternalToSystem = 'false' " +
                        "AND ParentID = 0 " +
                        "AND WorkflowAssociations.WorkflowID = (SELECT WorkflowID FROM SysParms) " +
                        "order by actions.DisplayOrder", new String[]{String.valueOf(statusId)}
        );
        List<ActionIssue> issues = null;
        if (c.moveToFirst()) {
            issues = new ArrayList<ActionIssue>();
            do {
                String code = c.getString(c.getColumnIndex("ActionCode"));
                actionId = c.getLong(c.getColumnIndex("ActionID"));
                prevActionId = c.getLong(c.getColumnIndex("PreviousActionID"));
                nextActionId = c.getLong(c.getColumnIndex("NextActionID"));
                visibleToAssignee = c.getString(c.getColumnIndex("VisibleToAssignee")).equals("true");
                visibleToCreatorDepartment = c.getString(c.getColumnIndex("VisibleToCreatorDepartment")).equals("true");
                actionVisible = false;
                if (db == null || !db.isOpen()) return null;
                Cursor ca = db.rawQuery("SELECT IssueTrackID FROM IssueTracks WHERE IssueID = ? AND ActionID = ? ORDER BY LastUpdateDate DESC LIMIT 1", new String[]{String.valueOf(issueId), String.valueOf(prevActionId)});
                if (ca.moveToFirst()) {
                    issueTrackIDForPrevAction = ca.getLong(0);
                } else {
                    issueTrackIDForPrevAction = 0;
                }
                ca.close();
                if (db == null || !db.isOpen()) return null;
                ca = db.rawQuery("SELECT IssueTrackID FROM IssueTracks WHERE IssueID = ? AND ActionID = ? ORDER BY LastUpdateDate DESC LIMIT 1", new String[]{String.valueOf(issueId), String.valueOf(nextActionId)});
                if (ca.moveToFirst()) {
                    issueTrackIDForNextAction = ca.getLong(0);
                }
                ca.close();
                if (db == null || !db.isOpen()) return null;
                ca = db.rawQuery("SELECT IssueTrackID FROM IssueTracks WHERE IssueID = ? AND ActionID = ? ORDER BY LastUpdateDate DESC LIMIT 1", new String[]{String.valueOf(issueId), String.valueOf(actionId)});
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
                    if (db == null || !db.isOpen()) return null;
                    ca = db.rawQuery("SELECT IssueTrackID FROM IssueTracks WHERE IssueID = ? AND ActionID = (SELECT NextActionID FROM Actions WHERE ActionID = ?) ORDER BY LastUpdateDate DESC LIMIT 1", new String[]{String.valueOf(issueId), String.valueOf(prevActionId)});
                    if (ca.moveToFirst()) {
                        tempIssueTrackID = ca.getLong(0);
                        issueTrackIDForPrevAction = issueTrackIDForPrevAction < tempIssueTrackID ? 0 : issueTrackIDForPrevAction;
                    } else {

                    }
                    ca.close();

                }


                if (visibleToAssignee) if (canManage) actionVisible = true;
                else if (assigneeUserID == user.getId()) {
                    actionVisible = true;
                }
                //Corrected by OnboarD
                else if (assigneeAnyUser) {
                    if (assignedDepartmentID == user.getDepartmentId()) actionVisible = true;
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
                    if (db == null || !db.isOpen()) return null;
                    ca = db.rawQuery("SELECT * FROM ActionVisibilityByGeneralGroup INNER JOIN GeneralGroups_Users " +
                            "ON ActionVisibilityByGeneralGroup.GeneralGroupID = GeneralGroups_Users.GeneralGroupID " +
                            "WHERE ActionID = ? AND UserID = ? " +
                            "UNION SELECT * FROM ActionVisibilityByGeneralGroup INNER JOIN GeneralGroups_Departments " +
                            "ON ActionVisibilityByGeneralGroup.GeneralGroupID = GeneralGroups_Departments.GeneralGroupID " +
                            "WHERE ActionID = ? AND DepartmentID = ?;", new String[]{String.valueOf(actionId), String.valueOf(user.getId()), String.valueOf(actionId), String.valueOf(user.getDepartmentId())});
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
                    if (db == null || !db.isOpen()) return null;
                    ca = db.rawQuery("SELECT VisibleToUserGroupThatExecutedPreviousAction FROM Actions WHERE ActionID = ?", new String[]{String.valueOf(actionId)});
                    if (ca.moveToFirst() && ca.getString(0).equals("true") && prevActionId > 0) {
//                        -- Check that this previous action was in this issue
//                        -- and that this action in the cursor did not happen afterwards
//                        -- and that the next action did not happen after this action
                        if (issueTrackIDForPrevAction > 0 && (issueTrackIDForThisAction == 0 || issueTrackIDForPrevAction < issueTrackIDForThisAction) &&
                                (issueTrackIDForNextAction <= 0 || issueTrackIDForThisAction > issueTrackIDForNextAction)) {
//                            -- Check if this user is part of the general groups that executed the previous action.
                            if (db == null || !db.isOpen()) return null;
                            Cursor cb = db.rawQuery("SELECT * FROM ActionVisibilityByGeneralGroup INNER JOIN GeneralGroups_Users " +
                                    "ON ActionVisibilityByGeneralGroup.GeneralGroupID = GeneralGroups_Users.GeneralGroupID " +
                                    "WHERE ActionID = ? AND UserID = ? " +
                                    "UNION SELECT * FROM ActionVisibilityByGeneralGroup INNER JOIN GeneralGroups_Departments " +
                                    "ON ActionVisibilityByGeneralGroup.GeneralGroupID = GeneralGroups_Departments.GeneralGroupID " +
                                    "WHERE ActionID = ? AND DepartmentID = ?", new String[]{String.valueOf(actionId), String.valueOf(user.getId()), String.valueOf(prevActionId), String.valueOf(user.getDepartmentId())});
                            if (cb.moveToFirst()) actionVisible = true;
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

                    if (db == null || !db.isOpen()) return null;
                    ca = db.rawQuery("SELECT * FROM ActionVisibilityByDepartment INNER JOIN UserDepartments " +
                            "ON ActionVisibilityByDepartment.DepartmentID = UserDepartments.DepartmentID " +
                            "WHERE ActionID = ? AND UserDepartments.UserID = ?", new String[]{String.valueOf(actionId), String.valueOf(user.getId())});
                    if (ca.moveToFirst()) {
//                        -- Check if any of those departments has the VisibleOnlyIfCurrentlyAssigned field to false
                        if (db == null || !db.isOpen()) return null;
                        Cursor cb = db.rawQuery("SELECT * FROM ActionVisibilityByDepartment INNER JOIN UserDepartments " +
                                "ON ActionVisibilityByDepartment.DepartmentID = UserDepartments.DepartmentID " +
                                "WHERE ActionID = ? AND UserDepartments.UserID = ? AND " +
                                "ActionVisibilityByDepartment.VisibleOnlyIfCurrentlyAssigned = 0", new String[]{String.valueOf(actionId), String.valueOf(user.getId())});
                        if (cb.moveToFirst()) actionVisible = true;
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
                        if (db == null || !db.isOpen()) return null;
                        ca = db.rawQuery("SELECT * FROM UserDepartments WHERE UserID = ? " +
                                "AND (DepartmentID = (SELECT CreateByDepartmentID FROM Issues WHERE IssueID = ?) " +
                                "OR DepartmentID IN (SELECT DepartmentID FROM UserDepartments WHERE UserID = " +
                                "(SELECT CreateByUserID FROM Issues WHERE IssueID = ?)))", new String[]{String.valueOf(user.getId()), String.valueOf(issueId), String.valueOf(issueId)});
                        if (ca.moveToFirst()) actionVisible = true;
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
                    if (db == null || !db.isOpen()) return null;
                    ca = db.rawQuery("SELECT * FROM ActionVisibilityByUser WHERE ActionID = ? AND UserID = ?", new String[]{String.valueOf(actionId), String.valueOf(user.getId())});
                    if (ca.moveToFirst()) actionVisible = true;
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

//                    long assignActionID = 0, reassignActionID = 0;
//                    if (db == null || !db.isOpen()) return null;
//                    ca = db.rawQuery("SELECT ActionID FROM Actions WHERE ActionCode = ?", new String[]{"Assign"});
//                    if (ca.moveToFirst()) assignActionID = ca.getLong(0);
//                    ca.close();
//                    if (db == null || !db.isOpen()) return null;
//                    ca = db.rawQuery("SELECT ActionID FROM Actions WHERE ActionCode = ?", new String[]{"Reassign"});
//                    if (ca.moveToFirst()) reassignActionID = ca.getLong(0);
//                    ca.close();


//                    if (actionId == assignActionID || actionId == reassignActionID) {
                    if (code.equals("Assign") || code.equals("Reassign")) {
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

                        if (code.equals("Assign")) {
//                            -- If a user has been assigned then don't show the Assign action
                            if (assigneeAnyUser || assigneeUserID > 0) {
                                actionVisible = false;
                            }
                        } else if (code.equals("Reassign")) {
//                            -- If no user has been assigned then don't show the Reassign action
                            if (!assigneeAnyUser || assigneeUserID <= 0) {
                                actionVisible = false;
                            } else {
//                            -- If the user doesn't belong to the assigned department then don't show the Reassign action
                                if (assignedDepartmentID != user.getDepartmentId() && canManage) {
                                    actionVisible = false;
                                }
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
                        }  //added by onboard
//                            --- Check if there is a Next ActionID for this action
                        if (actionVisible && nextActionId != 0) {    // Not Null   Onboard
//                                -- Check if the next action happened after this action
                            if (issueTrackIDForNextAction != 0) {   // Not Null Onboard
                                if (issueTrackIDForThisAction > issueTrackIDForPrevAction) {
                                    actionVisible = false;
                                }
                            } else {
                                if (issueTrackIDForThisAction != 0) {
                                    actionVisible = false;
                                }
                            }
//                            }
                            // } removed by onboard
                        }
                    }
                }
                if (actionVisible)
                    issues.add(new ActionIssue(c.getString(c.getColumnIndex("ActionDesc")),
                            actionId, prevActionId, nextActionId,
                            code, c.getString(c.getColumnIndex("KeepAssignee")).equals("true")));
            } while (c.moveToNext());
        }
        c.close();

        ActionIssue issue = null;
        for (ActionIssue actionIssue : issues) {
            if (db == null || !db.isOpen()) return null;
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
            if ((issue1.getCode().equals("AttachFile") && !allowAttach) || (issue != null && issue1.getId() != issue
                    .getId())) {
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
     * какая-то часть мистики,
     * часть данных берем из UserModel, DetailedIssue,ActionIssue.
     *
     * @return - if succes return true
     */
    public static String insertIssueTrack(UserModel user, DetailedIssue detailedIssue,
                                          List<ReturnDateWithActionDialog> data, long idAction, String actionCode, long prevActionID,
                                          long nextActionID, boolean keep,
                                          SQLiteDatabase db, Context context) {

        boolean billable = false;
        String notes = null;

//        SELECT @WorkflowID = WorkflowID FROM SysParms
        Cursor c = db.rawQuery("SELECT WorkflowID FROM SysParms", null);
        long workflowID = c.moveToFirst() ? c.getLong(0) : -1;
        if (workflowID < 0) {
            c.close();
            return "Error! DB - damage!";
        }
        c.close();
/*
    not need on mobile

        -- Check if this issue was updated by another user while this issue was being viewed.
        DECLARE @TempConcurrencyID int
        SELECT @TempConcurrencyID = ConcurrencyID FROM Issues WHERE IssueID = @IssueID

        IF (@TempConcurrencyID <> @ConcurrencyID)
        SELECT 'Unable to perform update. This issue was updated by another user while you were viewing it.' AS ErrorMessage
*/
/*
        DECLARE @IssueTrackID int

        DECLARE @NewStatusID int
        DECLARE @NewDepartmentID int
        DECLARE @TransferToPreviousDepartment bit
        DECLARE @TransferToCreatorDepartment bit
        DECLARE @TransferToDeptOfPrevActionID bit
        DECLARE @TransferToLocationOwnerDepartment bit


        DECLARE @UpdateBySystem INT = 0

                -- Get the workflow to use
        SELECT @WorkflowID = WorkflowID FROM SysParms
*/
        long actionWorkflowID = -1;
//        long IssueTrackID = -1;
//        long NewStatusID = detailedIssue.getStatusId();
//        long NewDepartmentID = detailedIssue.getCurrentDepartmentId();

/*
        -- Get information about the issue
        SELECT @CurrentStatusID = StatusID, @IssueTypeID = IssueTypeID, @CurrentDepartmentID = CurrentDepartmentID,
        @GuestServiceIssue = GuestServiceIssue, @RequiresGuestCallback = RequiresGuestCallback
        FROM Issues WHERE IssueID = @IssueID

        в DetailsIssue содержится.
*/


/*

-- This is to accomodate issues updated during old installations when the CurrentDepartmentID column didn't exist in the Issues table.
        IF @CurrentDepartmentID IS NULL
        SET @CurrentDepartmentID = (SELECT TOP 1 DepartmentID FROM IssueTracks WHERE IssueID = @IssueID ORDER BY LastUpdateDate DESC)
        ELSE
                BEGIN
        IF (SELECT TOP 1 LEN(DeviceID) FROM IssueTracks WHERE IssueID = @IssueID ORDER BY LastUpdateDate DESC) > 1
                -- When an issue was last updated by a device, the current department is not being updated.
        -- Therefore set the current department to be the department in the last issuetracks record.
                SET @CurrentDepartmentID = (SELECT TOP 1 DepartmentID FROM IssueTracks WHERE IssueID = @IssueID ORDER BY LastUpdateDate DESC)
        END
*/
/*
            в DetailsIssue содержится.
        - Get the issueclass for the issue's issuetype
        SET @IssueClassID = dbo.GetIssueClass(@IssueTypeID, NULL)
            в ActionIssue содержится.
        SELECT @KeepAssignee = KeepAssignee FROM Actions WHERE ActionID = @ActionID
*/

/*
        -- If this is a guest service issue, check if the issue type requires location owner action.
                IF @GuestServiceIssue = 1
        BEGIN
        SELECT @RequiresLocationOwnerAction = RequiresLocationOwnerAction FROM IssueTypes WHERE IssueTypeID = @IssueTypeID
        */

        if (detailedIssue.isGuestServiceIssueBoolean()) {
//            detailedIssue.getIssueTypeId();
//            detailedIssue.getIssueClassID();
//            actionIssue.isKeepAssignee();
//            detailedIssue.isRequiresGuestCallbackBoolean();
/*
            IF @RequiresLocationOwnerAction = 1
            BEGIN
                    -- Verify that the issue has a location owner. If it does not, then set RequiresLocationOwnerAction to 0.
            IF (SELECT LocationOwnerDepartmentID FROM Issues WHERE IssueID = @IssueID) IS NULL
            SET @RequiresLocationOwnerAction = 0
            END
*/
            if (detailedIssue.isRequiresGuestCallbackBoolean()) {
                if (!(detailedIssue.getLocationOwnerDepartmentID() > 0))
                    detailedIssue.setRequiresGuestCallbackBoolean(false);
            }

            if (detailedIssue.isRequiresGuestCallbackBoolean()) {
/*
                BEGIN
                        -- Get the action workflow for the current status, and GuestServiceIssue = 1 and RequiresGuestCallback = @RequiresGuestCallback
                -- and RequiresLocationOwnerAction = 1
                SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                        INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                        WHERE ActionID = @ActionID  AND GuestServiceIssue = 1 AND RequiresGuestCallback = @RequiresGuestCallback
                        AND RequiresLocationOwnerAction = 1 AND CurrentStatusID = @CurrentStatusID
                        IF @ActionWorkflowID IS NULL
                -- Get the action workflow for non-specific status, and GuestServiceIssue = 1 and RequiresGuestCallback = @RequiresGuestCallback
                -- and RequiresLocationOwnerAction = 1
                SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                        INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                        WHERE ActionID = @ActionID  AND GuestServiceIssue = 1 AND RequiresGuestCallback = @RequiresGuestCallback
                        AND RequiresLocationOwnerAction = 1
                IF @ActionWorkflowID IS NULL
                        -- Get the action workflow for the current status, and GuestServiceIssue = 1 and RequiresLocationOwnerAction = 1
                SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                        INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                        WHERE ActionID = @ActionID  AND GuestServiceIssue = 1
                AND RequiresLocationOwnerAction = 1 AND CurrentStatusID = @CurrentStatusID
                        IF @ActionWorkflowID IS NULL  -- Get the action workflow for non-specific status, and guestserviceissue = 1 and RequiresLocationOwnerAction = 1
                SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                        INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                        WHERE ActionID = @ActionID  AND GuestServiceIssue = 1
                AND RequiresLocationOwnerAction = 1 AND CurrentStatusID IS NULL
                IF @ActionWorkflowID IS NULL -- Get the action workflow for the current status  and RequiresLocationOwnerAction = 1
                SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                        INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                        WHERE ActionID = @ActionID  AND RequiresLocationOwnerAction = 1 AND CurrentStatusID = @CurrentStatusID
                        IF @ActionWorkflowID IS NULL -- Get the action workflow for non-specific status  and RequiresLocationOwnerAction = 1
                SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                        INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                        WHERE ActionID = @ActionID AND RequiresLocationOwnerAction = 1 AND CurrentStatusID IS NULL
                IF @ActionWorkflowID IS NULL -- Get the action workflow for the current status
                SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                        INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                        WHERE ActionID = @ActionID  AND CurrentStatusID = @CurrentStatusID
                        IF @ActionWorkflowID IS NULL -- Get the action workflow for non-specific status
                SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                        INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                        WHERE ActionID = @ActionID  AND CurrentStatusID IS NULL
                        END
*/

//               SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
//                INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
//                        INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
//                        WHERE ActionID = @ActionID  AND GuestServiceIssue = 1 AND RequiresGuestCallback = @RequiresGuestCallback
//                        AND RequiresLocationOwnerAction = 1 AND CurrentStatusID = @CurrentStatusID

//                -- Get the action workflow for the current status, and GuestServiceIssue = 1
//                      and RequiresGuestCallback = @RequiresGuestCallback
//                -- and RequiresLocationOwnerAction = 1
                c = db.rawQuery("SELECT ActionWorkflows.ActionWorkflowID FROM ActionWorkflows " +
                                "INNER JOIN ActionWorkflows_IssueClasses as b " +
                                "ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = ? " +
                                "INNER JOIN WorkflowAssociations as c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID " +
                                "AND WorkflowID = ? " +
                                "WHERE ActionID = ? AND GuestServiceIssue = 'true' AND RequiresGuestCallback = ? " +
                                "AND RequiresLocationOwnerAction = 'true' AND CurrentStatusID = ?",
                        new String[]{String.valueOf(detailedIssue.getIssueClassID()), String.valueOf(workflowID),
                                String.valueOf(idAction),
                                detailedIssue.isRequiresGuestCallbackBoolean() ? "true" : "false",
                                String.valueOf(detailedIssue.getStatusId())}
                );
                if (c.moveToFirst()) {
                    actionWorkflowID = c.getLong(0);
                } else {
                    c.close();
//                    return false;
                }

/*
                IF @ActionWorkflowID IS NULL
                        -- Get the action workflow for non-specific status, and GuestServiceIssue = 1 and RequiresGuestCallback = @RequiresGuestCallback
                -- and RequiresLocationOwnerAction = 1
                SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                        INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                        WHERE ActionID = @ActionID  AND GuestServiceIssue = 1 AND RequiresGuestCallback = @RequiresGuestCallback
                        AND RequiresLocationOwnerAction = 1
*/

                if (actionWorkflowID < 0) {
                    c = db.rawQuery("SELECT ActionWorkflows.ActionWorkflowID FROM ActionWorkflows " +
                                    "INNER JOIN ActionWorkflows_IssueClasses as b " +
                                    "ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID " +
                                    "AND b.IssueClassID = ? " +
                                    "INNER JOIN WorkflowAssociations as c " +
                                    "ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID " +
                                    "AND WorkflowID = ? " +
                                    "WHERE ActionID = ? AND GuestServiceIssue = 'true' " +
                                    "AND RequiresGuestCallback = ? " +
                                    "AND RequiresLocationOwnerAction = 'true'",
                            new String[]{String.valueOf(detailedIssue.getIssueClassID()),
                                    String.valueOf(workflowID), String.valueOf(idAction),
                                    detailedIssue.isRequiresGuestCallbackBoolean() ? "true" : "false"}
                    );
                    if (c.moveToFirst()) actionWorkflowID = c.getLong(0);
                    c.close();
                }
                if (actionWorkflowID < 0) {
/*
                    -- Get the action workflow for the current status, and GuestServiceIssue = 1 and RequiresLocationOwnerAction = 1
                    SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                    INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                            INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                            WHERE ActionID = @ActionID  AND GuestServiceIssue = 1
*/
                    c = db.rawQuery("SELECT ActionWorkflows.ActionWorkflowID FROM ActionWorkflows " +
                                    "INNER JOIN ActionWorkflows_IssueClasses as b " +
                                    "ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID " +
                                    "AND b.IssueClassID = ? " +
                                    "INNER JOIN WorkflowAssociations as c " +
                                    "ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID " +
                                    "AND WorkflowID = ? " +
                                    "WHERE ActionID = ? AND GuestServiceIssue = 'true' " +
                                    "AND RequiresLocationOwnerAction = 'true' AND CurrentStatusID = ?",
                            new String[]{String.valueOf(detailedIssue.getIssueClassID()),
                                    String.valueOf(workflowID), String.valueOf(idAction),
                                    String.valueOf(detailedIssue.getStatusId())}
                    );
                    if (c.moveToFirst()) actionWorkflowID = c.getLong(0);
                    c.close();
                }


                if (actionWorkflowID < 0) {
/*
                    IF @ActionWorkflowID IS NULL  -- Get the action workflow for non-specific status, and guestserviceissue = 1 and RequiresLocationOwnerAction = 1
                    SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                    INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                            INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                            WHERE ActionID = @ActionID  AND GuestServiceIssue = 1
                    AND RequiresLocationOwnerAction = 1 AND CurrentStatusID IS NULL
*/

                    c = db.rawQuery("SELECT ActionWorkflows.ActionWorkflowID FROM ActionWorkflows " +
                                    "INNER JOIN ActionWorkflows_IssueClasses as b " +
                                    "ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID " +
                                    "AND b.IssueClassID = ? " +
                                    "INNER JOIN WorkflowAssociations as c " +
                                    "ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID " +
                                    "AND WorkflowID = ? " +
                                    "WHERE ActionID = ? AND GuestServiceIssue = 'true' " +
                                    "AND RequiresLocationOwnerAction = 'true' AND CurrentStatusID IS NULL",
                            new String[]{String.valueOf(detailedIssue.getIssueClassID()),
                                    String.valueOf(workflowID), String.valueOf(idAction)}
                    );
                    if (c.moveToFirst()) actionWorkflowID = c.getLong(0);
                    c.close();
                }

                if (actionWorkflowID < 0) {
/*
                    IF @ActionWorkflowID IS NULL -- Get the action workflow for the current status  and RequiresLocationOwnerAction = 1
                    SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                    INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                            INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                            WHERE ActionID = @ActionID  AND RequiresLocationOwnerAction = 1 AND CurrentStatusID = @CurrentStatusID
*/

                    c = db.rawQuery("SELECT ActionWorkflows.ActionWorkflowID FROM ActionWorkflows " +
                                    "INNER JOIN ActionWorkflows_IssueClasses as b " +
                                    "ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID " +
                                    "AND b.IssueClassID = ? " +
                                    "INNER JOIN WorkflowAssociations as c " +
                                    "ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID " +
                                    "AND WorkflowID = ? " +
                                    "WHERE ActionID = ? AND RequiresLocationOwnerAction = 'true' " +
                                    "AND CurrentStatusID = ?",
                            new String[]{String.valueOf(detailedIssue.getIssueClassID()),
                                    String.valueOf(workflowID), String.valueOf(idAction),
                                    String.valueOf(detailedIssue.getStatusId())}
                    );
                    if (c.moveToFirst()) actionWorkflowID = c.getLong(0);
                    c.close();
                }

                if (actionWorkflowID < 0) {
/*
                IF @ActionWorkflowID IS NULL -- Get the action workflow for non-specific status  and RequiresLocationOwnerAction = 1
                SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                        INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                        WHERE ActionID = @ActionID AND RequiresLocationOwnerAction = 1 AND CurrentStatusID IS NULL
*/
                    c = db.rawQuery("SELECT ActionWorkflows.ActionWorkflowID FROM ActionWorkflows " +
                                    "INNER JOIN ActionWorkflows_IssueClasses as b " +
                                    "ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID " +
                                    "AND b.IssueClassID = ? " +
                                    "INNER JOIN WorkflowAssociations as c " +
                                    "ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID " +
                                    "AND WorkflowID = ? " +
                                    "WHERE ActionID = ? AND RequiresLocationOwnerAction = 'true' " +
                                    "AND CurrentStatusID IS NULL",
                            new String[]{String.valueOf(detailedIssue.getIssueClassID()),
                                    String.valueOf(workflowID), String.valueOf(idAction)}
                    );
                    if (c.moveToFirst()) actionWorkflowID = c.getLong(0);
                    c.close();
                }
                if (actionWorkflowID < 0) {
/*
                    IF @ActionWorkflowID IS NULL -- Get the action workflow for the current status
                    SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                    INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                            INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                            WHERE ActionID = @ActionID  AND CurrentStatusID = @CurrentStatusID
*/

                    c = db.rawQuery("SELECT ActionWorkflows.ActionWorkflowID FROM ActionWorkflows " +
                                    "INNER JOIN ActionWorkflows_IssueClasses as b " +
                                    "ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID " +
                                    "AND b.IssueClassID = ? " +
                                    "INNER JOIN WorkflowAssociations as c " +
                                    "ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID " +
                                    "AND WorkflowID = ? " +
                                    "WHERE ActionID = ? AND CurrentStatusID = ?",
                            new String[]{String.valueOf(detailedIssue.getIssueClassID()),
                                    String.valueOf(workflowID),
                                    String.valueOf(idAction),
                                    String.valueOf(detailedIssue.getStatusId())}
                    );
                    if (c.moveToFirst()) actionWorkflowID = c.getLong(0);
                    c.close();
                }

                if (actionWorkflowID < 0) {
/*
                    IF @ActionWorkflowID IS NULL -- Get the action workflow for non-specific status
                    SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                    INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                            INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                            WHERE ActionID = @ActionID  AND CurrentStatusID IS NULL
*/
                    c = db.rawQuery("SELECT ActionWorkflows.ActionWorkflowID FROM ActionWorkflows " +
                                    "INNER JOIN ActionWorkflows_IssueClasses as b " +
                                    "ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID " +
                                    "AND b.IssueClassID = ? " +
                                    "INNER JOIN WorkflowAssociations as c " +
                                    "ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID " +
                                    "AND WorkflowID = ? " +
                                    "WHERE ActionID = ? AND CurrentStatusID IS NULL",
                            new String[]{String.valueOf(detailedIssue.getIssueClassID()),
                                    String.valueOf(workflowID),
                                    String.valueOf(idAction)}
                    );
                    if (c.moveToFirst()) actionWorkflowID = c.getLong(0);
                    c.close();
                }

                //закончили
            } else {

/*
                BEGIN
                        -- Get the action workflow for the current status, and GuestServiceIssue = 1 and RequiresGuestCallback = @RequiresGuestCallback
                -- and RequiresLocationOwnerAction = 0
                SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                        INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                        WHERE ActionID = @ActionID  AND GuestServiceIssue = 1 AND RequiresGuestCallback = @RequiresGuestCallback
                        AND RequiresLocationOwnerAction = 0 AND CurrentStatusID = @CurrentStatusID AND Billable = 0
                IF @ActionWorkflowID IS NULL
                        -- Get the action workflow for non-specific status, and GuestServiceIssue = 1 and RequiresGuestCallback = @RequiresGuestCallback
                -- and RequiresLocationOwnerAction = 0
                SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                        INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                        WHERE ActionID = @ActionID  AND GuestServiceIssue = 1 AND RequiresGuestCallback = @RequiresGuestCallback
                        AND RequiresLocationOwnerAction = 0 AND Billable = 0
                IF @ActionWorkflowID IS NULL
                        -- Get the action workflow for the current status and guestserviceissue = 1 and RequiresLocationOwnerAction = 0
                SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                        INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                        WHERE ActionID = @ActionID  AND GuestServiceIssue = 1  AND RequiresLocationOwnerAction = 0
                AND CurrentStatusID = @CurrentStatusID AND Billable = 0
                IF @ActionWorkflowID IS NULL -- Get the action workflow for non-specific status and guestserviceissue = 1 and RequiresLocationOwnerAction = 0
                SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                        INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                        WHERE ActionID = @ActionID  AND GuestServiceIssue = 1  AND RequiresLocationOwnerAction = 0
                AND CurrentStatusID IS NULL AND Billable = 0
                IF @ActionWorkflowID IS NULL -- Get the action workflow for the current status  and RequiresLocationOwnerAction = 0
                SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                        INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                        WHERE ActionID = @ActionID  AND RequiresLocationOwnerAction = 0 AND CurrentStatusID = @CurrentStatusID
                        IF @ActionWorkflowID IS NULL -- Get the action workflow for non-specific status  and RequiresLocationOwnerAction = 0
                SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                        INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                        WHERE ActionID = @ActionID  AND RequiresLocationOwnerAction = 0 AND CurrentStatusID IS NULL
                IF @ActionWorkflowID IS NULL -- Get the action workflow for the current status
                SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                        INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                        WHERE ActionID = @ActionID  AND CurrentStatusID = @CurrentStatusID
                        IF @ActionWorkflowID IS NULL -- Get the action workflow for non-specific status
                SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                        INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                        WHERE ActionID = @ActionID  AND CurrentStatusID IS NULL
                        END
*/



/*
                -- Get the action workflow for the current status, and GuestServiceIssue = 1 and RequiresGuestCallback = @RequiresGuestCallback
                -- and RequiresLocationOwnerAction = 0
                SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                        INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                        WHERE ActionID = @ActionID  AND GuestServiceIssue = 1 AND RequiresGuestCallback = @RequiresGuestCallback
                        AND RequiresLocationOwnerAction = 0 AND CurrentStatusID = @CurrentStatusID AND Billable = 0
*/

                c = db.rawQuery("SELECT ActionWorkflows.ActionWorkflowID FROM ActionWorkflows " +
                                "INNER JOIN ActionWorkflows_IssueClasses as b " +
                                "ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID " +
                                "AND b.IssueClassID = ? " +
                                "INNER JOIN WorkflowAssociations as c " +
                                "ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID " +
                                "AND WorkflowID = ?" +
                                "WHERE ActionID = ? AND GuestServiceIssue = 'true' " +
                                "AND RequiresGuestCallback = ? " +
                                "AND RequiresLocationOwnerAction = 'false' AND CurrentStatusID = ? " +
                                "AND Billable = ?",
                        new String[]{String.valueOf(detailedIssue.getIssueClassID()),
                                String.valueOf(workflowID),
                                String.valueOf(idAction),
                                String.valueOf(detailedIssue.isRequiresGuestCallbackBoolean()),
                                String.valueOf(detailedIssue.getStatusId()),
                                String.valueOf(billable)}
                );
                if (c.moveToFirst()) actionWorkflowID = c.getLong(0);
                c.close();

                if (actionWorkflowID < 0) {
/*
                    -- Get the action workflow for non-specific status, and GuestServiceIssue = 1 and RequiresGuestCallback = @RequiresGuestCallback
                    -- and RequiresLocationOwnerAction = 0
                    SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                    INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                            INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                            WHERE ActionID = @ActionID  AND GuestServiceIssue = 1 AND RequiresGuestCallback = @RequiresGuestCallback
                            AND RequiresLocationOwnerAction = 0 AND Billable = 0
*/

                    c = db.rawQuery("SELECT ActionWorkflows.ActionWorkflowID FROM ActionWorkflows " +
                                    "INNER JOIN ActionWorkflows_IssueClasses as b " +
                                    "ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID " +
                                    "AND b.IssueClassID = ? " +
                                    "INNER JOIN WorkflowAssociations as c " +
                                    "ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID " +
                                    "AND WorkflowID = ? " +
                                    "WHERE ActionID = ? AND GuestServiceIssue = 'true' " +
                                    "AND RequiresGuestCallback = ? " +
                                    "AND RequiresLocationOwnerAction = 'false' AND Billable = ?",
                            new String[]{String.valueOf(detailedIssue.getIssueClassID()), String.valueOf(workflowID),
                                    String.valueOf(idAction),
                                    String.valueOf(detailedIssue.isRequiresGuestCallbackBoolean()),
                                    String.valueOf(billable)}
                    );
                    if (c.moveToFirst()) actionWorkflowID = c.getLong(0);
                    c.close();
                }

                if (actionWorkflowID < 0) {
/*
                    -- Get the action workflow for the current status and guestserviceissue = 1 and RequiresLocationOwnerAction = 0
                    SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                    INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                            INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                            WHERE ActionID = @ActionID  AND GuestServiceIssue = 1  AND RequiresLocationOwnerAction = 0
                    AND CurrentStatusID = @CurrentStatusID AND Billable = 0
*/

                    c = db.rawQuery("SELECT ActionWorkflows.ActionWorkflowID FROM ActionWorkflows " +
                                    "INNER JOIN ActionWorkflows_IssueClasses as b " +
                                    "ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID " +
                                    "AND b.IssueClassID = ? " +
                                    "INNER JOIN WorkflowAssociations as c " +
                                    "ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID " +
                                    "AND WorkflowID = ? " +
                                    "WHERE ActionID = ? AND GuestServiceIssue = 'true' " +
                                    "AND RequiresLocationOwnerAction = 'false' " +
                                    "AND CurrentStatusID = ? AND Billable = ?",
                            new String[]{String.valueOf(detailedIssue.getIssueClassID()),
                                    String.valueOf(workflowID),
                                    String.valueOf(idAction),
                                    String.valueOf(detailedIssue.getStatusId()),
                                    String.valueOf(billable)}
                    );
                    if (c.moveToFirst()) actionWorkflowID = c.getLong(0);
                    c.close();
                }

                if (actionWorkflowID < 0) {

/*
                    IF @ActionWorkflowID IS NULL -- Get the action workflow for non-specific status and guestserviceissue = 1 and RequiresLocationOwnerAction = 0
                    SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                    INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                            INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                            WHERE ActionID = @ActionID  AND GuestServiceIssue = 1  AND RequiresLocationOwnerAction = 0
                    AND CurrentStatusID IS NULL AND Billable = 0
*/


                    c = db.rawQuery("SELECT ActionWorkflows.ActionWorkflowID FROM ActionWorkflows " +
                                    "INNER JOIN ActionWorkflows_IssueClasses as b " +
                                    "ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID " +
                                    "AND b.IssueClassID = ? " +
                                    "INNER JOIN WorkflowAssociations as c " +
                                    "ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID " +
                                    "AND WorkflowID = ? " +
                                    "WHERE ActionID = ? AND GuestServiceIssue = 'true' " +
                                    "AND RequiresLocationOwnerAction = 'false' " +
                                    "AND CurrentStatusID IS NULL AND Billable = ?",
                            new String[]{String.valueOf(detailedIssue.getIssueClassID()),
                                    String.valueOf(workflowID),
                                    String.valueOf(idAction),
                                    String.valueOf(billable)}
                    );
                    if (c.moveToFirst()) actionWorkflowID = c.getLong(0);
                    c.close();
                }

                if (actionWorkflowID < 0) {
/*

                    IF @ActionWorkflowID IS NULL -- Get the action workflow for the current status  and RequiresLocationOwnerAction = 0
                    SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                    INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                            INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                            WHERE ActionID = @ActionID  AND RequiresLocationOwnerAction = 0 AND CurrentStatusID = @CurrentStatusID
*/
                    c = db.rawQuery("SELECT ActionWorkflows.ActionWorkflowID FROM ActionWorkflows " +
                                    "INNER JOIN ActionWorkflows_IssueClasses as b " +
                                    "ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID " +
                                    "AND b.IssueClassID = ? " +
                                    "INNER JOIN WorkflowAssociations as c " +
                                    "ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID " +
                                    "AND WorkflowID = ? " +
                                    "WHERE ActionID = ? AND RequiresLocationOwnerAction = 'false' " +
                                    "AND CurrentStatusID = ?",
                            new String[]{String.valueOf(detailedIssue.getIssueClassID()),
                                    String.valueOf(workflowID),
                                    String.valueOf(idAction),
                                    String.valueOf(detailedIssue.getStatusId())}
                    );
                    if (c.moveToFirst()) actionWorkflowID = c.getLong(0);
                    c.close();
                }

                if (actionWorkflowID < 0) {

/*
                    IF @ActionWorkflowID IS NULL -- Get the action workflow for non-specific status  and RequiresLocationOwnerAction = 0
                    SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                    INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                            INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                            WHERE ActionID = @ActionID  AND RequiresLocationOwnerAction = 0 AND CurrentStatusID IS NULL
*/

                    c = db.rawQuery("SELECT ActionWorkflows.ActionWorkflowID FROM ActionWorkflows " +
                                    "INNER JOIN ActionWorkflows_IssueClasses as b " +
                                    "ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID " +
                                    "AND b.IssueClassID = ? " +
                                    "INNER JOIN WorkflowAssociations as c " +
                                    "ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID " +
                                    "AND WorkflowID = ? " +
                                    "WHERE ActionID = ? AND RequiresLocationOwnerAction = 'false' " +
                                    "AND CurrentStatusID IS NULL",
                            new String[]{String.valueOf(detailedIssue.getIssueClassID()),
                                    String.valueOf(workflowID),
                                    String.valueOf(idAction)}
                    );
                    if (c.moveToFirst()) actionWorkflowID = c.getLong(0);
                    c.close();
                }

                if (actionWorkflowID < 0) {
/*
                    IF @ActionWorkflowID IS NULL -- Get the action workflow for the current status
                    SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                    INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                            INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                            WHERE ActionID = @ActionID  AND CurrentStatusID = @CurrentStatusID
*/
                    db.rawQuery("SELECT ActionWorkflows.ActionWorkflowID FROM ActionWorkflows " +
                                    "INNER JOIN ActionWorkflows_IssueClasses as b " +
                                    "ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID " +
                                    "AND b.IssueClassID = ? " +
                                    "INNER JOIN WorkflowAssociations as c " +
                                    "ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID " +
                                    "AND WorkflowID = ? " +
                                    "WHERE ActionID = ? AND CurrentStatusID = ?",
                            new String[]{String.valueOf(detailedIssue.getIssueClassID()),
                                    String.valueOf(workflowID),
                                    String.valueOf(idAction),
                                    String.valueOf(detailedIssue.getStatusId())}
                    );
                    if (c.moveToFirst()) actionWorkflowID = c.getLong(0);
                    c.close();
                }

                if (actionWorkflowID < 0) {

/*
                    IF @ActionWorkflowID IS NULL -- Get the action workflow for non-specific status
                    SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                    INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                            INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                            WHERE ActionID = @ActionID  AND CurrentStatusID IS NULL
*/

                    db.rawQuery("SELECT ActionWorkflows.ActionWorkflowID FROM ActionWorkflows " +
                                    "INNER JOIN ActionWorkflows_IssueClasses as b " +
                                    "ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID " +
                                    "AND b.IssueClassID = ? " +
                                    "INNER JOIN WorkflowAssociations as c " +
                                    "ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID " +
                                    "AND WorkflowID = ? " +
                                    "WHERE ActionID = ? AND CurrentStatusID IS NULL",
                            new String[]{String.valueOf(detailedIssue.getIssueClassID()), String.valueOf(workflowID),
                                    String.valueOf(idAction)}
                    );
                    if (c.moveToFirst()) actionWorkflowID = c.getLong(0);
                    c.close();
                }

            }

        } else {

/*
            --Check if there is a specific one for the billable option and for the current status.
                    SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
            INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                    INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                    WHERE ActionID = @ActionID AND RequiresLocationOwnerAction = 0 AND GuestServiceIssue = 0
            AND Billable = @Billable AND CurrentStatusID = @CurrentStatusID
*/

            c = db.rawQuery("SELECT ActionWorkflows.ActionWorkflowID FROM ActionWorkflows " +
                            "INNER JOIN ActionWorkflows_IssueClasses as b " +
                            "ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID " +
                            "AND b.IssueClassID = ? " +
                            "INNER JOIN WorkflowAssociations as c " +
                            "ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID " +
                            "AND WorkflowID = ? " +
                            "WHERE ActionID = ? AND RequiresLocationOwnerAction = 'false' " +
                            "AND GuestServiceIssue = 'false' " +
                            "AND Billable = ? AND CurrentStatusID = ?",
                    new String[]{String.valueOf(detailedIssue.getIssueClassID()),
                            String.valueOf(workflowID),
                            String.valueOf(idAction), String.valueOf(billable),
                            String.valueOf(detailedIssue.getStatusId())}
            );
            if (c.moveToFirst()) actionWorkflowID = c.getLong(0);
            c.close();

            if (actionWorkflowID < 0) {
/*
                IF @ActionWorkflowID IS NULL
                        BEGIN
                -- Check if there is one for the billable option and non-specific status.
                        SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                        INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                        WHERE ActionID = @ActionID  AND RequiresLocationOwnerAction = 0 AND GuestServiceIssue = 0
                AND Billable = @Billable AND CurrentStatusID IS NULL
*/

                c = db.rawQuery("SELECT ActionWorkflows.ActionWorkflowID FROM ActionWorkflows " +
                                "INNER JOIN ActionWorkflows_IssueClasses as b " +
                                "ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID " +
                                "AND b.IssueClassID = ? " +
                                "INNER JOIN WorkflowAssociations as c " +
                                "ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID " +
                                "AND WorkflowID = ? " +
                                "WHERE ActionID = ? AND RequiresLocationOwnerAction = 'false' " +
                                "AND GuestServiceIssue = 'false' " +
                                "AND Billable = ? AND CurrentStatusID IS NULL",
                        new String[]{String.valueOf(detailedIssue.getIssueClassID()),
                                String.valueOf(workflowID),
                                String.valueOf(idAction), String.valueOf(billable)}
                );
                if (c.moveToFirst()) actionWorkflowID = c.getLong(0);
                c.close();
                if (actionWorkflowID < 0) {
/*
                    IF @ActionWorkflowID IS NULL
                            BEGIN
                    -- Check if there is one for the current status.
                            SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                    INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                            INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                            WHERE ActionID = @ActionID  AND RequiresLocationOwnerAction = 0 AND GuestServiceIssue = 0
                    AND CurrentStatusID = @CurrentStatusID
*/
                    c = db.rawQuery("SELECT ActionWorkflows.ActionWorkflowID FROM ActionWorkflows " +
                                    "INNER JOIN ActionWorkflows_IssueClasses as b " +
                                    "ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID " +
                                    "AND b.IssueClassID = ? " +
                                    "INNER JOIN WorkflowAssociations as c " +
                                    "ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID " +
                                    "AND WorkflowID = ? " +
                                    "WHERE ActionID = ? AND RequiresLocationOwnerAction = 'false' " +
                                    "AND GuestServiceIssue = 'false' " +
                                    "AND CurrentStatusID = ?",
                            new String[]{String.valueOf(detailedIssue.getIssueClassID()),
                                    String.valueOf(workflowID),
                                    String.valueOf(idAction),
                                    String.valueOf(detailedIssue.getStatusId())}
                    );
                    if (c.moveToFirst()) actionWorkflowID = c.getLong(0);
                    c.close();
                    if (actionWorkflowID < 0) {
/*
                        -- Check if there is one for non-specific status
                        IF @ActionWorkflowID IS NULL
                        SELECT @ActionWorkflowID = ActionWorkflows.ActionWorkflowID FROM ActionWorkflows
                        INNER JOIN ActionWorkflows_IssueClasses b ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID AND b.IssueClassID = @IssueClassID
                                INNER JOIN WorkflowAssociations c ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID AND WorkflowID = @WorkflowID
                                WHERE ActionID = @ActionID  AND RequiresLocationOwnerAction = 0 AND GuestServiceIssue = 0
                        AND CurrentStatusID IS NULL
                        END
*/
                        c = db.rawQuery("SELECT ActionWorkflows.ActionWorkflowID FROM ActionWorkflows " +
                                        "INNER JOIN ActionWorkflows_IssueClasses as b " +
                                        "ON ActionWorkflows.ActionWorkflowID = b.ActionWorkflowID " +
                                        "AND b.IssueClassID = ? " +
                                        "INNER JOIN WorkflowAssociations as c " +
                                        "ON ActionWorkflows.ActionWorkflowID = c.ActionWorkflowID " +
                                        "AND WorkflowID = ? " +
                                        "WHERE ActionID = ? AND RequiresLocationOwnerAction = 'false' " +
                                        "AND GuestServiceIssue = 'false' " +
                                        "AND CurrentStatusID IS NULL",
                                new String[]{String.valueOf(detailedIssue.getIssueClassID()),
                                        String.valueOf(workflowID),
                                        String.valueOf(idAction)}
                        );
                        if (c.moveToFirst()) actionWorkflowID = c.getLong(0);
                        c.close();
                    }
                }
            }

        }


/*
        -- No action workflow was found. Exit stored proc.
                --
                -- При получении данного исключения нужно выводить ошибку пользователю?
        --
                IF @ActionWorkflowID IS NULL
        BEGIN
        SELECT 'Unable to update issue. Cannot find a worklow for this action.' AS ErrorMessage
        RETURN
                END
*/

        if (actionWorkflowID < 0)
            return "Error! Unable to update issue. Cannot find a worklow for this action"; // No action workflow was
        // found. Exit.

/*
        -- Get information from the action workflow being used
        SELECT @NewStatusID = NewStatusID, @NewDepartmentID = NewDepartmentID, @TransferToPreviousDepartment = TransferToPreviousDepartment,
        @TransferToCreatorDepartment = TransferToCreatorDepartment, @TransferToDeptOfPrevActionID = TransferToDeptOfPrevActionID,
        @TransferToLocationOwnerDepartment  = TransferToLocationOwnerDepartment
        FROM ActionWorkflows WHERE ActionWorkflowID = @ActionWorkflowID
*/

        c = db.rawQuery("SELECT NewStatusID, NewDepartmentID, TransferToPreviousDepartment," +
                        "TransferToCreatorDepartment, TransferToDeptOfPrevActionID, " +
                        "TransferToLocationOwnerDepartment " +
                        "FROM ActionWorkflows WHERE ActionWorkflowID = ?",
                new String[]{String.valueOf(actionWorkflowID)}
        );
        long statusNewID = -1;
        long departmentNewID = -1;
        boolean transferToPreviousDepartment = false, transferToCreatorDepartment = false,
                transferToDeptOfPrevActionID = false, transferToLocationOwnerDepartment = false;
        if (c.moveToFirst()) {
            statusNewID = c.getLong(c.getColumnIndex("NewStatusID"));
            departmentNewID = c.getLong(c.getColumnIndex("NewDepartmentID"));
            transferToPreviousDepartment = c.getString(c.getColumnIndex("TransferToPreviousDepartment")) != null
                    && c.getString(c.getColumnIndex("TransferToPreviousDepartment")).equals("true");
            transferToCreatorDepartment = c.getString(c.getColumnIndex("TransferToCreatorDepartment")) != null
                    && c.getString(c.getColumnIndex("TransferToCreatorDepartment")).equals("true");
            transferToDeptOfPrevActionID = c.getString(c.getColumnIndex("TransferToDeptOfPrevActionID")) != null
                    && c.getString(c.getColumnIndex("TransferToDeptOfPrevActionID")).equals("true");
            transferToLocationOwnerDepartment = c.getString(c.getColumnIndex("TransferToLocationOwnerDepartment")) != null
                    && c.getString(c.getColumnIndex("TransferToLocationOwnerDepartment")).equals("true");

        }
        c.close();
/*
        -- If there was no new status id, then it means that the status does not change. Therefore set it to the current status.
                IF @NewStatusID IS NULL SET @NewStatusID = @CurrentStatusID
    */

        if (statusNewID <= 0)
            statusNewID = detailedIssue.getStatusId();

//        DECLARE @DefineAssignedDepartment bit = 1
        boolean defineAssignedDepartment = true;

/*
        IF @CompensationID IS NOT NULL
        BEGIN
                -- If @MonetaryValueByUser is false, it means that the user was not required to enter a value
                -- for the compensation. Get the default monetary value from the Compensations table.
        IF @MonetaryValueByUser = 0
        SELECT @MonetaryValue = MonetaryValue FROM Compensations WHERE CompensationID = @CompensationID

                DECLARE @CompDepartmentID int
        SELECT @CompDepartmentID = NewDepartmentID FROM Compensations WHERE CompensationID = @CompensationID
                IF @CompDepartmentID IS NOT NULL
                BEGIN
        SET @AssignedDepartmentID =  @CompDepartmentID
        SET @DefineAssignedDepartment = 0
        END
                END
*/

/*

        no on mobile

        Use logic below to determine values for CompensationID, MonetaryValue, MonetaryValueByUser:

        1. If the Compensations panel is visible then
        CompensationID is the value selected in the Compensations dropdown.
                If the Monetary Value Panel is Visible Then
                MonetaryValue  = Use the value entered in the MonetaryValue textbox.
                MonetaryValueByUser = true
        2. Else use NULL for the 3 parameters (CompensationID, MonetaryValue, MonetaryValueByUser).
*/



        /*Declare “AssigneeAnyUser” boolean variable and set it to the current value for AssigneeAnyUser.
                Declare “AssigneeUserID “ integer variable and set it to the current value for AssigneeUserID.
                Declare “AssignedDepartmentID “ integer variable.
        */

        boolean assigneeAnyUser = detailedIssue.getIssueTypeModel().isAssigneeAnyUser();
        long assigneeUserID = detailedIssue.getIssueTypeModel().getAssigneeUserID();


        ReturnDateWithActionDialog transferDepartmentPanel = null;
        ReturnDateWithActionDialog assignPanel = null;
        ReturnDateWithActionDialog reassignPanel = null;
        ReturnDateWithActionDialog requestInfoPanel = null;
        ReturnDateWithActionDialog startTaskPanel = null;
        long causeID = -1;
        ReturnDateWithActionDialog fileAttach = null;

        for (ReturnDateWithActionDialog panelAction : data) {

            switch (panelAction.getIdPanel()) {
                case 1: {
                    // PrioritiesPanel
                    for (int i = 0; panelAction.getListSpinner().size() > i; i++) {
                        if (panelAction.getListSpinner().get(i).getNameTable().equals("Priorities")) {
                            detailedIssue.setPriorId(panelAction.getListSpinner().get(i).getId());
                            break;
                        }
                    }
                    break;
                }
                case 2: {
                    // TransferDepartmentPanel
                    transferDepartmentPanel = panelAction;
                    continue;
                }
                case 4:
                    // AssignPanel
                    assignPanel = panelAction;
                    continue;
                case 5: {
                    // RequestInfoPanel
                    requestInfoPanel = panelAction;
                    continue;
                }
                case 7: {
                    // NotesPanel
                    notes = panelAction.getNote();
                    break;
                }
                case 8: {
                    //StartTaskPanel
                    startTaskPanel = panelAction;
                    continue;
                }
                case 12: {
                    // CausesPanel
                    for (int i = 0; panelAction.getListSpinner().size() > i; i++) {
                        if (panelAction.getListSpinner().get(i).getNameTable().equals("Causes")) {
                            causeID = panelAction.getListSpinner().get(i).getId();
                            break;
                        }
                    }
                    break;
                }
                case 13: {
                    // FileAttachmentPanel
                    fileAttach = panelAction;
                    break;
                }
                case 14: {
                    // ReassignPanel
                    reassignPanel = panelAction;
                    continue;
                }

            }

        }
/* 1.  If TransferDepartmentPanel is visible Then
                AssignedDepartmentID = selected value on DepartmentsForTransfer dropdown
        2. ElseIf AssignPanel is visible Then
                AssignedDepartmentID = selected value on  DepartmentsForAssign dropdown
        3. ElseIf ReassignPanel is visible Then
        If ReassignSubPanel is visible Then
                AssignedDepartmentID = selected value on  DepartmentsForReassign dropdown
        AssigneeUserID = selected value on  Assignees dropdown under ReassignSubPanel
        AssigneeAnyUser = False
        Else
                AssignedDepartmentID = current value for DepartmentID
        If the selected item on the ReassignTo dropdown is “Any User” Then
                AssigneeAnyUser = True
        Else
                AssigneeAnyUser = False
        AssigneeUserID = DbNull
        End If
        End If
        4. ElseIf RequestInfoPanel is visible Then
                AssignedDepartmentID = selected value on DepartmentsForRequestInfo dropdown

        5. ElseIf StartTaskPanel is visible Then
                AssignedDepartmentID = current value for DepartmentID
        If AssigneeAnyUser  is true (user doing the update becomes the Assignee)  then
                AssigneeUserID = the user logged in to the device
        AssigneeAnyUser = False
        End If
        6. Else
                AssignedDepartmentID = current value for DepartmentID*/

        long assignedDepartmentID = -1;

//            Log.i("myInfo", "panelName= " + panelAction.getName() +
//                    " note= " + panelAction.getData().getNote() +
//                    " spinner= " + panelAction.getData().getListSpinner());
//            if (panelAction.getData().getListSpinner() != null) {
//                for (int i = 0; panelAction.getData().getListSpinner().size() > i; i++) {
//                    Log.i("myInfo", "tableName= " + panelAction.getData().getListSpinner().get(i).getNameTable() +
//                            " idNote= " + panelAction.getData().getListSpinner().get(i).getId() +
//                            " nameNote= " + panelAction.getData().getListSpinner().get(i).getNameNote());
//                }
//            }
        if (transferDepartmentPanel != null) {
            if (transferDepartmentPanel.getListSpinner() != null) {
                for (int i = 0; transferDepartmentPanel.getListSpinner().size() > i; i++) {
                    if (transferDepartmentPanel.getListSpinner().get(i).getNameTable()
                            .equals("Departments")) {
                        assignedDepartmentID = transferDepartmentPanel.getListSpinner().get(i).getId();
                        break;
                    }
                }
            }
        } else if (assignPanel != null) {
            if (assignPanel.getListSpinner() != null) {
                for (int i = 0; assignPanel.getListSpinner().size() > i; i++) {
                    if (assignPanel.getListSpinner().get(i).getNameTable().equals("Departments")) {
                        assignedDepartmentID = assignPanel.getListSpinner().get(i).getId();
                        break;
                    }
                }
            }
        } else if (reassignPanel != null) {


            if (assignPanel.getListSpinner() == null) {
                assigneeAnyUser = true;

            } else {
                assigneeAnyUser = false;
                for (int i = 0; assignPanel.getListSpinner().size() > i; i++) {
                    if (assignPanel.getListSpinner().get(i).getNameTable().equals("Departments")) {
                        assignedDepartmentID = assignPanel.getListSpinner().get(i).getId();
                        continue;
                    }

                    if (assignPanel.getListSpinner().get(i).getNameTable().equals("Users")) {
                        assigneeUserID = assignPanel.getListSpinner().get(i).getId();
                        continue;
                    }
                }

            }

                /*If ReassignSubPanel is visible Then
                        AssignedDepartmentID = selected value on  DepartmentsForReassign dropdown
                AssigneeUserID = selected value on  Assignees dropdown under ReassignSubPanel
                AssigneeAnyUser = False
                Else
                        AssignedDepartmentID = current value for DepartmentID
                If the selected item on the ReassignTo dropdown is “Any User” Then
                        AssigneeAnyUser = True
                Else
                        AssigneeAnyUser = False
                AssigneeUserID = DbNull
                End If
                */

        } else if (requestInfoPanel != null) {
            if (requestInfoPanel.getListSpinner() != null) {
                for (int i = 0; requestInfoPanel.getListSpinner().size() > i; i++) {
                    if (requestInfoPanel.getListSpinner().get(i).getNameTable().equals("Departments")) {
                        assignedDepartmentID = requestInfoPanel.getListSpinner().get(i).getId();
                        break;
                    }
                }
            }
        } else if (startTaskPanel != null) {
            assignedDepartmentID = user.getDepartmentId();
            if (assigneeAnyUser) {
                assigneeAnyUser = false;
                assigneeUserID = user.getId();
            }
//            5. ElseIf StartTaskPanel is visible Then
//                    AssignedDepartmentID = current value for DepartmentID
//            If AssigneeAnyUser  is true (user doing the update becomes the Assignee)  then
//                    AssigneeUserID = the user logged in to the device
//            AssigneeAnyUser = False
//            End If


        } else {
            assignedDepartmentID = detailedIssue.getCurrentDepartmentId();
        }

//        IF @DefineAssignedDepartment = 1
        if (defineAssignedDepartment) {

//            -- If the department hasn't been changed by the user updating the issue, check if the issue needs to be assigned to another department
//            IF @AssignedDepartmentID = @CurrentDepartmentID
            if (assignedDepartmentID == detailedIssue.getCurrentDepartmentId()) {
/*
                IF @NewDepartmentID IS NOT NULL
                -- Set the department to the one specified for this action
                SET @AssignedDepartmentID = @NewDepartmentID
*/

/*
                ELSE IF @TransferToPreviousDepartment = 1
                BEGIN
                        -- Get the previous department assigned to this issue
                SET @AssignedDepartmentID = (SELECT TOP 1 DepartmentID FROM IssueTracks WHERE IssueID = @IssueID
                        AND DepartmentID <> @CurrentDepartmentID ORDER BY LastUpdateDate DESC)

                */
/** Added by Silvia - Mar 16, 2012 **//*

                IF @AssignedDepartmentID IS NULL
                        -- Get the creator's department
                SELECT @AssignedDepartmentID = CreateByDepartmentID FROM Issues WHERE IssueID = @IssueID
                        */
/****//*

                        END
*/
                if (departmentNewID > 0) assignedDepartmentID = departmentNewID;
                else if (transferToPreviousDepartment) {
                    c = db.rawQuery("SELECT DepartmentID FROM IssueTracks WHERE IssueID = ? " +
                                    "AND DepartmentID <> ? ORDER BY LastUpdateDate DESC LIMIT 1",
                            new String[]{String.valueOf(detailedIssue.getId()),
                                    String.valueOf(detailedIssue.getCurrentDepartmentId())}
                    );
                    if (c.moveToFirst())
                        assignedDepartmentID = c.getLong(0);
                    if (assignedDepartmentID <= 0)
                        assignedDepartmentID = detailedIssue.getCreatorDepartmentId();

                } else if (transferToCreatorDepartment) {
                    assignedDepartmentID = detailedIssue.getCreatorDepartmentId();
                } else if (transferToDeptOfPrevActionID) {
/*
                    BEGIN
                            -- Get the department of the user that updated the last previous action.
                    -- Use "SELECT TOP 1" to get the first department since some users might belong
                    -- to more than one dept.
                    DECLARE @PrevActionID INT, @PrevActionLastUpdateUserID INT
                    SELECT @PrevActionID = PreviousActionID FROM Actions WHERE ActionID = @ActionID
                            SET @PrevActionLastUpdateUserID = (SELECT TOP 1 LastUpdateUserID FROM IssueTracks WHERE IssueID = @IssueID
                            AND ActionID = @PrevActionID ORDER BY LastUpdateDate DESC)
                    SET @AssignedDepartmentID = (SELECT TOP 1 DepartmentID FROM UserDepartments WHERE UserID = @PrevActionLastUpdateUserID)
                    END
*/
//                    @PrevActionID INT - from issueAction
                    long prevActionLastUpdateUserID = -1;
                    if (prevActionID > 0)
                        c = db.rawQuery("SELECT LastUpdateUserID FROM IssueTracks WHERE IssueID = ? " +
                                        "AND ActionID = ? ORDER BY LastUpdateDate DESC LIMIT 1",
                                new String[]{String.valueOf(detailedIssue.getId()),
                                        String.valueOf(prevActionID)}
                        );
                    else
                        c = db.rawQuery("SELECT LastUpdateUserID FROM IssueTracks WHERE IssueID = ? " +
                                        "ORDER BY LastUpdateDate DESC LIMIT 1",
                                new String[]{String.valueOf(detailedIssue.getId())}
                        );
                    if (c.moveToFirst())
                        prevActionLastUpdateUserID = c.getLong(0);
                    c.close();
                    c = db.rawQuery("SELECT DepartmentID FROM UserDepartments WHERE UserID = ? LIMIT 1",
                            new String[]{String.valueOf(prevActionLastUpdateUserID)});
                    if (c.moveToFirst())
                        assignedDepartmentID = c.getLong(0);
                    c.close();
                } else if (transferToLocationOwnerDepartment) {
/*
                    ELSE IF @TransferToLocationOwnerDepartment = 1
                    -- Location Owner Department
                            -- Get the location owner for this department
                    SELECT @AssignedDepartmentID = LocationOwnerDepartmentID FROM Locations WHERE LocationID = (SELECT LocationID FROM Issues WHERE IssueID = @IssueID)
*/
                    c = db.rawQuery("SELECT LocationOwnerDepartmentID FROM Locations " +
                                    "WHERE LocationID = (SELECT LocationID FROM Issues " +
                                    "WHERE IssueID = ?)",
                            new String[]{String.valueOf(detailedIssue.getId())}
                    );
                    if (c.moveToFirst())
                        assignedDepartmentID = c.getLong(0);
                    c.close();
                    /*
                    IF @AssignedDepartmentID IS NULL
                    SET @AssignedDepartmentID = @CurrentDepartmentID*/
                    if (assignedDepartmentID <= 0)
                        assignedDepartmentID = detailedIssue.getCurrentDepartmentId();
                }
            }
        }

//        -- Check if the user assignment needs to be removed. This is NOT done when the action
//                -- is "Assign", "Reassign" or "NotApprove".

/*
        DECLARE @AssignActionID int, @ReassignActionID int, @NotApproveActionID int
        SELECT @AssignActionID = ActionID FROM Actions WHERE ActionCode = 'Assign'
        SELECT @ReassignActionID = ActionID FROM Actions WHERE ActionCode = 'ReAssign'
        SELECT @NotApproveActionID = ActionID FROM Actions WHERE ActionCode = 'NotApprove'
        DECLARE @AssigneeUserIDToKeep int
*/
        long assigneeUserIDToKeep = -1;

        if (!actionCode.equals("Assign") && !actionCode.equals("Reassgn") && !actionCode.equals("NotApprove")) {
/*
            IF @ActionID <> @AssignActionID AND @ActionID <> @ReassignActionID AND @ActionID <> @NotApproveActionID
            BEGIN
            DECLARE @PrevAssigneeUserID int
            SET @PrevAssigneeUserID = (SELECT TOP 1 PrevAssigneeUserID FROM IssueTracks WHERE IssueID = @IssueID ORDER BY LastUpdateDate DESC)
            IF @KeepAssignee = 1
            BEGIN
            IF @AssignedDepartmentID <> @CurrentDepartmentID
            BEGIN
            SET @AssigneeUserIDToKeep = @AssigneeUserID
            SET @AssigneeUserID = NULL
            SET @AssigneeAnyUser = 0
            END
                    ELSE
            SET @AssigneeUserIDToKeep = @PrevAssigneeUserID
            END
                    ELSE
            BEGIN
            SET @PrevAssigneeUserID = NULL
            SET @AssigneeUserID = NULL
            SET @AssigneeAnyUser = 0
            END

                    -- Check if the issue needs to be assigned
            IF @AssignedDepartmentID <> @CurrentDepartmentID
            BEGIN
            IF @PrevAssigneeUserID IS NOT NULL
            BEGIN
                    -- Check if the PrevAssigneeUserID belongs to the newly assigned department
            IF EXISTS(SELECT * FROM UserDepartments WHERE DepartmentID = @AssignedDepartmentID AND UserID = @PrevAssigneeUserID)
            BEGIN
            SET @AssigneeUserID = @PrevAssigneeUserID
            SET @AssigneeAnyUser = 0
            END
                    END

            IF @AssigneeUserID IS NULL
                    BEGIN
            -- Check the routing for the newly assigned department
            SELECT @AssigneeUserID = AssigneeUserID, @AssigneeAnyUser = AssigneeAnyUser
            FROM Departments
            WHERE DepartmentID = @AssignedDepartmentID
                    END
            END
                    END
*/

            long prevAssigneeUserID = -1;
            c = db.rawQuery("SELECT PrevAssigneeUserID FROM IssueTracks " +
                            "WHERE IssueID = ? ORDER BY LastUpdateDate DESC LIMIT 1",
                    new String[]{String.valueOf(detailedIssue.getId())}
            );
            if (c.moveToFirst())
                prevAssigneeUserID = c.getLong(0);
            c.close();
            if (keep) {
                if (assignedDepartmentID != detailedIssue.getCurrentDepartmentId()) {
                    assigneeUserIDToKeep = assigneeUserID;
                    assigneeUserID = -1;
                    assigneeAnyUser = false;
                } else {
                    assigneeUserIDToKeep = prevAssigneeUserID;
                }
            } else {
                assigneeAnyUser = false;
                assigneeUserID = -1;
                prevAssigneeUserID = -1;
            }

//            -- Check if the issue needs to be assigned
            if (assignedDepartmentID != detailedIssue.getCurrentDepartmentId()) {
                if (prevAssigneeUserID > 0) {
//                    -- Check if the PrevAssigneeUserID belongs to the newly assigned department
//                    SELECT * FROM UserDepartments WHERE DepartmentID = @AssignedDepartmentID AND UserID = @PrevAssigneeUserID)
                    c = db.rawQuery("SELECT * FROM UserDepartments WHERE DepartmentID = ? " +
                                    "AND UserID = ?",
                            new String[]{String.valueOf(assignedDepartmentID),
                                    String.valueOf(prevAssigneeUserID)}
                    );
                    if (c.moveToFirst()) {
                        assigneeUserID = prevAssigneeUserID;
                        assigneeAnyUser = false;
                    }
                }
                if (assigneeUserID <= 0) {
//                    -- Check the routing for the newly assigned department
                    c = db.rawQuery("SELECT AssigneeUserID, AssigneeAnyUser " +
                            "FROM Departments " +
                            "WHERE DepartmentID = ?", new String[]{String.valueOf(assignedDepartmentID)});
                    if (c.moveToFirst()) {
                        assigneeUserID = c.getLong(c.getColumnIndex("AssigneeUserID"));
                        assigneeAnyUser = c.getString(c.getColumnIndex("AssigneeAnyUser")) != null
                                && c.getString(c.getColumnIndex("AssigneeAnyUser")).equals("true");
                    }
                    c.close();
                }


            }
        }

        c = db.rawQuery("Select Max(IssueTrackID) from Issuetracks", null);
        long count = 0;
        if (c.moveToFirst())
            count = c.getLong(0);
        c.close();
        count++;
        ContentValues cv = new ContentValues();
        cv.put("IssueID", detailedIssue.getId());
        cv.put("IssueTrackID", count);
        cv.put("DeviceID", user.getDeviceId());
        String uuid = UUID.randomUUID().toString();
        cv.put("IssueTrackGUI", uuid);
        cv.put("SiteID", user.getSiteID());
        cv.put("ActionID", idAction);
        cv.put("PriorityID", detailedIssue.getPriorId());
        cv.put("DepartmentID", assignedDepartmentID > 0 ? assignedDepartmentID : null);
        cv.put("AssigneeUserID", assigneeUserID > 0 ? assigneeUserID : null);
        cv.put("AssigneeAnyUser", String.valueOf(assigneeAnyUser));
        cv.put("PrevAssigneeUserID", assigneeUserIDToKeep > 0 ? assigneeUserIDToKeep : null);
        if (notes != null)
            cv.put("Notes", notes);

        // TODO: not find
//        cv.put("HoursWorked", null);
        cv.put("Billable", String.valueOf(billable));
        // TODO: not use on mobile
//        cv.put("InvoiceNumber", "");
        // TODO: not use on mobile
//        cv.put("CompensationID", "");
      //   cv.putNull("MonetaryValue");
//        cv.put("MonetaryValueByUser", "");
        if (causeID > 0)
            cv.put("CauseID", causeID);

        // TODO: not use on mobile
//        cv.put("PartsOrderID", "");
        // TODO: not use on mobile???
//        cv.put("EmployeeFirstName", "");
//        cv.put("EmployeeLastName", "");
        cv.put("UpdateByIVR", String.valueOf(false));
        cv.put("LastUpdateDate", System.currentTimeMillis());
        cv.put("LastUpdateUserID", user.getId());
        cv.put("UpdateBySystem", String.valueOf(false));
        cv.put("IsDirty", 1);
        cv.put("IsTombstone", 0);
        cv.put("UpdateByInspector", String.valueOf(false));
        cv.put("IgnoreDiscrepancy", String.valueOf(false));
        cv.put("LastEditDate", System.currentTimeMillis());
        cv.put("CreationDate", System.currentTimeMillis());

        //cdaly added
        String myguid = UUID.randomUUID().toString();
        cv.put("gUID", myguid);
        //cdaly end



//        db.insert("issuetracks", null, cv);
//        INSERT INTO IssueTracks (IssueID, ActionID, PriorityID, DepartmentID, AssigneeUserID, AssigneeAnyUser, PrevAssigneeUserID, Notes, HoursWorked, Billable,
//                InvoiceNumber, CompensationID, MonetaryValue, MonetaryValueByUser, CauseID, PartsOrderID, EmployeeFirstName, EmployeeLastName,
//                CorpEmployeeID, UpdateByIVR, LastUpdateDate, LastUpdateUserID, UpdateBySystem)
//        VALUES (@IssueID, @ActionID, @PriorityID, @AssignedDepartmentID, @AssigneeUserID, @AssigneeAnyUser, @AssigneeUserIDToKeep, @Notes, @HoursWorked, @Billable,
//        @InvoiceNumber, @CompensationID, @MonetaryValue, @MonetaryValueByUser, @CauseID, @PartsOrderID, @EmployeeFirstName, @EmployeeLastName,
//        @CorpEmployeeID, @UpdateByIVR, @CurrentDate, @LastUpdateUserID, @UpdateBySystem)

        long id = db.insert("IssueTracks", null, cv);

        if (id <= 0) return "Error!";

/*
        -- A little more about FileAttachments, what is UID and what we update?
        --File attachments are uploaded into the FileAttachments table before the IssueTrack record is created,
                --along with a UID (randomly generated unique identifier) to identify the files uploaded.
                --  For example, if 3 files are being uploaded then 3 records will be created in the FileAttachments table,
        --   all of them using the same UID. Then when the IssueTrack record is created, the column IssueTrackID in
                --   the FileAttachments table for those 3 file attachments will be updated with the corresponding IssueTrackID.
                --   This is used for the web app logic. If a different logic is being used to upload file attachments from the
                --   mobile app, then this piece of logic can be disregarded.
*/
        cv.clear();


        if (fileAttach != null) {
            if (fileAttach != null && fileAttach.getListPath() != null) {
                c = db.rawQuery("Select FileAttachmentID from FileAttachments ORDER BY FileAttachmentID DESC LIMIT 1", null);
//                c = db.rawQuery("Select Max(FileAttachmentID) from FileAttachments", null);

                long countFile = 0;
                if (c.moveToFirst())
                    countFile = c.getLong(0);
                c.close();
                for (int i = 0; i < fileAttach.getListPath().size(); i++) {
                    String name = fileAttach.getListPath().get(i).getFilename();
//                for (String name : fileAttach.getData().getListPath()) {
                    String uid = UUID.randomUUID().toString();
                    //        File file = new File(uri.getPath());

                    File file = new File(fileAttach.getListPath().get(i).getPath());
                    try {

                        byte[] fileBytes = Files.readFileLimit(file, user.getAttachFileLength());
                        if (fileBytes != null) {
                            cv.clear();
                            cv.put("IsDirty", 1);
                            cv.put("IsTombstone", 0);

                            countFile++;
                            cv.put("FileAttachmentID", countFile);
                            cv.put("SiteID", user.getSiteID());
                            cv.put("IssueTrackID", count);
                            cv.put("FileNme", file.getName());
                            cv.put("FileAttachment", fileBytes);
                            int index = name.lastIndexOf(".");

                            String ext = index < name.length() ? name.substring(index) : "";
                            cv.put("FileExtension", ext);
                            cv.put("FileSize", fileBytes.length);
                            cv.put("UID", uid);
                            cv.put("CreationDate", System.currentTimeMillis());
                            //cdaly added
                            String myfaguid = UUID.randomUUID().toString();
                            cv.put("gUID", myfaguid);
                            cv.put("IssueTrackGUI", uuid);
                            //cdaly end
                            long idfile = db.insert("FileAttachments", null, cv);
                            Log.d("file", "id=" + idfile);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (file.getAbsolutePath().contains(context.getPackageName())) {
                        file.delete();
                    }
                }
            }
        }

/*
        -- Update Issue
        UPDATE Issues
        SET StatusID = @NewStatusID, PreviousDepartmentID = @CurrentDepartmentID, CurrentDepartmentID = @AssignedDepartmentID, ConcurrencyID = @ConcurrencyID + 1, LastEditDate = getdate()
        WHERE IssueID = @IssueID
*/
        cv.clear();
        cv.put("StatusID", statusNewID);
        cv.put("PreviousDepartmentID", detailedIssue.getCurrentDepartmentId());
        cv.put("CurrentDepartmentID", assignedDepartmentID > 0 ? assignedDepartmentID : null);
        //TODO add DepartmentDesc;
        cv.put("ConcurrencyID", detailedIssue.getConcurrencyID() + 1);
        cv.put("LastEditDate", System.currentTimeMillis());
        cv.put("IsDirty", 1);
        int countUptade = db.update("Issues", cv, "IssueID = ?", new String[]{String.valueOf(detailedIssue.getId())});
        cv.remove("LastEditDate");
        cv.put("LastUpdateDate", System.currentTimeMillis());
        {
            Cursor cursor = db.query("Statuses", new String[]{"StatusDesc"}, "StatusID = " + statusNewID, null, null, null, null);
            if (cursor.moveToFirst()) {
                cv.put("StatusDesc", cursor.getString(0));
            } else {
                cv.put("StatusDesc", "");
            }

            cursor.close();
        }
        {
            String attachmentsQuery = "SELECT Count(*) FROM FileAttachments " +
                    " WHERE IssueTrackID IN (SELECT IssueTrackID FROM IssueTracks WHERE IssueID = ?)";
            Cursor cursor = db.rawQuery(attachmentsQuery, new String[]{String.valueOf(detailedIssue.getId())});

                int countAttach= cursor.getCount();
            cv.put("CountAttach", countAttach);
            cursor.close();
        }
        cv.put("AssigneeUserID", assigneeUserID > 0 ? assigneeUserID : 0);
        {
            Cursor cursor = db.query("actions", new String[]{"ActionCode"}, "ActionID = " + idAction, null, null, null, null);
            if (cursor.moveToFirst()) {
                String action =cursor.getString(0);
                if (action.equals("Alert") || action.equals("PreAlert"))
                cv.put("AlertDesc", cursor.getString(0));
            }
            cursor.close();
        }
        if (notes != null)
            cv.put("Notes", notes);



        int countUptadeSecond = db.update("mobileissues", cv, "IssueID = ?", new String[]{String.valueOf(detailedIssue.getId())});

        return id > 0 && countUptade > 0 ? null : "Error!";
    }

    @Deprecated
    public static void getIssue(long issueID, long userID, int hourDateFormat, int statusID, SQLiteDatabase db) {
        if (db == null) {
            return;
        }
        long assignedDepartmentID, userDepartmentID, issueClassID, canView;
        boolean isClosed;
        Cursor c = db.rawQuery("select SiteID FROM sysparms", null);
        if (!c.moveToFirst()) {
            c.close();
            return;
        }
        int siteID = c.getInt(c.getColumnIndex("SiteID"));
        c.close();
        c = db.rawQuery("SELECT DepartmentID FROM IssueTracks WHERE IssueID = ? AND SiteID = ? ORDER BY LastUpdateDate DESC LIMIT 1", new String[]{String.valueOf(issueID), String.valueOf(siteID)});
        if (!c.moveToFirst()) {
            c.close();
            return;
        }
        assignedDepartmentID = c.getLong(c.getColumnIndex("DepartmentID"));
        Log.d("db", "" + assignedDepartmentID);
        c.close();

        c = db.rawQuery("SELECT Closed FROM Statuses WHERE StatusID = ? AND SiteID = ?;", new String[]{String.valueOf(statusID), String.valueOf(siteID)});
        if (!c.moveToFirst()) {
            c.close();
            return;
        }
        isClosed = Boolean.parseBoolean(c.getString(c.getColumnIndex("Closed")));

        //TODO WTF???

    }


    public static String getUserPosition(long userId, SQLiteDatabase db) {
        String q = String.format("select userDesc from users WHERE UserID = '%d';", userId);
        Cursor c = db.rawQuery(q, null);
        c.moveToFirst();
        Log.d("query userDesc", q);
        String ans = c.getString(c.getColumnIndex("UserDesc"));
        c.close();
        return ans;
    }

    public static ArrayList<HistoryModel> getHistory(long issueID, UserModel model, SQLiteDatabase db, String limit) {
        if (db == null) {
            return null;
        }

        final String where = String.format(" WHERE IssueTracks.IssueID = %d ", issueID);
        final String from = " FROM IssueTracks ";
        final String select = "SELECT IssueTracks.IssueTrackID, IssueTrackGUI, IgnoreDiscrepancy, IssueTracks.DeviceID, IssueTracks.ActionID, " +
                "ActionDesc , Priorities.PriorityID AS PriorityID, CauseDesc, Priorities.PriorityDesc AS PriorityDesc, IssueTracks.DepartmentID, Departments.DepartmentDesc, Departments.DepartmentID,  " +
                "IssueTracks.AssigneeUserID, IssueTracks.AssigneeAnyUser, IssueTracks.LastUpdateDate, IssueTracks.LastUpdateUserID, Notes ";
        final String join = "INNER JOIN Actions ON IssueTracks.ActionID = Actions.ActionID " +
                "LEFT OUTER JOIN Causes ON IssueTracks.CauseID = Causes.CauseID AND IssueTracks.SiteID = Causes.SiteID " +
                "LEFT OUTER JOIN Priorities ON IssueTracks.PriorityID = Priorities.PriorityID AND IssueTracks.SiteID = Priorities.SiteID " +
                "LEFT OUTER JOIN Departments ON IssueTracks.DepartmentID = Departments.DepartmentID AND IssueTracks.SiteID = Departments.SiteID " +
                "LEFT OUTER JOIN Users AssigneeUser ON IssueTracks.AssigneeUserID = AssigneeUser.UserID AND IssueTracks.SiteID = AssigneeUser.SiteID " +
                "LEFT OUTER JOIN Users ON IssueTracks.LastUpdateUserID = Users.UserID";
        final String sort = " ORDER BY IssueTracks.LastUpdateDate ";

        Cursor c = db.rawQuery(select + from + join + where + sort + (limit != null ? limit : ""), null);
        Log.d("query history ", "" + select + from + join + where + sort);
        ArrayList<HistoryModel> history = new ArrayList<HistoryModel>();
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }
        HistoryModel h;
        do {
            h = new HistoryModel();
            h.setActionDesc(c.getString(c.getColumnIndex("ActionDesc")));
            h.setPriorityDesc(c.getString(c.getColumnIndex("PriorityDesc")));
            h.setDepartmentDesc(c.getString(c.getColumnIndex("DepartmentDesc")));
            h.setDepartmentID(c.getLong(c.getColumnIndex("DepartmentID")));
            h.setAssigneeUserID(c.getLong(c.getColumnIndex("AssigneeUserID")));
            h.setLastUpdateDate(c.getLong(c.getColumnIndex("LastUpdateDate")));
            h.setLastUpdateUserID(c.getLong(c.getColumnIndex("LastUpdateUserID")));

            h.setNotes(c.getString(c.getColumnIndex("Notes")));
            h.setAssigneeAnyUser(c.getString(c.getColumnIndex("AssigneeAnyUser")));
            if (h.getAssigneeUserID() == 0) {
                if (c.getString(c.getColumnIndex("AssigneeAnyUser")).equals("true")) {
                    h.setAssignedUser("Any user");
                } else {
                    h.setAssignedUser("Unassigned");
                }
            } else {
                h.setAssignedUser(getUserPosition(h.getAssigneeUserID(), db));
            }

            h.setUpdatedByUser(getUserPosition(h.getLastUpdateUserID(), db));
            history.add(h);

        } while (c.moveToNext());
        c.close();
        return history;
    }


    public static ArrayList<FileModel> getAttachments(long issueId, SQLiteDatabase db) {
        ArrayList<FileModel> attachments = new ArrayList<FileModel>();
//OnboarD Changed
        // changed where to use IssueTrackGUI instead of IssueTrackid as issutrackid is not unique
       /* String attachmentsQuery = String.format("SELECT IssueTrackID, FileAttachmentID, FileNme, FileSize," +
                " FileAttachment, FileExtension, FileSize FROM FileAttachments " +
                " WHERE IssueTrackID IN (SELECT IssueTrackID FROM IssueTracks WHERE IssueID = %d);", issueId);
*/
        String attachmentsQuery = String.format("SELECT IssueTrackID, FileAttachmentID, FileNme, FileSize," +
                " FileAttachment, FileExtension, FileSize FROM FileAttachments " +
                " WHERE IssueTrackGUI IN (SELECT IssueTrackGUI FROM IssueTracks WHERE IssueID = %d)", issueId);


        Cursor c = db.rawQuery(attachmentsQuery, null);
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }
        FileModel f;
        do {
            f = new FileModel();
            f.setExtension(c.getString(c.getColumnIndex("FileExtension")));
            f.setFile(c.getBlob(c.getColumnIndex("FileAttachment")));
            f.setFilesize(c.getLong(c.getColumnIndex("FileSize")));
            f.setFilename(c.getString(c.getColumnIndex("FileNme")));
            f.setId(c.getLong(c.getColumnIndex("FileAttachmentID")));
            attachments.add(f);

        } while (c.moveToNext());
        c.close();
        return attachments;
    }

    public static boolean getAttachmentsBoolean(long issueId, SQLiteDatabase db) {
        ArrayList<FileModel> attachments = new ArrayList<FileModel>();
        //Onboard Changed to use IssueTrackGUI
     /*   String attachmentsQuery = String.format("SELECT IssueTrackID, FileAttachmentID, FileNme, FileSize," +
                " FileAttachment, FileExtension, FileSize FROM FileAttachments " +
                " WHERE IssueTrackID IN (SELECT IssueTrackID FROM IssueTracks WHERE IssueID = %d);", issueId);
*/
        String attachmentsQuery = String.format("SELECT IssueTrackID, FileAttachmentID, FileNme, FileSize," +
                " FileAttachment, FileExtension, FileSize FROM FileAttachments " +
                " WHERE IssueTrackGUI IN (SELECT IssueTrackGUI FROM IssueTracks WHERE IssueID = %d);", issueId);


        Cursor c = db.rawQuery(attachmentsQuery, null);
        if (!c.moveToFirst()) {
            c.close();
            //TODO aware null in future?
            return false;
        }
        c.close();
        return true;

    }

    public static FileModel getAttachment(long id, SQLiteDatabase db) {

        Cursor c = db.rawQuery("SELECT FileAttachmentID, FileNme, FileSize," +
                " FileAttachment, FileExtension FROM FileAttachments " +
                " WHERE FileAttachmentID = ?", new String[]{String.valueOf(id)});
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }
        FileModel f;
        f = new FileModel();
        f.setExtension(c.getString(c.getColumnIndex("FileExtension")));
        f.setFile(c.getBlob(c.getColumnIndex("FileAttachment")));
        f.setFilesize(c.getLong(c.getColumnIndex("FileSize")));
        f.setFilename(c.getString(c.getColumnIndex("FileNme")));
        f.setId(c.getLong(c.getColumnIndex("FileAttachmentID")));


        c.close();
        return f;
    }

    /**
     * Get byte array for file.
     *
     * @param id - file id.
     * @param db
     * @return if exist byte[], else null
     */
    public static byte[] getImage(String id, SQLiteDatabase db) {
        String attachmentsQuery = String.format("SELECT FileAttachment FROM fileattachments WHERE FileAttachmentID = %s;", id);
        Cursor c = db.rawQuery(attachmentsQuery, null);
        if (!c.moveToFirst()) {
            c.close();
            //TODO aware null in future?
            return null;
        }
        byte[] image;

        image = c.getBlob(c.getColumnIndex("FileAttachment"));
        c.close();
        return image;
    }
}

