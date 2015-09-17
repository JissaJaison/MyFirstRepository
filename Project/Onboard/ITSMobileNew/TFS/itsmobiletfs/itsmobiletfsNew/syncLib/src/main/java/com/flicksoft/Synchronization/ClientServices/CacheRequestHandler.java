package com.flicksoft.Synchronization.ClientServices;

import java.util.*;
import java.net.*;

	/**
	*   <summary>
	*   Base class that will handle the processing of a CacheRequest
	*   < summary>
	* 
	**/

abstract class CacheRequestHandler {
	URI _baseUri;
	String _scopeName;

	public HashMap<String, String> scopeParameters;
	public CacheRequestHandler(URI baseUri, String scopeName) {
		this._baseUri = baseUri;
		this._scopeName = scopeName;
	}

	/**
	*   <summary>
	*   Method that will contain the actual implementation of the cache request processing.
	*   < summary>
	*   <param name="request">CacheRequest object< param>
	*   <returns>ChangeSet for a dowload request or ChangeSetResponse for an upload request< returns>
	 * @throws Exception 
	**/
	public abstract Object ProcessCacheRequest(com.flicksoft.Synchronization.ClientServices.CacheRequest request) throws Exception;

	/**
	*   <summary>
	*   Factory method for creating a cache handler. For labs only Http based implementation is provided.
	*   < summary>
	*   <param name="serviceUri">Base Uri to connect to< param>
	*   <param name="behaviors">The CacheControllerBehavior object< param>
	*   <returns>A CacheRequestHandler object< returns>
	**/
	public static CacheRequestHandler CreateRequestHandler(URI serviceUri, CacheControllerBehavior behaviors) {
		//  For labs its always Http
		return new HttpCacheRequestHandler(serviceUri,behaviors);
	}

	protected String getScopeName() {
		return _scopeName;
	}

	protected URI getBaseUri() {
		return _baseUri;
	}
}