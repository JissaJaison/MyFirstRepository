package com.onbts.ITSMobile.UI.dialogs.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onbts.ITSMobile.R;

/**
 * Created by tigre on 26.03.14.
 */
public abstract class BaseDialog extends DialogFragment {
    protected abstract void onAddCreateView(Bundle savedInstanceState, LayoutInflater inflater, ViewGroup viewGroup);

    protected abstract String onSetTitle();



    public Dialog onCreateDialog(Bundle savedInstanceState) {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        setStyle(android.app.DialogFragment.STYLE_NORMAL,
                 android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(
                R.layout.item_alert_header, null);
        ((TextView) viewGroup.findViewById(R.id.tvAlertTitle))
                .setText(onSetTitle());
        onAddCreateView(savedInstanceState, inflater, viewGroup);
        builder.setView(viewGroup);
        builder.setPositiveButton(R.string.dialog_btn_ok, null).setNegativeButton(R.string.dialog_btn_cancel, null);
        return builder.create();
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setOnDismissListener(null);
        super.onDestroyView();
    }

    protected abstract void onClickPosotiveButton();

    protected abstract void onClickNegativeButton();
    
    protected abstract void showDialogError(String message);

    @Override
    public void onStart() {
        super.onStart();


        final AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            View p = d.getButton(AlertDialog.BUTTON_POSITIVE);
            if (p != null)
                p.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickPosotiveButton();
                    }
                });

            View n = d.getButton(AlertDialog.BUTTON_NEGATIVE);
            if (n != null)
                n.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickNegativeButton();
                    }
                });
        }
    }
}