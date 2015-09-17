package com.onbts.ITSMobile.panels;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.UI.dialogs.action.ActionDialog;
import com.onbts.ITSMobile.adapters.SpinnerAdapter;
import com.onbts.ITSMobile.model.DetailedIssue;
import com.onbts.ITSMobile.model.ReturnDateWithActionDialog;
import com.onbts.ITSMobile.model.SpinnerNameAndId;
import com.onbts.ITSMobile.model.UserModel;
import com.onbts.ITSMobile.services.DbService;

import java.util.ArrayList;
import java.util.List;

public class PanelActionWithTwoSpinner extends PanelAction {

    private int positionFirstSpinner, positionTwoSpinner;
    private String nameFirstDesc, idFirst, nameFirstTable, nameTwoDesc, idTwo, nameTwoTable;
    private List<SpinnerNameAndId> listFirstSpinner = new ArrayList<SpinnerNameAndId>();
    private List<SpinnerNameAndId> listTwoSpinner = new ArrayList<SpinnerNameAndId>();

    public PanelActionWithTwoSpinner(int idPanel, String name) {
        super(idPanel, name);
    }

    @Override
    public ReturnDateWithActionDialog getData() {
        return new ReturnDateWithActionDialog(idPanel, null, getList(), null);
    }

    public List<SpinnerNameAndId> getList() {
        List<SpinnerNameAndId> returnList = new ArrayList<SpinnerNameAndId>();
        returnList.add(new SpinnerNameAndId(listFirstSpinner.get(positionFirstSpinner).getId(),
                listFirstSpinner.get(positionFirstSpinner).getNameNote(),
                listFirstSpinner.get(positionFirstSpinner).getNameTable()));
        returnList.add(new SpinnerNameAndId(listTwoSpinner.get(positionTwoSpinner).getId(),
                listTwoSpinner.get(positionTwoSpinner).getNameNote(),
                listTwoSpinner.get(positionTwoSpinner).getNameTable()));
        return returnList;
    }

    @Override
    public void setData(String nameFirstDesc, String idFirst, String nameFirstTable,
                        String nameTwoDesc, String idTwo, String nameTwoTable, String note, String filePatch) {
        this.nameFirstDesc = nameFirstDesc;
        this.idFirst = idFirst;
        this.nameFirstTable = nameFirstTable;
        this.nameTwoDesc = nameTwoDesc;
        this.idTwo = idTwo;
        this.nameTwoTable = nameTwoTable;
    }


    @Override
    public View onCreateView(Context context, LayoutInflater inflater, ActionDialog actionDialog, UserModel user, DetailedIssue details) {
        view = inflater.inflate(R.layout.panel_two_spinner, null);
        listFirstSpinner = DbService.getInstance(context).getListNameAndId(nameFirstDesc, idFirst, nameFirstTable);
        final SpinnerAdapter spinnerAdapterFirst = new SpinnerAdapter(context, listFirstSpinner);
        Spinner spinnerFirstDepartment = (Spinner) view.findViewById(R.id.panelFirstSpinner);
        spinnerFirstDepartment.setAdapter(spinnerAdapterFirst);
        spinnerFirstDepartment.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                positionFirstSpinner = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        listTwoSpinner = DbService.getInstance(context).getListNameAndId(nameTwoDesc, idTwo, nameTwoTable);
        final SpinnerAdapter spinnerAdapterTwo = new SpinnerAdapter(context, listTwoSpinner);
        Spinner spinnerUser = (Spinner) view.findViewById(R.id.panelTwoSpinner);
        spinnerUser.setAdapter(spinnerAdapterTwo);
        spinnerUser.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                positionTwoSpinner = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        return view;
    }

}
