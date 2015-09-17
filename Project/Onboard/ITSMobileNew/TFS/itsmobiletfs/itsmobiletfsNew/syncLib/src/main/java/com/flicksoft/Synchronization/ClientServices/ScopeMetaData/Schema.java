package com.flicksoft.Synchronization.ClientServices.ScopeMetaData;

import java.util.*;
import com.flicksoft.Synchronization.*;
	/**
	*   <summary>
	*   Denotes a list of changes that is either to be uploaded or downloaded.
	*   < summary>
	* 
	**/

public class Schema {

	public String NameSpace;

	public ArrayList<EntityType> EntityContainer;
	public Schema() {
		this.NameSpace = null;
		this.EntityContainer = new ArrayList<EntityType>();
		
	}

}