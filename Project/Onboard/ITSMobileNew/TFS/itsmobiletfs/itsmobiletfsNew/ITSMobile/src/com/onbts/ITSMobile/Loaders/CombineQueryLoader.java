package com.onbts.ITSMobile.Loaders;

import android.content.Context;
import android.database.Cursor;

import com.onbts.ITSMobile.services.DbService;
@Deprecated
public class CombineQueryLoader extends BaseCursorLoader {
    private String query;
    private int wasQuery;
    //here will be USERID or DEPARTMENT
    private long userId;
    private long department;

    public CombineQueryLoader(Context context, DbService db, int wasQuery, long userId, long department) {
        super(context);
        mDb = db;
        mContext = context;
        this.userId = userId;
        this.department = department;
        this.wasQuery = wasQuery;
    }

    @Override
    public Cursor loadInBackground() {
        return mDb.createCombineQuery(wasQuery, userId, department);
    }
}
