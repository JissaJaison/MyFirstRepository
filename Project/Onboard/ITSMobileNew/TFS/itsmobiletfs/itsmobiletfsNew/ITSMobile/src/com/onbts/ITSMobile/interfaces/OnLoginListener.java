package com.onbts.ITSMobile.interfaces;

import com.onbts.ITSMobile.model.UserModel;

/**
 * Created by tigre on 06.04.14.
 */
public interface OnLoginListener {

    public void onSetUser(UserModel user);

    public void onLogin(String login, String psw);

    public UserModel onGetUser();

    public void onNeedSync();

}
