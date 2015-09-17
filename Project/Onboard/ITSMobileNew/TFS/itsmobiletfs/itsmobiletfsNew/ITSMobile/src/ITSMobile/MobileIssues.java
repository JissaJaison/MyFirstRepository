package ITSMobile;


import com.flicksoft.Synchronization.SQLiteOfflineEntity;
import com.flicksoft.util.Key;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "MobileIssues")
public class MobileIssues extends SQLiteOfflineEntity

{


    @Key
    @DatabaseField(columnName = "IssueID", canBeNull = false)
    public int IssueID;

    @DatabaseField(columnName = "IssueTypeID", canBeNull = false)
    public int IssueTypeID;

    @DatabaseField(columnName = "IssueTypeDesc", canBeNull = false)
    public String IssueTypeDesc;

    @DatabaseField(columnName = "DefectName", canBeNull = true)
    public String DefectName;

    @DatabaseField(columnName = "LocationDesc", canBeNull = false)
    public String LocationDesc;

    @DatabaseField(columnName = "DeckID", canBeNull = true)
    public int DeckID;

    @DatabaseField(columnName = "DeckNumber", canBeNull = true)
    public String DeckNumber;

    @DatabaseField(columnName = "DeckDesc", canBeNull = true)
    public String DeckDesc;

    @DatabaseField(columnName = "TransverseID", canBeNull = true)
    public int TransverseID;

    @DatabaseField(columnName = "TransverseName", canBeNull = true)
    public String TransverseName;

    @DatabaseField(columnName = "ZoneID", canBeNull = true)
    public int ZoneID;

    @DatabaseField(columnName = "ZoneName", canBeNull = true)
    public String ZoneName;

    @DatabaseField(columnName = "FireZoneID", canBeNull = true)
    public int FireZoneID;

    @DatabaseField(columnName = "FireZoneName", canBeNull = true)
    public String FireZoneName;

    @DatabaseField(columnName = "PreviousDepartmentID", canBeNull = true)
    public int PreviousDepartmentID;

    @DatabaseField(columnName = "PreviousDepartmentDesc", canBeNull = true)
    public String PreviousDepartmentDesc;

    @DatabaseField(columnName = "CurrentDepartmentID", canBeNull = true)
    public int CurrentDepartmentID;

    @DatabaseField(columnName = "CurrentDepartmentDesc", canBeNull = true)
    public String CurrentDepartmentDesc;

    @DatabaseField(columnName = "LocationOwnerDepartmentID", canBeNull = true)
    public int LocationOwnerDepartmentID;

    @DatabaseField(columnName = "LocationOwnerDepartmentDesc", canBeNull = true)
    public String LocationOwnerDepartmentDesc;

    @DatabaseField(columnName = "StatusID", canBeNull = false)
    public int StatusID;

    @DatabaseField(columnName = "StatusDesc", canBeNull = false)
    public String StatusDesc;

    @DatabaseField(columnName = "ReportedByCrew", canBeNull = true)
    public Boolean ReportedByCrew;

    @DatabaseField(columnName = "ReportBy", canBeNull = true)
    public String ReportBy;

    @DatabaseField(columnName = "ReportByDepartmentDesc", canBeNull = true)
    public String ReportByDepartmentDesc;

    @DatabaseField(columnName = "ReportByPositionName", canBeNull = true)
    public String ReportByPositionName;

    @DatabaseField(columnName = "OnBehalfOfUserDesc", canBeNull = true)
    public String OnBehalfOfUserDesc;

    @DatabaseField(columnName = "DueDate", dataType = DataType.DATE_LONG, canBeNull = true)
    public java.util.Date DueDate;

    @DatabaseField(columnName = "GuestFirstName", canBeNull = true)
    public String GuestFirstName;

    @DatabaseField(columnName = "GuestLastName", canBeNull = true)
    public String GuestLastName;

    @DatabaseField(columnName = "PassengerInfoID", canBeNull = true)
    public int PassengerInfoID;

    @DatabaseField(columnName = "GuestServiceIssue", canBeNull = true)
    public Boolean GuestServiceIssue;

    @DatabaseField(columnName = "RequiresGuestCallback", canBeNull = true)
    public Boolean RequiresGuestCallback;

    @DatabaseField(columnName = "DateFirstExperienced", dataType = DataType.DATE_LONG, canBeNull = true)
    public java.util.Date DateFirstExperienced;

    @DatabaseField(columnName = "SeverityID", canBeNull = true)
    public int SeverityID;

    @DatabaseField(columnName = "SeverityName", canBeNull = true)
    public String SeverityName;

    @DatabaseField(columnName = "Summary", canBeNull = false)
    public String Summary;

    @DatabaseField(columnName = "SourceDesc", canBeNull = true)
    public String SourceDesc;

    @DatabaseField(columnName = "PAR", canBeNull = true)
    public String PAR;

    @DatabaseField(columnName = "ConcurrencyID", canBeNull = false)
    public int ConcurrencyID;

    @DatabaseField(columnName = "CreateDate", dataType = DataType.DATE_LONG, canBeNull = false)
    public java.util.Date CreateDate;

    @DatabaseField(columnName = "CreateByUserID", canBeNull = false)
    public int CreateByUserID;

    @DatabaseField(columnName = "CreateByUserDesc", canBeNull = false)
    public String CreateByUserDesc;

    @DatabaseField(columnName = "CreateByDepartmentID", canBeNull = true)
    public int CreateByDepartmentID;

    @DatabaseField(columnName = "CreateByDepartmentDesc", canBeNull = true)
    public String CreateByDepartmentDesc;

    @DatabaseField(columnName = "PriorityID", canBeNull = true)
    public int PriorityID;

    @DatabaseField(columnName = "PriorityDesc", canBeNull = true)
    public String PriorityDesc;

    @DatabaseField(columnName = "DeviceFavorite", canBeNull = true)
    public String DeviceFavorite;

    @DatabaseField(columnName = "OpenedOnDevice", canBeNull = false)
    public Boolean OpenedOnDevice;

    @DatabaseField(columnName = "CountAttach", canBeNull = true)
    public int CountAttach;

    @DatabaseField(columnName = "IssueClassID", canBeNull = true)
    public int IssueClassID;

    @DatabaseField(columnName = "LocationID", canBeNull = true)
    public int LocationID;

    @DatabaseField(columnName = "IssueClassCode", canBeNull = true)
    public String IssueClassCode;

    @DatabaseField(columnName = "IssueClassDesc", canBeNull = true)
    public String IssueClassDesc;

    @DatabaseField(columnName = "AmosFuncDescr", canBeNull = true)
    public String AmosFuncDescr;

    @DatabaseField(columnName = "AmosSerialNo", canBeNull = true)
    public String AmosSerialNo;

    @DatabaseField(columnName = "AssigneeUserID", canBeNull = true)
    public int AssigneeUserID;

    @DatabaseField(columnName = "Notes", canBeNull = true)
    public String Notes;
    @DatabaseField(columnName = "AlertDesc", canBeNull = true)
    public String AlertDesc;

    @DatabaseField(columnName = "LastUpdateDate", dataType = DataType.DATE_LONG, canBeNull = true)
    public java.util.Date LastUpdateDate;


}
