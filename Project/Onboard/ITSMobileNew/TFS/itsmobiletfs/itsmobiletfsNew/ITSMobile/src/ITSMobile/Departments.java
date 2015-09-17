package ITSMobile;


import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "Departments")
public class Departments extends SQLiteOfflineEntity {
    @Key
    @DatabaseField(columnName = "DepartmentID", canBeNull = false)
    public Integer DepartmentID;

    @Key
    @DatabaseField(columnName = "SiteID", canBeNull = false)
    public Integer SiteID;

    @DatabaseField(columnName = "DepartmentDesc", canBeNull = false)
    public String DepartmentDesc;

    @DatabaseField(columnName = "DeviceID", canBeNull = true)
    public Integer DeviceID;

    @DatabaseField(columnName = "AssigneeUserID", canBeNull = true)
    public Integer AssigneeUserID;

    @DatabaseField(columnName = "AssigneeAnyUser", canBeNull = false)
    public String AssigneeAnyUser;

    @DatabaseField(columnName = "Active", canBeNull = false)
    public String Active;

    @DatabaseField(columnName = "CreationDate", dataType = DataType.DATE_LONG, canBeNull = false)
    public java.util.Date CreationDate;

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

//    @DatabaseField(columnName = "IssueAssignerCount", canBeNull = true)
//    public Integer IssueAssignerCount;
//
//    @DatabaseField(columnName = "IssueCreatedCount", canBeNull = true)
//    public Integer IssueCreatedCount;



}
