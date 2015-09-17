package ITSMobile;
 
 
import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity; 
import com.j256.ormlite.field.DataType; 
import com.j256.ormlite.field.DatabaseField; 
import com.j256.ormlite.table.DatabaseTable; 
 
 
@DatabaseTable(tableName="GeneralGroups_Departments")
public class GeneralGroups_Departments extends SQLiteOfflineEntity 
 
{
 
 
@Key 
@DatabaseField(columnName="GeneralGroups_DepartmentsID", canBeNull = false)
public Integer GeneralGroups_DepartmentsID; 
 
@Key 
@DatabaseField(columnName="SiteID", canBeNull = false)
public Integer SiteID; 
 
@DatabaseField(columnName="GeneralGroupID", canBeNull = false)
public Integer GeneralGroupID; 
 
@DatabaseField(columnName="DepartmentID", canBeNull = false)
public Integer DepartmentID; 
 
@DatabaseField(columnName="CreateDate", dataType = DataType.DATE_LONG, canBeNull = false)
public java.util.Date CreateDate; 
 
@DatabaseField(columnName="CreateByUserID", canBeNull = false)
public Integer CreateByUserID; 
 
@DatabaseField(columnName="UpdateBySQLUser", canBeNull = true)
public String UpdateBySQLUser; 
 
@DatabaseField(columnName="UpdateDateUTC", dataType = DataType.DATE_LONG, canBeNull = true)
public java.util.Date UpdateDateUTC; 
 
@DatabaseField(columnName="ToDelete", canBeNull = false)
public String ToDelete; 
 
 
}
