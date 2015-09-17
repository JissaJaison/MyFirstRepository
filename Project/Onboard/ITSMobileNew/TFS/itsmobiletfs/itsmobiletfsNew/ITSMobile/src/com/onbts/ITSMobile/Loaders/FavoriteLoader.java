package com.onbts.ITSMobile.Loaders;

import android.content.Context;
import android.database.Cursor;

import com.onbts.ITSMobile.services.DbService;

/**
 * Created by JLAB on 20.03.14.
 */
@Deprecated
public class FavoriteLoader extends BaseCursorLoader {

    public FavoriteLoader(Context context, DbService db) {
        super(context);
        mDb = db;
        mContext = context;
    }

    @Override
    public Cursor loadInBackground() {
        return mDb.getFavorites();
    }
}
