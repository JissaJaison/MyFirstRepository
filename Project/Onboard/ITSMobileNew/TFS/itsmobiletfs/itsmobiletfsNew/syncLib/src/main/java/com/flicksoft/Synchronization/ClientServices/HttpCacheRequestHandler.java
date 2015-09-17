package com.flicksoft.Synchronization.ClientServices;

import java.util.*;
import java.lang.reflect.Type;
import java.net.*;
import java.io.*;

import org.xmlpull.v1.*;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import com.flicksoft.Synchronization.ClientServices.Formatters.*;
import com.flicksoft.Synchronization.ClientServices.ScopeMetaData.*;
import com.flicksoft.util.*;

	/**
	*   <summary>
	*   A Http transport implementation for processing a CachedRequest.
	*   < summary>
	* 
	**/

class HttpCacheRequestHandler extends CacheRequestHandler {

	public HashMap<String, IOfflineEntity> TempIdToEntityMapping;
	 private void CheckEntityServiceMetadataAndTempIds(HashMap<String, IOfflineEntity> tempIdToEntityMapping, 
            IOfflineEntity entity, String tempId, ChangeSetResponse response) throws CacheControllerException
        {
            // Check service ID 
            if (entity.ServiceMetadata.ID ==  null || entity.ServiceMetadata.ID == "")
            {
                throw new CacheControllerException(String.format("Service did not return a permanent Id for tempId '{0}'", tempId));
            }

            // If an entity has a temp id then it should not be a tombstone                
            if (entity.ServiceMetadata.IsTombstone)
            {
                throw new CacheControllerException(String.format("Service returned a tempId '{0}' in tombstoned entity.", tempId));
            }

            // Check that the tempId was sent by client
            if (!tempIdToEntityMapping.containsKey(tempId))
            {
                throw new CacheControllerException("Service returned a response for a tempId which was not uploaded by the client. TempId: " + tempId);
            }

            // Add the entity to the Updated list.
            response.AddUpdatedItem(entity);

            // Once received, remove the tempId from the mapping list.
            tempIdToEntityMapping.remove(tempId);
        } 


	ICredentials _credentials;
	SyncReader _syncReader;
	SyncWriter _syncWriter;
	Action<HttpURLConnection> _beforeRequestHandler;
	Action<HttpURLConnection> _afterResponseHandler;
	ArrayList<java.lang.reflect.Type> _knownTypes;

	public HttpCacheRequestHandler(URI serviceUri, CacheControllerBehavior behaviors) {
		super(serviceUri, behaviors.getScopeName());
		this._credentials = behaviors.getCredentials();
		this._beforeRequestHandler = behaviors.getBeforeSendingRequest();
		this._afterResponseHandler = behaviors.getAfterReceivingResponse();
		 this._knownTypes = new ArrayList<java.lang.reflect.Type>();
		 this._knownTypes = (ArrayList<Type>) behaviors.getKnownTypes().clone();
		/* TODO [ behaviors.KnownTypes.CopyTo(this._knownTypes, 0) ] */;
		
	}

	/**
	*   <summary>
	*   Called by the CacheController when it wants this CacheRequest to be processed.
	*   < summary>
	*   <param name="request">CacheRequest to be processed< param>
	*   <param name="state">User state object< param>
	 * @throws Exception 
	**/
	public Object ProcessCacheRequest(CacheRequest request) throws Exception {
		
		
		URL requestUri = composeUrl(request);
		
//cdaly                                                                     ITSMobileSyncService
		if (requestUri != null && (requestUri.getPath().toUpperCase().contains("ITSMOBILESYNCSERVICE.SVC")))
		{
			try {
				HttpURLConnection theConnection = (HttpURLConnection) requestUri.openConnection();
				Log.i("****Connection****", "Setting Connection Read Timeout to 300 000");
				theConnection.setReadTimeout(1000 * 60*5);
				Log.i("****Connection****", "Setting Connection Timeout to 300 000");
				theConnection.setConnectTimeout(1000 * 60*5);
				Log.i("****Connection****",  "ReadTimeout property says it is set to  " + theConnection.getReadTimeout());
				Log.i("****Connection****",  "ConnectionTimeout property says it is set to  " + theConnection.getConnectTimeout());
				if ( this._credentials != null ) {
					//theConnection..Credentials = this._credentials;
				}
				//  Set the method type
				
				
				theConnection.setDoInput(true);
				if (request.Format == SyncSerializationFormat.ODataAtom)
				{
					theConnection.setRequestProperty("Accept", "application/atom+xml");
					theConnection.setRequestProperty("Content-Type", "application/atom+xml");
				}
				else if (request.Format == SyncSerializationFormat.ODataJson)
				{
					theConnection.setRequestProperty("Accept", "application/json");
					theConnection.setRequestProperty("Content-Type", "application/json");
				}
				
				try {
					switch (request.RequestType)
					{
					    case UploadChanges:
					    	theConnection.setRequestMethod("POST");
					    	theConnection.setDoOutput(true);
					        return ProcessUploadRequest(theConnection, request);
					    case DownloadChanges:
					    	theConnection.setRequestMethod("POST");
					    	theConnection.setDoOutput(true);
					    	return ProcessDownloadRequest(theConnection, request);
					    case Metadata:
					    	theConnection.setRequestMethod("GET");
					    	theConnection.setDoOutput(false);
					    	return ProcessScopeMetadata(theConnection, request);
					    default:
					        return ProcessDownloadRequest(theConnection, request);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("****Connection****", "Exception1:" + e.getMessage());
					throw (e);
				}
				finally{
					if (theConnection != null)
					{
						theConnection.disconnect();
					}
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.i("****Connection****", "Exception2:" + e.getMessage());
				throw (e);
			}
		}
		return null;
		
		
		
	}

	private URL composeUrl(CacheRequest request) {
		
		String requestUri = new String();
		requestUri = String.format("%s%s%s/%s",
                    super.getBaseUri(),
                    (super.getBaseUri().toString().endsWith("/")) ? "" : "/",
                    		URLEncoder.encode(super.getScopeName()),
                    		(request.RequestType == CacheRequestType.Metadata ? "$" : "") +  request.RequestType.toString());
		if (request.RequestType == CacheRequestType.UploadChanges || request.RequestType == CacheRequestType.DownloadChanges)
		{
			String prefix = "?";
			
			Iterator<?> it = scopeParameters.keySet().iterator();
			while(it.hasNext()) {	
				String key = (String)it.next();
				String val = scopeParameters.get(key);	
				
				requestUri = requestUri + String.format("%s%s=%s", prefix, URLEncoder.encode(key), URLEncoder.encode(val));
				if (prefix == "?")
	            {
	                prefix = "&";
	            }
			}
		}
		try {
			return new URL(requestUri);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	private Object ProcessUploadRequest(HttpURLConnection webRequest, CacheRequest request) throws IOException, Exception {
		// a 'using' block: start block 
		//  Create a SyncWriter to write the contents
		Log.i("SyncLib", "ProcessUploadRequest started");
		ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
		if (request.Format == SyncSerializationFormat.ODataAtom) {
			this._syncWriter = new ODataAtomWriter(super.getBaseUri());

		} else if (request.Format == SyncSerializationFormat.ODataJson) {
			this._syncWriter = new ODataJsonWriter(super.getBaseUri());
		}
		this._syncWriter.StartFeed(true, request.KnowledgeBlob, bufferStream);
		for (IOfflineEntity entity: request.Changes) {
			
			//  Skip tombstones that dont have a ID element.
			if (entity.ServiceMetadata.IsTombstone && (entity.ServiceMetadata.ID == null || entity.ServiceMetadata.ID.equals(""))) {
				continue;
			}
			String tempId = null;
			//  Check to see if this is an insert. i.e ServiceMetadata.ID is null or empty
			if ( entity.ServiceMetadata.ID == null || entity.ServiceMetadata.ID.equals("")) {
				if ( TempIdToEntityMapping == null ) {
					 TempIdToEntityMapping = new  HashMap<String, IOfflineEntity>();
				}
				tempId = UUID.randomUUID().toString();
				TempIdToEntityMapping.put(tempId, entity);
			}
			this._syncWriter.AddItem(entity, tempId);
		}

		if (request.Format == SyncSerializationFormat.ODataAtom) {
			this._syncWriter.WriteFeed(webRequest.getOutputStream());
		} else if (request.Format == SyncSerializationFormat.ODataJson) {
			webRequest.getOutputStream().write(
					this._syncWriter.outputS.toByteArray());
		}

		webRequest.getOutputStream().flush();
		
		webRequest.getOutputStream().close();

		//  Fire the Before request handler
		//this.FirePreRequestHandler(webRequest)  ;
		//  Get the response
		
		// TODO [ HttpWebResponse webResponse = (HttpWebResponse)webRequest.GetResponse() ]
		if (webRequest.getResponseCode() ==  HttpURLConnection.HTTP_OK ) {
			ChangeSetResponse changeSetResponse = new ChangeSetResponse();
			// a 'using' block: start block
			
			InputStream reponseStream = webRequest.getInputStream(); 
			//  Create the SyncReader
			if (request.Format == SyncSerializationFormat.ODataAtom) {
				this._syncReader = new ODataAtomReader(reponseStream, this._knownTypes);

			} else if (request.Format == SyncSerializationFormat.ODataJson) {
				this._syncReader = new ODataJsonReader(reponseStream, this._knownTypes);
			}
			//  Read the response
			while (this._syncReader.Next()) {
				switch (this._syncReader.getItemType())
				{
				case Entry:
					
						IOfflineEntity entity = this._syncReader.getItem();
						IOfflineEntity ackedEntity = entity;
						String tempId = null;
						//  If conflict only one temp ID should be set
						if ( ( this._syncReader.HasTempId() && this._syncReader.HasConflictTempId() ) ) {
							throw new Exception(String.format("Service returned a TempId '{0}' in both live and conflicting entities.", this._syncReader.GetTempId()));
						}
						//  Validate the live temp ID if any, before adding anything to the offline context
						if ( this._syncReader.HasTempId() ) {
							tempId = this._syncReader.GetTempId();
							 CheckEntityServiceMetadataAndTempIds(TempIdToEntityMapping, entity, tempId, changeSetResponse);
						}
						//   If conflict
						if ( this._syncReader.HasConflict() ) {
							Conflict conflict = this._syncReader.GetConflict();
							IOfflineEntity conflictEntity = (IOfflineEntity) ((conflict instanceof SyncConflict) ? ((SyncConflict)conflict).LosingEntity : ((SyncError) conflict).ErrorEntity);
							//  Validate conflict temp ID if any
							if ( this._syncReader.HasConflictTempId() ) {
								tempId = this._syncReader.GetConflictTempId();
								CheckEntityServiceMetadataAndTempIds(TempIdToEntityMapping, conflictEntity, tempId, changeSetResponse);
							}
							//  Add conflict
							changeSetResponse.AddConflict(conflict);
							// 
							//  If there is a conflict and the tempId is set in the conflict entity then the client version lost the
							//  conflict and the live entity is the server version (ServerWins)
							// 
							if ( this._syncReader.HasConflictTempId() && entity.ServiceMetadata.IsTombstone) {
								// 
								//  This is a ServerWins conflict, or conflict error. The winning version is a tombstone without temp Id
								//  so there is no way to map the winning entity with a temp Id. The temp Id is in the conflict so we are
								//  using the conflict entity, which has the PK, to build a tombstone entity used to update the offline context
								// 
								//  In theory, we should copy the service metadata but it is the same end result as the service fills in
								//  all the properties in the conflict entity
								// 
								//                                           Add the conflict entity                                              
	                                         conflictEntity.ServiceMetadata.IsTombstone = true;
								ackedEntity = conflictEntity;
							}
						}
						//  Add ackedEntity to storage. If ackedEntity is still equal to entity then add non-conflict entity.
						if ( tempId != null && tempId != "" ) {
							changeSetResponse.AddUpdatedItem(ackedEntity);
						}
					break;
				case SyncBlob:
					changeSetResponse.ServerBlob= this._syncReader.getServerBlob();
					break;
				case HasMoreChanges:
					this._syncReader.GetHasMoreChangesValue();
                    break;
				default:
					break;
				}
				
				
			}
			// a 'using' block: end block 
			if ( ( TempIdToEntityMapping != null &&  TempIdToEntityMapping.size()  != 0 ) ) {
				//  The client sent some inserts which werent ack'd by the service. Throw.
				String builder = new String("Server did not acknowledge with a permanant Id for the following tempId's: ");
				builder = builder +TempIdToEntityMapping.toString();
				throw new Exception("" + builder);
			}

			Log.i("SyncLib", "ProcessUploadRequest finished");
			webRequest.getInputStream().close();
			return changeSetResponse;
		}
		 else {
			 webRequest.getInputStream().close();
			throw new CacheControllerException(String.format("Remote service returned error status. Status: %d, Description: %s",
					webRequest.getResponseCode(),
					webRequest.getResponseMessage()));
		}
	}

	private Object ProcessDownloadRequest(HttpURLConnection webRequest, CacheRequest request) throws IOException, Exception {
		// a 'using' block: start block 
		
		ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
	    if (request.Format == SyncSerializationFormat.ODataAtom)
				{
					this._syncWriter = new ODataAtomWriter(super.getBaseUri());
		
				}
				else if (request.Format == SyncSerializationFormat.ODataJson)
				{
				this._syncWriter = new ODataJsonWriter(super.getBaseUri());
            }
			this._syncWriter.StartFeed(true, request.KnowledgeBlob, bufferStream);

		this._syncWriter.WriteFeed(bufferStream);
		
		webRequest.getOutputStream().write(bufferStream.toByteArray());
		webRequest.getOutputStream().flush();
		webRequest.getOutputStream().close();

		

		if ( webRequest.getResponseCode() == HttpURLConnection.HTTP_OK ) {
			ChangeSet changeSet = new ChangeSet();
			// a 'using' block: start block 
			//  Create the SyncReader
			if (request.Format == SyncSerializationFormat.ODataAtom)
				{
					this._syncReader =  new ODataAtomReader(webRequest.getInputStream(),this._knownTypes);
		
				}
				else if (request.Format == SyncSerializationFormat.ODataJson)
				{
				this._syncReader = new ODataJsonReader(webRequest.getInputStream(), this._knownTypes);
            }
			
			//  Read the response
			while (this._syncReader.Next()) {
				
				switch (this._syncReader.getItemType())
                {
                    case Entry:
                    	
                        changeSet.AddItem(this._syncReader.getItem());
                        break;
                    case SyncBlob:
                        changeSet.ServerBlob = this._syncReader.getServerBlob();
                        break;
                    case HasMoreChanges:
                        changeSet.IsLastBatch = !this._syncReader.GetHasMoreChangesValue();
                        break;
                }
			}

			webRequest.getInputStream().close();
			return changeSet;
		}
		 else {
			 webRequest.getInputStream().close();
				throw new CacheControllerException(String.format("Remote service returned error status. Status: %d, Description: %s",
						webRequest.getResponseCode(),
						webRequest.getResponseMessage()));
		}
	}
	private Object ProcessScopeMetadata(HttpURLConnection webRequest, CacheRequest request) throws IOException, Exception {
		// a 'using' block: start block 
		
		Schema result = new Schema();
		
        InputStream stream =  webRequest.getInputStream();

		if ( webRequest.getResponseCode() == HttpURLConnection.HTTP_OK ) {
			
			 XmlReader theReader = XmlReader.Create(stream);
	         
			theReader.MoveToContent();
			theReader.MovetoTag("Schema");
			//result.NameSpace = theReader.getAttributeValue(2);

			while (theReader.MovetoTag("EntityType"))
			{
				EntityType theType = new EntityType();
				
				theType.Name = theReader.getAttributeValue(null, "Name");
				theReader.MovetoTag("Key");
				theReader.MovetoTag("PropertyRef");
				String name =  theReader.getLocalName();
				int type = theReader.getNodeType();
				while ( theReader.getLocalName().equals("PropertyRef") && theReader.getNodeType() ==  XmlPullParser.START_TAG)
				{
					theType.Key.add(theReader.getAttributeValue("", "Name"));
					theReader.Read();
				};
				
				theReader.MovetoTag("Property");
				while ( theReader.getLocalName().equals("Property") && theReader.getNodeType() ==  XmlPullParser.START_TAG)
				{
					Property theProp = new Property();
					theProp.Name = theReader.getAttributeValue("", "Name");
					theProp.Nullable = theReader.getAttributeValueAsBoolean("", "Nullable");
					theProp.Type = theReader.getAttributeValue("", "Type");
					
					theType.Properties.add( theProp);
					
					if(theReader.Read()) {
						continue;
					} else {
						break;
					}
				}
					
				result.EntityContainer.add(theType);	
			}
			Log.i("SyncLib", "ProcessDownloadRequest finished");
			webRequest.getInputStream().close();
			return result;
		}
		 else {
			 webRequest.getInputStream().close();
				throw new CacheControllerException(String.format("Remote service returned error status. Status: %d, Description: %s",
						webRequest.getResponseCode(),
						webRequest.getResponseMessage()));
		}
	}

//	private void CopyStreamContent(com.sun.dn.library.System.IO.StreamSupport src, com.sun.dn.library.System.IO.StreamSupport dst) {
//		/* TODO [ src.Seek(0, SeekOrigin.Begin) ] */;
//		byte[] buffer = new byte[2048]; // dim statement
//		int len = 0;
//		while ((len = /* TODO [ src.Read(buffer, 0, buffer.Length) ] */) > 0) {
//			/* TODO [ dst.Write(buffer, 0, len) ] */;
//		}
//	}

	/**
	*   <summary>
	*   Invokes the user BeforeSendingRequest callback.
	*   < summary>
	*   <param name="state">Async user state object. Ignored.< param>
	**/
	void FirePreRequestHandler(HttpURLConnection request) {
		if ( this._beforeRequestHandler != null ) {
			//  Invoke the user code.
			// this._beforeRequestHandler(request) ;
		}
	}

	/**
	*   <summary>
	*   Invokes the user's AfterReceivingResponse callback.
	*   < summary>
	*   <param name="state">AsyncArgsWrapper object< param>
	**/
	void FirePostResponseHandler(HttpURLConnection response) {
		if ( this._afterResponseHandler != null ) {
			//  Invoke the user code.
			/* TODO [ this._afterResponseHandler(response) ] */;
		}
	}
}