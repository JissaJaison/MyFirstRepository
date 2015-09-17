package com.flicksoft.Synchronization.ClientServices;

import java.util.*;
import java.net.*;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
	/**
	*   <summary>
	
	*   Class that represents the metadata required for the sync protocol to work correctly.
	*   Applications should not change these properties except when required by the protocol
	*   (the Id will change for an item that is inserted for the first time).
	*   The exception to this is the IsTombstone property, which should be set when the application
	*   is using a custom store and an item is being deleted.  Applications using the
	*   IsolatedStorageOfflineContext should never set any properties.
	*   < summary>
	* 
	**/

public class OfflineEntityMetadata {
	@SerializedName("isDeleted")
	public boolean IsTombstone;
	@SerializedName("uri")
	public String ID;
	@SerializedName("etag")
	public String Etag;
	@SerializedName("edituri")
	public URI EditUri;
	public boolean IsDirty;
	@SerializedName("tempId")
	public String tempId;
	@SerializedName("type")
	public String type;

	public OfflineEntityMetadata() {
		IsTombstone = false;
		ID = null;
		Etag = null;
		EditUri = null;
	}

	public OfflineEntityMetadata(boolean isTombstone, String id, String etag, URI editUri) {
		this.IsTombstone = isTombstone;
		this.ID = id;
		this.Etag = etag;
		this.EditUri = editUri;
	}

	/**
	*   <summary>
	*   Used while creating Snapshot to do a depp copy of the original copy's metadata
	*   < summary>
	*   <returns>< returns>
	**/
	com.flicksoft.Synchronization.ClientServices.OfflineEntityMetadata Clone() {
		OfflineEntityMetadata metaData = new OfflineEntityMetadata(IsTombstone,ID,Etag,EditUri);
		return metaData;
	}

//	public boolean getIsTombstone() {
//		return _isTombstone;
//	}
//
//	public void setIsTombstone(boolean value) {
//		if ( value != _isTombstone ) {
//			_isTombstone = value;
//		}
//	}
//
//	public String getId() {
//		return _id;
//	}
//
//	public void setId(String value) {
//		if ( value != _id ) {
//			_id = value;
//		}
//	}
//
//	public String getETag() {
//		return _etag;
//	}
//
//	public void setETag(String value) {
//		if ( value != _etag ) {
//			_etag = value;
//		}
//	}
//
//	public URI getEditUri() {
//		return _editUri;
//	}
//
//	public void setEditUri(URI value) {
//		if ( value != _editUri ) {
//			_editUri = value;
//		}
//	}
}