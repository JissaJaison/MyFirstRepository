package com.flicksoft.Synchronization.ClientServices;

import java.util.*;

import com.flicksoft.Synchronization.ClientServices.ScopeMetaData.Schema;
	/**
	* abstract class  that android offline sync providers need to extend
	**/

public abstract class OfflineSyncProvider {

	public abstract void BeginSession();

	public abstract ChangeSet GetChangeSet(UUID state);

	public abstract void OnChangeSetUploaded(UUID state,ChangeSetResponse response);

	public abstract byte[] GetServerBlob();

	public abstract void SaveChangeSet(ChangeSet changeSet);
	
	public abstract void initDB(String dbFile, Schema  schema, ArrayList<java.lang.reflect.Type> types);
	public abstract boolean needSchemaChange();
	public abstract void EndSession();
}