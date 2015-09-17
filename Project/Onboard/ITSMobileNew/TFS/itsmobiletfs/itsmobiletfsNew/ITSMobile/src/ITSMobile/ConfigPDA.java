package ITSMobile;
 
 
import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity; 
import com.j256.ormlite.field.DataType; 
import com.j256.ormlite.field.DatabaseField; 
import com.j256.ormlite.table.DatabaseTable; 
 
 
@DatabaseTable(tableName="ConfigPDA")
public class ConfigPDA extends SQLiteOfflineEntity 
 
{
 
 
@Key 
@DatabaseField(columnName="ConfigID", canBeNull = false)
public Integer ConfigID; 
 
@Key 
@DatabaseField(columnName="SiteID", canBeNull = false)
public Integer SiteID; 
 
@DatabaseField(columnName="SyncFrec", canBeNull = true)
public Integer SyncFrec; 
 
@DatabaseField(columnName="BackgSync", canBeNull = true)
public String BackgSync; 
 
@DatabaseField(columnName="TryConnect", canBeNull = true)
public Integer TryConnect; 
 
@DatabaseField(columnName="ShowAvailableMem", canBeNull = true)
public String ShowAvailableMem; 
 
@DatabaseField(columnName="WiFiSignal", canBeNull = true)
public Integer WiFiSignal; 
 
@DatabaseField(columnName="AdminPassword", canBeNull = true)
public String AdminPassword; 
 
@DatabaseField(columnName="AppCenterPassword", canBeNull = true)
public String AppCenterPassword; 
 
@DatabaseField(columnName="AppAutoUpdate", canBeNull = true)
public String AppAutoUpdate; 
 
@DatabaseField(columnName="ForceAppAutoUpdate", canBeNull = true)
public String ForceAppAutoUpdate; 
 
@DatabaseField(columnName="DBAutoUpdate", canBeNull = true)
public String DBAutoUpdate; 
 
@DatabaseField(columnName="UrlCabFileUser", canBeNull = true)
public String UrlCabFileUser; 
 
@DatabaseField(columnName="UrlCabPassword", canBeNull = true)
public String UrlCabPassword; 
 
@DatabaseField(columnName="DisplayOptionTab", canBeNull = true)
public String DisplayOptionTab; 
 
@DatabaseField(columnName="DisplayChangePassword", canBeNull = true)
public String DisplayChangePassword; 
 
@DatabaseField(columnName="DisplayMessages", canBeNull = true)
public String DisplayMessages; 
 
@DatabaseField(columnName="DisplayCheckForUpdate", canBeNull = true)
public String DisplayCheckForUpdate; 
 
@DatabaseField(columnName="LastUpdateDate", dataType = DataType.DATE_LONG, canBeNull = true)
public java.util.Date LastUpdateDate; 
 
@DatabaseField(columnName="LastUpdateUserID", canBeNull = true)
public Integer LastUpdateUserID; 
 
@DatabaseField(columnName="UpdateBySQLUser", canBeNull = true)
public String UpdateBySQLUser; 
 
@DatabaseField(columnName="UpdateDateUTC", dataType = DataType.DATE_LONG, canBeNull = true)
public java.util.Date UpdateDateUTC; 
 
 
}
