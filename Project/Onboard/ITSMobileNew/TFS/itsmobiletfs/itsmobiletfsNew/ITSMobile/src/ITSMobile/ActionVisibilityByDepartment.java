package ITSMobile;


import com.flicksoft.Synchronization.SQLiteOfflineEntity;
import com.flicksoft.util.Key;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "ActionVisibilityByDepartment")
public class ActionVisibilityByDepartment extends SQLiteOfflineEntity
{
    @Key
    @DatabaseField(columnName = "ActionDepartmentID", canBeNull = false)
    public Integer ActionDepartmentID;

    @Key
    @DatabaseField(columnName = "SiteID", canBeNull = false)
    public Integer SiteID;

    @DatabaseField(columnName = "ActionID", canBeNull = false)
    public Integer ActionID;

    @DatabaseField(columnName = "DepartmentID", canBeNull = false)
    public Integer DepartmentID;

    @DatabaseField(columnName = "VisibleOnlyIfCurrentlyAssigned", canBeNull = false)
    public String VisibleOnlyIfCurrentlyAssigned;

    @DatabaseField(columnName = "CreationDate", dataType = DataType.DATE_LONG, canBeNull = false)
    public java.util.Date CreationDate;

    @DatabaseField(columnName = "CreateByUserID", canBeNull = false)
    public Integer CreateByUserID;

    @DatabaseField(columnName = "UpdateBySQLUser", canBeNull = true)
    public String UpdateBySQLUser;

    @DatabaseField(columnName = "UpdateDateUTC", dataType = DataType.DATE_LONG, canBeNull = true)
    public java.util.Date UpdateDateUTC;

    @DatabaseField(columnName = "ToDelete", canBeNull = false)
    public String ToDelete;


}
