package com.onbts.ITSMobile.UI.fragments.base;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.onbts.ITSMobile.interfaces.OnNavigationChange;
import com.onbts.ITSMobile.services.DBRequest;
import com.onbts.ITSMobile.services.ServiceDataBase;


public abstract class BaseFragment extends Fragment {
    protected OnNavigationChange mNavigator;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnNavigationChange) {
            mNavigator = (OnNavigationChange) activity;
        } else {
            throw new ClassCastException("Activity must implements fragment's callbacks");
        }
        Log.d("life", "onattach created issuesfragment");
    }

    @Override
    public void onDetach() {
        mNavigator = null;
        super.onDetach();
    }
    public abstract void onHandelDBMessage(DBRequest request);

}
