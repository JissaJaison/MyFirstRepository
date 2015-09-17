package ITSMobile;


import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "Issues")
public class Issues extends SQLiteOfflineEntity

{


    @Key
    @DatabaseField(columnName = "IssueID", canBeNull = false)
    public Integer IssueID;

    @Key
    @DatabaseField(columnName = "SiteID", canBeNull = false)
    public Integer SiteID;

    @DatabaseField(columnName = "IssueTypeID", canBeNull = false)
    public Integer IssueTypeID;

    @DatabaseField(columnName = "DefectID", canBeNull = true)
    public Integer DefectID;

    @DatabaseField(columnName = "LocationID", canBeNull = false)
    public Integer LocationID;

    @DatabaseField(columnName = "DeckID", canBeNull = true)
    public Integer DeckID;

    @DatabaseField(columnName = "TransverseID", canBeNull = true)
    public Integer TransverseID;

    @DatabaseField(columnName = "ZoneID", canBeNull = true)
    public Integer ZoneID;

    @DatabaseField(columnName = "FireZoneID", canBeNull = true)
    public Integer FireZoneID;

    @DatabaseField(columnName = "PreviousDepartmentID", canBeNull = true)
    public Integer PreviousDepartmentID;

    @DatabaseField(columnName = "CurrentDepartmentID", canBeNull = true)
    public Integer CurrentDepartmentID;

    @DatabaseField(columnName = "LocationOwnerDepartmentID", canBeNull = true)
    public Integer LocationOwnerDepartmentID;

    @DatabaseField(columnName = "StatusID", canBeNull = true)
    public Integer StatusID;

    @DatabaseField(columnName = "ReportBy", canBeNull = true)
    public String ReportBy;

    @DatabaseField(columnName = "ReportByDepartmentID", canBeNull = true)
    public Integer ReportByDepartmentID;

    @DatabaseField(columnName = "ReportByPositionID", canBeNull = true)
    public Integer ReportByPositionID;

    @DatabaseField(columnName = "OnBehalfOfUserID", canBeNull = true)
    public Integer OnBehalfOfUserID;

    @DatabaseField(columnName = "OnBehalfOfGeneralGroupID", canBeNull = true)
    public Integer OnBehalfOfGeneralGroupID;

    @DatabaseField(columnName = "DueDate", dataType = DataType.DATE_LONG, canBeNull = true)
    public java.util.Date DueDate;

    @DatabaseField(columnName = "GuestFirstName", canBeNull = true)
    public String GuestFirstName;

    @DatabaseField(columnName = "GuestLastName", canBeNull = true)
    public String GuestLastName;

    @DatabaseField(columnName = "PassengerInfoID", canBeNull = true)
    public Integer PassengerInfoID;

    @DatabaseField(columnName = "GuestServiceIssue", canBeNull = true)
    public String GuestServiceIssue;

    @DatabaseField(columnName = "RequiresGuestCallback", canBeNull = true)
    public String RequiresGuestCallback;

    @DatabaseField(columnName = "DateFirstExperienced", dataType = DataType.DATE_LONG, canBeNull = true)
    public java.util.Date DateFirstExperienced;

    @DatabaseField(columnName = "SeverityID", canBeNull = true)
    public Integer SeverityID;

    @DatabaseField(columnName = "Summary", canBeNull = false)
    public String Summary;

    @DatabaseField(columnName = "CCUserIDs", canBeNull = true)
    public String CCUserIDs;

    @DatabaseField(columnName = "ConcurrencyID", canBeNull = false)
    public Integer ConcurrencyID;

    @DatabaseField(columnName = "CreateDate", dataType = DataType.DATE_LONG, canBeNull = false)
    public java.util.Date CreateDate;

    @DatabaseField(columnName = "CreateByUserID", canBeNull = false)
    public Integer CreateByUserID;

    @DatabaseField(columnName = "CreateByDepartmentID", canBeNull = true)
    public Integer CreateByDepartmentID;

    @DatabaseField(columnName = "LastEditDate", dataType = DataType.DATE_LONG, canBeNull = true)
    public java.util.Date LastEditDate;

    @DatabaseField(columnName = "UpdateBySQLUser", canBeNull = true)
    public String UpdateBySQLUser;

    @DatabaseField(columnName = "UpdateDateUTC", dataType = DataType.DATE_LONG, canBeNull = true)
    public java.util.Date UpdateDateUTC;

    @DatabaseField(columnName = "SourceOfFindingID", canBeNull = true)
    public Integer SourceOfFindingID;

    @DatabaseField(columnName = "PAR", canBeNull = true)
    public String PAR;

    @DatabaseField(columnName = "OpenedOnDevice", canBeNull = true)
    public String OpenedOnDevice;

    @DatabaseField(columnName = "DeviceFavorite", canBeNull = true)
    public String DeviceFavorite;
}
