package ITSMobile;
 
 
import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity; 
import com.j256.ormlite.field.DataType; 
import com.j256.ormlite.field.DatabaseField; 
import com.j256.ormlite.table.DatabaseTable; 
 
 
@DatabaseTable(tableName="Actions")
public class Actions extends SQLiteOfflineEntity 
 
{
 
 
@Key 
@DatabaseField(columnName="ActionID", canBeNull = false)
public Integer ActionID;
 
@Key 
@DatabaseField(columnName="SiteID", canBeNull = false)
public Integer SiteID;
 
@DatabaseField(columnName="ActionCode", canBeNull = false)
public String ActionCode; 
 
@DatabaseField(columnName="ActionDesc", canBeNull = false)
public String ActionDesc; 
 
@DatabaseField(columnName="ActionImage", canBeNull = true)
public String ActionImage; 
 
@DatabaseField(columnName="DisplayOrder", canBeNull = false)
public Integer DisplayOrder;
 
@DatabaseField(columnName="ParentID", canBeNull = false)
public Integer ParentID;
 
@DatabaseField(columnName="PreviousActionID", canBeNull = true)
public Integer PreviousActionID;
 
@DatabaseField(columnName="NextActionID", canBeNull = true)
public Integer NextActionID;
 
@DatabaseField(columnName="VisibleToUserGroupThatExecutedPreviousAction", canBeNull = false)
public String VisibleToUserGroupThatExecutedPreviousAction; 
 
@DatabaseField(columnName="VisibleToCreatorDepartment", canBeNull = false)
public String VisibleToCreatorDepartment; 
 
@DatabaseField(columnName="VisibleToAssignee", canBeNull = false)
public String VisibleToAssignee; 
 
@DatabaseField(columnName="SetsOtherActionsToNotVisible", canBeNull = false)
public String SetsOtherActionsToNotVisible; 
 
@DatabaseField(columnName="VisibleOnITSMobile", canBeNull = false)
public String VisibleOnITSMobile; 
 
@DatabaseField(columnName="KeepAssignee", canBeNull = false)
public String KeepAssignee; 
 
@DatabaseField(columnName="StopTimer", canBeNull = false)
public String StopTimer; 
 
@DatabaseField(columnName="StartTimer", canBeNull = false)
public String StartTimer; 
 
@DatabaseField(columnName="InternalToSystem", canBeNull = false)
public String InternalToSystem; 
 
@DatabaseField(columnName="RequiresNotes", canBeNull = false)
public String RequiresNotes; 
 
@DatabaseField(columnName="HoursWorkedEntry", canBeNull = false)
public String HoursWorkedEntry; 
 
@DatabaseField(columnName="DisplayHelp", canBeNull = false)
public String DisplayHelp; 
 
@DatabaseField(columnName="HelpPage", canBeNull = true)
public String HelpPage; 
 
@DatabaseField(columnName="Active", canBeNull = false)
public String Active; 
 
@DatabaseField(columnName="CreationDate", dataType = DataType.DATE_LONG, canBeNull = false)
public java.util.Date CreationDate; 
 
@DatabaseField(columnName="LastUpdateDate", dataType = DataType.DATE_LONG, canBeNull = false)
public java.util.Date LastUpdateDate; 
 
@DatabaseField(columnName="LastUpdateUserID", canBeNull = false)
public Integer LastUpdateUserID;
 
@DatabaseField(columnName="UpdateBySQLUser", canBeNull = true)
public String UpdateBySQLUser; 
 
@DatabaseField(columnName="UpdateDateUTC", dataType = DataType.DATE_LONG, canBeNull = true)
public java.util.Date UpdateDateUTC; 
 
@DatabaseField(columnName="ToDelete", canBeNull = false)
public String ToDelete; 
 
@DatabaseField(columnName="SetIssueAsResolved", canBeNull = false)
public String SetIssueAsResolved; 
 
@DatabaseField(columnName="ChangeDepartmentToAssign", canBeNull = false)
public String ChangeDepartmentToAssign; 
 
 
}
