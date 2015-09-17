package com.flicksoft.Synchronization.ClientServices.Formatters;

import java.util.*;
import java.net.*;
import java.io.*;

import javax.xml.*;
import javax.xml.parsers.*;

import com.flicksoft.Synchronization.ClientServices.*;
	/**
	*   <summary>
	*   Abstract class for SyncWriter that individual format writers needs to extend
	*   < summary>
	* 
	**/

public abstract class SyncWriter {
	URI _baseUri;
	public ByteArrayOutputStream outputS;

	public SyncWriter(URI serviceUri) {
		if ( serviceUri == null ) {
			throw new IllegalArgumentException("serviceUri");
		}
		this._baseUri = serviceUri;
	}

	public void StartFeed(boolean isLastBatch, byte[] serverBlob, 
			ByteArrayOutputStream bufferStream) {
	}

	/**
	*   <summary>
	*   Called to add a particular Entity
	*   < summary>
	*   <param name="entry">Entity to add to serialize to the stream< param>
	*   <param name="tempId">TempId for the Entity< param>
	**/
	public void AddItem(IOfflineEntity entry, String tempId) {
		this.AddItem(entry, tempId, false);
	}

	/**
	*   <summary>
	*   Called to add a particular Entity
	*   < summary>
	*   <param name="entry">Entity to add to serialize to the stream< param>
	*   <param name="tempId">TempId for the Entity< param>
	*   <param name="emitMetadataOnly">Bool flag that denotes whether a partial metadata only entity is to be written< param>
	**/
	public void AddItem(IOfflineEntity entry, String tempId, boolean emitMetadataOnly) {
		if ( entry == null ) {
			throw new IllegalArgumentException("entry");
		}
		if ((entry.ServiceMetadata.ID == null) && entry.ServiceMetadata.IsTombstone) {
			//  Skip sending tombstones that dont have an Id as these were local create + delete.
			return;
		}
		 WriteItemInternal(entry, tempId, null /*conflicting*/, null/*conflictingTempId*/, null /*desc*/, false /*isconflict*/, emitMetadataOnly) ;
	}

	public abstract void WriteItemInternal(IOfflineEntity live, String liveTempId, IOfflineEntity conflicting, String conflictingTempId, String desc, boolean isConflict, boolean emitMetadataOnly);

	public abstract void WriteFeed(OutputStream outputStream) throws Exception ;

	protected URI getBaseUri() {
		return this._baseUri;
	}
}