package com.onbts.ITSMobile.UI.dialogs.action;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.UI.dialogs.base.BaseDialog;
import com.onbts.ITSMobile.model.ActionIssue;
import com.onbts.ITSMobile.model.DetailedIssue;

import com.onbts.ITSMobile.model.ReturnDateWithActionDialog;
import com.onbts.ITSMobile.model.FileModel;
import com.onbts.ITSMobile.model.UserModel;
import com.onbts.ITSMobile.panels.PanelAction;
import com.onbts.ITSMobile.panels.PanelFileAttachment;
import com.onbts.ITSMobile.services.DbService;
import com.onbts.ITSMobile.util.Files;

import com.onbts.ITSMobile.model.ReturnDateWithActionDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tigre on 26.03.14.
 */
public class ActionDialog extends BaseDialog {

    public static final int ATTACH_FILE_REQUEST_CODE = 1;
    public static final int ATTACH_IMAGE_REQUEST_CODE = 2;
    public static final int MAKE_PHOTO_REQUEST_CODE = 3;

    private ActionIssue actionIssue;
    private UserModel user;
    private DetailedIssue details;
    private ActionDialogListener actionDialogListener;

    private PanelAction panelAction;
    private Uri uri;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (panelAction == null)
            return;
        if (!(resultCode == Activity.RESULT_OK && requestCode == 3)) {
            if (data == null) {
                return;
            }
            if (data.getData() == null) {
                return;
            }
        }
        String filePath = null;

        switch (requestCode) {
            case ATTACH_FILE_REQUEST_CODE: {
                if ("file".equalsIgnoreCase(data.getData().getScheme())) {
                    if (Files.checkFiles(data.getData())) {
                        filePath = data.getData().getPath();
                    } else
                        showDialogError("Invalid file format! Select please .doc .docx .pdf");
                } else
                    showDialogError("Invalid file format! Select please .doc .docx .pdf");
                break;
            }
            case ATTACH_IMAGE_REQUEST_CODE: {
                if ("content".equalsIgnoreCase(data.getData().getScheme())) {
                    final String[] projection = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(data.getData(), projection, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        final int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        if (!Files.checkImage(cursor.getString(column_index))) {
                            showDialogError("Invalid file format! Select please .jpg .jpeg .png .gif");
                        } else
                            filePath = cursor.getString(column_index);
                        cursor.close();
                        //TODO: add ErrorDialog?
                    } else /* add ErrorDialog*/ return;
                } else
                    showDialogError("Invalid file format! Select please .jpg .jpeg .png .gif");
                break;
            }
            case MAKE_PHOTO_REQUEST_CODE: {
                try {
                    filePath = String.valueOf(uri);
                } catch (Exception e) {
                }
                filePath = filePath.replace("file://", "");
                break;
            }
        }

        if (filePath != null)
            if (!Files.chechFileSize(user, filePath) && !(requestCode == 3)) {
                showDialogError("Invalid file size!");
                return;
            }

        List<FileModel> listPath = panelAction.getData().getListPath();
        for (int i = 0; i < listPath.size(); i++) {
            if (listPath.get(i).getPath().equals(filePath)) {
                showDialogError("File already exists!");
                return;
            }
        }

        panelAction.setData(null, null, null, null, null, null, null, filePath);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("actions", actionIssue);
        outState.putParcelable("user", user);
        outState.putParcelable("details", details);
        if (panelAction!=null) {
            outState.putParcelable("panel", panelAction);
            outState.putParcelableArrayList("ListFileModel", ((PanelFileAttachment) panelAction).getListPath());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void onStartCamera(PanelAction panelAction, int attach) {
        this.panelAction = panelAction;
        Intent intent = null;
        switch (attach) {
            case ATTACH_FILE_REQUEST_CODE:
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                break;
            case ATTACH_IMAGE_REQUEST_CODE:
                intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                break;
            case MAKE_PHOTO_REQUEST_CODE:
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    Toast.makeText(getActivity(), "SD card not available", Toast.LENGTH_LONG).show();
                    return;
                }
                File path = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getActivity().getPackageName());
                if (!path.mkdirs()) {
                    path.mkdirs();
                }
                if (!path.exists()) {
                    Toast.makeText(getActivity(), "Can not create folder.", Toast.LENGTH_LONG).show();
                    return;
                }
                String photoName = String.valueOf(System.currentTimeMillis());
                File newFile = new File(path.getPath() + File.separator + photoName + ".jpg");
                uri = Uri.fromFile(newFile);
                Log.i("ActionDialog", "uri = " + uri);
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                break;
        }

        if (intent == null)
            return;

        try {
            startActivityForResult(/*Intent.createChooser(*/intent,/* "tttt"),*/ attach);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "No handler App for this type of file.", Toast.LENGTH_SHORT).show();
        }


    }

    public void setActionDialogListener(
            ActionDialogListener actionDialogListener) {
        this.actionDialogListener = actionDialogListener;
    }

    public void setIssue(ActionIssue issue) {
        this.actionIssue = issue;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public UserModel getUser() {
        return user;
    }



    @Override
    protected void onAddCreateView(Bundle savedInstanceState, LayoutInflater inflater, ViewGroup viewGroup) {
        if (savedInstanceState == null) {
            actionIssue.setPanelsList(DbService.getInstance(getActivity()).getActionPanels(actionIssue.getId()));
        } else {
            actionIssue = savedInstanceState.getParcelable("actions");
            panelAction = savedInstanceState.getParcelable("panel");
            details = savedInstanceState.getParcelable("details");
            user = savedInstanceState.getParcelable("user");
            ((TextView) viewGroup.findViewById(R.id.tvAlertTitle)).setText(onSetTitle());
        }
        if (actionIssue.getPanelsList() != null) {
            ViewGroup viewGroupChild = (LinearLayout) viewGroup.findViewById(R.id.linearLayoutChild);
            for (PanelAction panelAction : actionIssue.getPanelsList()) {
                View view = panelAction.onCreateView(getActivity(), inflater, this, user, details);
                View viewSeparator = null;
                if (view != null) {
                    if (viewGroupChild != null) {
                        viewSeparator = inflater.inflate(R.layout.separator, null);
                        if (panelAction.getIdPanel() == 7) {
                            if (viewGroupChild.getChildCount() < 1) {
                                viewGroupChild.addView(view, 0);
                            } else {
                                viewGroupChild.addView(view, 0);
                                viewGroupChild.addView(viewSeparator, 1);
                            }
                        } else {
                            if (viewGroupChild.getChildCount() < 1) {
                                viewGroupChild.addView(view);
                            } else {
                                viewGroupChild.addView(viewSeparator);
                                viewGroupChild.addView(view);
                            }
                        }
//                        panelAction.setView(viewSeparator);

                    }
                }
            }
        } else {
            dismiss();
        }
        if (savedInstanceState != null && panelAction != null && panelAction instanceof  PanelFileAttachment){
            ArrayList<FileModel> fm = savedInstanceState.getParcelableArrayList("ListFileModel");
            ((PanelFileAttachment)panelAction).setListPath(fm);
        }
    }

    @Override
    protected void onClickPosotiveButton() {
        if (actionDialogListener != null) {
            ArrayList<ReturnDateWithActionDialog> data = new ArrayList<>();
            for (PanelAction panel : actionIssue.getPanelsList()) {
                if (panel.getIdPanel() == 7) {
                    if (!TextUtils.isEmpty(panel.getData().getNote())) {
                    } else {
                        Toast.makeText(getActivity(), "need notes!", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                if (panel.getIdPanel() == 13) {
                    ReturnDateWithActionDialog fileData = panel.getData();

             /*       OnboarD - Some Actions we do not want to force the file, but give an option.
                        if(fileData == null || fileData.getListPath() == null || fileData.getListPath().size() == 0) {
                        Toast.makeText(getActivity(), "No files attached", Toast.LENGTH_LONG).show();
                        return;
                    }


                    boolean validFile = false;
                    for(FileModel fileModel : fileData.getListPath()){
                        if(!TextUtils.isEmpty(fileModel.getPath())) {
                            validFile = true;
                            break;
                        }
                    }
                    if(!validFile){
                        Toast.makeText(getActivity(), "No files attached", Toast.LENGTH_LONG).show();
                        return;
                    }

                    */


                }

                data.add(panel.getData());

            }
            actionDialogListener.onActionDataConfirm(data, actionIssue.getId(),
                    actionIssue.getCode(), actionIssue.getPrevActionID(),
                    actionIssue.getNextActionID(), actionIssue.isKeepAssignee());
        }
        dismiss();
    }

    @Override
    protected void onClickNegativeButton() {
        if (actionDialogListener != null)
            actionDialogListener.onClose();
        dismiss();
    }

    @Override
    protected String onSetTitle() {
        if (actionIssue != null)
            return actionIssue.getName();
        return "";
    }

    public DetailedIssue getDetails() {
        return details;
    }

    public void setDetails(DetailedIssue details) {
        this.details = details;
    }

    public interface ActionDialogListener {
        void onClose();

//        void onActionTextConfirm(String texts);

        void onActionDataConfirm(ArrayList<ReturnDateWithActionDialog> data, long idAction,String actionCode, long prevActionID,
                                 long nextActionID, boolean keep);
    }

    @Override
    protected void showDialogError(String message) {
        Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.warning);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.dialog_btn_ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();

    }
}
