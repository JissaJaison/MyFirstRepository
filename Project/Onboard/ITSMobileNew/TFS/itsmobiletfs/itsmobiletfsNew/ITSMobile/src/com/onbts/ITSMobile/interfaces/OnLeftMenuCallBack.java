package com.onbts.ITSMobile.interfaces;

import com.onbts.ITSMobile.model.NavDrawerItemLeft;
import com.onbts.ITSMobile.model.UserModel;

/**
 * Created by tigre on 18.04.14.
 */
public interface OnLeftMenuCallBack {

    public UserModel onGetUser();

    void onCategorySelected(NavDrawerItemLeft item);
}
