package com.onbts.ITSMobile.Loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.onbts.ITSMobile.model.ActionIssue;
import com.onbts.ITSMobile.model.UserModel;
import com.onbts.ITSMobile.services.DB.DBRequest;
import com.onbts.ITSMobile.services.DbService;

import java.util.List;

/**
 * Created by tigre on 02.04.14.
 */
@Deprecated
public class ActionLoader extends AsyncTaskLoader<List<ActionIssue>> {
    private final UserModel user;
    private final long issueId;
    private final long statusId;

    public ActionLoader(Context context, UserModel user, long issueId, long statusId) {
        super(context);
        this.user = user;
        this.issueId = issueId;
        this.statusId = statusId;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
    }

    @Override
    public List<ActionIssue> loadInBackground() {
        try {
            return DBRequest.getActionForIssue(user, issueId, statusId, true, DbService.getInstance(getContext()).getIssutraxdb());
        } catch (Exception e) {
            return null;
        }
    }
}
