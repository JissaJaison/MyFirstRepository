package com.onbts.ITSMobile.interfaces;

import com.onbts.ITSMobile.model.UserModel;
import com.onbts.ITSMobile.model.issue.IssueModel;

import java.util.ArrayList;

/**
 * Created by tigre on 16.04.14.
 */
public interface OnIssueListCallBack {

    public void onTitleChange(String title);


    public void onStartIssueDetail(long issueId, ArrayList<IssueModel> models, int position);


    public UserModel onGetUser();

    public void onNeedSync();

    public void updateIssue(long issueId, boolean open, boolean favorite);

    public void refreshRightMenu();

    void updateTitle(int count, int allCount);

    void onSetDefaultLocation();
}
