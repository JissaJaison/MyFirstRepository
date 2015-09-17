package com.onbts.ITSMobile.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.onbts.ITSMobile.model.base.Model;

import java.util.ArrayList;
import java.util.List;

public class ReturnDateWithActionDialog extends Model{

    public static final Parcelable.Creator<ReturnDateWithActionDialog> CREATOR
            = new Parcelable.Creator<ReturnDateWithActionDialog>() {
        public ReturnDateWithActionDialog createFromParcel(Parcel in) {
            return new ReturnDateWithActionDialog(in);
        }

        public ReturnDateWithActionDialog[] newArray(int size) {
            return new ReturnDateWithActionDialog[size];
        }
    };

    private String note;
    private int idPanel;
    private List<SpinnerNameAndId> listSpinner;
    private ArrayList<FileModel> listPath;

    public ReturnDateWithActionDialog(int idAction, String note, List<SpinnerNameAndId> listSpinner,
                                      ArrayList<FileModel> listPath) {
        this.idPanel = idAction;
        this.listSpinner = listSpinner;
        this.note = note;
        this.listPath = listPath;
    }

    public ReturnDateWithActionDialog(Parcel in) {
        idPanel = in.readInt();
        note = in.readString();
        if (listSpinner == null) {
            listSpinner = new ArrayList<>();
        }
        in.readTypedList(listSpinner, SpinnerNameAndId.CREATOR);

        if (listPath == null) {
            listPath = new ArrayList<>();
        }
        in.readTypedList(listPath, FileModel.CREATOR);

    }

    public List<SpinnerNameAndId> getListSpinner(){
    	return listSpinner;
    }
    
    public String getNote(){
    	return note;
    }
    
    public ArrayList<FileModel> getListPath(){
	return listPath;
    }

    public int getIdPanel() {
        return idPanel;
    }

    @Override

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idPanel);
        dest.writeString(note);
        dest.writeTypedList(listSpinner);
        dest.writeTypedList(listPath);
    }
}
