package com.flicksoft.Synchronization;
import com.flicksoft.Synchronization.ClientServices.*;
import java.util.*;

	/**
	*   <summary>
	*   This class is the base entity from which all entities used by the application must inherit.
	*   < summary>
	* 
	**/

public abstract class SQLiteOfflineEntity extends  IOfflineEntity  {

	public SQLiteOfflineEntity() {
		 ServiceMetadata = new OfflineEntityMetadata();
	}

	public OfflineEntityMetadata getServiceMetadata() {
		return ServiceMetadata;
	}

	public void setServiceMetadata(OfflineEntityMetadata value) {
		ServiceMetadata = value;
	}
}