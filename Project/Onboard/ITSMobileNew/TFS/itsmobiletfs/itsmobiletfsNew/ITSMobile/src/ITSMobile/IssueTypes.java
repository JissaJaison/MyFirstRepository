package ITSMobile;
 
 
import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity; 
import com.j256.ormlite.field.DataType; 
import com.j256.ormlite.field.DatabaseField; 
import com.j256.ormlite.table.DatabaseTable; 
 
 
@DatabaseTable(tableName="IssueTypes")
public class IssueTypes extends SQLiteOfflineEntity 
 
{
 
 
@Key 
@DatabaseField(columnName="IssueTypeID", canBeNull = false)
public Integer IssueTypeID; 
 
@Key 
@DatabaseField(columnName="SiteID", canBeNull = false)
public Integer SiteID; 
 
@DatabaseField(columnName="IssueTypeDesc", canBeNull = false)
public String IssueTypeDesc; 
 
@DatabaseField(columnName="PriorityID", canBeNull = false)
public Integer PriorityID; 
 
@DatabaseField(columnName="IssueClassID", canBeNull = true)
public Integer IssueClassID; 
 
@DatabaseField(columnName="IssueGroupID", canBeNull = false)
public Integer IssueGroupID; 
 
@DatabaseField(columnName="IssueCategoryID", canBeNull = true)
public Integer IssueCategoryID; 
 
@DatabaseField(columnName="Level1DepartmentID", canBeNull = false)
public Integer Level1DepartmentID; 
 
@DatabaseField(columnName="Level2DepartmentID", canBeNull = true)
public Integer Level2DepartmentID; 
 
@DatabaseField(columnName="AssigneeUserID", canBeNull = true)
public Integer AssigneeUserID; 
 
@DatabaseField(columnName="AssigneeAnyUser", canBeNull = false)
public String AssigneeAnyUser; 
 
@DatabaseField(columnName="GuestServiceIssue", canBeNull = false)
public String GuestServiceIssue; 
 
@DatabaseField(columnName="RequiresGuestCallback", canBeNull = false)
public String RequiresGuestCallback; 
 
@DatabaseField(columnName="RequiresLocationOwnerAction", canBeNull = false)
public String RequiresLocationOwnerAction; 
 
@DatabaseField(columnName="IVRCode", canBeNull = true)
public Integer IVRCode; 
 
@DatabaseField(columnName="SendPreAlert", canBeNull = true)
public String SendPreAlert; 
 
@DatabaseField(columnName="SendAlert", canBeNull = true)
public String SendAlert; 
 
@DatabaseField(columnName="AlertForGuestServiceAndPublic", canBeNull = true)
public Integer AlertForGuestServiceAndPublic; 
 
@DatabaseField(columnName="AlertForGuestServiceAndCrew", canBeNull = true)
public Integer AlertForGuestServiceAndCrew; 
 
@DatabaseField(columnName="AlertForNonGuestServiceAndPublic", canBeNull = true)
public Integer AlertForNonGuestServiceAndPublic; 
 
@DatabaseField(columnName="AlertForNonGuestServiceAndCrew", canBeNull = true)
public Integer AlertForNonGuestServiceAndCrew; 
 
@DatabaseField(columnName="DisplayOnMonitor", canBeNull = true)
public String DisplayOnMonitor; 
 
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
 
 
}
