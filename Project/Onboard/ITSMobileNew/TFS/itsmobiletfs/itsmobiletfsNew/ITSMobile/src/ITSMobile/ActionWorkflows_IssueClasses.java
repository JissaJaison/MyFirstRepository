package ITSMobile;
 
 
import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity; 
import com.j256.ormlite.field.DataType; 
import com.j256.ormlite.field.DatabaseField; 
import com.j256.ormlite.table.DatabaseTable; 
 
 
@DatabaseTable(tableName="ActionWorkflows_IssueClasses")
public class ActionWorkflows_IssueClasses extends SQLiteOfflineEntity 
 
{
 
 
@Key 
@DatabaseField(columnName="ActionWorkflows_IssueClassesID", canBeNull = false)
public Integer ActionWorkflows_IssueClassesID; 
 
@Key 
@DatabaseField(columnName="SiteID", canBeNull = false)
public Integer SiteID; 
 
@DatabaseField(columnName="ActionWorkflowID", canBeNull = false)
public Integer ActionWorkflowID; 
 
@DatabaseField(columnName="IssueClassID", canBeNull = false)
public Integer IssueClassID; 
 
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
