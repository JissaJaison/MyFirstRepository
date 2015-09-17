package com.flicksoft.Synchronization.ClientServices;

import java.util.*;

	/**
	*   <summary>
	*   Represents the resolution that the server employed to resolve a conflict.
	*   < summary>
	* 
	**/

public enum SyncConflictResolution {
	
        ServerWins,
        ClientWins,
        Merge
}