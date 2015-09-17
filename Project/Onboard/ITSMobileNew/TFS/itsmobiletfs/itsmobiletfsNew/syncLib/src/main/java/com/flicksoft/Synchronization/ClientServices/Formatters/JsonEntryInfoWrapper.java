package com.flicksoft.Synchronization.ClientServices.Formatters;

import java.util.*;
import org.w3c.dom.*;
import java.net.*;

import com.flicksoft.util.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

	/**
	* Class converted from .NET code
	**/

class JsonEntryInfoWrapper extends EntryInfoWrapper {
	/**
	* // TODO [ entry/&gt ]
	* // TODO [ or a &lt ]
	* // TODO [ deleted-entry/&gt ]
	**/

	

	public JsonEntryInfoWrapper(JsonObject jo) {
		super(jo);
	}

	/**
	*   <summary>
	*   Looks for a sync:syncConflict or an sync:syncError element
	*   < summary>
	*   <param name="entry">entry element< param>
	 * @throws Exception 
	**/
	protected void LoadConflictEntry(JsonObject jo) throws Exception {
		 //look for conflict element
		JsonObject syncConflict = null;
		if(jo.has("__syncConflict")) {
			syncConflict = jo.get("__syncConflict").getAsJsonObject();
		}

		if(syncConflict != null) {
			this.IsConflict = true;
			if(syncConflict.has("conflictResolution")) {
				this.ConflictDesc = syncConflict.get("conflictResolution").getAsString();
			} else {
				throw new Exception("Conflict resolution not specified for Json object " + this.TypeName);
			}
			
			if(syncConflict.has("conflictingChange")) {
				this.ConflictJson = syncConflict.get("conflictingChange").getAsJsonObject();
				this.ConflictWrapper = new JsonEntryInfoWrapper(this.ConflictJson);
				//this.ConflictWrapper.TempId = this.ConflictJson.get("__metadata").getAsJsonObject().get("tempId").getAsString();
			} else {
				throw new Exception("conflictingChange not specified for Json syncConflict object " + this.TypeName);
			}
			return;
		}
		
		//look for error element
		JsonObject syncError = null;
		if(jo.has("__syncConflict")) {
			syncError = jo.get("__syncError").getAsJsonObject();
		}
		if(syncError != null) {
			 // Its not an conflict
            this.IsConflict = false;

            if(syncError.has("errorDescription")) {
            	this.ConflictDesc = syncError.get("errorDescription").getAsString();
            } 
            
            if(syncError.has("changeInError")) {
            	this.ConflictJson = syncError.get("changeInError").getAsJsonObject();
            } else {
            	throw new Exception("errorInChange not specified for Json syncError object " + this.TypeName);
            }
		}
	}

	/**
	*   <summary>
	*   Inspects all m.properties element in the entry element to load all properties.
	*   < summary>
	*   <param name="entry">Entry element< param>
	 * @throws Exception 
	**/
	protected void LoadEntryProperties(JsonObject jo) throws Exception {

	}
    
	/**
	*   <summary>
	*   Looks for the category element in the entry for the type name
	*   < summary>
	*   <param name="entry">Entry element< param>
	 * @throws Exception 
	**/
	protected void LoadTypeName(JsonObject jo) throws Exception {
		JsonObject metadata = null;
			metadata = jo.get("__metadata").getAsJsonObject();
		
		if(metadata.has("type")) {
			if(metadata.has("uri")) {
				this.Id = metadata.get("uri").getAsString();
				//this.EditUri = new URI(this.Id);
			}
			
			if(metadata.has("tempId")) {
				this.TempId = metadata.get("tempId").getAsString();
			}
			
			if(!metadata.has("uri") && !metadata.has("tempId")) {
				throw new Exception("A uri or a tempId key must be present in the __metadata object. Entity in error: " + metadata);
			}
			
			if(metadata.has("etag")) {
				this.ETag = metadata.get("etag").getAsString();
			}
			
//			if(metadata.has("edituri")) {
//				this.EditUri = new URI(metadata.get("edituri").getAsString());
//			}
			
			if(metadata.has("isDeleted")) {
				this.IsTombstone = metadata.get("isDeleted").getAsBoolean();
			}
		} else {
			throw new Exception("Json object does not have a _metadata tag containing the type information.");
		}
	}

	@Override
	protected void LoadConflictEntry(XmlReader reader) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void LoadEntryProperties(XmlReader reader) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void LoadTypeName(XmlReader reader) throws Exception {
		// TODO Auto-generated method stub
		
	}
}