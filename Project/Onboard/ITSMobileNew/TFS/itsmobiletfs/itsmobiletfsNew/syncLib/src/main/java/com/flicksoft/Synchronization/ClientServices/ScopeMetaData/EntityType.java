package com.flicksoft.Synchronization.ClientServices.ScopeMetaData;

import java.util.*;
import com.flicksoft.Synchronization.*;
	/**
	*   <summary>
	*   Denotes a list of changes that is either to be uploaded or downloaded.
	*   < summary>
	* 
	**/

public class EntityType {

	public String Name;

	public ArrayList<String> Key;
	public ArrayList< Property> Properties;
	public EntityType() {

		Name = null;
		this.Key = new ArrayList<String>();
		this.Properties = new ArrayList<Property>();
	}

	

	
}