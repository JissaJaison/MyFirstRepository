package ITSMobile;
 
 
import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity; 
import com.j256.ormlite.field.DataType; 
import com.j256.ormlite.field.DatabaseField; 
import com.j256.ormlite.table.DatabaseTable; 
 
 
@DatabaseTable(tableName="MobileDevice")
public class MobileDevice extends SQLiteOfflineEntity 
 
{
 
 
@Key 
@DatabaseField(columnName="DeviceID", canBeNull = false)
public String DeviceID; 
 
@Key 
@DatabaseField(columnName="SiteID", canBeNull = false)
public Integer SiteID; 
 
@DatabaseField(columnName="AppVersion", canBeNull = true)
public String AppVersion; 
 
@DatabaseField(columnName="DBVersion", canBeNull = true)
public String DBVersion; 
 
@DatabaseField(columnName="UserID", canBeNull = true)
public Integer UserID; 
 
@DatabaseField(columnName="Description", canBeNull = true)
public String Description; 
 
@DatabaseField(columnName="LastSyncDate", dataType = DataType.DATE_LONG, canBeNull = true)
public java.util.Date LastSyncDate; 
 
@DatabaseField(columnName="LastSyncType", canBeNull = true)
public String LastSyncType; 
 
@DatabaseField(columnName="LastSyncStatus", canBeNull = true)
public String LastSyncStatus; 
 
@DatabaseField(columnName="ReadOnly", canBeNull = false)
public String ReadOnly; 
 
@DatabaseField(columnName="Locked", canBeNull = false)
public String Locked; 
 
@DatabaseField(columnName="UpdateBySQLUser", canBeNull = true)
public String UpdateBySQLUser; 
 
@DatabaseField(columnName="UpdateDateUTC", dataType = DataType.DATE_LONG, canBeNull = true)
public java.util.Date UpdateDateUTC; 
 
@DatabaseField(columnName="ToDelete", canBeNull = false)
public String ToDelete; 
 
 
}
