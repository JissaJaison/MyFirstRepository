package com.onbts.ITSMobile.panels;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup;
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

/**
 * НЕДОРАБОТАНО?
 */
public class PanelReassign extends PanelAction {

    private int positionFirstSpinner, positionTwoSpinner;
    private String nameFirstDesc, idFirst, nameFirstTable, nameTwoDesc, idTwo,
            nameTwoTable;
    private List<SpinnerNameAndId> listFirstSpinner = new ArrayList<SpinnerNameAndId>();
    private List<SpinnerNameAndId> listTwoSpinner = new ArrayList<SpinnerNameAndId>();
    private Spinner spinnerUser, spinnerDepartment;
    private RadioGroup radiogroup;

    public PanelReassign(int idPanel, String name) {
        super(idPanel, name);
    }

    @Override
    public ReturnDateWithActionDialog getData() {
        return new ReturnDateWithActionDialog(idPanel, null, getList(), null);
    }

    public List<SpinnerNameAndId> getList() {

        List<SpinnerNameAndId> returnList = null;
        switch (radiogroup.getCheckedRadioButtonId()) {

            case R.id.radioButtonSpecificUser:
                returnList = new ArrayList<SpinnerNameAndId>();
                returnList.add(new SpinnerNameAndId(listTwoSpinner.get(
                        positionTwoSpinner).getId(), listTwoSpinner.get(
                        positionTwoSpinner).getNameNote(), listTwoSpinner.get(
                        positionTwoSpinner).getNameTable()));

            case R.id.radioButtonAnyUser:
                if (returnList == null)
                    returnList = new ArrayList<SpinnerNameAndId>();

                returnList.add(new SpinnerNameAndId(listFirstSpinner.get(
                        positionFirstSpinner).getId(), listFirstSpinner.get(
                        positionFirstSpinner).getNameNote(), listFirstSpinner.get(
                        positionFirstSpinner).getNameTable()));
                return returnList;
            default:
                return null;
        }
    }

    @Override
    public void setData(String nameFirstDesc, String idFirst,
                        String nameFirstTable, String nameTwoDesc, String idTwo,
                        String nameTwoTable, String note, String filePatch) {
        this.nameFirstDesc = nameFirstDesc;
        this.idFirst = idFirst;
        this.nameFirstTable = nameFirstTable;
        this.nameTwoDesc = nameTwoDesc;
        this.idTwo = idTwo;
        this.nameTwoTable = nameTwoTable;
    }

    @Override
    public View onCreateView(Context context, LayoutInflater inflater,
                             ActionDialog actionDialog, UserModel user, DetailedIssue details) {
        view = inflater.inflate(R.layout.panel_reassign, null);
        listFirstSpinner = DbService.getInstance(context).getListNameAndId(
                nameFirstDesc, idFirst, nameFirstTable);
        final SpinnerAdapter spinnerAdapterFirst = new SpinnerAdapter(context, listFirstSpinner);
        spinnerDepartment = (Spinner) view.findViewById(R.id.spinnerReassignDepartment);
        spinnerDepartment.setAdapter(spinnerAdapterFirst);
        spinnerDepartment.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                positionFirstSpinner = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        listTwoSpinner = DbService.getInstance(context).getListNameAndId(
                nameTwoDesc, idTwo, nameTwoTable);
        final SpinnerAdapter spinnerAdapterTwo = new SpinnerAdapter(context, listTwoSpinner);
        spinnerUser = (Spinner) view.findViewById(R.id.spinnerReassignUser);
        spinnerUser.setAdapter(spinnerAdapterTwo);
        spinnerUser.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                positionTwoSpinner = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        radiogroup = (RadioGroup) view.findViewById(R.id.radioGroupReassign);
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButtonUnassigned:
                        spinnerUser.setEnabled(false);
                        spinnerDepartment.setEnabled(false);
                        break;
                    case R.id.radioButtonAnyUser:
                        spinnerUser.setEnabled(false);
                        spinnerDepartment.setEnabled(true);
                        break;
                    case R.id.radioButtonSpecificUser:
                        spinnerUser.setEnabled(true);
                        spinnerDepartment.setEnabled(true);
                        break;

                    default:
                        break;
                }

            }
        });
        return view;
    }

}
