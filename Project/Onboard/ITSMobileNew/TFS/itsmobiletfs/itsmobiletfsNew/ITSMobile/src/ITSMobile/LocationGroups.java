package ITSMobile;
 
 
import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity; 
import com.j256.ormlite.field.DataType; 
import com.j256.ormlite.field.DatabaseField; 
import com.j256.ormlite.table.DatabaseTable; 
 
 
@DatabaseTable(tableName="LocationGroups")
public class LocationGroups extends SQLiteOfflineEntity 
 
{
 
 
@Key 
@DatabaseField(columnName="LocationGroupID", canBeNull = false)
public Integer LocationGroupID; 
 
@Key 
@DatabaseField(columnName="SiteID", canBeNull = false)
public Integer SiteID; 
 
@DatabaseField(columnName="LocationGroupDesc", canBeNull = false)
public String LocationGroupDesc; 
 
@DatabaseField(columnName="PassengerCabin", canBeNull = false)
public String PassengerCabin; 
 
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
 
@DatabaseField(columnName="ShowOnCreate", canBeNull = false)
public String ShowOnCreate; 
 
@DatabaseField(columnName="DisplayOnDevice", canBeNull = false)
public String DisplayOnDevice; 
 
@DatabaseField(columnName="HKSection", canBeNull = false)
public String HKSection; 
 
 
}
