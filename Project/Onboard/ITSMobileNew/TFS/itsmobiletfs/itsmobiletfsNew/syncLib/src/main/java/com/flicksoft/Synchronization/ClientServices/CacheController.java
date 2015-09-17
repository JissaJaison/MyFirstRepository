package com.flicksoft.Synchronization.ClientServices;

import java.util.*;
import java.io.File;
import java.io.IOException;
import java.net.*;

import android.util.Log;

import com.flicksoft.util.*;
import com.flicksoft.Synchronization.SQLiteStorageHandler;
import com.flicksoft.Synchronization.ClientServices.ScopeMetaData.*;
	/**
	*   <summary>
	*   Class used for synchronizing an offline cache with a remote sync service.
	*   < summary>
	*
	**/

public class CacheController {
	//boolean Locked;
	OfflineSyncProvider _localProvider;
	URI _serviceUri;
	CacheControllerBehavior _controllerBehavior;
	CacheRequestHandler _cacheRequestHandler;
	//Object _lockObject = new Object();

	public CacheController(URI serviceUri, String scopeName, com.flicksoft.Synchronization.ClientServices.OfflineSyncProvider localProvider) {
		if ( serviceUri == null ) {
			throw new IllegalArgumentException("serviceUri");
		}
		if ( scopeName == null && scopeName == "") {
			throw  new IllegalArgumentException("scopeName");
		}
		if (!serviceUri.getScheme().startsWith("http") &&
                !serviceUri.getScheme().startsWith("https")){
			throw new IllegalArgumentException("Uri must be http or https schema");
		}
		if ( localProvider == null ) {
			throw new IllegalArgumentException("localProvider");
		}
		this._serviceUri = serviceUri;
		this._localProvider = localProvider;
		this._controllerBehavior = new CacheControllerBehavior();
		this._controllerBehavior.setScopeName(scopeName);
	}


	/**
	*   <summary>
	*   Method that refreshes the Cache by uploading all modified changes and then downloading the
	*   server changes.
	*   < summary>
	*   <returns>A CacheRefreshStatistics object denoting the statistics from the Refresh call< returns>
	 * @throws Exception 
	**/
	public CacheRefreshStatistics Refresh(CallBack notifier) throws Exception {
		CacheRefreshStatistics refreshStats = new CacheRefreshStatistics();
		try {

			refreshStats.StartTime = Calendar.getInstance().getTime();
			
			boolean uploadComplete = false;
			boolean downloadComplete = false;
			
			this._cacheRequestHandler.scopeParameters =  new HashMap<String, String>(this.getControllerBehavior().getScopeParametersInternal());
			//  Start sync by executin an Upload request
			while (( !(uploadComplete) || !(downloadComplete) )) {
				if ( !(uploadComplete) ) {
					
					UUID changeSetId = UUID.randomUUID();
					
					ChangeSet changeSet = this._localProvider.GetChangeSet(changeSetId);
					notifier.invoke(String.format("Uploading %d Records...", changeSet.Data.size()));
					if ( (changeSet == null || changeSet.Data == null || changeSet.Data.size() == 0 ) ) {
						//  No data to upload. Skip upload phase.
						
						uploadComplete = true;
					}
					 else {
						//  Create a SyncRequest out of this.
						CacheRequest request = new CacheRequest();
						request.RequestId = changeSetId;
						request.RequestType = CacheRequestType.UploadChanges;
						request.Changes = changeSet.Data;
						request.KnowledgeBlob = changeSet.ServerBlob;
						request.IsLastBatch = changeSet.IsLastBatch;
						request.Format = SyncSerializationFormat.ODataJson;
						//  Increment the stats
						refreshStats.TotalChangeSetsUploaded++;
						
						ChangeSetResponse response = (ChangeSetResponse) this._cacheRequestHandler.ProcessCacheRequest(request);
						//  Increment the stats
						refreshStats.TotalUploads += request.Changes.size();
						ArrayList<Conflict> tempConflicts = response.getConflictsInternal();
						for (Conflict curConflict: tempConflicts)
						{
							if (curConflict instanceof SyncConflict)
                            {
                                refreshStats.TotalSyncConflicts++;
                            }
                            else
                            {
                                refreshStats.TotalSyncErrors++;
                            }
						}
						
						
						//  Send the response to the local provider
						
							this._localProvider.OnChangeSetUploaded(changeSetId, response) ;
							
							
						uploadComplete = request.IsLastBatch;
					}
					Log.i("SyncLib",  "Upload completed.");
					notifier.invoke(50);
					notifier.invoke("Upload completed.");
				} else if ( !(downloadComplete) ) {
					notifier.invoke("Downloading in progress...");
					//  Create a SyncRequest for download.
					CacheRequest request = new CacheRequest( );
					request.RequestType = CacheRequestType.DownloadChanges;
					request.KnowledgeBlob = this._localProvider.GetServerBlob() ;
					request.Format = SyncSerializationFormat.ODataJson;
					
					//save the changeset
					String message = String.format("Download #%d started ...", refreshStats.TotalChangeSetsDownloaded);
					
					Log.i("SyncLib", message);
					ChangeSet changeSet = (ChangeSet) this._cacheRequestHandler.ProcessCacheRequest(request);
					
					message = String.format("Download #%d with %d records completed", refreshStats.TotalChangeSetsDownloaded, changeSet.Data.size());
					
					Log.i("SyncLib",  message);
					
					this.getLocalProvider().SaveChangeSet(changeSet);
					
					message = String.format(String.format("Saving download  #%d completed", refreshStats.TotalChangeSetsDownloaded));
					
					Log.i("SyncLib",  message);
					//  Increment the refresh stats
					refreshStats.TotalChangeSetsDownloaded++;
					refreshStats.TotalDownloads += changeSet.Data.size();
					downloadComplete = changeSet.IsLastBatch;
					if (downloadComplete)
					{
						notifier.invoke("Downloading 100% finished");
						//notifier.invoke(100);
					}
					else
					{
						int percentage = 50 + 50 * refreshStats.TotalDownloads / (refreshStats.TotalDownloads + 1) ;
						//notifier.invoke(percentage);
						notifier.invoke(String.format("Downloading  %d%% finished...", percentage));
					}
				}
			}
			refreshStats.EndTime = Calendar.getInstance().getTime();
			//  Finally return the statistics object
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			throw(e);
		}
		finally {
		}
		return refreshStats;
	}

	public void init(String dbFile) throws Exception {
		CacheRequest requestSchema = new CacheRequest();
		requestSchema.Format = SyncSerializationFormat.ODataAtom;
		this._cacheRequestHandler = com.flicksoft.Synchronization.ClientServices.CacheRequestHandler.CreateRequestHandler(this._serviceUri, this._controllerBehavior);
		
		//deal with null db and schema change
		File theFile = new File(dbFile);
		if (!theFile.exists())
		{
			requestSchema.RequestType = CacheRequestType.Metadata;
			

			Schema response = (Schema) this._cacheRequestHandler.ProcessCacheRequest(requestSchema);
			
			this._localProvider.initDB(dbFile, response, this.getControllerBehavior().getKnownTypes());
		}
		else
		{ 
			this._localProvider.initDB(dbFile, null, this.getControllerBehavior().getKnownTypes());
		}
	}

	public com.flicksoft.Synchronization.ClientServices.OfflineSyncProvider getLocalProvider() {
		return this._localProvider;
	}

	public URI getServiceUri() {
		return this._serviceUri;
	}
	public void setServiceUri(URI theUrl) {
		 this._serviceUri = theUrl;
	}

	public com.flicksoft.Synchronization.ClientServices.CacheControllerBehavior getControllerBehavior() {
		return this._controllerBehavior;
	}
}