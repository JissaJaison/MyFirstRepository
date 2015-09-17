package ITSMobile;
 
 
import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity; 
import com.j256.ormlite.field.DataType; 
import com.j256.ormlite.field.DatabaseField; 
import com.j256.ormlite.table.DatabaseTable; 
 
 
@DatabaseTable(tableName="Priorities")
public class Priorities extends SQLiteOfflineEntity 
 
{
 
 
@Key 
@DatabaseField(columnName="PriorityID", canBeNull = false)
public Integer PriorityID; 
 
@Key 
@DatabaseField(columnName="SiteID", canBeNull = false)
public Integer SiteID; 
 
@DatabaseField(columnName="PriorityDesc", canBeNull = false)
public String PriorityDesc; 
 
@DatabaseField(columnName="PriorityImage", canBeNull = true)
public String PriorityImage; 
 
@DatabaseField(columnName="PriorityLevel", canBeNull = false)
public Integer PriorityLevel; 
 
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
