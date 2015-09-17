package com.onbts.ITSMobile.Loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.onbts.ITSMobile.model.DetailedIssue;
import com.onbts.ITSMobile.services.DbService;

/**
 * Create Async DetailsIssue with history.
 * Created by tigre on 01.04.14.
 */
@Deprecated
public class LoaderIssueDetails extends AsyncTaskLoader<DetailedIssue> {
    long id;

    public LoaderIssueDetails(Context context, long id) {
        super(context);
        this.id = id;
    }

    @Override
    public DetailedIssue loadInBackground() {
        DetailedIssue issue = DbService.getInstance(getContext()).getIssueDetails(id);
//        if (issue!=null)
//        issue.setHistoryList(DBRequest.getHistory((int) id, DbService.getInstance(getContext()).getIssutraxdb()));
        return issue;
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
        Log.i("LoaderIssueDetails", "onForceLoad");
    }

    @Override
    public void onCanceled(DetailedIssue data) {
        super.onCanceled(data);
        Log.i("LoaderIssueDetails", "onCanceled");
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        Log.i("LoaderIssueDetails", "onStartLoading");
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        Log.i("LoaderIssueDetails", "onStopLoading");
    }

    @Override
    protected void onAbandon() {
        super.onAbandon();
        Log.i("LoaderIssueDetails", "onAbandon");
    }

    @Override
    protected void onReset() {
        super.onReset();
        Log.i("LoaderIssueDetails", "onReset");
    }
}
