package com.flicksoft.Synchronization.ClientServices;

import java.util.*;

	/**
	*   <summary>
	*   Represents a Synchronization related Conflict that was raised and handled on the server.
	*   < summary>
	* 
	**/

public class SyncConflict extends Conflict {

	public IOfflineEntity LosingEntity = null;
	
	public SyncConflictResolution Resolution;
	
	

}