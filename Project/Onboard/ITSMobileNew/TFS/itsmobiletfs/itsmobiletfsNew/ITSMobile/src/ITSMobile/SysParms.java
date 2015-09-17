package ITSMobile;
 
 
import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteOfflineEntity; 
import com.j256.ormlite.field.DataType; 
import com.j256.ormlite.field.DatabaseField; 
import com.j256.ormlite.table.DatabaseTable; 
 
 
@DatabaseTable(tableName="SysParms")
public class SysParms extends SQLiteOfflineEntity 
 
{
 
 
@Key 
@DatabaseField(columnName="SysParmsID", canBeNull = false)
public Integer SysParmsID; 
 
@Key 
@DatabaseField(columnName="SiteID", canBeNull = false)
public Integer SiteID; 
 
@DatabaseField(columnName="SiteName", canBeNull = false)
public String SiteName; 
 
@DatabaseField(columnName="SiteInitials", canBeNull = true)
public String SiteInitials; 
 
@DatabaseField(columnName="SiteURL", canBeNull = false)
public String SiteURL; 
 
@DatabaseField(columnName="EmailFrom", canBeNull = false)
public String EmailFrom; 
 
@DatabaseField(columnName="EmailSubject", canBeNull = false)
public String EmailSubject; 
 
@DatabaseField(columnName="Shipboard", canBeNull = false)
public String Shipboard; 
 
@DatabaseField(columnName="WorkflowID", canBeNull = true)
public Integer WorkflowID; 
 
@DatabaseField(columnName="AssignUser", canBeNull = false)
public String AssignUser; 
 
@DatabaseField(columnName="DefaultSummary", canBeNull = false)
public String DefaultSummary; 
 
@DatabaseField(columnName="AllowFileAttachments", canBeNull = false)
public String AllowFileAttachments; 
 
@DatabaseField(columnName="FileAttachmentsDir", canBeNull = false)
public String FileAttachmentsDir; 
 
@DatabaseField(columnName="FileAttachmentsMaxBytesPerAction", canBeNull = false)
public Integer FileAttachmentsMaxBytesPerAction; 
 
@DatabaseField(columnName="FileAttachmentsMaxBytesPerIssue", canBeNull = false)
public Integer FileAttachmentsMaxBytesPerIssue; 
 
@DatabaseField(columnName="DateFormatGeneral", canBeNull = true)
public String DateFormatGeneral; 
 
@DatabaseField(columnName="DateFormatMinutes", canBeNull = true)
public String DateFormatMinutes; 
 
@DatabaseField(columnName="DateFormatSeconds", canBeNull = true)
public String DateFormatSeconds; 
 
@DatabaseField(columnName="DateFormatMilliSeconds", canBeNull = true)
public String DateFormatMilliSeconds; 
 
@DatabaseField(columnName="DateSeparator", canBeNull = true)
public String DateSeparator; 
 
@DatabaseField(columnName="RepeatedIssueTimeFrame", canBeNull = true)
public Integer RepeatedIssueTimeFrame; 
 
@DatabaseField(columnName="ReportJobApp", canBeNull = true)
public String ReportJobApp; 
 
@DatabaseField(columnName="ReportJobLoadDir", canBeNull = true)
public String ReportJobLoadDir; 
 
@DatabaseField(columnName="SendIssueApp", canBeNull = true)
public String SendIssueApp; 
 
@DatabaseField(columnName="GuestServiceEnabled", canBeNull = false)
public String GuestServiceEnabled; 
 
@DatabaseField(columnName="RequireGuestName", canBeNull = false)
public String RequireGuestName; 
 
@DatabaseField(columnName="SearchGuestNameInPMS", canBeNull = false)
public String SearchGuestNameInPMS; 
 
@DatabaseField(columnName="HandheldEnabled", canBeNull = false)
public String HandheldEnabled; 
 
@DatabaseField(columnName="DisplayHoursWorked", canBeNull = false)
public String DisplayHoursWorked; 
 
@DatabaseField(columnName="DisplayTotalTimeForIssue", canBeNull = false)
public String DisplayTotalTimeForIssue; 
 
@DatabaseField(columnName="DashboardAutoRefresh", canBeNull = false)
public Integer DashboardAutoRefresh; 
 
@DatabaseField(columnName="ReportHourDateFormat", canBeNull = false)
public String ReportHourDateFormat; 
 
@DatabaseField(columnName="PartsQueryMaxRecords", canBeNull = false)
public Integer PartsQueryMaxRecords; 
 
@DatabaseField(columnName="RequireCause", canBeNull = false)
public String RequireCause; 
 
@DatabaseField(columnName="CMDShellLogin", canBeNull = false)
public String CMDShellLogin; 
 
@DatabaseField(columnName="WSBesUrl", canBeNull = true)
public String WSBesUrl; 
 
@DatabaseField(columnName="WSNotificationTicket", canBeNull = true)
public String WSNotificationTicket; 
 
@DatabaseField(columnName="ImportFromXmlApp", canBeNull = true)
public String ImportFromXmlApp; 
 
@DatabaseField(columnName="UsesHESS", canBeNull = false)
public String UsesHESS; 
 
@DatabaseField(columnName="PONumberLabel", canBeNull = false)
public String PONumberLabel; 
 
@DatabaseField(columnName="DisplayCheckSpelling", canBeNull = false)
public String DisplayCheckSpelling; 
 
@DatabaseField(columnName="AllowDepartmentSelectionInCreateIssue", canBeNull = false)
public String AllowDepartmentSelectionInCreateIssue; 
 
@DatabaseField(columnName="DisplayEnteredByOnCreate", canBeNull = false)
public String DisplayEnteredByOnCreate; 
 
@DatabaseField(columnName="DisplayReportByCrewOnCreate", canBeNull = false)
public String DisplayReportByCrewOnCreate; 
 
@DatabaseField(columnName="PriorityReadOnlyOnCreate", canBeNull = false)
public String PriorityReadOnlyOnCreate; 
 
@DatabaseField(columnName="TrackIssuesUI_DisplayIssueClass", canBeNull = false)
public String TrackIssuesUI_DisplayIssueClass; 
 
@DatabaseField(columnName="TrackIssuesUI_DisplayIssueType", canBeNull = false)
public String TrackIssuesUI_DisplayIssueType; 
 
@DatabaseField(columnName="TrackIssuesUI_DisplaySummary", canBeNull = false)
public String TrackIssuesUI_DisplaySummary; 
 
@DatabaseField(columnName="LastUpdateDate", dataType = DataType.DATE_LONG, canBeNull = false)
public java.util.Date LastUpdateDate; 
 
@DatabaseField(columnName="LastUpdateUserID", canBeNull = false)
public Integer LastUpdateUserID; 
 
@DatabaseField(columnName="UpdateBySQLUser", canBeNull = true)
public String UpdateBySQLUser; 
 
@DatabaseField(columnName="UpdateDateUTC", dataType = DataType.DATE_LONG, canBeNull = true)
public java.util.Date UpdateDateUTC; 
 
@DatabaseField(columnName="SummaryReadOnlyOnCreate", canBeNull = false)
public String SummaryReadOnlyOnCreate; 
 
@DatabaseField(columnName="CreateIssueUI_LocationHistoryDays", canBeNull = false)
public Integer CreateIssueUI_LocationHistoryDays; 
 
@DatabaseField(columnName="VoyagesToList", canBeNull = false)
public Integer VoyagesToList; 
 
@DatabaseField(columnName="ReportIGConfigurationFile", canBeNull = true)
public String ReportIGConfigurationFile; 
 
@DatabaseField(columnName="EnhancedModifyIssue", canBeNull = false)
public String EnhancedModifyIssue; 
 
@DatabaseField(columnName="PasswordComplexityEnabled", canBeNull = false)
public String PasswordComplexityEnabled; 
 
@DatabaseField(columnName="InspectorEnabled", canBeNull = false)
public String InspectorEnabled; 
 
@DatabaseField(columnName="EnableSqlAgent", canBeNull = false)
public String EnableSqlAgent; 
 
 
}
