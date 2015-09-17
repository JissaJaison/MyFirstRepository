package com.onbts.ITSMobile.panels;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;

import com.onbts.ITSMobile.UI.dialogs.action.ActionDialog;
import com.onbts.ITSMobile.model.DetailedIssue;
import com.onbts.ITSMobile.model.ReturnDateWithActionDialog;
import com.onbts.ITSMobile.model.UserModel;
import com.onbts.ITSMobile.model.base.Model;

/**
 * Created by tigre on 28.03.14.
 */
public class PanelAction extends Model {
    public static final Parcelable.Creator<PanelAction> CREATOR
            = new Parcelable.Creator<PanelAction>() {
        public PanelAction createFromParcel(Parcel in) {
            return new PanelAction(in);
        }

        public PanelAction[] newArray(int size) {
            return new PanelAction[size];
        }
    };
    protected View view;
    protected String name;
    protected int idPanel;

    public PanelAction(int idPanel, String name) {
        this.name = name;
        this.idPanel = idPanel;
    }

    ;

    /**
     * recreate object from parcel
     */
    protected PanelAction(Parcel in) {
        this.idPanel = in.readInt();
        this.name = in.readString();

    }

    public View onCreateView(Context context, LayoutInflater inflater, ActionDialog actionDialog, UserModel user, DetailedIssue details) {
        return null;
    }

    public ReturnDateWithActionDialog getData() {
        return null;
    }

    public void setData(String nameFirstDesc, String idFirst, String nameFirstTable,
                        String nameTwoDesc, String idTwo, String nameTwo, String note, String filePatch) {
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdPanel() {
        return idPanel;
    }

    public void setId(int id) {
        this.idPanel = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idPanel);
        dest.writeString(name);
    }
}
