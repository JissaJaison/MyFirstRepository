package com.onbts.ITSMobile.UI.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.UI.dialogs.action.ActionDialog;
import com.onbts.ITSMobile.UI.fragments.base.BaseFragment;
import com.onbts.ITSMobile.model.ActionIssue;
import com.onbts.ITSMobile.model.DetailedIssue;
import com.onbts.ITSMobile.model.ReturnDateWithActionDialog;
import com.onbts.ITSMobile.services.DBRequest;
import com.onbts.ITSMobile.services.ServiceDataBase;

import java.util.ArrayList;

/**
 * Created by tigre on 02.06.14.
 */
public class ActionsFragment extends BaseFragment implements AdapterView.OnItemClickListener, ActionDialog.ActionDialogListener, AdapterView.OnItemSelectedListener {

    private DetailedIssue detailed;
    private LayoutInflater inflater;

    public void setDetailed(DetailedIssue detailed) {
        this.detailed = detailed;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        setRetainInstance(true);
        View v = inflater.inflate(R.layout.frag_actions, container, false);
        GridView grid = (GridView) v.findViewById(R.id.grid);
        grid.setAdapter(new ActionAdapter());
        grid.setOnItemSelectedListener(this);
        grid.setOnItemClickListener(this);
        return v;
    }

    @Override
    public void onHandelDBMessage(DBRequest request) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        for (ActionIssue issue : detailed.getActionIssues()) {
            if (id == issue.getId()) {
                ActionDialog dialog = new ActionDialog();
                dialog.setActionDialogListener(this);
                dialog.setIssue(issue);
                dialog.setUser(mNavigator.onGetUser());
                dialog.setDetails(detailed);
                dialog.show(getFragmentManager(), "dialog_action");

            }
        }
    }

    @Override
    public void onActionDataConfirm(ArrayList<ReturnDateWithActionDialog> data, long idAction, String actionCode, long prevActionID, long nextActionID, boolean keep) {
        if (data == null || data.size() == 0 || detailed == null) {
            Toast.makeText(getActivity(), "Something is wrong!", Toast.LENGTH_LONG).show();
            return;
        }
        onAddActions(detailed, data, idAction, actionCode, prevActionID, nextActionID, keep);
    }

    @Override
    public void onClose() {

    }

    public void onAddActions(DetailedIssue details, ArrayList<ReturnDateWithActionDialog> data, long idAction,
                             String actionCode, long prevActionID,
                             long nextActionID, boolean keep) {
        Intent intent = mNavigator.onGetDB();
        intent.putExtra("user", mNavigator.onGetUser());
        intent.putExtra("issue", details);
        intent.putParcelableArrayListExtra("data", data);
        intent.putExtra("idAction", idAction);
        intent.putExtra("ActionCode", actionCode);
        intent.putExtra("nextActionID", nextActionID);
        intent.putExtra("prevActionID", prevActionID);
        intent.putExtra("keep", keep);
        intent.putExtra(ServiceDataBase.KEY_REQUEST, new DBRequest(DBRequest.DBRequestType.INSERT_ISSUE_TRACK));
        mNavigator.onSendDBRequest(intent);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class ActionAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return detailed != null && detailed.getActionIssues() != null && detailed.getActionIssues().size() > 2 ?
                    detailed.getActionIssues().size() - 2 : 0;
        }

        @Override
        public Object getItem(int position) {
            return detailed != null && detailed.getActionIssues() != null && detailed.getActionIssues().size()
                    > position + 2 ? detailed.getActionIssues().get(position + 2) : null;
        }

        @Override
        public long getItemId(int position) {
            return detailed != null && detailed.getActionIssues() != null && detailed.getActionIssues().size()
                    > position + 2 ? detailed.getActionIssues().get(position + 2).getId() : -1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(R.layout.item_action, parent, false);
            ((TextView) convertView.findViewById(R.id.title)).setText(detailed.getActionIssues().get(position + 2)
                    .getName());
            return convertView;
        }
    }

}
