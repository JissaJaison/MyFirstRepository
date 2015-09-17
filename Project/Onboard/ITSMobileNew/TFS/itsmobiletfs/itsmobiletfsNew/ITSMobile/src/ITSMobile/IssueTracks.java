package ITSMobile;
 
 
import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity; 
import com.j256.ormlite.field.DataType; 
import com.j256.ormlite.field.DatabaseField; 
import com.j256.ormlite.table.DatabaseTable; 
 
 
@DatabaseTable(tableName="Issuetracks")
public class IssueTracks extends SQLiteOfflineEntity 
 
{
 
 
@Key 
@DatabaseField(columnName="IssueTrackID", canBeNull = false)
public Integer IssueTrackID; 
 
@Key 
@DatabaseField(columnName="DeviceID", canBeNull = false)
public String DeviceID; 
 
@Key 
@DatabaseField(columnName="IssueTrackGUI", canBeNull = false)
public String IssueTrackGUI; 
 
@Key 
@DatabaseField(columnName="SiteID", canBeNull = false)
public Integer SiteID; 
 
@DatabaseField(columnName="IssueID", canBeNull = false)
public Integer IssueID; 
 
@DatabaseField(columnName="ActionID", canBeNull = true)
public Integer ActionID; 
 
@DatabaseField(columnName="ResolutionID", canBeNull = true)
public Integer ResolutionID; 
 
@DatabaseField(columnName="PriorityID", canBeNull = true)
public Integer PriorityID; 
 
@DatabaseField(columnName="DepartmentID", canBeNull = true)
public Integer DepartmentID; 
 
@DatabaseField(columnName="AssignerUserID", canBeNull = true)
public Integer AssignerUserID; 
 
@DatabaseField(columnName="AssigneeUserID", canBeNull = true)
public Integer AssigneeUserID; 
 
@DatabaseField(columnName="AssigneeAnyUser", canBeNull = false)
public String AssigneeAnyUser; 
 
@DatabaseField(columnName="PrevAssigneeUserID", canBeNull = true)
public Integer PrevAssigneeUserID; 
 
@DatabaseField(columnName="Notes", canBeNull = true)
public String Notes; 
 
@DatabaseField(columnName="HoursWorked", canBeNull = true)
public float HoursWorked; 
 
@DatabaseField(columnName="Billable", canBeNull = false)
public String Billable; 
 
@DatabaseField(columnName="InvoiceNumber", canBeNull = true)
public String InvoiceNumber; 
 
@DatabaseField(columnName="CallerID", canBeNull = true)
public Integer CallerID; 
 
@DatabaseField(columnName="CompensationID", canBeNull = true)
public Integer CompensationID; 
 
@DatabaseField(columnName="CauseID", canBeNull = true)
public Integer CauseID; 
 
@DatabaseField(columnName="PartsOrderID", canBeNull = true)
public String PartsOrderID; 
 
@DatabaseField(columnName="AutoInsertForActionID", canBeNull = true)
public Integer AutoInsertForActionID; 
 
@DatabaseField(columnName="UpdateByIVR", canBeNull = true)
public String UpdateByIVR; 
 
@DatabaseField(columnName="UpdateBySystem", canBeNull = false)
public String UpdateBySystem; 
 
@DatabaseField(columnName="LastUpdateDate", dataType = DataType.DATE_LONG, canBeNull = false)
public java.util.Date LastUpdateDate; 
 
@DatabaseField(columnName="LastUpdateUserID", canBeNull = false)
public Integer LastUpdateUserID; 
 
@DatabaseField(columnName="LastEditDate", dataType = DataType.DATE_LONG, canBeNull = true)
public java.util.Date LastEditDate; 
 
@DatabaseField(columnName="CreationDate", dataType = DataType.DATE_LONG, canBeNull = true)
public java.util.Date CreationDate; 
 
@DatabaseField(columnName="UpdateBySQLUser", canBeNull = true)
public String UpdateBySQLUser; 
 
@DatabaseField(columnName="UpdateDateUTC", dataType = DataType.DATE_LONG, canBeNull = true)
public java.util.Date UpdateDateUTC; 
 /*
@DatabaseField(columnName="MonetaryValue", canBeNull = true)
public float MonetaryValue; 
 */
@DatabaseField(columnName="MonetaryValueByUser", canBeNull = true)
public String MonetaryValueByUser; 
 
@DatabaseField(columnName="IgnoreDiscrepancy", canBeNull = true)
public String IgnoreDiscrepancy; 
 
@DatabaseField(columnName="EmployeeFirstName", canBeNull = true)
public String EmployeeFirstName; 
 
@DatabaseField(columnName="EmployeeLastName", canBeNull = true)
public String EmployeeLastName; 
 
@DatabaseField(columnName="CorpEmployeeID", canBeNull = true)
public Integer CorpEmployeeID; 
 
@DatabaseField(columnName="UpdateByInspector", canBeNull = true)
public String UpdateByInspector; 
 
 
}
