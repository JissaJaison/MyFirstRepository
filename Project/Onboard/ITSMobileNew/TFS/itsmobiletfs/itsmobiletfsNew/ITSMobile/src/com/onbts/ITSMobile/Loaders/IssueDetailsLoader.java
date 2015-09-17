package com.onbts.ITSMobile.Loaders;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.onbts.ITSMobile.services.DbService;
@Deprecated
public class IssueDetailsLoader extends BaseCursorLoader {
    private long idIssue;
    public IssueDetailsLoader(Context context, long id) {
        super(context);
        mContext = context;
        mDb = DbService.getInstance(context);
        this.idIssue = id;
        Log.d("db", "IssueDetailtsLoader constructor");
    }

    @Override
    public Cursor loadInBackground() {
        Log.d("db", "IssueCursorLoader loadInBcakground");
        return mDb.getIssueDetailsCursor(idIssue);
    }
}
