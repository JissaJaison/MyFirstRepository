package ITSMobile;
 
 
import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity; 
import com.j256.ormlite.field.DataType; 
import com.j256.ormlite.field.DatabaseField; 
import com.j256.ormlite.table.DatabaseTable; 
 
 
@DatabaseTable(tableName="AmosComponentType")
public class AmosComponentType extends SQLiteOfflineEntity 
 
{
 
 
@Key 
@DatabaseField(columnName="CompTypeID", canBeNull = false)
public Integer CompTypeID; 
 
@DatabaseField(columnName="ComponentClassID", canBeNull = true)
public Integer ComponentClassID; 
 
@DatabaseField(columnName="ParentCompTypeID", canBeNull = true)
public Integer ParentCompTypeID; 
 
@DatabaseField(columnName="PrimaryVendorID", canBeNull = true)
public Integer PrimaryVendorID; 
 
@DatabaseField(columnName="CompTypeNo", canBeNull = false)
public String CompTypeNo; 
 
@DatabaseField(columnName="CompName", canBeNull = true)
public String CompName; 
 
@DatabaseField(columnName="CompType", canBeNull = true)
public String CompType; 
 
@DatabaseField(columnName="MakerID", canBeNull = true)
public Integer MakerID; 
 
@DatabaseField(columnName="LayoutID", canBeNull = true)
public Integer LayoutID; 
 
@DatabaseField(columnName="Notes", canBeNull = true)
public String Notes; 
 
@DatabaseField(columnName="DeptID", canBeNull = false)
public Integer DeptID; 
 
@DatabaseField(columnName="ExportMarker", canBeNull = false)
public Integer ExportMarker; 
 
@DatabaseField(columnName="LastUpdated", dataType = DataType.DATE_LONG, canBeNull = false)
public java.util.Date LastUpdated; 
 
@DatabaseField(columnName="UserDefMarpol", canBeNull = false)
public Integer UserDefMarpol; 
 
@DatabaseField(columnName="UserDefImono", canBeNull = true)
public String UserDefImono; 
 
@DatabaseField(columnName="CreationDate", dataType = DataType.DATE_LONG, canBeNull = true)
public java.util.Date CreationDate; 
 
@DatabaseField(columnName="UpdateBySQLUser", canBeNull = true)
public String UpdateBySQLUser; 
 
@DatabaseField(columnName="UpdateDateUTC", dataType = DataType.DATE_LONG, canBeNull = false)
public java.util.Date UpdateDateUTC; 
 
 
}
