package com.onbts.ITSMobile.UI.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.UI.fragments.base.BaseFragment;
import com.onbts.ITSMobile.adapters.HistoryAdapter;
import com.onbts.ITSMobile.model.HistoryModel;
import com.onbts.ITSMobile.model.base.Model;
import com.onbts.ITSMobile.services.DBRequest;
import com.onbts.ITSMobile.services.ServiceDataBase;

import java.util.ArrayList;

/**
 * Created by tigre on 01.06.14.
 */
public class HistoryFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private HistoryAdapter adapter;
    private ArrayList<HistoryModel> models;
    private View progressView;

    private long issueId;

    public void setIssueId(long issueId) {
        this.issueId = issueId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        View v = inflater.inflate(R.layout.frag_history, container, false);
        ExpandableListView list = (ExpandableListView) v.findViewById(R.id.lvIssueHistory);
        setHasOptionsMenu(true);
        list.setOnItemClickListener(this);
        adapter = new HistoryAdapter(models, inflater);
        list.setAdapter(adapter);
        progressView = v.findViewById(R.id.view_progress_bar);
        progressView.setVisibility(View.VISIBLE);
        Intent intent = mNavigator.onGetDB();
        intent.putExtra("user", mNavigator.onGetUser());
        intent.putExtra("issueID", issueId);
        intent.putExtra(ServiceDataBase.KEY_REQUEST, new DBRequest(DBRequest.DBRequestType.HISTORY_ISSUE));
        mNavigator.onSendDBRequest(intent);
        int width = getResources().getDisplayMetrics().widthPixels;
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            list
                    .setIndicatorBounds(width - GetPixelFromDips(50), width - GetPixelFromDips(10));
        } else {
            list.setIndicatorBoundsRelative(width - GetPixelFromDips(50), width
                    - GetPixelFromDips(10));
        }
        return v;
    }

    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    @Override
    public void onHandelDBMessage(DBRequest request) {
        if (request.getType() == DBRequest.DBRequestType.HISTORY_ISSUE) {
            ArrayList<HistoryModel> models = (ArrayList<HistoryModel>) request.getModels();
            if (models != null) {
                this.models = models;
                adapter.setModels(models);
                if (progressView != null)
                    progressView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private BroadcastReceiver dbListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                DBRequest request =  intent
                        .getParcelableExtra(ServiceDataBase.KEY_REQUEST);
                if (request != null) {
                    request.setModels(intent.<Model>getParcelableArrayListExtra(ServiceDataBase.KEY_REQUEST_MODELS));
                    onHandelDBMessage(request);
                }
            } else {
            }

            Log.e("BroadcastReceiver", "intent = " + intent);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        mNavigator.onLoadDetaileIssue(issueId);
        IntentFilter filterDB = new IntentFilter(ServiceDataBase.BROADCAST_ACTION);
        getActivity().registerReceiver(dbListener, filterDB);

    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(dbListener);
    }
}
