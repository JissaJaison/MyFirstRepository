package com.onbts.ITSMobile.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import android.util.SparseArray;
import android.view.ViewGroup;

import com.onbts.ITSMobile.UI.fragments.DetailedPage;
import com.onbts.ITSMobile.model.DetailedIssue;

/**
 * Created by tigre on 16.04.14.
 */
public class IssueDetailedPageAdapter extends FragmentStatePagerAdapter {
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    private long[] ids;
    private FragmentManager fm;
    private Context mContext;

    public IssueDetailedPageAdapter(FragmentManager fm, long[] ids, Context context) {
        super(fm);
        this.fm = fm;
        this.ids = ids;
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int pos) {
        if (pos == getCount() - 1)
            pos = 0;
        else if (pos == 0)
            pos = getCount() - 3;
        else {
            pos--;
        }
        return DetailedPage.newInstance(pos, ids[pos]);
    }

    @Override
    public int getCount() {
        return ids != null ? ids.length + 2 : 0;
    }

    @Deprecated
    public DetailedIssue getDetailsFromCursor(int pos) {
        DetailedIssue details = new DetailedIssue();
/*
            if (mCursor.moveToPosition(pos)) {
                long id= mCursor.getLong(mCursor.getColumnIndex("_id"));
                details = DbService.getInstance(mContext).getIssueDetails(id);
                Log.d("page adapter", "from adapter id: " + details.getId() + "issue type: " + details.getIssueType());
            }
*/

        return details;
    }

    @Deprecated
    public void swapCursor(Cursor c) {
/*
            if (mCursor == c)
                return;

            this.mCursor = c;
*/
        notifyDataSetChanged();
    }

    public void swapData(long[] ids) {
        this.ids = ids;
        notifyDataSetChanged();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    int toRealPosition(int position) {
        int realCount = getRealCount();

        int realPosition = (position - 1) % realCount;
        if (realPosition < 0)
            realPosition += realCount;

        return realPosition;
    }

    public int getRealCount() {
        return ids != null ? ids.length : 0;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }



    public DetailedPage getRegisteredFragment(int position) {
        DetailedPage page = (DetailedPage) registeredFragments.get(position);
        return page;
    }
}
