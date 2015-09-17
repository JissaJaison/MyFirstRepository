package util;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.sql.SQLException;

import ITSMobile.ActionPanels;
import ITSMobile.ActionVisibilityByDepartment;
import ITSMobile.ActionVisibilityByGeneralGroup;
import ITSMobile.ActionVisibilityByUser;
import ITSMobile.ActionWorkflows;
import ITSMobile.ActionWorkflows_IssueClasses;
import ITSMobile.Actions;
import ITSMobile.ActionsByStatus;
import ITSMobile.AmosComponentType;
import ITSMobile.AmosComponentUnit;
import ITSMobile.AmosIssuesComponentUnit;
import ITSMobile.Causes;
import ITSMobile.ComplaintSeverities;
import ITSMobile.ConfigPDA;
import ITSMobile.Decks;
import ITSMobile.Defects;
import ITSMobile.DepartmentPositions;
import ITSMobile.Departments;
import ITSMobile.FileAttachments;
import ITSMobile.FireZones;
import ITSMobile.GeneralGroups_Departments;
import ITSMobile.GeneralGroups_Users;
import ITSMobile.IssueClasses;
import ITSMobile.IssueGroups;
import ITSMobile.IssueTypes;
import ITSMobile.LocationGroups;
import ITSMobile.Locations;
import ITSMobile.Locations_LocationGroups;
import ITSMobile.MessageAddressee;
import ITSMobile.Messages;
import ITSMobile.MobileDevice;
/* import ITSMobile.MobileIssueTracks;*/
import ITSMobile.MobileIssues;
import ITSMobile.Panels;
import ITSMobile.PassengerInfo;
import ITSMobile.PermissionGroups_Departments;
import ITSMobile.PermissionGroups_Users;
import ITSMobile.Permissions;
import ITSMobile.Permissions_Departments;
import ITSMobile.Priorities;
import ITSMobile.Statuses;
import ITSMobile.SysParms;
import ITSMobile.Transverses;
import ITSMobile.UserDepartments;
import ITSMobile.Users;
import ITSMobile.UsersMobile;
import ITSMobile.WorkflowAssociations;
import ITSMobile.Zones;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class ConfigUtil extends OrmLiteConfigUtil {
    private static final Class<?>[] classes = new Class[]{
            Actions.class,
            ActionsByStatus.class,
            ActionVisibilityByDepartment.class,
            ActionVisibilityByGeneralGroup.class,
            ActionVisibilityByUser.class,
            ActionWorkflows.class,
            ActionWorkflows_IssueClasses.class,
            Causes.class,
            ConfigPDA.class,
            Decks.class,
            Departments.class,
            FileAttachments.class,
            FireZones.class,
            GeneralGroups_Departments.class,
            GeneralGroups_Users.class,
            IssueClasses.class,
            IssueGroups.class,
            IssueTypes.class,
            LocationGroups.class,
            Locations.class,
            Locations_LocationGroups.class,
            MessageAddressee.class,
            Messages.class,
            MobileDevice.class,
            PassengerInfo.class,
            PermissionGroups_Departments.class,
            PermissionGroups_Users.class,
            Permissions.class,
            Permissions_Departments.class,
            Priorities.class,
            Statuses.class,
            SysParms.class,
            Transverses.class,
            UserDepartments.class,
            Users.class,
            UsersMobile.class,
            WorkflowAssociations.class,
            Zones.class,
            Panels.class,
            ActionPanels.class,

            //Added 3/2
            DepartmentPositions.class,
            AmosComponentType.class,
            AmosComponentUnit.class,
            ComplaintSeverities.class,
            Defects.class,
            AmosIssuesComponentUnit.class,
            //Added 6/30
            MobileIssues.class,
            /*MobileIssueTracks.class*/
    };

    public static void main(String[] args) throws SQLException, IOException {
            writeConfigFile("ormlite_config.txt", classes);
    }
}
