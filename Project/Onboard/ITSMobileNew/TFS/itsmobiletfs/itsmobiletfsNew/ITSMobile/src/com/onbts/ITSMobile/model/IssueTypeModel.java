package com.onbts.ITSMobile.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.onbts.ITSMobile.model.base.Model;

/**
 * Created by tigre on 14.04.14.
 */
public class IssueTypeModel extends Model {

    public static final Parcelable.Creator<IssueTypeModel> CREATOR
            = new Parcelable.Creator<IssueTypeModel>() {
        public IssueTypeModel createFromParcel(Parcel in) {
            return new IssueTypeModel(in);
        }

        public IssueTypeModel[] newArray(int size) {
            return new IssueTypeModel[size];
        }
    };

    private long issueTypeID;
    private String issueTypeDesc;
    private long priorityID;
    private String priorityDesc;
    private long issueClassID;
    private long issueGroupID;
    private long IssueCategoryID;

    private long assigneeUserID;
    private boolean assigneeAnyUser;
    private boolean guestServiceIssue;
    private boolean requiresGuestCallback;
    private boolean requiresLocationOwnerAction;

    public IssueTypeModel() {

    }

    public IssueTypeModel(Parcel parcel) {
        issueTypeID = parcel.readLong();
        issueTypeDesc = parcel.readString();
        priorityDesc = parcel.readString();
        priorityID = parcel.readLong();
        issueClassID = parcel.readLong();
        issueGroupID = parcel.readLong();
        assigneeUserID = parcel.readLong();
        assigneeAnyUser = parcel.readByte() == 1;
        guestServiceIssue = parcel.readByte() == 1;
        requiresGuestCallback = parcel.readByte() == 1;
        requiresLocationOwnerAction = parcel.readByte() == 1;

    }

    public long getIssueTypeID() {
        return issueTypeID;
    }

    public void setIssueTypeID(long issueTypeID) {
        this.issueTypeID = issueTypeID;
    }

    public String getIssueTypeDesc() {
        return issueTypeDesc;
    }

    public void setIssueTypeDesc(String issueTypeDesc) {
        this.issueTypeDesc = issueTypeDesc;
    }

    public long getPriorityID() {
        return priorityID;
    }

    public void setPriorityID(long priorityID) {
        this.priorityID = priorityID;
    }

    public long getIssueClassID() {
        return issueClassID;
    }

    public void setIssueClassID(long issueClassID) {
        this.issueClassID = issueClassID;
    }

    public long getIssueGroupID() {
        return issueGroupID;
    }

    public void setIssueGroupID(long issueGroupID) {
        this.issueGroupID = issueGroupID;
    }

    public long getIssueCategoryID() {
        return IssueCategoryID;
    }

    public void setIssueCategoryID(long issueCategoryID) {
        IssueCategoryID = issueCategoryID;
    }


    public long getAssigneeUserID() {
        return assigneeUserID;
    }

    public void setAssigneeUserID(long assigneeUserID) {
        this.assigneeUserID = assigneeUserID;
    }

    public boolean isAssigneeAnyUser() {
        return assigneeAnyUser;
    }

    public void setAssigneeAnyUser(boolean assigneeAnyUser) {
        this.assigneeAnyUser = assigneeAnyUser;
    }

    public boolean isGuestServiceIssue() {
        return guestServiceIssue;
    }

    public void setGuestServiceIssue(boolean guestServiceIssue) {
        this.guestServiceIssue = guestServiceIssue;
    }

    public boolean isRequiresGuestCallback() {
        return requiresGuestCallback;
    }

    public void setRequiresGuestCallback(boolean requiresGuestCallback) {
        this.requiresGuestCallback = requiresGuestCallback;
    }

    public boolean isRequiresLocationOwnerAction() {
        return requiresLocationOwnerAction;
    }

    public void setRequiresLocationOwnerAction(boolean requiresLocationOwnerAction) {
        this.requiresLocationOwnerAction = requiresLocationOwnerAction;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getPriorityDesc() {
        return priorityDesc;
    }

    public void setPriorityDesc(String priorityDesc) {
        this.priorityDesc = priorityDesc;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(issueTypeID);
        dest.writeString(issueTypeDesc);
        dest.writeString(priorityDesc);
        dest.writeLong(priorityID);
        dest.writeLong(issueClassID);
        dest.writeLong(issueGroupID);
        dest.writeLong(assigneeUserID);
        dest.writeByte(assigneeAnyUser ? (byte) 1 : 0);
        dest.writeByte(guestServiceIssue ? (byte) 1 : 0);
        dest.writeByte(requiresGuestCallback ? (byte) 1 : 0);
        dest.writeByte(requiresLocationOwnerAction ? (byte) 1 : 0);
    }
}
