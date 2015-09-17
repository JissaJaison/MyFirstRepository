package ITSMobile;
 
 
import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity; 
import com.j256.ormlite.field.DataType; 
import com.j256.ormlite.field.DatabaseField; 
import com.j256.ormlite.table.DatabaseTable; 
 
 
@DatabaseTable(tableName="IssueGroups")
public class IssueGroups extends SQLiteOfflineEntity 
 
{
 
 
@Key 
@DatabaseField(columnName="IssueGroupID", canBeNull = false)
public Integer IssueGroupID; 
 
@Key 
@DatabaseField(columnName="SiteID", canBeNull = false)
public Integer SiteID; 
 
@DatabaseField(columnName="IssueGroupDesc", canBeNull = false)
public String IssueGroupDesc; 
 
@DatabaseField(columnName="IssueClassID", canBeNull = false)
public Integer IssueClassID; 
 
@DatabaseField(columnName="SendPreAlert", canBeNull = false)
public String SendPreAlert; 
 
@DatabaseField(columnName="SendAlert", canBeNull = false)
public String SendAlert; 
 
@DatabaseField(columnName="AlertForGuestServiceAndPublic", canBeNull = true)
public Integer AlertForGuestServiceAndPublic; 
 
@DatabaseField(columnName="AlertForGuestServiceAndCrew", canBeNull = true)
public Integer AlertForGuestServiceAndCrew; 
 
@DatabaseField(columnName="AlertForNonGuestServiceAndPublic", canBeNull = true)
public Integer AlertForNonGuestServiceAndPublic; 
 
@DatabaseField(columnName="AlertForNonGuestServiceAndCrew", canBeNull = true)
public Integer AlertForNonGuestServiceAndCrew; 
 
@DatabaseField(columnName="DisplayOnMonitor", canBeNull = false)
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
