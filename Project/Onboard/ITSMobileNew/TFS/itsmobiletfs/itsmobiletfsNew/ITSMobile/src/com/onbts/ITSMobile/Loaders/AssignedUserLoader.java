package com.onbts.ITSMobile.Loaders;

import android.content.Context;
import android.database.Cursor;

import com.onbts.ITSMobile.services.DbService;
@Deprecated
public class AssignedUserLoader extends BaseCursorLoader {
    private long userId;

    public AssignedUserLoader(Context context, DbService db, long id) {
        super(context);
        mDb = db;
        mContext = context;
        userId = id;
    }

    @Override
    public Cursor loadInBackground() {
        return mDb.getUserAssigned(userId);
    }
}
