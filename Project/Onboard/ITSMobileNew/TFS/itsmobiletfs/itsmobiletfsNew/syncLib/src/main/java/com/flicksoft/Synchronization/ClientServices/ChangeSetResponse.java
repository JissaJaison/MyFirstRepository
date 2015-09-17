package com.flicksoft.Synchronization.ClientServices;

import java.util.*;

	/**
	*   <summary>
	*   Denotes a response for the a ChangeSet that was uploaded.
	*   < summary>
	* 
	**/

public class ChangeSetResponse {
	 ArrayList<Conflict>  _conflicts;
	 ArrayList<IOfflineEntity>  _updatedItems;
	 public byte[] ServerBlob;
	public ChangeSetResponse() {
		_conflicts =new ArrayList<Conflict>(3);
		_updatedItems = new ArrayList<IOfflineEntity>();
	}

	void AddConflict(Conflict conflict) {
		this._conflicts.add(conflict);
	}

	void AddUpdatedItem(IOfflineEntity item) {
		this._updatedItems.add(item);
	}

	public Exception Error;


	public ArrayList<Conflict> getConflicts() {
		return new ArrayList<Conflict>(_conflicts) ;
	}

	public ArrayList<IOfflineEntity>  getUpdatedItems() {
		return new ArrayList<IOfflineEntity>(_updatedItems) ;
	}

	public ArrayList<Conflict> getConflictsInternal() {
		return this._conflicts;
	}
}