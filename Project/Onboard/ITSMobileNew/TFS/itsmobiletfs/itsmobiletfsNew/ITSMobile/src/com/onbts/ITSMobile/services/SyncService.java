package com.onbts.ITSMobile.services;
//ADDRESS TO UPDATE 
//http://67.215.180.182/ITSMobile/itsmobileSyncService.svc

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.flicksoft.Synchronization.ClientServices.CacheController;
import com.flicksoft.Synchronization.ClientServices.CacheRefreshStatistics;
import com.flicksoft.Synchronization.SQLiteOfflineSyncProvider;
import com.flicksoft.Synchronization.SQLiteStorageHandler;
import com.flicksoft.util.CallBack;
import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.UI.fragments.preference.UpdateServerFragment;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

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
import ITSMobile.IssueTracks;
import ITSMobile.IssueTypes;
import ITSMobile.Issues;
import ITSMobile.LocationGroups;
import ITSMobile.Locations;
import ITSMobile.Locations_LocationGroups;
import ITSMobile.MessageAddressee;
import ITSMobile.Messages;
import ITSMobile.MobileDevice;
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
 * class used by application to sync mobile db with server db
 */


public class SyncService {
    public String _synckStatus;
    public static final int MSG_BACKGROUND_SYNC_WAITING = 1;

    public static final int MSG_SYNC_IN_PROGRESS = 2;
    public static final int MSG_SYNC_COMPLETED = 3;
    public static final int MSG_SYNC_CANCELED = 4;
    public static final int MSG_SYNC_FAILED = 5;
    public static final int MSG_SYNC_REJECTED = 6;
    public static final int MSG_HARD_SYNC_COMPLETED = 7;
    public static final int MSG_HARD_SYNC_IN_PROGRESS = 8;
    private static final long CHECK_TIME_UNIT = 10 * 1000;
    private static SyncService _instance = null;
    protected Context _context;
    protected URI _syncServiceUrl;
    protected Handler _uiHandler;
    protected AtomicBoolean _isPeriodicSyncThreadRunning;
    protected AtomicBoolean _isSyncing;
    private long _syncTimeInterval = 0;
    private long _nextSyncTime = 0;
    private long _nextCheckTime = 0;
    private String _localDbFilePath = "";
    private CacheController _controller;
    private Thread _periodicSyncThread;
    private Thread _oneTimeSyncThread;

    public static SyncService getInstance() {
        if (_instance == null) {
            _instance = new SyncService();
        }
        return _instance;
    }

    public void setSyncServiceUrl(String urlString) {
        try {
            _syncServiceUrl = new URI(urlString);
            _controller.setServiceUri(_syncServiceUrl);
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            Log.e("SyncLib", e.getMessage());
        }
    }

    public void init(Context theContext, Handler uiHandler, String urlString, String localDbFile) {

        SQLiteStorageHandler.configFile = R.raw.ormlite_config;

        _localDbFilePath = localDbFile;
        _uiHandler = uiHandler;
        _context = theContext;
        _isPeriodicSyncThreadRunning = new AtomicBoolean(false);
        _isSyncing = new AtomicBoolean(false);

        try {
            _syncServiceUrl = new URI(urlString);
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            Log.e("Inspections", e.getMessage());
        }


        SQLiteStorageHandler.Instance(theContext);
        SQLiteOfflineSyncProvider localProvider = new SQLiteOfflineSyncProvider();

        _controller = new CacheController(_syncServiceUrl, "ITSMobile", localProvider);

        _controller.getControllerBehavior().AddType(Actions.class);
        _controller.getControllerBehavior().AddType(ActionsByStatus.class);
        _controller.getControllerBehavior().AddType(ActionVisibilityByDepartment.class);
        _controller.getControllerBehavior().AddType(ActionVisibilityByGeneralGroup.class);
        _controller.getControllerBehavior().AddType(ActionVisibilityByUser.class);
        _controller.getControllerBehavior().AddType(ActionWorkflows.class);
        _controller.getControllerBehavior().AddType(ActionWorkflows_IssueClasses.class);
        _controller.getControllerBehavior().AddType(Causes.class);
        _controller.getControllerBehavior().AddType(ConfigPDA.class);
        _controller.getControllerBehavior().AddType(Decks.class);
        _controller.getControllerBehavior().AddType(Departments.class);
        _controller.getControllerBehavior().AddType(FileAttachments.class);
        _controller.getControllerBehavior().AddType(FireZones.class);
        _controller.getControllerBehavior().AddType(GeneralGroups_Departments.class);
        _controller.getControllerBehavior().AddType(GeneralGroups_Users.class);
        _controller.getControllerBehavior().AddType(IssueClasses.class);
        _controller.getControllerBehavior().AddType(IssueGroups.class);
        _controller.getControllerBehavior().AddType(IssueTypes.class);
        _controller.getControllerBehavior().AddType(LocationGroups.class);
        _controller.getControllerBehavior().AddType(Locations.class);
        _controller.getControllerBehavior().AddType(Locations_LocationGroups.class);
        _controller.getControllerBehavior().AddType(MessageAddressee.class);
        _controller.getControllerBehavior().AddType(Messages.class);
        _controller.getControllerBehavior().AddType(MobileDevice.class);
        _controller.getControllerBehavior().AddType(PassengerInfo.class);
        _controller.getControllerBehavior().AddType(PermissionGroups_Departments.class);
        _controller.getControllerBehavior().AddType(PermissionGroups_Users.class);
        _controller.getControllerBehavior().AddType(Permissions.class);
        _controller.getControllerBehavior().AddType(Permissions_Departments.class);
        _controller.getControllerBehavior().AddType(Priorities.class);
        _controller.getControllerBehavior().AddType(Statuses.class);
        _controller.getControllerBehavior().AddType(SysParms.class);
        _controller.getControllerBehavior().AddType(Transverses.class);
        _controller.getControllerBehavior().AddType(UserDepartments.class);
        _controller.getControllerBehavior().AddType(Users.class);
        _controller.getControllerBehavior().AddType(UsersMobile.class);
        _controller.getControllerBehavior().AddType(WorkflowAssociations.class);
        _controller.getControllerBehavior().AddType(Zones.class);
        _controller.getControllerBehavior().AddType(Issues.class);
        _controller.getControllerBehavior().AddType(IssueTracks.class);

        _controller.getControllerBehavior().AddType(Panels.class);
        _controller.getControllerBehavior().AddType(ActionPanels.class);

//Added 3/2
        _controller.getControllerBehavior().AddType(DepartmentPositions.class);
        _controller.getControllerBehavior().AddType(AmosComponentType.class);
        _controller.getControllerBehavior().AddType(AmosComponentUnit.class);
        _controller.getControllerBehavior().AddType(AmosIssuesComponentUnit.class);
        _controller.getControllerBehavior().AddType(ComplaintSeverities.class);
        _controller.getControllerBehavior().AddType(Defects.class);

        //Added 6/30
        _controller.getControllerBehavior().AddType(MobileIssues.class);
    //    _controller.getControllerBehavior().AddType(MobileIssueTracks.class);
    }

    synchronized private void sendMessage(int task, String msg) {
        Log.i("sendMessage", "start");
        Message msgToSend = Message.obtain();
        msgToSend.what = task;
        msgToSend.obj = msg;
        _uiHandler.sendMessage(msgToSend);
        Log.i("sendMessage", "finish");
    }

    synchronized public void sendMessageForCacheController(String msg) {
        Message msgToSend = Message.obtain();
        msgToSend.what = MSG_SYNC_IN_PROGRESS;
        msgToSend.obj = msg;
        _uiHandler.sendMessage(msgToSend);
    }

    synchronized public void sendMessageForCacheController(Integer percentage) {
        Message msgToSend = Message.obtain();
        msgToSend.what = MSG_SYNC_IN_PROGRESS;
        msgToSend.obj = percentage;
        _uiHandler.sendMessage(msgToSend);
    }

    synchronized private void setTimesForBackgroundSync(long checkTime, long syncTime) {
        _nextSyncTime = syncTime;
        _nextCheckTime = checkTime;
    }

    public void hardSync() {
        try {

            if (_isSyncing.get()) {
                sendMessage(MSG_SYNC_REJECTED, "Can't start hard sync. Another sync is processing");
            } else {
                _isSyncing.set(true);
                if (isSyncServiceAvailable()) {
                    sendMessage(MSG_SYNC_IN_PROGRESS, "Hard sync in progress...");

                    File file = new File(_localDbFilePath);

                    file.delete();
                    _controller.init(_localDbFilePath);

                    sendMessage(MSG_SYNC_IN_PROGRESS, "Hard sync in progress.... ");
                    _controller.getControllerBehavior().AddScopeParameters("Completed", "0");


                    CallBack callBack = new CallBack(this, "sendMessageForCacheController");
                    CacheRefreshStatistics result = _controller.Refresh(callBack);

                    //	DbService.getInstance(App.getAppContext()).init();
                    DbService.getInstance(_context).init();
                    DbService.getInstance(_context).createViews();
                    DbService.getInstance(_context).deleteCloses();
                    sendMessage(MSG_HARD_SYNC_COMPLETED, String.format("Hard sync completed. %d uploaded. %d downloaded", result.TotalUploads, result.TotalDownloads));
                    Log.e("total Uploads","result.TotalUploads");
                } else {
                    sendMessage(MSG_SYNC_FAILED, "Hard sync failed. No connection.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
//            Log.e("Inspections", e.getMessage());
            sendMessage(MSG_SYNC_FAILED, e.getMessage());
//            Crashlytics.logException(e);
        } finally {
            _isSyncing.set(false);
        }
    }

    /**
     * <summary>
     * the sync when driver start their work.
     * < summary>
     */
    private boolean manualSync() {

        boolean result = true;
        Log.i("Inspections", "manualSync");
        try {
            if (_isSyncing.get()) {
                sendMessage(MSG_SYNC_REJECTED, "Can't start  sync. Another sync is progressing");
                Log.i("Inspections", "Can't start  sync. Another sync is progressing");
            } else {
                _isSyncing.set(true);
                Log.i("Inspections", " _isSyncing.set(true);");
                if (isSyncServiceAvailable()) {
                    Log.i("Inspections", " isSyncServiceAvailable = true");
                    sendMessage(MSG_SYNC_IN_PROGRESS, "Sync in progress...");
                    CacheRefreshStatistics stat1 = new CacheRefreshStatistics();
                    Log.i("Inspections", " stat1 init");
                    CacheRefreshStatistics stat = new CacheRefreshStatistics();
                    //clean up the checklist, items... that are not created/picked up on this device
                    //DbService.getInstance(null).deleteCheckListsByDeviceID(DbService.getInstance(null).getDeviceId());
                    _controller.init(_localDbFilePath);
                    Log.i("Inspections", "  _controller.init(_localDbFilePath);");
                    CallBack callBack = new CallBack(this, "sendMessageForCacheController");
                    Log.i("Inspections", " new CallBack(this, \"sendMessageForCacheController\");");
                    _controller.getControllerBehavior().ClearScopeParameters();
                    //changed 2 to 0
                    _controller.getControllerBehavior().AddScopeParameters("Completed", "2");
                    //_controller.getControllerBehavior().AddScopeParameters("Completed","1");
                    stat = _controller.Refresh(callBack);

//			    	_controller.getControllerBehavior().ClearScopeParameters();
//			    	_controller.getControllerBehavior().AddScopeParameters("Completed","0");
//			    	CacheRefreshStatistics  stat1 = _controller.Refresh(callBack);
                    //sendMessage(MSG_SYNC_COMPLETED, Stri ng.format("Sync completed. %d kb uploaded. %d kb downloaded", stat.TotalUploads, stat.TotalDownloads));

                    //	DbService.getInstance(null).deleteCompletedCheckLists();
                    DbService.getInstance(_context).init();
                    DbService.getInstance(_context).createViews();
                    DbService.getInstance(_context).deleteCloses();
                    sendMessage(MSG_SYNC_COMPLETED, String.format("Sync completed. %d uploaded. %d downloaded", stat.TotalUploads + stat1.TotalUploads, stat.TotalDownloads + stat1.TotalDownloads));

                } else {
                    sendMessage(MSG_SYNC_FAILED, "Sync failed. No connection.");
                    result = false;
                }
            }
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
            Log.e("Inspections", e.getMessage() != null ? e.getMessage() : e.toString());
            sendMessage(MSG_SYNC_FAILED, e.getMessage());
//            Crashlytics.logException(e);
        } finally {
            _isSyncing.set(false);
        }
        return result;
    }


    private Runnable getOneTimeRunnable(final SyncTask theTask) {

        Runnable t = new Runnable() {
            public void run() {
                switch (theTask) {
                    case HARD_SYNC_TASK:
                        hardSync();
                        break;
                    case MANUAL_SYNC_TASK:

                        if (manualSync())
                        //reset background sync timer after successful manual sync
                        {
                            setTimesForBackgroundSync(CHECK_TIME_UNIT, _syncTimeInterval);
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        return t;
    }

    // -- Class Members --

    private String getNextSyncTimeString() {
        long totalSeconds = (_nextSyncTime - _nextCheckTime + CHECK_TIME_UNIT) / 1000;
        if (totalSeconds >= 60) {
            return String.format("Next sync: %d Min", totalSeconds / 60);
        } else {
            return String.format("Next sync: < 1 Min");
        }
    }

    // -- Set Timer --
    private Runnable getPeriodicRunnable(final long timeElapsed) {

        Runnable t = new Runnable() {
            public void run() {
                _syncTimeInterval = timeElapsed;
                setTimesForBackgroundSync(CHECK_TIME_UNIT, _syncTimeInterval);

                sendMessage(MSG_BACKGROUND_SYNC_WAITING, getNextSyncTimeString());
                while (_isPeriodicSyncThreadRunning.get()) {
                    try {
                        Thread.sleep(CHECK_TIME_UNIT);
                        //check again when thread out of sleep to cancel the sync
                        if (_isPeriodicSyncThreadRunning.get()) {
                            //try more frequently if the scheduled sync fails until a successfully sync
                            if (_nextCheckTime < _nextSyncTime) {
                                setTimesForBackgroundSync(_nextCheckTime + CHECK_TIME_UNIT, _syncTimeInterval);
                            } else {
                                if (manualSync()) {
                                    setTimesForBackgroundSync(CHECK_TIME_UNIT, _syncTimeInterval);
                                } else
                                {
                                    setTimesForBackgroundSync(CHECK_TIME_UNIT, CHECK_TIME_UNIT);
                                }
                            }
                            if (!_isSyncing.get()) {
                                sendMessage(MSG_BACKGROUND_SYNC_WAITING, getNextSyncTimeString());
                            }
                        }
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        };
        return t;
    }

    private boolean isSyncServiceAvailable() throws UnknownHostException, IOException {
        ConnectivityManager cm =
                (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;

        }
        return false;
    }

    public void StartHardSync() {

        _oneTimeSyncThread = new Thread(getOneTimeRunnable(SyncTask.HARD_SYNC_TASK));
        _oneTimeSyncThread.start();
        //Changes made by Jissa on Issue 10 of mobile changes.
//                    toggleButton1.setEnabled(true);
//                    toggleButton1.setChecked(true);
    }

    public void StartManualSync() {

        _oneTimeSyncThread = new Thread(getOneTimeRunnable(SyncTask.MANUAL_SYNC_TASK));
        _oneTimeSyncThread.start();
    }

    //start backgorund sync with interval(milli seconds)
    public void StartBackgroundSync(long syncInterval) {


        if (_isPeriodicSyncThreadRunning.get())
        //one and only background thread
        {

        } else {
            // Start the background thread to increment the ticker
            _periodicSyncThread = new Thread(getPeriodicRunnable(syncInterval));
            _isPeriodicSyncThreadRunning.set(true);
            _periodicSyncThread.start();
        }

    }

    public void EndBackgroundSync() {

        sendMessage(MSG_HARD_SYNC_COMPLETED, "");
        _isPeriodicSyncThreadRunning.set(false);

    }

    public static enum SyncTask {
        HARD_SYNC_TASK,
        MANUAL_SYNC_TASK,
        PERIODIC_SYNC_TASK,
        STATE_SYNC_TASK
    }

    public static enum SyncTaskState {
        MSG_NO,
        MSG_BACKGROUND_SYNC_WAITING,
        MSG_SYNC_IN_PROGRESS,
        MSG_SYNC_COMPLETED,
        MSG_SYNC_CANCELED,
        MSG_SYNC_FAILED,
        MSG_SYNC_REJECTED,
        MSG_HARD_SYNC_COMPLETED,
        MSG_HARD_SYNC_IN_PROGRESS;

        // public static final int MSG_BACKGROUND_SYNC_WAITING = 1;
        // public static final int MSG_SYNC_IN_PROGRESS = 2;
        // public static final int MSG_SYNC_COMPLETED = 3;
        // public static final int MSG_SYNC_CANCELED = 4;
        // public static final int MSG_SYNC_FAILED = 5;
        // public static final int MSG_SYNC_REJECTED = 6;
        // public static final int MSG_HARD_SYNC_COMPLETED = 7;
        // public static final int MSG_HARD_SYNC_IN_PROGRESS = 8;
        public static SyncTaskState getValue(int id) {
            switch (id) {
                case 1:
                    return MSG_BACKGROUND_SYNC_WAITING;
                case 2:
                    return MSG_SYNC_IN_PROGRESS;
                case 3:
                    return MSG_SYNC_COMPLETED;
                case 4:
                    return MSG_SYNC_CANCELED;
                case 5:
                    return MSG_SYNC_FAILED;
                case 6:
                    return MSG_SYNC_REJECTED;
                case 7:
                    return MSG_HARD_SYNC_COMPLETED;
                case 8:
                    return MSG_HARD_SYNC_IN_PROGRESS;
                default:
                    return MSG_NO;
            }
        }
    }
}