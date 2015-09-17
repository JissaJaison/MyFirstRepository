package ITSMobile;


import com.flicksoft.Synchronization.SQLiteOfflineEntity;
import com.flicksoft.util.Key;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "UsersMobile")
public class UsersMobile extends SQLiteOfflineEntity

{


    @Key
    @DatabaseField(columnName = "UserID", canBeNull = false)
    public Integer UserID;

    @Key
    @DatabaseField(columnName = "SiteID", canBeNull = false)
    public Integer SiteID;

    @DatabaseField(columnName = "UserName", canBeNull = false)
    public String UserName;

    @DatabaseField(columnName = "Password", canBeNull = false)
    public String Password;

    @DatabaseField(columnName = "aspnetUserPasswordReset", canBeNull = false)
    public String aspnetUserPasswordReset;

    @DatabaseField(columnName = "UpdateBySQLUser", canBeNull = true)
    public String UpdateBySQLUser;

    @DatabaseField(columnName = "UpdateDateUTC", dataType = DataType.DATE_LONG, canBeNull = true)
    public java.util.Date UpdateDateUTC;

    @DatabaseField(columnName = "ToDelete", canBeNull = false)
    public String ToDelete;

//    @DatabaseField(columnName = "IssueAssignerCount", canBeNull = true)
//    public Integer IssueAssignerCount;
//
//    @DatabaseField(columnName = "IssueFavoriteCount", canBeNull = true)
//    public Integer IssueFavoriteCount;
//
//    @DatabaseField(columnName = "IssueCreatedCount", canBeNull = true)
//    public Integer IssueCreatedCount;


}
