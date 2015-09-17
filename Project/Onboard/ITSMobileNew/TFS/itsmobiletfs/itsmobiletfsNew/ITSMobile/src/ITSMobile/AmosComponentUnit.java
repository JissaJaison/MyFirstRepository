package ITSMobile;
 
 
import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity; 
import com.j256.ormlite.field.DataType; 
import com.j256.ormlite.field.DatabaseField; 
import com.j256.ormlite.table.DatabaseTable; 
 
 
@DatabaseTable(tableName="AmosComponentUnit")
public class AmosComponentUnit extends SQLiteOfflineEntity 
 
{
 
 
@Key 
@DatabaseField(columnName="CompID", canBeNull = false)
public Integer CompID; 
 
@DatabaseField(columnName="CompTypeID", canBeNull = false)
public Integer CompTypeID; 
 
@DatabaseField(columnName="CompNo", canBeNull = false)
public String CompNo; 
 
@DatabaseField(columnName="SerialNo", canBeNull = true)
public String SerialNo; 
 
@DatabaseField(columnName="StatusID", canBeNull = false)
public Integer StatusID; 
 
@DatabaseField(columnName="DeptID", canBeNull = false)
public Integer DeptID; 
 
@DatabaseField(columnName="FuncNo", canBeNull = true)
public String FuncNo; 
 
@DatabaseField(columnName="FuncDescr", canBeNull = true)
public String FuncDescr; 
 
@DatabaseField(columnName="FunctionID", canBeNull = true)
public Integer FunctionID; 
 
@DatabaseField(columnName="CreationDate", dataType = DataType.DATE_LONG, canBeNull = true)
public java.util.Date CreationDate; 
 
@DatabaseField(columnName="UpdateBySQLUser", canBeNull = true)
public String UpdateBySQLUser; 
 
@DatabaseField(columnName="UpdateDateUTC", dataType = DataType.DATE_LONG, canBeNull = false)
public java.util.Date UpdateDateUTC; 
 
 
}
