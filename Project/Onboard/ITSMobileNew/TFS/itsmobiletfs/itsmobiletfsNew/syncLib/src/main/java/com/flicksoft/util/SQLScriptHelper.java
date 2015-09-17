package com.flicksoft.util;

import java.io.IOException;
import java.io.InputStream;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class SQLScriptHelper {
	public static void executeBatchSql(SQLiteDatabase db, String sqlString){ 
    	// use something like StringTokenizer to separate sql statements 
    	  String[] sqlStatements = sqlString.split(";");
    	for (int i = 0; i < sqlStatements.length; i++){ 
    		if (!sqlStatements[i].contains("--"))
    		{
    			db.execSQL(sqlStatements[i]); 
    		}
    	} 
    } 
	public static  void executeBatchSqlFile(SQLiteDatabase db, Context appContext, String fileName)
	{
		InputStream is;
		try {
			is = appContext.getAssets().open(fileName);
			byte[] data = new byte[is.available()];
		    is.read(data);
		    String sqlStrings = new String(data);
		    is.close();
		    
		    executeBatchSql(db, sqlStrings);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	
	    
	}
	
}
