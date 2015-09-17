package com.flicksoft.Synchronization.ClientServices.Formatters;

import java.util.*;
import java.io.*;
import javax.xml.parsers.*;

import com.flicksoft.Synchronization.ClientServices.*;
import com.flicksoft.util.*;
	/**
	*   <summary>
	*   Abstract class for SyncReader that individual format readers needs to extend
	*   < summary>
	* 
	**/

public abstract class SyncReader  {
	/**
	* // TODO [ public abstract ReaderItemType ItemType { get; } ]
	**/

	protected XmlReader _reader;
	protected InputStream _inputStream;
	protected ArrayList<java.lang.reflect.Type> _knownTypes;
	protected EntryInfoWrapper _currentEntryWrapper;
	protected ReaderItemType _currentType;
	protected boolean _currentNodeRead = false;
	protected IOfflineEntity _liveEntity;

	public SyncReader(InputStream stream, ArrayList<java.lang.reflect.Type> knownTypes) {
		if ( stream == null ) {
			throw new IllegalArgumentException("stream") ;
		}
		this._inputStream = stream;
		this._knownTypes = knownTypes;
	}

	public abstract void Start()  throws Exception;

	public abstract ReaderItemType getItemType();
	public abstract IOfflineEntity getItem();

	public abstract byte[] getServerBlob() throws Exception;

	public abstract boolean GetHasMoreChangesValue()throws Exception;

	public abstract boolean Next();

	/**
	*   <summary>
	*   Check to see if the current object that was just parsed had a conflict element on it or not.
	*   < summary>
	*   <returns>bool< returns>
	**/
	public boolean HasConflict() {
		if ( _currentEntryWrapper != null ) {
			return this._currentEntryWrapper.ConflictWrapper != null;
		}
		return false;
	}

	/**
	*   <summary>
	*   Check to see if the current conflict object that was just parsed has a tempId element on it or not.
	*   < summary>
	*   <returns>bool< returns>
	**/
	public boolean HasConflictTempId() {
		if ( ( _currentEntryWrapper != null && _currentEntryWrapper.ConflictWrapper != null ) ) {
			return this._currentEntryWrapper.ConflictWrapper.TempId != null;
		}
		return false;
	}

	/**
	*   <summary>
	*   Check to see if the current object that was just parsed has a tempId element on it or not.
	*   < summary>
	*   <returns>bool< returns>
	**/
	public boolean HasTempId() {
		if ( _currentEntryWrapper != null ) {
			return this._currentEntryWrapper.TempId != null;
		}
		return false;
	}

	/**
	*   <summary>
	*   Returns the TempId parsed from the current object if present
	*   < summary>
	*   <returns>string< returns>
	**/
	public String GetTempId() {
		if ( !(HasTempId()) ) {
			return null;
		}
		return this._currentEntryWrapper.TempId;
	}

	/**
	*   <summary>
	*   Returns the TempId parsed from the current conflict object if present
	*   < summary>
	*   <returns>string< returns>
	**/
	public String GetConflictTempId() {
		if ( !(HasConflictTempId()) ) {
			return null;
		}
		return this._currentEntryWrapper.ConflictWrapper.TempId;
	}

	/**
	*   <summary>
	*   Get the conflict item
	*   < summary>
	*   <returns>Conflict item< returns>
	**/
	public Conflict GetConflict() {
		Conflict result= null;
		if ( !(HasConflict()) ) {
			return null;
		}
		
		if ( _currentEntryWrapper.IsConflict ) {
			SyncConflict conflict = new SyncConflict();
			conflict.LiveEntity = _liveEntity;
            try {
				this._knownTypes.add(IOfflineEntity.class);
            	conflict.LosingEntity = ReflectionUtility.GetObjectForType(_currentEntryWrapper.ConflictWrapper, this._knownTypes);
				
				conflict.Resolution = SyncConflictResolution.valueOf( _currentEntryWrapper.ConflictDesc);
	            result= conflict;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
		}
		 else {
			SyncError conflict = new SyncError();
			conflict.LiveEntity = _liveEntity;
            try {
				conflict.ErrorEntity = ReflectionUtility.GetObjectForType(_currentEntryWrapper.ConflictWrapper, this._knownTypes );
				result = conflict;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
		}
		
		
		return result;
	}

	protected void CheckItemType(ReaderItemType type) throws Exception {
		if ( _currentType != type ) {
			throw new Exception(String.format("{0} is not a valid {1} element.", _reader.toString(), type));
		}
		_currentNodeRead = true;
	}

	

//	public void Dispose() {
//		if ( this._inputStream != null ) {
//			// a 'using' block: start block 
//			this._inputStream.close();
//			// a 'using' block: end block 
//		}
//		this._inputStream = null;
//		null[] this._knownTypes = null;
//	}
}