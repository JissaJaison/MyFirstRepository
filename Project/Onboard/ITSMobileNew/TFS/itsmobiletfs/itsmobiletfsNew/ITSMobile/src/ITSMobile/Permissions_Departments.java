package ITSMobile;

import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Permissions_Departments")
public class Permissions_Departments extends SQLiteOfflineEntity

{

	@Key
	@DatabaseField(columnName = "Permissions_DepartmentsID", canBeNull = false)
	public Integer Permissions_DepartmentsID;

	@Key
	@DatabaseField(columnName = "SiteID", canBeNull = false)
	public Integer SiteID;

	@DatabaseField(columnName = "PermissionID", canBeNull = false)
	public Integer PermissionID;

	@DatabaseField(columnName = "DepartmentID", canBeNull = false)
	public Integer DepartmentID;

	@DatabaseField(columnName = "CanView", canBeNull = false)
	public String CanView;

	@DatabaseField(columnName = "CanManage", canBeNull = false)
	public String CanManage;

	@DatabaseField(columnName = "CanTransfer", canBeNull = false)
	public String CanTransfer;

	@DatabaseField(columnName = "CanRequestInfo", canBeNull = false)
	public String CanRequestInfo;

	@DatabaseField(columnName = "CreateDate", dataType = DataType.DATE_LONG, canBeNull = false)
	public java.util.Date CreateDate;

	@DatabaseField(columnName = "LastUpdateDate", dataType = DataType.DATE_LONG, canBeNull = false)
	public java.util.Date LastUpdateDate;

	@DatabaseField(columnName = "LastUpdateUserID", canBeNull = false)
	public Integer LastUpdateUserID;

	@DatabaseField(columnName = "UpdateBySQLUser", canBeNull = true)
	public String UpdateBySQLUser;

	@DatabaseField(columnName = "UpdateDateUTC", dataType = DataType.DATE_LONG, canBeNull = true)
	public java.util.Date UpdateDateUTC;

	@DatabaseField(columnName = "ToDelete", canBeNull = false)
	public String ToDelete;

}
