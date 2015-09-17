package com.onbts.ITSMobile.Loaders;

import android.content.Context;
import android.database.Cursor;

import com.onbts.ITSMobile.services.DbService;
@Deprecated
public class CreatedByDepartmentLoader extends BaseCursorLoader {
    private long depId;

    public CreatedByDepartmentLoader(Context context, DbService db, long id) {
        super(context);
        mDb = db;
        mContext = context;
        depId = id;
    }

    @Override
    public Cursor loadInBackground() {
        return mDb.getCreatedByDepartment(depId);
    }
}
