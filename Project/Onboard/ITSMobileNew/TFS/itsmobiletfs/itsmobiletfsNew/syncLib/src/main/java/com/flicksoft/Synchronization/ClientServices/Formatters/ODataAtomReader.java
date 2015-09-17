package com.flicksoft.Synchronization.ClientServices.Formatters;
import com.flicksoft.Synchronization.ClientServices.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.xml.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import com.flicksoft.util.*;
	/**
	*   <summary>
	*   SyncReader implementation for the OData Atompub format
	*   < summary>
	* 
	**/

public class ODataAtomReader extends SyncReader {

	public ODataAtomReader(InputStream stream) {
		super(stream, null);
		
	}

	public ODataAtomReader(InputStream stream, ArrayList<java.lang.reflect.Type> knownTypes) {
		super(stream, knownTypes);
		
		try
		{
			_reader = XmlReader.Create(stream);;

			Start();
			this._currentType = ReaderItemType.BOF;
			this._currentNodeRead = false;
		}
		catch (Exception ex)
		{
		
		}

	}

	/**
	*   <summary>
	*   Validates that the stream contains a valid feed item.
	*   < summary>
	 * @throws Exception 
	 * @throws Exception 
	**/
	public void Start() throws Exception {
		try {
			_reader.MoveToContent();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if ( !(AtomHelper.IsAtomElement(_reader, FormatterConstants.AtomPubFeedElementName)) ) {
			throw new Exception("Not a valid ATOM feed.") ;
		}
	}

	/**
	*   <summary>
	*   Returns the current entry element casted as an IOfflineEntity element
	*   < summary>
	*   <returns>Typed entry element< returns>
	**/
	public IOfflineEntity getItem() {
		 try {
			CheckItemType(ReaderItemType.Entry);
			//  Get the type name and the list of properties.
			 _currentEntryWrapper = new AtomEntryInfoWrapper(_reader);
			_liveEntity = ReflectionUtility.GetObjectForType(_currentEntryWrapper, this._knownTypes);
			return  _liveEntity;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	/**
	*   <summary>
	*   Returns the value of the sync:hasMoreChanges element
	*   < summary>
	*   <returns>bool< returns>
	 * @throws Exception 
	**/
	public boolean GetHasMoreChangesValue() throws Exception {
		CheckItemType(ReaderItemType.HasMoreChanges);
		return   _reader.ReadElementContentAsBoolean() ;
	}

	/**
	*   <summary>
	*   Returns the sync:serverBlob element contents
	*   < summary>
	*   <returns>byte[]< returns>
	 * @throws Exception 
	**/
	public byte[] getServerBlob() throws Exception {
		CheckItemType(ReaderItemType.SyncBlob);
		String encodedBlob =  _reader.ReadElementContentAsString();
		return com.flicksoft.util.Base64Coder.decode(encodedBlob);
	}
	public ReaderItemType getItemType() {
		return _currentType ;
	}
	/**
	*   <summary>
	*   Traverses through the feed and returns when it arrives at the necessary element.
	*   < summary>
	*   <returns>bool detecting whether or not there is more elements to be read.< returns>
	**/
	public boolean Next() {
		//  safeguard the end of the reading
		if  ( _reader.IsEndDocument()) {
			
			 return false;
		}
		do {
			_currentEntryWrapper = null;
			 _liveEntity = null;
			if ( AtomHelper.IsAtomElement(_reader, FormatterConstants.AtomPubEntryElementName) ||
                    AtomHelper.IsAtomTombstone(_reader, FormatterConstants.AtomDeletedEntryElementName) ) {
				 _currentType = ReaderItemType.Entry;
				 _currentNodeRead = false;
				return true;
			} else if (AtomHelper.IsSyncElement(_reader, FormatterConstants.ServerBlobText)) {
				 _currentType = ReaderItemType.SyncBlob;
				 _currentNodeRead = false;
				return true;
			} else if ( AtomHelper.IsSyncElement(_reader, FormatterConstants.MoreChangesAvailableText) ) {
				_currentType = ReaderItemType.HasMoreChanges;
				_currentNodeRead = false;
				return true;
			}
		} while ( (_reader.Read() ));
		this._currentType = ReaderItemType.EOF;
		return false;
	}



	

	
}