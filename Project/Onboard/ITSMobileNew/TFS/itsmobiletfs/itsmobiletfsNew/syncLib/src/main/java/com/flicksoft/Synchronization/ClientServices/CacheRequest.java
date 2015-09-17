package com.flicksoft.Synchronization.ClientServices;

import java.util.*;

	/**
	*   <summary>
	*   Wrapper Class representing all the related information about an Sync request
	*   < summary>
	* 
	**/

class CacheRequest {
	public UUID RequestId;
	public ArrayList<IOfflineEntity> Changes;
	public CacheRequestType RequestType;
	public byte[] KnowledgeBlob;
	public boolean IsLastBatch;
	public SyncSerializationFormat Format;
}