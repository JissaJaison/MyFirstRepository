package com.onbts.ITSMobile.Loaders;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.onbts.ITSMobile.services.DbService;
@Deprecated
public class IssueCursorLoader extends BaseCursorLoader {

    public IssueCursorLoader(Context context, DbService db) {
        super(context);
        mContext = context;
        mDb = db;
        Log.d("db", "IssueCursorLoader constructor");
    }

    @Override
    public Cursor loadInBackground() {
        Log.d("db", "IssueCursorLoader loadInBcakground");
        return mDb.getIssues();
    }
}

