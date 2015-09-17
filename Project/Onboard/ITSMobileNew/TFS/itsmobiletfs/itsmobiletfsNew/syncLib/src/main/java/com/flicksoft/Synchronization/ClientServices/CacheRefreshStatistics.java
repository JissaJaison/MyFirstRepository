package com.flicksoft.Synchronization.ClientServices;

import java.util.*;

	/**
	*   <summary>
	*   Class that represents the stats for a sync session.
	*   < summary>
	* 
	**/

public class CacheRefreshStatistics {

	/// <summary>
    /// Start Time of Sync Session
    /// </summary>
    public Date StartTime ;

    /// <summary>
    /// End Time of Sync Session
    /// </summary>
    public Date EndTime;

    /// <summary>
    /// Total number of change sets downloaded
    /// </summary>
    public int TotalChangeSetsDownloaded;

    /// <summary>
    /// Total number of change sets uploaded
    /// </summary>
    public int TotalChangeSetsUploaded;

    /// <summary>
    /// Total number of Uploded Items
    /// </summary>
    public int TotalUploads;

    /// <summary>
    /// Total number of downloaded items
    /// </summary>
    public int TotalDownloads;

    /// <summary>
    /// Total number of Sync Conflicts
    /// </summary>
    public int TotalSyncConflicts;

    /// <summary>
    /// Total number of Sync Conflicts
    /// </summary>
    public int TotalSyncErrors;
}
