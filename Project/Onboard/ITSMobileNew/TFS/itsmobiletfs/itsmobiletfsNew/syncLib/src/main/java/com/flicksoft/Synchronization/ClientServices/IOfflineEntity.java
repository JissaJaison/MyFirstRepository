package com.flicksoft.Synchronization.ClientServices;

import java.util.*;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;

	/**
	*   <summary>
	*   Represents the base interface that all offline cacheable object should derive from.
	*   < summary>
	* 
	**/

public abstract class IOfflineEntity {
	@SerializedName("__metadata")
	public OfflineEntityMetadata ServiceMetadata = null;
	@DatabaseField(id = true)
	public UUID  gUID;//              char(38) NOT NULL,


	@DatabaseField(columnName="IsTombstone") 
	public boolean IsTombstone;
	@DatabaseField(columnName="_MetadataID")
	public String ID;
	@DatabaseField(columnName="IsDirty")
	public boolean IsDirty;
}