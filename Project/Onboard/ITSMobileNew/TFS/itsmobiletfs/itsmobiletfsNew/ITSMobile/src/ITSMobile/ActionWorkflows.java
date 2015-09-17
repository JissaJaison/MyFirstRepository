package ITSMobile;
 
 
import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity; 
import com.j256.ormlite.field.DataType; 
import com.j256.ormlite.field.DatabaseField; 
import com.j256.ormlite.table.DatabaseTable; 
 
 
@DatabaseTable(tableName="ActionWorkflows")
public class ActionWorkflows extends SQLiteOfflineEntity 
 
{
 
 
@Key 
@DatabaseField(columnName="ActionWorkflowID", canBeNull = false)
public Integer ActionWorkflowID; 
 
@Key 
@DatabaseField(columnName="SiteID", canBeNull = false)
public Integer SiteID; 
 
@DatabaseField(columnName="CurrentStatusID", canBeNull = true)
public Integer CurrentStatusID; 
 
@DatabaseField(columnName="ActionID", canBeNull = false)
public Integer ActionID; 
 
@DatabaseField(columnName="RequiresLocationOwnerAction", canBeNull = true)
public String RequiresLocationOwnerAction; 
 
@DatabaseField(columnName="Billable", canBeNull = true)
public String Billable; 
 
@DatabaseField(columnName="NewStatusID", canBeNull = true)
public Integer NewStatusID; 
 
@DatabaseField(columnName="NewDepartmentID", canBeNull = true)
public Integer NewDepartmentID; 
 
@DatabaseField(columnName="TransferToPreviousDepartment", canBeNull = false)
public String TransferToPreviousDepartment; 
 
@DatabaseField(columnName="TransferToDeptOfPrevActionID", canBeNull = false)
public String TransferToDeptOfPrevActionID; 
 
@DatabaseField(columnName="TransferToLocationOwnerDepartment", canBeNull = false)
public String TransferToLocationOwnerDepartment; 
 
@DatabaseField(columnName="GuestServiceIssue", canBeNull = false)
public String GuestServiceIssue; 
 
@DatabaseField(columnName="RequiresGuestCallback", canBeNull = false)
public String RequiresGuestCallback; 
 
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
 
@DatabaseField(columnName="TransferToCreatorDepartment", canBeNull = false)
public String TransferToCreatorDepartment; 
 
 
}
