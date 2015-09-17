package ITSMobile;
 
 
import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity; 
import com.j256.ormlite.field.DataType; 
import com.j256.ormlite.field.DatabaseField; 
import com.j256.ormlite.table.DatabaseTable; 
 
 
@DatabaseTable(tableName="FileAttachments")
public class FileAttachments extends SQLiteOfflineEntity 
 
{
 
 
@Key 
@DatabaseField(columnName="FileAttachmentID", canBeNull = false)
public Integer FileAttachmentID; 
 
@Key 
@DatabaseField(columnName="SiteID", canBeNull = false)
public Integer SiteID; 
 
@DatabaseField(columnName="IssueTrackID", canBeNull = false)
public Integer IssueTrackID; 
 
@DatabaseField(columnName="FileNme", canBeNull = false)
public String FileNme; 
 
@DatabaseField(columnName="FileAttachment", dataType = DataType.BYTE_ARRAY, canBeNull = false)
public byte[] FileAttachment; 
 
@DatabaseField(columnName="FileExtension", canBeNull = false)
public String FileExtension; 
 
@DatabaseField(columnName="FileSize", canBeNull = false)
public Integer FileSize; 
 
@DatabaseField(columnName="UID", canBeNull = false)
public String UID; 
 
@DatabaseField(columnName="CreationDate", dataType = DataType.DATE_LONG, canBeNull = false)
public java.util.Date CreationDate; 
 
@DatabaseField(columnName="UpdateBySQLUser", canBeNull = true)
public String UpdateBySQLUser; 
 
@DatabaseField(columnName="UpdateDateUTC", dataType = DataType.DATE_LONG, canBeNull = true)
public java.util.Date UpdateDateUTC;

 @DatabaseField(columnName="IssueTrackGUI", canBeNull = false)
 public String IssueTrackGUI;


}
