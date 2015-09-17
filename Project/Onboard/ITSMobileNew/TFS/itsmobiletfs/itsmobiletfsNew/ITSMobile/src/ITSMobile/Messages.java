package ITSMobile;
 
 
import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity; 
import com.j256.ormlite.field.DataType; 
import com.j256.ormlite.field.DatabaseField; 
import com.j256.ormlite.table.DatabaseTable; 
 
 
@DatabaseTable(tableName="Messages")
public class Messages extends SQLiteOfflineEntity 
 
{
 
 
@Key 
@DatabaseField(columnName="MessageID", canBeNull = false)
public Integer MessageID; 
 
@Key 
@DatabaseField(columnName="SiteID", canBeNull = false)
public Integer SiteID; 
 
@DatabaseField(columnName="Subject", canBeNull = true)
public String Subject; 
 
@DatabaseField(columnName="Message", canBeNull = true)
public String Message; 
 
@DatabaseField(columnName="CreatedDate", dataType = DataType.DATE_LONG, canBeNull = true)
public java.util.Date CreatedDate; 
 
@DatabaseField(columnName="Priority", canBeNull = true)
public String Priority; 
 
@DatabaseField(columnName="UserID", canBeNull = true)
public Integer UserID; 
 
@DatabaseField(columnName="UpdateBySQLUser", canBeNull = true)
public String UpdateBySQLUser; 
 
@DatabaseField(columnName="UpdateDateUTC", dataType = DataType.DATE_LONG, canBeNull = true)
public java.util.Date UpdateDateUTC; 
 
@DatabaseField(columnName="ToDelete", canBeNull = false)
public String ToDelete; 
 
 
}
