package com.flicksoft.Synchronization.ClientServices;

import java.util.*;
import com.flicksoft.Synchronization.*;
	/**
	*   <summary>
	*   Denotes a list of changes that is either to be uploaded or downloaded.
	*   < summary>
	* 
	**/

public class ChangeSet {

	public byte[] ServerBlob;
	public boolean IsLastBatch;
	public ArrayList<IOfflineEntity> Data;
	public ChangeSet() {
		this.ServerBlob = null;
		this.Data = new ArrayList<IOfflineEntity>();
		IsLastBatch = true;
	}

	void AddItem(IOfflineEntity iOfflineEntity) {
		if ( Data == null ) {
			Data = new ArrayList<IOfflineEntity>();
		}
		Data.add(iOfflineEntity);
	}

	
}