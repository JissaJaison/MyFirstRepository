package ITSMobile;
 
 
import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity; 
import com.j256.ormlite.field.DataType; 
import com.j256.ormlite.field.DatabaseField; 
import com.j256.ormlite.table.DatabaseTable; 
 
 
@DatabaseTable(tableName="MessageAddressee")
public class MessageAddressee extends SQLiteOfflineEntity 
 
{
 
 
@Key 
@DatabaseField(columnName="MessageAddresseeID", canBeNull = false)
public Integer MessageAddresseeID; 
 
@Key 
@DatabaseField(columnName="SiteID", canBeNull = false)
public Integer SiteID; 
 
@DatabaseField(columnName="MessageID", canBeNull = false)
public Integer MessageID; 
 
@DatabaseField(columnName="UserID", canBeNull = false)
public Integer UserID; 
 
@DatabaseField(columnName="DeviceID", canBeNull = false)
public String DeviceID; 
 
@DatabaseField(columnName="Read", canBeNull = true)
public String Read; 
 
@DatabaseField(columnName="DateTimeRead", dataType = DataType.DATE_LONG, canBeNull = true)
public java.util.Date DateTimeRead; 
 
@DatabaseField(columnName="MessageType", canBeNull = true)
public String MessageType; 
 
@DatabaseField(columnName="UpdateBySQLUser", canBeNull = true)
public String UpdateBySQLUser; 
 
@DatabaseField(columnName="UpdateDateUTC", dataType = DataType.DATE_LONG, canBeNull = true)
public java.util.Date UpdateDateUTC; 
 
@DatabaseField(columnName="ToDelete", canBeNull = false)
public String ToDelete; 
 
 
}
