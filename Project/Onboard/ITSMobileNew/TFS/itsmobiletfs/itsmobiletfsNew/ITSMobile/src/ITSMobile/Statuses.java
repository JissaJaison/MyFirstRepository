package ITSMobile;
 
 
import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity; 
import com.j256.ormlite.field.DataType; 
import com.j256.ormlite.field.DatabaseField; 
import com.j256.ormlite.table.DatabaseTable; 
 
 
@DatabaseTable(tableName="Statuses")
public class Statuses extends SQLiteOfflineEntity 
 
{
 
 
@Key 
@DatabaseField(columnName="StatusID", canBeNull = false)
public Integer StatusID; 
 
@Key 
@DatabaseField(columnName="SiteID", canBeNull = false)
public Integer SiteID; 
 
@DatabaseField(columnName="StatusCode", canBeNull = true)
public String StatusCode; 
 
@DatabaseField(columnName="StatusDesc", canBeNull = false)
public String StatusDesc; 
 
@DatabaseField(columnName="DisplayOrder", canBeNull = false)
public Integer DisplayOrder; 
 
@DatabaseField(columnName="DisplayOnlyIfCurrent", canBeNull = false)
public String DisplayOnlyIfCurrent; 
 
@DatabaseField(columnName="DisplayOnlyIfGuestServiceIssue", canBeNull = false)
public String DisplayOnlyIfGuestServiceIssue; 
 
@DatabaseField(columnName="DisplayOnlyIfAssigned", canBeNull = false)
public String DisplayOnlyIfAssigned; 
 
@DatabaseField(columnName="DisplayOnlyIfBillable", canBeNull = false)
public String DisplayOnlyIfBillable; 
 
@DatabaseField(columnName="Alert", canBeNull = false)
public String Alert; 
 
@DatabaseField(columnName="DisplayOnMonitorForPreAlert", canBeNull = false)
public String DisplayOnMonitorForPreAlert; 
 
@DatabaseField(columnName="DisplayOnMonitorForAlert", canBeNull = false)
public String DisplayOnMonitorForAlert; 
 
@DatabaseField(columnName="Closed", canBeNull = false)
public String Closed; 
 
@DatabaseField(columnName="Active", canBeNull = false)
public String Active; 
 
@DatabaseField(columnName="CreationDate", dataType = DataType.DATE_LONG, canBeNull = false)
public java.util.Date CreationDate; 
 
@DatabaseField(columnName="LastUpdateDate", dataType = DataType.DATE_LONG, canBeNull = false)
public java.util.Date LastUpdateDate; 
 
@DatabaseField(columnName="UpdateBySQLUser", canBeNull = true)
public String UpdateBySQLUser; 
 
@DatabaseField(columnName="UpdateDateUTC", dataType = DataType.DATE_LONG, canBeNull = true)
public java.util.Date UpdateDateUTC; 
 
@DatabaseField(columnName="ToDelete", canBeNull = false)
public String ToDelete; 
 
@DatabaseField(columnName="Delayed", canBeNull = false)
public String Delayed; 
 
 
}
