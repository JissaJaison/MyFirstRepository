package com.flicksoft.util;

import java.util.UUID;
import java.io.*;

import android.content.Context;
import android.os.Environment;

/**
* UUID helper class
*
*/
public class FileHelper {
	
	public static void deleteExternalStoragePrivateFile( String fileName) {
	    // Get path for the file on external storage.  If external
	    // storage is not currently mounted this will fail.
	    File file = new File(Environment.getExternalStoragePublicDirectory("OData"), fileName);
	    if (file != null) {
	        file.delete();
	    }
	}

	public static String getExternalStoragePath() {
		File path = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_PICTURES);
		
		return path.getAbsolutePath();
	}
	public static boolean hasExternalStoragePrivateFile( String fileName) {
	    // Get path for the file on external storage.  If external
	    // storage is not currently mounted this will fail.
		File path = Environment.getExternalStorageDirectory();
	    File file = new File(path, fileName);


	   
	    if (file != null) {
	        return file.exists();
	    }
	    return false;
	}

}