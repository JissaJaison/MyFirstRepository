package ITSMobile;
 
 
import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity; 
import com.j256.ormlite.field.DataType; 
import com.j256.ormlite.field.DatabaseField; 
import com.j256.ormlite.table.DatabaseTable; 
 
 
@DatabaseTable(tableName="ComplaintSeverities")
public class ComplaintSeverities extends SQLiteOfflineEntity 
 
{
 
 
@Key 
@DatabaseField(columnName="ComplaintSeverityID", canBeNull = false)
public Integer ComplaintSeverityID; 
 
@Key 
@DatabaseField(columnName="SiteID", canBeNull = false)
public Integer SiteID; 
 
@DatabaseField(columnName="ComplaintSeverityName", canBeNull = false)
public String ComplaintSeverityName; 
 
@DatabaseField(columnName="ComplaintSeverityDesc", canBeNull = false)
public String ComplaintSeverityDesc; 
 
@DatabaseField(columnName="IsDefault", canBeNull = false)
public Boolean IsDefault; 
 
@DatabaseField(columnName="DisplayOrder", canBeNull = false)
public Integer DisplayOrder; 
 
@DatabaseField(columnName="Active", canBeNull = false)
public Boolean Active; 
 
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
public Boolean ToDelete; 
 
 
}
