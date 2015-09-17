package util;

public class RawQueries {
    /**
     * Request getIssue details
     */
    public static final String getIssueDetails = "select " +
            "iss.IssueID, iss.ConcurrencyID, iss.StatusID,iss.LocationID, iss.IssueTypeID, iss.ReportBy, " +
            "iss.CreateDate, iss.CurrentDepartmentID,iss.LocationOwnerDepartmentID, igroup.IssueClassID, " +
            "NotesLast.Notes as NotesLast, itype.IssueTypeDesc, pr.PriorityID, " +
            "stat.StatusDesc, stat.StatusID, loc.LocationDesc, pr.PriorityLevel, u.UserDesc, u.OfficeNumber, " +
            "u.Extension, u.MobileNumber, u.Pager, deck.DeckDesc, " +
            "deppos.PositionName, defects.DefectName, trans.TransverseName, zon.ZoneName, " +
            "fz.FireZoneName, dep.DepartmentDesc as DepartmentDescCrew, iss.GuestServiceIssue, " +
            "iss.RequiresGuestCallback, " +
            "iss.GuestFirstName, iss.GuestLastName, iss.DateFirstExperienced, pi.Cabin, pi.BookingID, " +
            "pi.DebarkationDate, cs.ComplaintSeverityDesc, aa.CompName, aa.FuncNo, aa.FuncDescr, aa.SerialNo, " +
            "be.UserDesc as beUserDesc, AlertID, " +
            "iss.CreateByDepartmentID as CreateByDepartmentID, depCreate.DepartmentDesc as DepartmentDescCreate, " +
            "iss.DeviceFavorite as DeviceFavorite, iss.OpenedOnDevice as OpenedOnDevice " +
            "FROM issues  AS iss " +
            //add (!)
            "LEFT join (Select  ActionCode as AlertID, itr.ActionID as ActionID, iss.IssueID as IssueID  FROM issues AS iss LEFT join issuetracks as itr " +
            "ON iss.IssueID = itr.IssueID " +
            "INNER JOIN actions on actions.ActionID = itr.ActionID " +
            "where ActionCode = 'PreAlert' OR ActionCode = 'Alert' group by (iss.IssueID)) as alert ON  (iss.IssueID = alert.IssueID) " +
            //end add (!)
            "LEFT join (SELECT Notes  FROM IssueTracks " +
            "WHERE IssueTracks.IssueID = ? AND length(Notes)>0 ORDER BY IssueTracks.LastUpdateDate DESC LIMIT '1') as NotesLast " +

            "LEFT JOIN statuses AS stat ON iss.StatusID = stat.StatusID " +
            "LEFT JOIN locations AS loc ON iss.LocationID = loc.LocationID " +

            "left join users as be on iss.OnBehalfOfUserID = be.UserID " +
            "LEFT JOIN issuetypes AS itype ON iss.IssueTypeID = itype.IssueTypeID " +
            "LEFT JOIN issuegroups AS igroup ON itype.IssueGroupID = igroup.IssueGroupID " +
            "LEFT JOIN issuetracks AS itracks ON iss.IssueID = itracks.IssueID AND itracks.ActionID = 1 " +
            "LEFT JOIN priorities AS pr ON itracks.PriorityID = pr.PriorityID " +
            "LEFT JOIN users AS u ON (iss.CreateByUserID = u.UserID) " +
            "LEFT JOIN departments AS dep ON (iss.ReportByDepartmentID = dep.DepartmentID) " +
            "LEFT JOIN departments AS depCreate ON (iss.CreateByDepartmentID = depCreate.DepartmentID) " +
            "LEFT JOIN departmentpositions AS deppos ON (iss.ReportByPositionID = deppos.PositionID) " +
            "LEFT JOIN decks AS deck ON deck.DeckID = iss.DeckID " +
            "LEFT JOIN defects ON (iss.DefectID = defects.DefectID) " +
            "LEFT JOIN transverses AS trans ON (iss.TransverseID = trans.TransverseID) " +
            "LEFT JOIN zones AS zon ON (iss.ZoneID = zon.ZoneID) " +
            "LEFT JOIN firezones AS fz ON (iss.FireZoneID = fz.FireZoneID) " +
            "LEFT JOIN passengerInfo AS pi ON (iss.PassengerInfoID = pi.PassengerInfoID) " +
            "LEFT JOIN complaintseverities AS cs ON (iss.SeverityID = cs.ComplaintSeverityID) " +
            "LEFT JOIN (amoscomponenttype as a0 INNER JOIN amoscomponentunit as a1 ON " +
            "a1.CompTypeID =  a0.CompTypeID INNER JOIN amosissuescomponentunit as a2 on a1.CompID = a2.AmosCompID) as aa " +
            "ON (aa.IssueID = iss.IssueID) " +
            "WHERE iss.IssueID = ? group by (iss.IssueID)";


    public static final String CREATE_BY_USER = "select iss.IssueID as _id,iss.StatusID as StatusID, " +
            "iss.ZoneID as ZoneID, iss.IssueTypeID as TypeID, iss.DeckID as DeckID, iss.FireZoneID as FireZoneID,iss.LocationID as LocationID, iss.CreateDate as CreateDate," +
            " iss.OpenedOnDevice as OpenedOnDevice, t.Notes as Notes, itype.IssueTypeDesc as IssueTypeDesc, " +
            "stat.StatusDesc as StatusDesc, iss.CreateByDepartmentID as DepartmentID, " +
            "loc.LocationDesc as LocationDesc, " +
            "pr.PriorityLevel as PriorityLevel, " +
            "iss.DeviceFavorite as DeviceFavorite, " +
            "pr.PriorityID as PriorityID, AlertID, " +
            "locgroups.LocationGroupID as LocationGroupID " +
            "FROM issues AS iss LEFT JOIN statuses AS stat ON iss.StatusID = stat.StatusID " +
            //add (!)
            "LEFT join (Select  ActionCode as AlertID, itr.ActionID as ActionID, iss.IssueID as IssueID  FROM issues AS iss LEFT join issuetracks as itr " +
            "ON iss.IssueID = itr.IssueID " +
            "INNER JOIN actions on actions.ActionID = itr.ActionID " +
            "where ActionCode = 'PreAlert' OR ActionCode = 'Alert' group by (iss.IssueID)) as alert ON  (iss.IssueID = alert.IssueID) " +
            //end add (!)
            "LEFT JOIN locations_locationgroups AS locgroups ON iss.LocationID = locgroups.LocationID " +
            "LEFT JOIN locations AS loc ON iss.LocationID = loc.LocationID " +
            "LEFT JOIN issuetypes AS itype ON iss.IssueTypeID = itype.IssueTypeID " +
            "INNER JOIN issuetracks AS itracks ON (iss.IssueID = itracks.IssueID) " +
            "and (iss.CreateByUserID = ?) " +
            "inner join  (select   issueid, max(LastUpdateDate) as lastupdatedate " +
            "from issuetracks group by issueid) as t1 on t1.IssueID = itracks.IssueID and t1.lastupdatedate = itracks.LastUpdateDate " +
            "LEFT JOIN priorities AS pr ON itracks.PriorityID = pr.PriorityID " +
            "inner join (select notes, issueid from issuetracks  where actionid = 1) as t on t.IssueID = iss.issueid group by (_id)";

    public static final String CREATE_BY_USER_V2 = "select iss.IssueID as _id, " +
            "StatusID , " +
            "ZoneID, " +
            "DeckID, " +
            "FireZoneID, " +
            "AlertDesc, " +
            "CreateDate," +
            "OpenedOnDevice, " +
            "Notes,  " +
            "IssueTypeDesc, " +
            "StatusDesc, " +
            "CreateByDepartmentID as DepartmentID, " +
            "LocationDesc, " +
            "CountAttach, " +
            "DeviceFavorite, " +
            "LocationID, " +
            "IssueTypeID, "+
            "PriorityID " +
            "FROM mobileissues AS iss " +
            "where CreateByUserID = ?";

    public static final String CREATE_BY_USER_COUNT = "select Count (distinct iss.IssueID) as countIssues, " +
            "Count (CASE WHEN CreateDate > ? then 1 ELSE NULL END) as countIssuesNew, " +
            "Count (CASE WHEN AlertDesc = 'Alert' then 1 ELSE NULL END) as countAlert, " +
            "Count (CASE WHEN AlertDesc = 'PreAlert' then 1 ELSE NULL END) as countPreAlert " +
            "FROM mobileissues AS iss " +
            "where CreateByUserID = ?";


    public static final String DEPARTMENT_ASSIGNED = "select iss.IssueID as _id,iss.StatusID as StatusID, " +
            "iss.ZoneID as ZoneID, iss.IssueTypeID as TypeID, iss.DeckID as DeckID, iss.FireZoneID as FireZoneID,iss.LocationID as LocationID, iss.CreateDate as CreateDate," +
            " iss.OpenedOnDevice as OpenedOnDevice, t.Notes as Notes, itype.IssueTypeDesc as IssueTypeDesc, " +
            "stat.StatusDesc as StatusDesc, iss.CreateByDepartmentID as DepartmentID, " +
            "loc.LocationDesc as LocationDesc, " +
            "iss.DeviceFavorite as DeviceFavorite, " +
            "pr.PriorityLevel as PriorityLevel, " +
            "pr.PriorityID as PriorityID, AlertID, " +
            "locgroups.LocationGroupID as LocationGroupID " +
            "FROM issues AS iss " +
            //add (!)
            "LEFT join (Select  ActionCode as AlertID, itr.ActionID as ActionID, iss.IssueID as IssueID  FROM issues AS iss LEFT join issuetracks as itr " +
            "ON iss.IssueID = itr.IssueID " +
            "INNER JOIN actions on actions.ActionID = itr.ActionID " +
            "where ActionCode = 'PreAlert' OR ActionCode = 'Alert' group by (iss.IssueID)) as alert ON  (iss.IssueID = alert.IssueID) " +
            //end add (!)
            "LEFT JOIN locations_locationgroups AS locgroups ON iss.LocationID = locgroups.LocationID " +
            "LEFT JOIN statuses AS stat ON iss.StatusID = stat.StatusID " +
            "LEFT JOIN locations AS loc ON iss.LocationID = loc.LocationID " +
            "LEFT JOIN issuetypes AS itype ON iss.IssueTypeID = itype.IssueTypeID " +
            "INNER JOIN issuetracks AS itracks ON (iss.IssueID = itracks.IssueID) AND (itracks.DepartmentID = ?)  and ( iss.StatusID <> 7) AND ((itracks.AssigneeUserID <> ?) or itracks.AssigneeUserID is null) " +
            "inner join  (select   issueid, max(LastUpdateDate) as lastupdatedate from issuetracks group by issueid) as t1 on t1.IssueID = itracks.IssueID and t1.lastupdatedate = itracks.LastUpdateDate " +
            "LEFT JOIN priorities AS pr ON itracks.PriorityID = pr.PriorityID " +
            "INNER JOIN (select notes, issueid from issuetracks  where actionid = 1) as t on t.IssueID = iss.issueid group by (_id)";


    public static final String DEPARTMENT_ASSIGNED_V2 =
            "select IssueID as _id, " +
                    "StatusID, " +
                    "ZoneID, " +
                    "DeckID, " +
                    "FireZoneID, " +
                    "AlertDesc, " +
                    "CreateDate, " +
                    "OpenedOnDevice, " +
                    "Notes, " +
                    "IssueTypeDesc, " +
                    "StatusDesc, " +
                    "CreateByDepartmentID as DepartmentID, " +
                    "LocationDesc, " +
                    "DeviceFavorite, " +
                    "CountAttach, " +
                    "LocationID, " +
                    "IssueTypeID, "+
                    "PriorityID " +
                    "FROM mobileissues AS iss " +
                    "where CurrentDepartmentID = ?  AND  AssigneeUserID <> ?";


    public static final String DEPARTMENT_ASSIGNED_COUNT = "select Count (distinct iss.IssueID) as countIssues, " +
            "Count (CASE WHEN CreateDate > ? then 1 ELSE NULL END) as countIssuesNew, " +
            "Count (CASE WHEN AlertDesc = 'Alert' then 1 ELSE NULL END) as countAlert, " +
            "Count (CASE WHEN AlertDesc = 'PreAlert' then 1 ELSE NULL END) as countPreAlert " +
            "FROM mobileissues AS iss " +
            "where CurrentDepartmentID = ?  AND  AssigneeUserID <> ?";


    public static final String CREATE_BY_DEPARTMENT_V2 = "select IssueID as _id, " +
            "StatusID, " +
            "ZoneID, " +
            "DeckID, " +
            "FireZoneID, " +
            "AlertDesc, " +
            "CreateDate, " +
            "OpenedOnDevice, " +
            "Notes, " +
            "IssueTypeDesc, " +
            "StatusDesc, " +
            "CreateByDepartmentID as DepartmentID, " +
            "LocationDesc, " +
            "DeviceFavorite, " +
            "CountAttach, " +
            "LocationID, " +
            "IssueTypeID, "+
            "PriorityID " +

            "FROM mobileissues AS iss " +
            "where CreateByDepartmentID = ?";

    public static final String CREATE_BY_DEPARTMENT = "select iss.IssueID as _id,iss.StatusID as StatusID, " +
            "iss.ZoneID as ZoneID, iss.IssueTypeID as TypeID, iss.DeckID as DeckID, iss.FireZoneID as FireZoneID,iss.LocationID as LocationID, iss.CreateDate as CreateDate," +
            " iss.OpenedOnDevice as OpenedOnDevice, t.Notes as Notes, itype.IssueTypeDesc as IssueTypeDesc, " +
            "stat.StatusDesc as StatusDesc, iss.CreateByDepartmentID as DepartmentID, " +
            "loc.LocationDesc as LocationDesc, " +
            "iss.DeviceFavorite as DeviceFavorite, " +
            "pr.PriorityLevel as PriorityLevel, " +
            "pr.PriorityID as PriorityID, " +
            "locgroups.LocationGroupID as LocationGroupID, " +
            "AlertID " +
            "FROM issues AS iss " +
            //add (!)
            "LEFT join (Select  ActionCode as AlertID, itr.ActionID as ActionID, iss.IssueID as IssueID  FROM issues AS iss LEFT join issuetracks as itr " +
            "ON iss.IssueID = itr.IssueID " +
            "INNER JOIN actions on actions.ActionID = itr.ActionID " +
            "where ActionCode = 'PreAlert' OR ActionCode = 'Alert' group by (iss.IssueID)) as alert ON  (iss.IssueID = alert.IssueID) " +
            //end add (!)
            "LEFT JOIN locations_locationgroups AS locgroups ON iss.LocationID = locgroups.LocationID " +
            "LEFT JOIN statuses AS stat ON iss.StatusID = stat.StatusID " +
            "LEFT JOIN locations AS loc ON iss.LocationID = loc.LocationID " +
            "LEFT JOIN issuetypes AS itype ON iss.IssueTypeID = itype.IssueTypeID " +
            "INNER JOIN issuetracks AS itracks ON (iss.IssueID = itracks.IssueID) AND (iss.CreateByDepartmentID = ?)  and ( iss.StatusID <> 7) " +
            "inner join  (select   issueid, max(LastUpdateDate) as lastupdatedate from issuetracks group by issueid) as t1 " +
            "on t1.IssueID = itracks.IssueID and t1.lastupdatedate = itracks.LastUpdateDate " +
            "LEFT JOIN priorities AS pr ON itracks.PriorityID = pr.PriorityID " +
            "INNER JOIN (select notes, issueid from issuetracks  where actionid = 1) as t on t.IssueID = iss" +
            ".issueid group by (_id)";

    public static final String CREATE_BY_DEPARTMENT_COUNT = "select Count (distinct iss.IssueID) as countIssues, " +
            "Count (CASE WHEN CreateDate > ? then 1 ELSE NULL END) as countIssuesNew, " +
            "Count (CASE WHEN AlertDesc = 'Alert' then 1 ELSE NULL END) as countAlert, " +
            "Count (CASE WHEN AlertDesc = 'PreAlert' then 1 ELSE NULL END) as countPreAlert " +
            "FROM mobileissues AS iss " +
            "where CreateByDepartmentID = ?";

    public static final String USER_ASSIGNED = "select iss.IssueID as _id,iss.StatusID as StatusID, " +
            "iss.ZoneID as ZoneID, iss.IssueTypeID as TypeID, iss.DeckID as DeckID, iss.FireZoneID as FireZoneID,iss.LocationID as LocationID, iss.CreateDate as CreateDate," +
            " iss.OpenedOnDevice as OpenedOnDevice, t.Notes as Notes, itype.IssueTypeDesc as IssueTypeDesc, " +
            "stat.StatusDesc as StatusDesc, iss.CreateByDepartmentID as DepartmentID, " +
            "loc.LocationDesc as LocationDesc, " +
            "iss.DeviceFavorite as DeviceFavorite, " +
            "pr.PriorityLevel as PriorityLevel, " +
            "pr.PriorityID as PriorityID, " +
            "locgroups.LocationGroupID as LocationGroupID, " +
            "AlertID " +
            "FROM issues AS iss " +
            "LEFT JOIN locations_locationgroups AS locgroups ON iss.LocationID = locgroups.LocationID " +
            "LEFT JOIN statuses AS stat ON iss.StatusID = stat.StatusID " +
            "LEFT JOIN locations AS loc ON iss.LocationID = loc.LocationID " +
            "LEFT JOIN issuetypes AS itype ON iss.IssueTypeID = itype.IssueTypeID " +
            //add (!)
            "LEFT join (Select  ActionCode as AlertID, itr.ActionID as ActionID, iss.IssueID as IssueID  FROM issues AS iss LEFT join issuetracks as itr " +
            "ON iss.IssueID = itr.IssueID " +
            "INNER JOIN actions on actions.ActionID = itr.ActionID " +
            "where ActionCode = 'PreAlert' OR ActionCode = 'Alert' group by (iss.IssueID)) as alert ON  (iss.IssueID = alert.IssueID) " +
            //end add (!)
            "INNER JOIN issuetracks AS itracks ON (iss.IssueID = itracks.IssueID) and " +
            "(itracks.AssigneeUserID = ?) " +
            "inner join (select issueid, MAX(LastUpdateDate) as lastupdatedate from issuetracks " +
            "group by issueid) as t1 on t1.IssueID = itracks.IssueID and " +
            "t1.lastupdatedate = itracks.LastUpdateDate " +
            "LEFT JOIN priorities AS pr ON itracks.PriorityID = pr.PriorityID " +
            "inner join (select   notes, issueid from issuetracks  where actionid = 1) as t " +
            "on t.IssueID = iss.issueid group by (_id)";

    public static final String USER_ASSIGNED_V2 = "select IssueID as _id, " +
            "StatusID, " +
            "ZoneID, " +
            "DeckID, " +
            "FireZoneID, " +
            "AlertDesc, " +
            "CreateDate, " +
            "OpenedOnDevice, " +
            "Notes, " +
            "IssueTypeDesc, " +
            "StatusDesc, " +
            "CreateByDepartmentID as DepartmentID, " +
            "LocationDesc, " +
            "DeviceFavorite, " +
            "CountAttach, " +
            "LocationID, " +
            "IssueTypeID, "+
            "PriorityID " +

            "FROM mobileissues AS iss " +
            "where AssigneeUserID = ?";

    public static final String USER_ASSIGNED_COUNT = "select Count (distinct iss.IssueID) as countIssues, " +
            "Count (CASE WHEN CreateDate > ? then 1 ELSE NULL END) as countIssuesNew, " +
            "Count (CASE WHEN AlertDesc = 'Alert' then 1 ELSE NULL END) as countAlert, " +
            "Count (CASE WHEN AlertDesc = 'PreAlert' then 1 ELSE NULL END) as countPreAlert " +
            "FROM mobileissues AS iss " +
            "where AssigneeUserID = ?";

    public static final String USER_FAVORITE = "select iss.IssueID as _id,iss.StatusID as StatusID, " +
            "iss.ZoneID as ZoneID, iss.IssueTypeID as TypeID, iss.DeckID as DeckID, iss.FireZoneID as FireZoneID,iss.LocationID as LocationID, iss.CreateDate as CreateDate," +
            " iss.OpenedOnDevice as OpenedOnDevice, t.Notes as Notes, itype.IssueTypeDesc as IssueTypeDesc, " +
            "stat.StatusDesc as StatusDesc, iss.CreateByDepartmentID as DepartmentID, " +
            "loc.LocationDesc as LocationDesc, " +
            "iss.DeviceFavorite as DeviceFavorite, " +
            "pr.PriorityLevel as PriorityLevel, " +
            "pr.PriorityID as PriorityID, " +
            "locgroups.LocationGroupID as LocationGroupID, " +
            "AlertID " +
            "FROM issues AS iss " +
            //add (!)
            "LEFT join (Select  ActionCode as AlertID, itr.ActionID as ActionID, iss.IssueID as IssueID  FROM issues AS iss LEFT join issuetracks as itr " +
            "ON iss.IssueID = itr.IssueID " +
            "INNER JOIN actions on actions.ActionID = itr.ActionID " +
            "where ActionCode = 'PreAlert' OR ActionCode = 'Alert' group by (iss.IssueID)) as alert ON  (iss.IssueID = alert.IssueID) " +
            //end add (!)
            "LEFT JOIN locations_locationgroups AS locgroups ON iss.LocationID = locgroups.LocationID " +
            "LEFT JOIN statuses AS stat ON iss.StatusID = stat.StatusID " +
            "LEFT JOIN locations AS loc ON iss.LocationID = loc.LocationID " +
            "LEFT JOIN issuetypes AS itype ON iss.IssueTypeID = itype.IssueTypeID " +
            "INNER JOIN issuetracks AS itracks ON (iss.IssueID = itracks.IssueID) " +
            "inner join (select issueid, MAX(LastUpdateDate) as lastupdatedate from issuetracks " +
            "group by issueid) as t1 on t1.IssueID = itracks.IssueID and " +
            "t1.lastupdatedate = itracks.LastUpdateDate " +
            "LEFT JOIN priorities AS pr ON itracks.PriorityID = pr.PriorityID " +
            "inner join (select   notes, issueid from issuetracks  where actionid = 1) as t " +
            "on t.IssueID = iss.issueid " +
            "WHERE iss.DeviceFavorite ='true' group by (_id)";

    public static final String USER_FAVORITE_V2 = "select IssueID as _id, " +
            "StatusID, " +
            "ZoneID, " +
            "DeckID, " +
            "FireZoneID, " +
            "AlertDesc, " +
            "CreateDate, " +
            "OpenedOnDevice, " +
            "Notes, " +
            "IssueTypeDesc, " +
            "StatusDesc, " +
            "CreateByDepartmentID as DepartmentID, " +
            "LocationDesc, " +
            "LocationID, " +
            "IssueTypeID, "+
            "DeviceFavorite, " +
            "CountAttach, " +
            "PriorityID " +
            "FROM mobileissues AS iss " +
            "where DeviceFavorite = ?";

    /**
     * Count of favorites
     */
    public static final String USER_FAVORITE_COUNT = "select Count (distinct iss.IssueID) as countIssues, " +
            "Count (CASE WHEN CreateDate > ? then 1 ELSE NULL END) as countIssuesNew, " +
            "Count (CASE WHEN AlertDesc = 'Alert' then 1 ELSE NULL END) as countAlert, " +
            "Count (CASE WHEN AlertDesc = 'PreAlert' then 1 ELSE NULL END) as countPreAlert " +
            "FROM mobileissues AS iss " +
            "where DeviceFavorite = 'true'";
}