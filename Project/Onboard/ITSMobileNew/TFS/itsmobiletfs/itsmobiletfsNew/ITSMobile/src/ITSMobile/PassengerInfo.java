package ITSMobile;
 
 
import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity; 
import com.j256.ormlite.field.DataType; 
import com.j256.ormlite.field.DatabaseField; 
import com.j256.ormlite.table.DatabaseTable; 
 
 
@DatabaseTable(tableName="PassengerInfo")
public class PassengerInfo extends SQLiteOfflineEntity 
 
{
 
 
@Key 
@DatabaseField(columnName="PassengerInfoID", canBeNull = false)
public Integer PassengerInfoID; 
 
@Key 
@DatabaseField(columnName="SiteID", canBeNull = false)
public Integer SiteID; 
 
@DatabaseField(columnName="Greeting", canBeNull = true)
public String Greeting; 
 
@DatabaseField(columnName="FirstName", canBeNull = true)
public String FirstName; 
 
@DatabaseField(columnName="LastName", canBeNull = true)
public String LastName; 
 
@DatabaseField(columnName="BookingID", canBeNull = true)
public String BookingID; 
 
@DatabaseField(columnName="SequenceNumber", canBeNull = true)
public String SequenceNumber; 
 
@DatabaseField(columnName="PassengerID", canBeNull = true)
public String PassengerID; 
 
@DatabaseField(columnName="GroupID", canBeNull = true)
public String GroupID; 
 
@DatabaseField(columnName="EmbarkationDate", dataType = DataType.DATE_LONG, canBeNull = true)
public java.util.Date EmbarkationDate; 
 
@DatabaseField(columnName="DebarkationDate", dataType = DataType.DATE_LONG, canBeNull = true)
public java.util.Date DebarkationDate; 
 
@DatabaseField(columnName="VoyageID", canBeNull = true)
public String VoyageID; 
 
@DatabaseField(columnName="LoyaltyTier", canBeNull = true)
public String LoyaltyTier; 
 
@DatabaseField(columnName="RepeatCruiser", canBeNull = true)
public String RepeatCruiser; 
 
@DatabaseField(columnName="Cabin", canBeNull = true)
public String Cabin; 
 
@DatabaseField(columnName="Language", canBeNull = true)
public String Language; 
 
@DatabaseField(columnName="FolioID", canBeNull = true)
public String FolioID; 
 
@DatabaseField(columnName="UpdateBySQLUser", canBeNull = true)
public String UpdateBySQLUser; 
 
@DatabaseField(columnName="UpdateDateUTC", dataType = DataType.DATE_LONG, canBeNull = true)
public java.util.Date UpdateDateUTC; 
 
@DatabaseField(columnName="ToDelete", canBeNull = false)
public String ToDelete; 
 
@DatabaseField(columnName="UniqueGuestID", canBeNull = true)
public String UniqueGuestID; 
 
 
}
