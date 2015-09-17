package com.onbts.ITSMobile.model.issue;

import android.os.Parcel;
import android.os.Parcelable;

import com.onbts.ITSMobile.model.base.Model;

/**
 * Created by tigre on 21.04.14.
 */
public class UpdateIssueModel extends Model {
    public static final Parcelable.Creator<UpdateIssueModel> CREATOR
            = new Parcelable.Creator<UpdateIssueModel>() {
        public UpdateIssueModel createFromParcel(Parcel in) {
            return new UpdateIssueModel(in);
        }

        public UpdateIssueModel[] newArray(int size) {
            return new UpdateIssueModel[size];
        }
    };
    public final boolean open;
    public final boolean favorite;
    public final long issueID;

    public UpdateIssueModel(boolean open, boolean favorite, long issueID) {
        this.open = open;
        this.favorite = favorite;
        this.issueID = issueID;
    }

    public UpdateIssueModel(Parcel in) {
        issueID = in.readLong();
        open = in.readByte() == 1;
        favorite = in.readByte() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(issueID);
        dest.writeByte(open ? (byte) 1 : 0);
        dest.writeByte(favorite ? (byte) 1 : 0);

    }
}
