package ITSMobile;
 
 
import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity; 
import com.j256.ormlite.field.DataType; 
import com.j256.ormlite.field.DatabaseField; 
import com.j256.ormlite.table.DatabaseTable; 
 
 
@DatabaseTable(tableName="ActionVisibilityByUser")
public class ActionVisibilityByUser extends SQLiteOfflineEntity 
 
{
 
 
@Key 
@DatabaseField(columnName="ActionUserID", canBeNull = false)
public Integer ActionUserID; 
 
@Key 
@DatabaseField(columnName="SiteID", canBeNull = false)
public Integer SiteID; 
 
@DatabaseField(columnName="ActionID", canBeNull = false)
public Integer ActionID; 
 
@DatabaseField(columnName="UserID", canBeNull = false)
public Integer UserID; 
 
@DatabaseField(columnName="CreationDate", dataType = DataType.DATE_LONG, canBeNull = false)
public java.util.Date CreationDate; 
 
@DatabaseField(columnName="CreateByUserID", canBeNull = false)
public Integer CreateByUserID; 
 
@DatabaseField(columnName="UpdateBySQLUser", canBeNull = true)
public String UpdateBySQLUser; 
 
@DatabaseField(columnName="UpdateDateUTC", dataType = DataType.DATE_LONG, canBeNull = true)
public java.util.Date UpdateDateUTC; 
 
@DatabaseField(columnName="ToDelete", canBeNull = false)
public String ToDelete; 
 
 
}
