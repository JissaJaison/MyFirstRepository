package ITSMobile;
 
 
import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity; 
import com.j256.ormlite.field.DataType; 
import com.j256.ormlite.field.DatabaseField; 
import com.j256.ormlite.table.DatabaseTable; 
 
 
@DatabaseTable(tableName="Users")
public class Users extends SQLiteOfflineEntity 
 
{
 
 
@Key 
@DatabaseField(columnName="UserID", canBeNull = false)
public Integer UserID; 
 
@Key 
@DatabaseField(columnName="SiteID", canBeNull = false)
public Integer SiteID; 
 
@DatabaseField(columnName="aspnet_UserID", canBeNull = true)
public String aspnet_UserID; 
 
@DatabaseField(columnName="UserDesc", canBeNull = false)
public String UserDesc; 
 
@DatabaseField(columnName="Email", canBeNull = true)
public String Email; 
 
@DatabaseField(columnName="SMSEmail", canBeNull = true)
public String SMSEmail; 
 
@DatabaseField(columnName="BESUsername", canBeNull = true)
public String BESUsername; 
 
@DatabaseField(columnName="CanSendAsSelf", canBeNull = false)
public String CanSendAsSelf; 
 
@DatabaseField(columnName="GroupLoginID", canBeNull = true)
public Integer GroupLoginID; 
 
@DatabaseField(columnName="IVRCode", canBeNull = true)
public Integer IVRCode; 
 
@DatabaseField(columnName="NonWebUser", canBeNull = false)
public String NonWebUser; 
 
@DatabaseField(columnName="HasMobileDevice", canBeNull = false)
public String HasMobileDevice; 
 
@DatabaseField(columnName="StartPageID", canBeNull = true)
public Integer StartPageID; 
 
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
 
@DatabaseField(columnName="OfficeNumber", canBeNull = true)
public String OfficeNumber; 
 
@DatabaseField(columnName="Extension", canBeNull = true)
public String Extension; 
 
@DatabaseField(columnName="Pager", canBeNull = true)
public String Pager; 
 
@DatabaseField(columnName="MobileNumber", canBeNull = true)
public String MobileNumber; 
 
@DatabaseField(columnName="HasInspector", canBeNull = false)
public String HasInspector; 
 
@DatabaseField(columnName="CanInspect", canBeNull = false)
public String CanInspect; 
 
@DatabaseField(columnName="PasswordLastChangeDate", dataType = DataType.DATE_LONG, canBeNull = true)
public java.util.Date PasswordLastChangeDate; 
 
@DatabaseField(columnName="LocationGroupID", canBeNull = true)
public Integer LocationGroupID; 
 
 
}
