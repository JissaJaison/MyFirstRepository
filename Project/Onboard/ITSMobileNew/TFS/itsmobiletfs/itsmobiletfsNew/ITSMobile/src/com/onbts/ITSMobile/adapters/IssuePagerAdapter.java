package com.onbts.ITSMobile.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.onbts.ITSMobile.UI.fragments.DetailedPage;
import com.onbts.ITSMobile.model.DetailedIssue;
import com.onbts.ITSMobile.services.DbService;


/**
 * Created by JLAB on 12.03.14.
 */
@Deprecated
public class IssuePagerAdapter extends FragmentStatePagerAdapter {
    private boolean mBoundaryCaching;
    private Cursor mCursor;
    private FragmentManager fm;
    private Context mContext;
    public IssuePagerAdapter(FragmentManager fm, Cursor c, Context context) {
        super(fm);
        this.fm = fm;
        this.mCursor = c;
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int pos) {
        Log.d("page adapter", "get item i BEFORE  = " + pos);
        if(pos == getCount() - 1)
            pos = 0;
        else if(pos == 0)
            pos = getCount() - 3;
        else {
            pos--;
        }
        Log.d("page adapter", "get item i AFTER = " + pos);
        return DetailedPage.newInstance(pos, getDetailsFromCursor(pos));
    }

    @Override
    public int getCount() {
        return mCursor.getCount() + 2;
    }

    public DetailedIssue getDetailsFromCursor(int pos) {
        DetailedIssue details = new DetailedIssue();
        if (mCursor.moveToPosition(pos)) {
            long id= mCursor.getLong(mCursor.getColumnIndex("_id"));
            details = DbService.getInstance(mContext).getIssueDetails(id);
            Log.d("page adapter", "from adapter id: " + details.getId() + "issue type: " + details.getIssueType());
        }

        return details;
    }

    public void swapCursor(Cursor c) {
        if (mCursor == c)
            return;

        this.mCursor = c;
        notifyDataSetChanged();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        Log.d("page adapter", "item destroed");
    }
    int toRealPosition(int position) {
        int realCount = getRealCount();

        int realPosition = (position-1) % realCount;
        if (realPosition < 0)
            realPosition += realCount;

        return realPosition;
    }
    public String getIdByPosition(int position) {
        mCursor.moveToPosition(position);
        String ans = mCursor.getString(mCursor.getColumnIndex("_id"));
        return ans;
    }

    public long getStatusIdByPosition(int position) {
        mCursor.moveToPosition(position);
        long status = mCursor.getLong(mCursor.getColumnIndex("StatusID"));
        return status;
    }
    public int getRealCount() {
        return mCursor.getCount();
    }
    public void setBoundaryCaching(boolean flag) {
        mBoundaryCaching = flag;
    }
}
