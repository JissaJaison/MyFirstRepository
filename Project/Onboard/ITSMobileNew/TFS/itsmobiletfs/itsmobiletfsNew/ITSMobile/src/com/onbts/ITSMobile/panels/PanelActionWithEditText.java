package com.onbts.ITSMobile.panels;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.UI.dialogs.action.ActionDialog;
import com.onbts.ITSMobile.model.DetailedIssue;
import com.onbts.ITSMobile.model.ReturnDateWithActionDialog;
import com.onbts.ITSMobile.model.UserModel;

public class PanelActionWithEditText extends PanelAction {

    private EditText etNote;

    public PanelActionWithEditText(int idPanel, String name) {
        super(idPanel, name);
    }

    @Override
    public ReturnDateWithActionDialog getData() {
        return new ReturnDateWithActionDialog(idPanel, etNote.getText().toString(), null, null);
    }

    @Override
    public void setData(String nameFirstDesc, String idFirst, String nameFirstTable,
                        String nameTwoDesc, String idTwo, String nameTwoTable, String note, String filePatch) {
        etNote.setText(note);
    }

    @Override
    public View onCreateView(Context context, LayoutInflater inflater, ActionDialog actionDialog, UserModel user, DetailedIssue details) {
        view = inflater.inflate(R.layout.panel_notes, null);
        etNote = (EditText) view.findViewById(R.id.panelNoteEdit);
        return view;
    }
}
