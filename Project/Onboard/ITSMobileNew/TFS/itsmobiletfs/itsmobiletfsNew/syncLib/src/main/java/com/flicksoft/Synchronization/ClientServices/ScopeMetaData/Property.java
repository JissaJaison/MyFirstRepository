package com.flicksoft.Synchronization.ClientServices.ScopeMetaData;

import java.util.*;
import com.flicksoft.Synchronization.*;
	/**
	*   <summary>
	*   Denotes a list of changes that is either to be uploaded or downloaded.
	*   < summary>
	* 
	**/

public class Property {

	public String Name;
	public String Type;
	public boolean Nullable;
	public Property() {
		this.Name = null;
		this.Nullable = true;
		this.Type = null;
	}

	
	
}