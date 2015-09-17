package com.flicksoft.Synchronization;

import java.util.*;

import android.util.Log;

import com.flicksoft.Synchronization.ClientServices.*;
import com.flicksoft.Synchronization.ClientServices.ScopeMetaData.*;
import com.flicksoft.util.*;
	/**
	* The Android SQLite Sync Provider required by Sync Framework 2.1
	**/

public class SQLiteOfflineSyncProvider extends OfflineSyncProvider {
	private SQLiteStorageHandler _storageHandler = SQLiteStorageHandler.Instance(null);

	/**
	*   <summary>
	*   OfflineSyncProvider method called when the controller is about to start a sync session.
	*   < summary>
	**/
	public void BeginSession() {
	}

	/**
	*   <summary>
	*   OfflineSyncProvider method implementation to return a set of sync changes.
	*   < summary>
	*   <param name="state">A unique identifier for the changes that are uploaded< param>
	*   <returns>The set of incremental changes to send to the service< returns>
	**/
	public ChangeSet GetChangeSet(UUID state) {
		ChangeSet changeSet = new ChangeSet();
		Iterable<SQLiteOfflineEntity> changes = _storageHandler.GetChanges(state);
		
		ArrayList<IOfflineEntity> result = new ArrayList<IOfflineEntity>();
		for (SQLiteOfflineEntity theEntity:changes)
		{
			result.add(theEntity);
		}
		changeSet.Data =  result;
		changeSet.IsLastBatch = true;
		changeSet.ServerBlob = _storageHandler.GetAnchor();
		return changeSet ;
	}

	/**
	*   <summary>
	*   OfflineSyncProvider method implementation called when a change set returned from GetChangeSet has been
	*   successfully uploaded.
	*   < summary>
	*   <param name="state">The unique identifier passed in to the GetChangeSet call.< param>
	*   <param name="response">ChangeSetResponse that contains an updated server blob and any conflicts or errors that
	*   happened on the service.< param>
	 * @throws Exception 
	**/
	public void OnChangeSetUploaded(UUID state, ChangeSetResponse  response) {
		if ( null == response ) {
			//throw new Exception("response");
		}
		if ( null != response.Error ) {
			//throw new Exception("Exception during sync!");
		}
		try
		{
			_storageHandler.beginTransaction();
			
			if ( ( null != response.getUpdatedItems() && 0 != response.getUpdatedItems().size() ) ) {
				for (IOfflineEntity eachThing : response.getUpdatedItems()) {
					
					// TODO [ var offlineEntity = (SqlCeOfflineEntity) item ]
					_storageHandler.ApplyItem((SQLiteOfflineEntity)eachThing);
				}
			}
			if ( ( null != response.getConflicts() && 0 != response.getConflicts().size())) {
				for (Conflict theThing: response.getConflicts()) {
					
					//  We have an conflict so apply the LiveEntity
					SQLiteOfflineEntity liveEntity = (SQLiteOfflineEntity)theThing.LiveEntity;
					//  For a SyncError, which resulted from a client insert, the winning item may be a tombstone version
					//  of the client entity. In this case, the ServiceMetadata.Id property of the LiveEntity will be null.
					//  We need to lookup the item using primary keys in order to update it.
					if (  theThing.getClass() == SyncError .class ) {
						IOfflineEntity errorEntity = ((SyncError) theThing).ErrorEntity;
						if ( !liveEntity.getServiceMetadata().IsTombstone) {
							//  If the live entity is not a tombstone, then we just need to update the entity.
							_storageHandler.ApplyItem(liveEntity) ;
						}
						 else {
							//  At this point, the LiveEntity is a tombstone and does not have primary key info.
							//                               If the live entity is a tombstone, then delete the item by looking up the primary key	                        //        from the error entity.	                         //       The error entity in this case will have both Id and the primary keys.	                         errorEntity.ServiceMetadata.IsTombstone =  true;
							 errorEntity.ServiceMetadata.ID = null;
							 _storageHandler.ApplyItem((SQLiteOfflineEntity) errorEntity) ;
						}
					}
					 else {
						 _storageHandler.ApplyItem(liveEntity) ;
					}
				}
			}
			//  Clear all the isdirty flags and delete all rows with IsTombstone = true
			 _storageHandler.ResetDirtyAndDeleteTombstones() ;
			 _storageHandler.SaveAnchor(response.ServerBlob);
			 _storageHandler.setTransactionSuccessful();
		}
		catch (Exception ex)
		{
			
			Log.e("SyncLib", ex.getMessage());
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			Log.e("SyncLib", e.getMessage());
		}
		finally
		{
			_storageHandler.endTransaction();
		}

		
	}

	/**
	*   <summary>
	*   Returns the last server blob that was received during sync
	*   < summary>
	*   <returns>The server blob.< returns>
	**/
	public byte[] GetServerBlob() {
		return  _storageHandler.GetAnchor();
	}
	
	/**
	*   <summary>
	*   Returns the last server blob that was received during sync
	*   < summary>
	*   <returns>The server blob.< returns>
	**/
	public boolean needSchemaChange() {
		return ! (FileHelper.hasExternalStoragePrivateFile( SQLiteStorageHandler.DATABASE_NAME));

	}

	/**
	*   <summary>
	*   OfflineSyncProvider method called to save changes retrieved from the sync service.
	*   < summary>
	*   <param name="changeSet">The set of changes from the service to save. Also contains an updated server
	*   blob.< param>
	 * @throws Exception 
	**/
	public void SaveChangeSet(ChangeSet changeSet) {
		if (null == changeSet) {
			// throw new Exception("changeSet is null");
		}
		ArrayList<SQLiteOfflineEntity> entities = new ArrayList<SQLiteOfflineEntity>();
		for (IOfflineEntity theEntity : changeSet.Data) {
			entities.add((SQLiteOfflineEntity) theEntity);
		}

		try
		{
			_storageHandler.beginTransaction();
			_storageHandler.SaveDownloadedChanges(changeSet.ServerBlob, entities);
			_storageHandler.setTransactionSuccessful();
		}
		catch (Exception ex)
		{
			Log.e("SyncLib", ex.getMessage());
			
		}
		finally
		{
			_storageHandler.endTransaction();
		}
	}
	private static final Map<String, String> _oDataToSqlTypeMap;
    static
    {
    	_oDataToSqlTypeMap = new HashMap<String, String>();
    	_oDataToSqlTypeMap.put("Edm.Boolean","NUMERIC");
    	_oDataToSqlTypeMap.put("Edm.Byte","INTEGER");
		
    	_oDataToSqlTypeMap.put("Edm.String", "TEXT");
    	_oDataToSqlTypeMap.put("Edm.DateTime", "NUMERIC");
    	_oDataToSqlTypeMap.put("Edm.Decimal", "NUMERIC");
    	_oDataToSqlTypeMap.put("Edm.Double", "REAL");
    	_oDataToSqlTypeMap.put("Edm.Int16", "INTEGER");
    	_oDataToSqlTypeMap.put("Edm.Int32", "INTEGER");
    	_oDataToSqlTypeMap.put("Edm.Int64", "INTEGER");
    	_oDataToSqlTypeMap.put("Edm.SByte", "INTEGER");
    	_oDataToSqlTypeMap.put("Edm.Single", "REAL");
    	_oDataToSqlTypeMap.put("Edm.Binary", "BLOB");
    	_oDataToSqlTypeMap.put("Edm.Guid", "TEXT");
    	_oDataToSqlTypeMap.put("Edm.Time", "NUMERIC");
    	_oDataToSqlTypeMap.put("Edm.DateTimeOffset", "NUMERIC");
    }
	
	public void initDB(String dbFile, Schema schema,  ArrayList<java.lang.reflect.Type> types) {
		String schemaString = null;
		if (schema != null)
		{
			schemaString = "CREATE TABLE __sync (" 
				+ " Anchor  blob"
				+ ");";

			for (int i = 0; i < schema.EntityContainer.size(); i++)
			{
				EntityType curType = schema.EntityContainer.get(i);
				
	
				
				schemaString += "CREATE TABLE " + curType.Name + "(";
				for (int j = 0 ; j < curType.Properties.size(); j++)
				{
					
					Property curProp = curType.Properties.get(j); 
					schemaString += curProp.Name + " " + _oDataToSqlTypeMap.get(curProp.Type) + (curProp.Nullable ? " NULL" : " NOT NULL") + ",";
	

				}
				schemaString += "IsDirty NUMERIC,";
				schemaString += "Ischris NUMERIC,";
				schemaString += "IsTombstone NUMERIC,";
				schemaString += "_MetadataID TEXT,";
				schemaString += "PRIMARY KEY (";
				for (int k = 0; k < curType.Key.size(); k++)
				{
					if (k != curType.Key.size() - 1)
					{
						schemaString +=  curType.Key.get(k) + ",";
					}
					else
					{
						schemaString +=  curType.Key.get(k) + ")";
					}
				}
				schemaString += ");";
			}
		}

	//	Log.i("Schema Create", schemaString);
		SQLiteStorageHandler.Instance(null).init(dbFile, schemaString, types);
	}

	/**
	*   <summary>
	*   OfflineSyncProvider method called when sync is completed.
	*   < summary>
	**/
	public void EndSession() {
	}
}