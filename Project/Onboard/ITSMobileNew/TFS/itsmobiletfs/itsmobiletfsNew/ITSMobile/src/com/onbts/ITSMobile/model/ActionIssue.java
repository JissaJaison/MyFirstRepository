package com.onbts.ITSMobile.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.onbts.ITSMobile.model.base.Model;
import com.onbts.ITSMobile.panels.PanelAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tigre on 26.03.14.
 */
public class ActionIssue extends Model {
    private final String name;
    private final long id;
    private final long prevActionID;
    private final long nextActionID;
    private final String code;
    private List<PanelAction> panelsList;

    public long getPrevActionID() {
        return prevActionID;
    }

    public long getNextActionID() {
        return nextActionID;
    }

    private boolean keepAssignee;

    public boolean isKeepAssignee() {
        return keepAssignee;
    }

    public void setKeepAssignee(boolean keepAssignee) {
        this.keepAssignee = keepAssignee;
    }

    public List<PanelAction> getPanelsList() {
        return panelsList;
    }

    public void setPanelsList(List<PanelAction> panelsList) {
        this.panelsList = panelsList;
    }

    public String getCode() {
        return code;
    }

    public ActionIssue(String name, long id, long prevActionID, long nextActionID, String code, boolean keep) {
        this.name = name;
        this.id = id;
        this.prevActionID = prevActionID;
        this.nextActionID = nextActionID;
        this.code = code;

        this.keepAssignee = keep;

    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    /**
     * recreate object from parcel
     */
    private ActionIssue(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.code = in.readString();
        this.prevActionID = in.readLong();
        this.nextActionID = in.readLong();
        this.keepAssignee = in.readByte() == 1;
        if (panelsList == null) {
            panelsList = new ArrayList<PanelAction>();
        }
        in.readTypedList(panelsList, PanelAction.CREATOR);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(code);
        dest.writeLong(prevActionID);
        dest.writeLong(nextActionID);
        dest.writeByte(keepAssignee? (byte)1:0);
        dest.writeTypedList(panelsList);

    }

    public static final Parcelable.Creator<ActionIssue> CREATOR
            = new Parcelable.Creator<ActionIssue>() {
        public ActionIssue createFromParcel(Parcel in) {
            return new ActionIssue(in);
        }

        public ActionIssue[] newArray(int size) {
            return new ActionIssue[size];
        }
    };


}
