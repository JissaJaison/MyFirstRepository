package com.flicksoft.Synchronization.ClientServices.Formatters;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;


import android.util.Log;

import com.flicksoft.Synchronization.ClientServices.Conflict;
import com.flicksoft.Synchronization.ClientServices.IOfflineEntity;
import com.flicksoft.Synchronization.ClientServices.SyncConflict;
import com.flicksoft.Synchronization.ClientServices.SyncConflictResolution;
import com.flicksoft.Synchronization.ClientServices.SyncError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
	/**
	*   <summary>
	*   SyncReader implementation for the OData Atompub format
	*   < summary>
	* 
	**/

public class ODataJsonReader extends SyncReader {

	private JsonReader _readerJson;
	private JsonReader newJReader;
	private Gson _gson;
	private JsonObject d;
	private String className;
	
	public ODataJsonReader(InputStream stream) {
		super(stream, null);
		
	}

	public ODataJsonReader(InputStream stream, ArrayList<java.lang.reflect.Type> knownTypes) {
		super(stream, knownTypes);
		
		try
		{
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(byte[].class, new ByteArrayDeserializer()).registerTypeAdapter(Date.class, new DateDeserializer());
            _gson = gsonBuilder.create();
			_readerJson = new JsonReader(new InputStreamReader(stream));
			this._currentType = ReaderItemType.BOF;
			this._currentNodeRead = false;
		}
		catch (Exception ex)
		{
		
		}

	}
	public class DateDeserializer implements JsonDeserializer<Date> 
    {

		private Date gmtToLocal(long miliseconds)
		{
			Date result = null;
			 Calendar c = Calendar.getInstance();
			 int zoneOffset = c.get(java.util.Calendar.ZONE_OFFSET);
	        int dstOffset = c.get(java.util.Calendar.DST_OFFSET);
	        
	        result = new Date(miliseconds - zoneOffset - dstOffset);
	        
	        return result;
		}
        public Date deserialize(JsonElement json, Type typfOfT, JsonDeserializationContext context)
        {
            try
            {
                String dateStr = json.getAsString();
                if (dateStr != null) dateStr = dateStr.replace("/Date(", "");
                
                if (dateStr != null) dateStr = dateStr.replace(")/", "");
                if (dateStr != null) dateStr = dateStr.replace("+0000)/", "");
                long time = Long.parseLong(dateStr);
                return gmtToLocal(time);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                return null;
            }

        }
    }
	
    private static class ByteArrayDeserializer implements JsonDeserializer<byte[]> {
        public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException 
    {
                String jsonString = json.getAsString();
                byte[] byteArr = com.flicksoft.util.Base64Coder.decodeLines(jsonString);
                return byteArr;
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
			String typeName = "";
			 
			 CheckItemType(ReaderItemType.Entry);
			//  Get the type name and the list of properties.
			
			JsonParser jp = new JsonParser();
			
			JsonObject entity = jp.parse(_readerJson).getAsJsonObject();
			Log.i("ODataJsonReader", entity.toString());
			 _currentEntryWrapper = new JsonEntryInfoWrapper(entity);
			typeName = entity.get("__metadata").getAsJsonObject().get("type").getAsString();
			_liveEntity = (IOfflineEntity) _gson.fromJson(entity, Class.forName(typeName));
			this.setMetadata();
			return  _liveEntity;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("SyncLib", e.getMessage());
			return null;
		}

	}
	
	private void setMetadata() {
		this._liveEntity.ServiceMetadata.ID = this._currentEntryWrapper.Id;
		this._liveEntity.ServiceMetadata.EditUri = this._currentEntryWrapper.EditUri;
		this._liveEntity.ServiceMetadata.Etag = this._currentEntryWrapper.ETag;
		this._liveEntity.ServiceMetadata.IsTombstone = this._currentEntryWrapper.IsTombstone;
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
		return _readerJson.nextBoolean();
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
		String encodedBlob =  _readerJson.nextString();
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
		String name = "";
		try {
			if(this._currentType == ReaderItemType.BOF) {
				//do "d" to "moreChangesAvailable" part
				_readerJson.beginObject();
				
				while (_readerJson.hasNext()) {
					name = _readerJson.nextName();
					if (name.equals("d")) {
						_readerJson.beginObject();
						while (_readerJson.hasNext()) {
							name = _readerJson.nextName();
							if (name.equals("__sync")) {
								_readerJson.beginObject();
								while (_readerJson.hasNext()) {
									name = _readerJson.nextName();
									if (name.equals("moreChangesAvailable")) {
										_currentType = ReaderItemType.HasMoreChanges;
										_currentNodeRead = false;
										return true;	
									}
								}
							}
						}
					}
				}
			} else if (this._currentType == ReaderItemType.HasMoreChanges) {
				//do serverblob part
				name = _readerJson.nextName();
				_currentType = ReaderItemType.SyncBlob;
				_currentNodeRead = false;
				return true;
			} else if (this._currentType == ReaderItemType.SyncBlob) {
				//do results part
				_readerJson.endObject();
				name = _readerJson.nextName();
				_readerJson.beginArray();
				if(_readerJson.hasNext()) {
					_currentType = ReaderItemType.Entry;
				} else {
					_readerJson.endArray();
					_readerJson.endObject();
					this._currentType = ReaderItemType.EOF;
				}
				_currentNodeRead = false;
				return true;
			} else if (this._currentType == ReaderItemType.Entry) {
				while(_readerJson.hasNext()){
					_currentType = ReaderItemType.Entry;
					_currentNodeRead = false;
					return true;
  			  	}
				_readerJson.endArray();
				_readerJson.endObject();

			}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		this._currentType = ReaderItemType.EOF;
		return false;
	}
	
	@Override
	public boolean HasConflict() {
		if ( _currentEntryWrapper != null ) {
			return this._currentEntryWrapper.ConflictJson != null;
		}
		return false;
	}
	
	@Override
	public boolean HasConflictTempId() {
		if ( ( _currentEntryWrapper != null && _currentEntryWrapper.ConflictJson != null ) ) {
			return this._currentEntryWrapper.ConflictJson.get("__metadata").getAsJsonObject().has("tempId");
		}
		return false;
	}

	
	@Override
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

    			JsonObject entity = this._currentEntryWrapper.ConflictJson;
    			String typeName = entity.get("__metadata").getAsJsonObject().get("type").getAsString();
    			conflict.LosingEntity = (IOfflineEntity) _gson.fromJson(entity, Class.forName(typeName));
            	
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
    			JsonObject entity = this._currentEntryWrapper.ConflictJson;
    			String typeName = entity.get("__metadata").getAsJsonObject().get("type").getAsString();
    			conflict.ErrorEntity = (IOfflineEntity) _gson.fromJson(entity, Class.forName(typeName));
				result = conflict;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
		}
		
		
		return result;
	}
	

	@Override
	public void Start() throws Exception {
		// TODO Auto-generated method stub
		
	}



	

	
}