package com.flicksoft.Synchronization.ClientServices;

import java.util.*;
import java.net.*;

import com.flicksoft.util.*;
	/**
	* Class converted from .NET code
	**/

public class CacheControllerBehavior {

    HashMap <String, String> _scopeParameters;


	public boolean Locked = false;
	//Object _lockObject = new Object();
	ArrayList<java.lang.reflect.Type>  _knownTypes;
	private ICredentials  _credentials;
	String _scopeName;
	Action<HttpURLConnection> _beforeSendingRequestHandler;
	Action<HttpURLConnection>  _afterSendingResponse;

	/**
	*   <summary>
	*   Adds an Type to the collection of KnownTypes.
	*   < summary>
	*   <typeparam name="T">Type to include in search< typeparam>
	**/
	public void AddType(java.lang.reflect.Type T) {
		//synchronized (this._lockObject) {
			{
			try {
				CheckLockState();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this._knownTypes.add(T);
		}
	}

	/**
	*   <summary>
	*   Function that users will use to add any custom scope level filter parameters and their values.
	*   < summary>
	*   <param name="key">parameter name as string< param>
	*   <param name="value">parameter value as string< param>
	 * @throws Exception 
	**/
	public void AddScopeParameters(String key, String value) throws Exception {
		if ( key == null ) {
			throw new Exception("key");
		}
		if ( key == null || key == "") {
			throw new Exception("key cannot be empty key");
		}
		if ( value == null ) {
			throw new Exception("value");
		}
		//synchronized (this._lockObject) {
			{
			CheckLockState();
			this._scopeParameters.put(key, value);
		}
	}
	public void ClearScopeParameters() throws Exception {
		
		//synchronized (this._lockObject) {
			{
			CheckLockState();
			this._scopeParameters.clear();
		}
	}

	private void CheckLockState() throws Exception {
		if ( this.Locked ) 
		{
			throw new Exception("Cannot modify CacheControllerBehavior when sync is in progress.");
		}
	}

	public CacheControllerBehavior() {
		this._knownTypes =  new ArrayList<java.lang.reflect.Type>();
		this._scopeParameters =  new HashMap<String, String>();
	}

	public ArrayList<java.lang.reflect.Type> getKnownTypes() {
		return  this._knownTypes;
	}

//	public HashMap<String,String> */ getScopeParameters() {
//		return /* TODO [ this._scopeParameters.GetEnumerator() ] */;
//	}

	public ICredentials getCredentials() {
		return this._credentials;
	}

	public void setCredentials(ICredentials  value) throws Exception {
		if ( value == null ) {
			throw new Exception("value") ;
		}
		//synchronized (this._lockObject) {
			{
			CheckLockState();
			this._credentials = value;
		}
	}

	public String getScopeName() {
		return this._scopeName;
	}

	public String setScopeName(String value) {
		return _scopeName = value;
	}
	public Action<HttpURLConnection> getBeforeSendingRequest() {
		return this._beforeSendingRequestHandler;
	}

	public void setBeforeSendingRequest(Action<HttpURLConnection> value) {
		//synchronized (this._lockObject)
		{
			try {
				CheckLockState();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this._beforeSendingRequestHandler = value;
		}
	}

	public Action<HttpURLConnection>  getAfterReceivingResponse() {
		return this._afterSendingResponse;
	}

	public void setAfterReceivingResponse(Action<HttpURLConnection> value) {
		//synchronized (this._lockObject)
		{
			try {
				CheckLockState();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this._afterSendingResponse = value;
		}
	}

	HashMap<String, String> getScopeParametersInternal() {
		return this._scopeParameters;
	}



	



	
}