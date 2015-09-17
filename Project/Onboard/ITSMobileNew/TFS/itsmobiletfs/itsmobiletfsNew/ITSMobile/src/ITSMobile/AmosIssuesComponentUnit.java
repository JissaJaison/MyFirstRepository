package ITSMobile;
 
 
import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity; 
import com.j256.ormlite.field.DataType; 
import com.j256.ormlite.field.DatabaseField; 
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName="AmosIssuesComponentUnit")
public class AmosIssuesComponentUnit extends SQLiteOfflineEntity 
 
{
 
 
@Key 
@DatabaseField(columnName="IssueID", canBeNull = false)
public Integer IssueID; 
 
@Key 
@DatabaseField(columnName="AmosCompID", canBeNull = false)
public Integer AmosCompID;
 
@Key 
@DatabaseField(columnName="SiteID", canBeNull = false)
public Integer SiteID; 
 
@DatabaseField(columnName="CreationDate", dataType = DataType.DATE_LONG, canBeNull = false)
public java.util.Date CreationDate; 
 
@DatabaseField(columnName="UpdateBySQLUser", canBeNull = true)
public String UpdateBySQLUser; 
 
@DatabaseField(columnName="UpdateDateUTC", dataType = DataType.DATE_LONG, canBeNull = true)
public java.util.Date UpdateDateUTC; 
 
 
}
