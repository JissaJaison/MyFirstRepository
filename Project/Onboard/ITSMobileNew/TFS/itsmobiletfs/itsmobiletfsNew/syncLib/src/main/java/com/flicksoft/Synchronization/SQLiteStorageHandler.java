package com.flicksoft.Synchronization;

import java.lang.reflect.Type;
import java.util.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.flicksoft.Synchronization.ClientServices.OfflineEntityMetadata;
import com.flicksoft.util.*;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.support.ConnectionSource;


public class SQLiteStorageHandler {
	
	// Added for axamit
	public static int configFile = 0;
	// Added for axamit

	
	private static class OpenHelper extends OrmLiteSqliteOpenHelper {

		public Context theContext = null;
		private String _schema = null;
		private Dao<SQLiteOfflineEntity, UUID> entityDao = null;

		OpenHelper(Context context, String dbFileName, String schema,
				//int version, int configFile) {
			int version /* for axamit, int configFile */) {
			super(context, dbFileName, null, version, configFile);
			
			theContext = context;
			_schema = schema;
		}

		public void onOpen(SQLiteDatabase db) {

		}

		@Override
		public void onCreate(SQLiteDatabase db,
				ConnectionSource connectionSource) {
			
			if (_schema != null) {
				SQLScriptHelper.executeBatchSql(db, _schema);
			}
			

		}

		@Override
		public void onUpgrade(SQLiteDatabase db,
				ConnectionSource connectionSource, int oldVersion,
				int newVersion) {
			if (_schema != null) {
				SQLScriptHelper.executeBatchSql(db, _schema);
			}
		}

		public Dao<SQLiteOfflineEntity, UUID> getDao(
				SQLiteOfflineEntity entity) throws Throwable {
			entityDao = (Dao<SQLiteOfflineEntity, UUID>) getDao(entity.getClass());
			return entityDao;
		}
		
		public Dao<SQLiteOfflineEntity, UUID> getDao(
				Type t) throws Throwable {
			String name = t.toString().split(" ")[1];
			entityDao = (Dao<SQLiteOfflineEntity, UUID>) getDao(Class.forName(name));
			return entityDao;
		}

		/**
		 * Close the database connections and clear any cached DAOs.
		 */
		@Override
		public void close() {
			super.close();
			entityDao = null;
		}

	}

	public static String DATABASE_NAME = "mobile.db";
	private SQLiteDatabase _connection;
	private Context _context;

	public Context get_context() {
		return _context;
	}

	private static SQLiteStorageHandler _instance = null;
	private ArrayList<java.lang.reflect.Type> _bidirectionalTypes;
	private OpenHelper openHelper;
	private Dao<SQLiteOfflineEntity, UUID> eDao = null;


	
	public static SQLiteStorageHandler Instance(Context ct) {
		if (_instance == null) {
			_instance = new SQLiteStorageHandler(ct);
		}
		return _instance;
	}

	public SQLiteDatabase getDatabase() {
		return _connection;
	}

	public boolean init(String dbFile, String schema, ArrayList<java.lang.reflect.Type> types) {
		// table arraylist initialize
		
		
		if (schema != null)
		//recreate db
		{
			cleanUp();
		}
		if ((schema == null && _connection == null) || schema != null)
		//db exists
		{

				this._bidirectionalTypes.addAll(types);
				//Collections.copy(this._bidirectionalTypes, types);
				openHelper = new OpenHelper(_context,dbFile,
						schema, 1 /* For Axamit, 0x7f040000 */);
	
				_connection = openHelper.getWritableDatabase();
				mapCommands();
		}
		

		return true;
	}

	private void cleanUp() {
		if (_connection != null)
		{
			_connection.close();
			openHelper.close();
			clearMapCommands();
			_bidirectionalTypes.clear();
		}
	}
	
	

	private SQLiteStorageHandler(Context ct) {
		_context = ct;
		_bidirectionalTypes = new ArrayList<java.lang.reflect.Type>();
	}

	// Anchor table statements
	private String GET_ANCHOR = "SELECT Anchor FROM __sync";
	private String UPDATE_ANCHOR = "UPDATE __sync SET Anchor = ?";
	private String INSERT_ANCHOR = "INSERT  INTO __sync(Anchor) VALUES( ZEROBLOB( 1024 ))";
	private String DELETE_ANCHOR = "DELETE FROM __sync";

	private HashMap<java.lang.reflect.Type, IDelegate> _insertCommands = new HashMap<java.lang.reflect.Type, IDelegate>();
	private HashMap<java.lang.reflect.Type, IDelegate> _updateCommands = new HashMap<java.lang.reflect.Type, IDelegate>();
	private HashMap<java.lang.reflect.Type, IDelegate> _updateByMetadataIdCommands = new HashMap<java.lang.reflect.Type, IDelegate>();
	private HashMap<java.lang.reflect.Type, IDelegate> _deleteCommands = new HashMap<java.lang.reflect.Type, IDelegate>();
	private HashMap<java.lang.reflect.Type, IDelegate> _tombstoneCommands = new HashMap<java.lang.reflect.Type, IDelegate>();
	private HashMap<java.lang.reflect.Type, IDelegate> _getCommands = new HashMap<java.lang.reflect.Type, IDelegate>();
	Class<?>[] paramsForInsertUpdate = { SQLiteOfflineEntity.class,
			boolean.class };
	Class<?>[] params = { SQLiteOfflineEntity.class };
	Delegator voidReturnDelegatorForInsertUpdate = new Delegator(
			paramsForInsertUpdate, Void.TYPE);
	// Delegator booleanReturnDelegatorForGet = new
	// Delegator(paramsForInsertUpdate, Void.TYPE);
	Delegator voidReturnDelegatorForDeleteTombstone = new Delegator(params,
			Void.TYPE);
	Delegator booleanReturnDelegatorForGet = new Delegator(params,
			boolean.class);

	private void clearMapCommands() {
		
			_updateByMetadataIdCommands.clear();
			_insertCommands.clear();
			_updateCommands.clear();
			_deleteCommands.clear();
			_tombstoneCommands.clear();
			_getCommands.clear();

	}
	private void mapCommands() {
		
		for (int i = 0; i < this._bidirectionalTypes.size(); i++) {
			_insertCommands.put(this._bidirectionalTypes.get(i),
					voidReturnDelegatorForInsertUpdate.build(this,
							"InsertRowInTable"));
			_updateByMetadataIdCommands.put(this._bidirectionalTypes.get(i),
					voidReturnDelegatorForInsertUpdate.build(this,
							"UpdateRowByMetadataIdInTable"));
			_updateCommands.put(this._bidirectionalTypes.get(i),
					voidReturnDelegatorForInsertUpdate.build(this,
							"UpdateRowInTable"));
			_deleteCommands.put(this._bidirectionalTypes.get(i),
					voidReturnDelegatorForDeleteTombstone.build(this,
							"DeleteRowInTableUsingMetadataId"));
			_tombstoneCommands.put(this._bidirectionalTypes.get(i),
					voidReturnDelegatorForDeleteTombstone.build(this,
							"TombstoneRowInTable"));
			_getCommands.put(this._bidirectionalTypes.get(i),
					booleanReturnDelegatorForGet.build(this, "recordExists"));
		}
	}

	/**
	 * <summary> Delete all tombstones and reset all the IsDirty bits from
	 * tables. This method is called after a successful upload to clear the
	 * local tracking information. < summary>
	 **/
	public void ResetDirtyAndDeleteTombstones() {
		

			for (int i = 0; i < this._bidirectionalTypes.size(); i++) {
				String tableName = this._bidirectionalTypes.get(i).toString().split(" ")[1].split("\\.")[1];
				
				_connection.execSQL("DELETE FROM " + tableName
						+ " WHERE IsTombstone=1;");

				_connection.execSQL("UPDATE " + tableName
						+ " SET IsDirty=0 WHERE IsDirty=1;");

			}
			
		

	}
	public void beginTransaction()
	{
		_connection.beginTransaction();
	}
	public void setTransactionSuccessful()
	{
		_connection.setTransactionSuccessful();
	}
	public void endTransaction()
	{
		_connection.endTransaction();
	}

	/**
	 * <summary> Gets all entities that were created modified deleted locally
	 * after the last sync. < summary> <param name="state">A unique identifier
	 * for the changes that are uploaded< param> <returns>The set of incremental
	 * changes to send to the service.< returns>
     *     OnboarD - This is where we pull the changed data from the database.
	 **/
	public Iterable<SQLiteOfflineEntity> GetChanges(UUID state) {
		java.util.List<SQLiteOfflineEntity> changeList = new ArrayList<SQLiteOfflineEntity>();
		java.util.List<SQLiteOfflineEntity> tempchangeList = new ArrayList<SQLiteOfflineEntity>();
		for (int i = 0; i < this._bidirectionalTypes.size(); i++) {
			try {
				eDao = openHelper.getDao(_bidirectionalTypes.get(i));
				tempchangeList = eDao.queryForEq("IsDirty", true);
				for(SQLiteOfflineEntity item : tempchangeList) {
					copyDataToMetadata(item);
					changeList.add(item);
				}
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return changeList;
	}

	/**
	 * <summary> Save changes retrieved from the sync service. This method is
	 * called to save changes from a download response. < summary> <param
	 * name="serverBlob">New blob received from the service.< param> <param
	 * name="entities">List of entities received from the service.< param>
	 **/
	public void SaveDownloadedChanges(byte[] serverBlob,
			Iterable<SQLiteOfflineEntity> entities) {
		// a 'using' block: start block

		

			for (SQLiteOfflineEntity entity : entities) {
				if (entity.getServiceMetadata().IsTombstone) {
					// Call delete command
					IDelegate deleteMethod = _deleteCommands.get(entity
							.getClass());
					deleteMethod.invoke(entity);
				} else {

					// Call insert/update command
					IDelegate getMethod = _getCommands.get(entity.getClass());

					if (getMethod == null)
					{
						Log.e("SycLib", "Get method for " + entity.getClass().getSimpleName() + " missing!");
					}
					else if ((Boolean) getMethod.invoke(entity)) {
						IDelegate updateMethod = _updateByMetadataIdCommands.get(entity
								.getClass());
						if (updateMethod != null)
						{
							updateMethod.invoke(entity, false);
						}
						else
						{
							Log.e("SyncLib", "update by metadata method for " + entity.getClass().getSimpleName() + " missing!");
						}

					} else {
						IDelegate insertMethod = _insertCommands.get(entity
								.getClass());
						if (insertMethod != null)
						{
							insertMethod.invoke(entity, false);
						}
						else
						{
							Log.e("SyncLib", "insert method for " + entity.getClass().getSimpleName() + " missing!");
						}
					}
				}
			}

			SaveAnchor(serverBlob);

		
	}

	/**
	 * <summary> Update Delete an existing item in the local database. This
	 * method is called to apply changes received in the upload response. <
	 * summary> <param name="entity">< param>
	 * @throws Throwable 
	 **/
	public void ApplyItem(SQLiteOfflineEntity entity) throws Throwable {

		if (entity.getServiceMetadata().IsTombstone) {
			if (entity.getServiceMetadata() != null
					&& entity.getServiceMetadata().ID != null
					&& !entity.getServiceMetadata().ID.equals("")) {
				DeleteRowInTableUsingMetadataId(entity);
			} else {
				DeleteRowInTable(entity);
			}
		} else {
			UpdateRowInTable(entity, false);
		}
	}

	/**
	 * <summary> Get the anchor blob that was last saved received from the
	 * service. < summary> <returns>Server blob< returns>
	 **/
	public byte[] GetAnchor() {
		byte[] result = null;
		Cursor cursor = _connection.rawQuery(GET_ANCHOR, null);
		if (cursor.moveToFirst()) {
			result = cursor.getBlob(0);
		}
		cursor.close();
		return result;

	}

	/**
	 * <summary> Save the blob that was retrieved from the service. < summary>
	 * <param name="anchor">Server blob< param>
	 **/
	public void SaveAnchor(byte[] anchor) {

		_connection.execSQL(DELETE_ANCHOR);
		ContentValues values = new ContentValues();
		values.put("Anchor", anchor);
		_connection.insert("__sync", null, values);



	}

	// generic tombstone method
	public void TombstoneRowInTable(SQLiteOfflineEntity entity) {

		String tableName = entity.getClass().getSimpleName();

		String guid = entity.gUID.toString();
		_connection.execSQL("UPDATE " + tableName
				+ " SET IsDirty=1,IsTombstone=1 WHERE gUID=?",
				new Object[] { guid });
	}


	// generic exists method
	public boolean recordExists(SQLiteOfflineEntity entity) {
		String tableName = entity.getClass().getSimpleName();
		Cursor cursor = _connection.rawQuery("SELECT 1 FROM " + tableName
				+ " WHERE trim(_MetadataID)=\"" +  entity.ServiceMetadata.ID.trim() + "\"", new String[]{} );
		int count = cursor.getCount();
		cursor.close();
		return count > 0;
	}

	// generic delete row in table method
	public void DeleteRowInTable(SQLiteOfflineEntity entity) {
		String tableName = entity.getClass().getSimpleName();
		_connection.execSQL("DELETE FROM " + tableName + " WHERE gUID=?",
				new Object[] { entity.gUID.toString() });
	}

	// generic delete row in table method for metadata
	public void DeleteRowInTableUsingMetadataId(SQLiteOfflineEntity entity) {
		String tableName = entity.getClass().getSimpleName();
		_connection.execSQL(
				"DELETE FROM " + tableName + " WHERE _MetadataID=?",
				new Object[] { (entity.getServiceMetadata().ID) });
	}

	// generic insert row in table method
	public void InsertRowInTable(SQLiteOfflineEntity entity, boolean isDirty) throws Throwable {
		
			entity.ServiceMetadata.IsDirty = isDirty;
			copyDataFromMetadata(entity);
			eDao = openHelper.getDao(entity);
			eDao.create(entity);
		
	}

	// generic update row in table method
	public void UpdateRowInTable(SQLiteOfflineEntity entity, boolean isDirty) throws Throwable {

			entity.ServiceMetadata.IsDirty = isDirty;
			copyDataFromMetadata(entity);
			eDao = openHelper.getDao(entity);
			eDao.update(entity);

		
	}
	// generic update row in table method
		public void UpdateRowByMetadataIdInTable(SQLiteOfflineEntity entity, boolean isDirty) throws Throwable {

				entity.ServiceMetadata.IsDirty = isDirty;
				copyDataFromMetadata(entity);
				
				eDao = openHelper.getDao(entity);
				if (eDao.update(entity) ==0)
				{
					Map<String,Object> args =  new HashMap<String, Object>();
					
					SelectArg idSelectArg = new SelectArg();
					idSelectArg.setValue(entity.getServiceMetadata().ID);
					args.put("_MetadataID", idSelectArg);
					List<SQLiteOfflineEntity> results = eDao.queryForFieldValuesArgs(args);
					if (results != null && results.size() == 1)
					{
						UUID temp =  entity.gUID;
						entity.gUID = results.get(0).gUID;
						eDao.updateId(entity, temp);
						eDao.update(entity);
						//eDao.delete(results.get(0));
						
						//eDao.create(entity);
					}
				}
				

			
		}

	// generic get all rows in table method
	public List<SQLiteOfflineEntity> GetAllRowsInTable(
			SQLiteOfflineEntity entity) {
		java.util.List<SQLiteOfflineEntity> allItems = new ArrayList<SQLiteOfflineEntity>();

		try {
			eDao = openHelper.getDao(entity);
			allItems = eDao.queryForEq("IsTombstone", false);

		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return allItems;

	}
	
	public void copyDataFromMetadata(SQLiteOfflineEntity entity){
		if (entity.ID == null) {
			entity.ID = entity.ServiceMetadata.ID;
		}
		
		if (entity.ServiceMetadata.IsDirty) {
			entity.IsDirty = entity.ServiceMetadata.IsDirty;
		}
		
		if (entity.ServiceMetadata.IsTombstone) {
			entity.IsTombstone = entity.ServiceMetadata.IsTombstone;
		}
		
	}
	
	public void copyDataToMetadata(SQLiteOfflineEntity item) {
		item.ServiceMetadata.IsTombstone = item.IsTombstone;
		item.ServiceMetadata.IsDirty = item.IsDirty;
		item.ServiceMetadata.ID = item.ID;
	}
}