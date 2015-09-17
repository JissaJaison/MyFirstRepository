package com.onbts.ITSMobile;


import android.app.Application;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.onbts.ITSMobile.services.DbService;
import com.onbts.ITSMobile.util.Files;

import util.SqliteImageDownloader;

public class App extends Application {

    public final static double SCREEN_ORIENTATION_THRESHOLD = 9; // Screen size in inches
//    private static Context context;
//    DbService db;
    public void onCreate() {
        super.onCreate();

//        App.context = getApplicationContext();
//        com.flicksoft.Synchronization.setConfigFile(R.raw.ormlite_config);
        Files.checkAppDataDirectory(this);
//       / String syncUrl = Settings.getInstance().getSettingAsString("url");
//        SyncService.getInstance().init(context, BaseActivity.syncServiceHandler, syncUrl, DbService.getDbFilePath());
//        db= DbService.getInstance(this);
//        db.init();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .imageDownloader(new SqliteImageDownloader(this))
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).writeDebugLogs().build();
        ImageLoader.getInstance().init(config);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
