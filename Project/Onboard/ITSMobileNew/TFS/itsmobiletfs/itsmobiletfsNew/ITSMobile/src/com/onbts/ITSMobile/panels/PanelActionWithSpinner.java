package com.onbts.ITSMobile.panels;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.UI.dialogs.action.ActionDialog;
import com.onbts.ITSMobile.adapters.SpinnerAdapter;
import com.onbts.ITSMobile.model.DetailedIssue;
import com.onbts.ITSMobile.model.IssueClassModel;
import com.onbts.ITSMobile.model.ReturnDateWithActionDialog;
import com.onbts.ITSMobile.model.SpinnerNameAndId;
import com.onbts.ITSMobile.model.UserModel;
import com.onbts.ITSMobile.services.DbService;

import java.util.ArrayList;
import java.util.List;

public class PanelActionWithSpinner extends PanelAction {

    private int positionSpinner;
    private List<SpinnerNameAndId> listSpinner;
    private String nameDesc, id, nameTable;

    public PanelActionWithSpinner(int idPanel, String name) {
        super(idPanel, name);
    }

    @Override
    public ReturnDateWithActionDialog getData() {
        return new ReturnDateWithActionDialog(idPanel, null, getList(), null);
    }

    public List<SpinnerNameAndId> getList() {
        List<SpinnerNameAndId> returnList = new ArrayList<SpinnerNameAndId>();
        returnList.add(new SpinnerNameAndId(listSpinner.get(positionSpinner).getId(),
                listSpinner.get(positionSpinner).getNameNote(),
                listSpinner.get(positionSpinner).getNameTable()));
        return returnList;
    }

    @Override
    public void setData(String nameDesc, String id, String nameTable,
                        String nameTwoDesc, String idTwo, String nameTwoTable, String note, String filePatch) {
        this.nameDesc = nameDesc;
        this.id = id;
        this.nameTable = nameTable;
    }

    @Override
    public View onCreateView(Context context, LayoutInflater inflater, ActionDialog actionDialog, UserModel user, DetailedIssue details) {
        view = inflater.inflate(R.layout.panel_spinner, null);
        TextView tv = (TextView) view.findViewById(R.id.textViewSpinnerPanel);
        tv.setText(nameTable + ":");
        Spinner spinner = (Spinner) view.findViewById(R.id.panelSpinner);

        //TODO del after refactor
        long permissionGroupID = 0, issueClassID = 0;
        for (IssueClassModel icm : user.getIssueClasses()) {
            if (icm.id == details.getIssueClassID()) {
                permissionGroupID = icm.getPermissionses().getPermissionGroupId();
                issueClassID = icm.id;

            }
        }

        switch (idPanel) {
            case 2:
                //HARCODED??? transfer panel
                listSpinner = DbService.getInstance(context).getTransferPositions(user, nameTable, details);
                break;
            case 5:
                listSpinner = DbService.getInstance(context).getRequestInfoPositions(permissionGroupID, issueClassID, nameTable, details.getId(), user.getDepartmentId(), details);
                break;
            default:
                listSpinner = DbService.getInstance(context).getListNameAndId(nameDesc, id, nameTable);
                break;
        }
        final SpinnerAdapter spinnerAdapter = new SpinnerAdapter(context, listSpinner);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                positionSpinner = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        return view;
    }

}
