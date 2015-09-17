package ITSMobile;
 
 
import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity; 
import com.j256.ormlite.field.DataType; 
import com.j256.ormlite.field.DatabaseField; 
import com.j256.ormlite.table.DatabaseTable; 
 
 
@DatabaseTable(tableName="Locations_LocationGroups")
public class Locations_LocationGroups extends SQLiteOfflineEntity 
 
{
 
 
@Key 
@DatabaseField(columnName="Locations_LocationGroupsID", canBeNull = false)
public Integer Locations_LocationGroupsID; 
 
@Key 
@DatabaseField(columnName="SiteID", canBeNull = false)
public Integer SiteID; 
 
@DatabaseField(columnName="LocationID", canBeNull = false)
public Integer LocationID; 
 
@DatabaseField(columnName="LocationGroupID", canBeNull = false)
public Integer LocationGroupID; 
 
@DatabaseField(columnName="UpdateBySQLUser", canBeNull = true)
public String UpdateBySQLUser; 
 
@DatabaseField(columnName="UpdateDateUTC", dataType = DataType.DATE_LONG, canBeNull = true)
public java.util.Date UpdateDateUTC; 
 
@DatabaseField(columnName="ToDelete", canBeNull = false)
public String ToDelete; 
 
 
}
