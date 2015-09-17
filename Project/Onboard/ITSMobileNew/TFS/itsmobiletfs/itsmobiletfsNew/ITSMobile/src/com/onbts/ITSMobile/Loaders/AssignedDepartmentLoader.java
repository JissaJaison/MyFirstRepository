package com.onbts.ITSMobile.Loaders;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.onbts.ITSMobile.services.DbService;
@Deprecated
public class AssignedDepartmentLoader extends BaseCursorLoader {
    private final long department;
    private final long userId;
    public AssignedDepartmentLoader(Context context, DbService db, long department, long userId) {
        super(context);
        this.userId = userId;
        mContext = context;
        mDb = db;
        this.department = department;
    }

    @Override
    protected Cursor onLoadInBackground() {
        Log.d("db", "assigned by department loader background");
        return mDb.getDepartmentAssigned(department, userId);
    }
}
