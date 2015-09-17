package com.onbts.ITSMobile.UI.fragments.base;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.onbts.ITSMobile.interfaces.OnLoginListener;


/**
 * Created by tigre on 06.04.14.
 */
public abstract class BaseLoginFragment extends Fragment{

    protected OnLoginListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnLoginListener) {
            mListener = (OnLoginListener) activity;
        } else {
            throw new ClassCastException("Activity must implements OnLoginListener");
        }
        Log.d("life", "onattach created BaseLoginFragment");
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }
}
