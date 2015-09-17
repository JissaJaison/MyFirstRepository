package com.onbts.ITSMobile.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Settings {
    private Properties _entries;
    private String file;
    private static Settings _instance = null;

    public static Settings getInstance(Context context) {
        if (_instance == null) {
            _instance = new Settings(context);
        }
        return _instance;
    }

    public Settings(Context context) {
        _entries = new Properties();
        file = Files.getAppDataDirectory(context);

        try {
            File confiFile = new File(file + File.separator + "config.txt");
            Log.d("path", confiFile.toString());
            if (confiFile.exists()) {
                _entries.load(new FileInputStream(confiFile));
            } else {
                _entries.setProperty("password", "m4X/VtrAaXqu3Y7QTyaKmA==" /* onboard */);
                _entries.setProperty("url", "http://67.215.180.182/ITSMobileV2/ITSMobileSyncService.svc");
                _entries.setProperty("syncTimeInterval", "10000");
                _entries.setProperty("compressionPercentage", "30");
//OnboarD -- ^^^^^^^ Not in use

                //save properties to project root folder
                _entries.store(new FileOutputStream(confiFile.getAbsoluteFile()), null);
            }
            //set the properties value


        } catch (IOException ex) {
            Log.e("Inspections", ex.getMessage());
        }
    }

    public String getSettingAsString(String key) {
        return _entries.getProperty(key);
    }

    public int getSettingAsInt(String key) {
        return Integer.parseInt(_entries.getProperty(key));
    }

    public int getSettingAsInteger(String key) {
        return Integer.parseInt(_entries.getProperty(key));
    }

    public void setSetting(String key, String value) {
        _entries.setProperty(key, value);
        try {
            _entries.store(new FileOutputStream(file + File.separator + "config.txt"), null);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            Log.e("Inspections", e.getMessage());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e("Inspections", e.getMessage());
        }
    }

    public void setProperOrientation(Activity activity) {/*
            double size = 9;
         
        	try {

        		// Compute screen size
        		DisplayMetrics dm = App.getAppContext().getResources().getDisplayMetrics();
                float screenWidth  = dm.widthPixels / dm.xdpi;
        		float screenHeight = dm.heightPixels / dm.ydpi;
           		size = Math.sqrt(Math.pow(screenWidth, 2) + Math.pow(screenHeight, 2));
        	} catch(Throwable t) {
        		// Insert a log message here
        	} finally {
        		if (size < App.SCREEN_ORIENTATION_THRESHOLD) {
                	activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            	}
        		else
        		{
        			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        		}
        	}   */
    }
}
