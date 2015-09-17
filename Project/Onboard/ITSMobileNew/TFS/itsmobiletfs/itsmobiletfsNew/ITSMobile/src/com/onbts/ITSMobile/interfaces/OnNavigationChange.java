package com.onbts.ITSMobile.interfaces;

import android.content.Intent;

import com.onbts.ITSMobile.model.DetailedIssue;

import com.onbts.ITSMobile.model.ReturnDateWithActionDialog;
import com.onbts.ITSMobile.model.UserModel;

import java.util.ArrayList;

public interface OnNavigationChange {
    public void onOpenFile(long id);

    @Deprecated
    public void onStartIssueList();

    public void onTitleChange(String title);

    @Deprecated
    public void onShowActionBar();

    @Deprecated
    public void onHideActionBar();

    @Deprecated
    public void onShowProgressDialog();

    @Deprecated
    public void onDismissProgressDialog();

    @Deprecated
    public void onSetUser(UserModel user);

    public UserModel onGetUser();

    @Deprecated
    public void onNeedSync();

    @Deprecated
    void onAddActions(DetailedIssue details, ArrayList<ReturnDateWithActionDialog> data, long idAction, long prevActionID,
                      long nextActionID, boolean keep);

    void onLoadDetaileIssue(long issueId);

    @Deprecated
    public void updateIssue(long issueId, boolean open, boolean favorite);

    public Intent onGetDB();

    public void onSendDBRequest(Intent i);

    void onShowHistory(long id);

    void onActionMoreClock(DetailedIssue details);

    void onShowDetails(long id);

}
