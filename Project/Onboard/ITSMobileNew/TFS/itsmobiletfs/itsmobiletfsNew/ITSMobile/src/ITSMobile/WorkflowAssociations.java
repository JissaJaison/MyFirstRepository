package ITSMobile;


import com.flicksoft.Synchronization.SQLiteOfflineEntity;
import com.flicksoft.util.Key;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "WorkflowAssociations")
public class WorkflowAssociations extends SQLiteOfflineEntity {
    @Key
    @DatabaseField(columnName = "AssociationID", canBeNull = false)
    public Integer AssociationID;

    @Key
    @DatabaseField(columnName = "SiteID", canBeNull = false)
    public Integer SiteID;

    @DatabaseField(columnName = "WorkflowID", canBeNull = false)
    public Integer WorkflowID;

    @DatabaseField(columnName = "ActionWorkflowID", canBeNull = false)
    public Integer ActionWorkflowID;

    @DatabaseField(columnName = "CreationDate", dataType = DataType.DATE_LONG, canBeNull = false)
    public java.util.Date CreationDate;

    @DatabaseField(columnName = "UpdateBySQLUser", canBeNull = true)
    public String UpdateBySQLUser;

    @DatabaseField(columnName = "UpdateDateUTC", dataType = DataType.DATE_LONG, canBeNull = true)
    public java.util.Date UpdateDateUTC;

    @DatabaseField(columnName = "ToDelete", canBeNull = false)
    public String ToDelete;


}
