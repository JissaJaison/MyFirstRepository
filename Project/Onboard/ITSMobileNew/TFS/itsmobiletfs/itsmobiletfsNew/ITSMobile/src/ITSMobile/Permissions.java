package ITSMobile;

import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Permissions")
public class Permissions extends SQLiteOfflineEntity

{

	@Key
	@DatabaseField(columnName = "PermissionID", canBeNull = false)
	public Integer PermissionID;

	@Key
	@DatabaseField(columnName = "SiteID", canBeNull = false)
	public Integer SiteID;

	@DatabaseField(columnName = "PermissionGroupID", canBeNull = false)
	public Integer PermissionGroupID;

	@DatabaseField(columnName = "IssueClassID", canBeNull = false)
	public Integer IssueClassID;

	@DatabaseField(columnName = "CanCreateIssue", canBeNull = false)
	public String CanCreateIssue;

	@DatabaseField(columnName = "CanTransferToPrevDept", canBeNull = false)
	public String CanTransferToPrevDept;

	@DatabaseField(columnName = "CanTransferToCreatorDept", canBeNull = false)
	public String CanTransferToCreatorDept;

	@DatabaseField(columnName = "CanRequestInfoFromPrevDept", canBeNull = false)
	public String CanRequestInfoFromPrevDept;

	@DatabaseField(columnName = "CanRequestInfoFromCreatorDept", canBeNull = false)
	public String CanRequestInfoFromCreatorDept;

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
