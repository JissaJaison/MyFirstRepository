package com.flicksoft.Synchronization.ClientServices.Formatters;

import java.util.*;
import java.net.*;

import org.w3c.dom.*;
import java.util.HashMap;

import android.util.Log;

import com.flicksoft.util.*;
import com.google.gson.JsonObject;
	/**
	*   <summary>
	*   Internal helper class that reads and parses all relevant information about an entry element.
	*   < summary>
	* 
	**/

abstract class EntryInfoWrapper {
	public String TypeName;
	public HashMap<String, String> PropertyBag = new HashMap<String, String>();
	public boolean IsTombstone;
	public String ConflictDesc;
	public com.flicksoft.Synchronization.ClientServices.Formatters.EntryInfoWrapper ConflictWrapper;
	public JsonObject ConflictJson;
	public boolean IsConflict;
	public String ETag;
	public String TempId;
	public URI EditUri;
	public String Id;

	protected abstract void LoadConflictEntry(XmlReader reader) throws Exception;

	protected abstract void LoadEntryProperties(XmlReader reader) throws Exception;

	protected abstract void LoadTypeName(XmlReader reader) throws Exception;
	
	protected void LoadConflictEntry(JsonObject jo) throws Exception {}

	protected void LoadEntryProperties(JsonObject jo) throws Exception {}

	protected void LoadTypeName(JsonObject jo) throws Exception {}
	
	public EntryInfoWrapper(JsonObject jo) {
		if ( jo == null ) {
			throw  new IllegalArgumentException("JsonObject");
		}
        
        try {
        	LoadTypeName(jo);
        	LoadEntryProperties(jo);
			LoadConflictEntry(jo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("SyncLib", e.getMessage());
			//e.printStackTrace();
		}
        
	}

	public EntryInfoWrapper(XmlReader reader) {
		if ( reader == null ) {
			throw  new IllegalArgumentException("reader");
		}
		 PropertyBag = new HashMap<String, String>();
		//try {
		//	LoadTypeName(reader);
		//} catch (Exception e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		try {
			LoadEntryProperties(reader);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		try {
//			LoadConflictEntry(reader);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}