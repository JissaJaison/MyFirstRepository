package com.onbts.ITSMobile.Loaders;

import android.content.Context;
import android.support.v4.content.CursorLoader;

import com.onbts.ITSMobile.services.DbService;
@Deprecated
public abstract class BaseCursorLoader extends CursorLoader {
    public Context mContext;
    public DbService mDb;

    public BaseCursorLoader(Context context) {
        super(context);
    }

}
