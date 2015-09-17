package ITSMobile;
 
 
import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity; 
import com.j256.ormlite.field.DataType; 
import com.j256.ormlite.field.DatabaseField; 
import com.j256.ormlite.table.DatabaseTable; 
 
 
@DatabaseTable(tableName="UserDepartments")
public class UserDepartments extends SQLiteOfflineEntity 
 
{
 
 
@Key 
@DatabaseField(columnName="UserDepartmentID", canBeNull = false)
public Integer UserDepartmentID; 
 
@Key 
@DatabaseField(columnName="SiteID", canBeNull = false)
public Integer SiteID; 
 
@DatabaseField(columnName="UserID", canBeNull = false)
public Integer UserID; 
 
@DatabaseField(columnName="DepartmentID", canBeNull = false)
public Integer DepartmentID; 
 
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
