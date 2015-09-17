package com.onbts.ITSMobile.panels;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.UI.dialogs.action.ActionDialog;
import com.onbts.ITSMobile.model.DetailedIssue;
import com.onbts.ITSMobile.model.FileModel;
import com.onbts.ITSMobile.model.ReturnDateWithActionDialog;
import com.onbts.ITSMobile.model.UserModel;

import java.io.File;
import java.util.ArrayList;

public class PanelFileAttachment extends PanelAction {

    private ActionDialog actionDialog;
    private ArrayList<FileModel> listPath = new ArrayList<FileModel>();
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;

    public PanelFileAttachment(int idPanel, String name) {
        super(idPanel, name);
    }

    @Override
    public ReturnDateWithActionDialog getData() {
        return new ReturnDateWithActionDialog(idPanel, null, null, getListPath());
    }

    @Override
    public void setData(String nameFirstDesc, String idFirst, String nameFirstTable,
                        String nameTwoDesc, String idTwo, String nameTwoTable, String note, String filePath) {
        if (filePath != null) {
            File file = new File(filePath);
            FileModel fm = new FileModel();
            fm.setPath(filePath);
            fm.setFilesize(file.length());
            fm.setFilename(checkLastSymbol(filePath));
            fm.setExtension(filePath.substring(filePath.lastIndexOf("."), filePath.length()));
            listPath.add(fm);
            updateText(filePath, fm.isImage());
        }
    }

    public void updateText(String filePath, boolean image) {
        ViewGroup viewGroupChild = (LinearLayout) view.findViewById(R.id.linearLayoutAttachFile);
        LinearLayout childLayout = (LinearLayout) LayoutInflater.from(actionDialog.getActivity())
                .inflate(R.layout.item_attach_file, null);
        ImageView imageView = (ImageView) childLayout.findViewById(R.id.imageViewAttachFile);
        if (image) {
            ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(actionDialog.getActivity());
            imageLoader.init(config);
            options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_empty)
                    .showImageOnFail(R.drawable.ic_empty)
                    .resetViewBeforeLoading(true)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(true).build();
            imageLoader.displayImage("file://" + filePath, imageView, options);
        }
        TextView text = (TextView) childLayout.findViewById(R.id.textViewAttachFilePath);
        if (filePath.contains("/")) {
            filePath = checkLastSymbol(filePath);
        }
        text.setText(filePath);
        viewGroupChild.addView(childLayout);
    }

    public String checkLastSymbol(String text) {
        if (text.lastIndexOf("/") == text.length()) {
            text = text.substring(0, text.length() - 1);
            checkLastSymbol(text);
        } else
            text = text.substring(text.lastIndexOf("/") + 1);
        return text;
    }

    public ArrayList<FileModel> getListPath() {
        return listPath;
    }

    public void setListPath(ArrayList<FileModel> listPath) {
        this.listPath = listPath;
        for (int i = 0; i < listPath.size(); i++) {
            updateText(listPath.get(i).getPath(), listPath.get(i).isImage());
        }
    }

    @Override
    public View onCreateView(Context context, LayoutInflater inflater, ActionDialog actionDialog, UserModel user, DetailedIssue details) {
        this.actionDialog = actionDialog;
        view = inflater.inflate(R.layout.panel_attach_file, null);
        TextView sizeAttachFile = (TextView) view.findViewById(R.id.textViewSizeAttachFile);
        sizeAttachFile.setText("Maximum file size " + actionDialog.getUser().getAttachFileLength() / 1000 + "Kb");
        view.findViewById(R.id.buttonAttachFile).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startIntentFiles(ActionDialog.ATTACH_FILE_REQUEST_CODE);
            }
        });
        view.findViewById(R.id.buttonAttachImage).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startIntentFiles(ActionDialog.ATTACH_IMAGE_REQUEST_CODE);
            }
        });
        view.findViewById(R.id.buttonAttachMakePhoto).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startIntentFiles(ActionDialog.MAKE_PHOTO_REQUEST_CODE);
            }
        });
        return view;
    }

    private void startIntentFiles(int attach) {
        actionDialog.onStartCamera(this, attach);
    }
}
