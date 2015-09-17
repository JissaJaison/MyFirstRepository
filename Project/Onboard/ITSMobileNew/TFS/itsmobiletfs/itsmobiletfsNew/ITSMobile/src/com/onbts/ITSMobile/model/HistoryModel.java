package com.onbts.ITSMobile.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.onbts.ITSMobile.model.base.Model;

/**
 * Created by JLAB on 03.04.2014.
 */

public class HistoryModel extends Model {
//    private long issueTrackID;
//    private String issueTrackGUI;
//    private boolean ignoreDiscrepancy;
//    private long deviceID;
//    private long actionID;
    private String actionDesc;
//    private long priorityID;
    private String priorityDesc;
    private long departmentID;
    private String departmentDesc;
    private long assigneeUserID;
//    private boolean assigneeAnyUser;
//    private int HoursWorked;
//    private boolean billable;
//    private long invoiceNumber;
//    private long partsOrderID;
//    private String causeDesc;
//    private long monetaryValue;
    private String notes;
//    private long autoInsertForActionID;
    private long lastUpdateDate;
    private long lastUpdateUserID;
    private String assignedUser;
    private String updatedByUser;
    private boolean assigneeAnyUser;

    public String getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(String assignedUser) {
        this.assignedUser = assignedUser;
    }

    public String getUpdatedByUser() {
        return updatedByUser;
    }

    public void setUpdatedByUser(String updatedByUser) {
        this.updatedByUser = updatedByUser;
    }



    public HistoryModel(Parcel parcel) {
        readFromParcel(parcel);
    }



    public HistoryModel() {
    }
/*
    public long getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(long invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
*/

    public String getPriorityDesc() {
        return priorityDesc;
    }

    public void setPriorityDesc(String priorityDesc) {
        this.priorityDesc = priorityDesc;
    }

/*
    public long getActionID() {
        return actionID;
    }

    public void setActionID(long actionID) {
        this.actionID = actionID;
    }

    public long getIssueTrackID() {
        return issueTrackID;
    }

    public void setIssueTrackID(long issueTrackID) {
        this.issueTrackID = issueTrackID;
    }

    public String getIssueTrackGUI() {
        return issueTrackGUI;
    }

    public void setIssueTrackGUI(String issueTrackGUI) {
        this.issueTrackGUI = issueTrackGUI;
    }

    public boolean isIgnoreDiscrepancy() {
        return ignoreDiscrepancy;
    }

    public void setIgnoreDiscrepancy(boolean ignoreDiscrepancy) {
        this.ignoreDiscrepancy = ignoreDiscrepancy;
    }

    public long getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(long deviceID) {
        this.deviceID = deviceID;
    }
*/

    public String getActionDesc() {
        return actionDesc;
    }

    public void setActionDesc(String actionDesc) {
        this.actionDesc = actionDesc;
    }

 /*   public long getPriorityID() {
        return priorityID;
    }

    public void setPriorityID(long priorityID) {
        this.priorityID = priorityID;
    }
*/
    public long getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(long departmentID) {
        this.departmentID = departmentID;
    }

    public String getDepartmentDesc() {
        return departmentDesc;
    }

    public void setDepartmentDesc(String departmentDesc) {
        this.departmentDesc = departmentDesc;
    }

    public long getAssigneeUserID() {
        return assigneeUserID;
    }

    public void setAssigneeUserID(long assigneeUserID) {
        this.assigneeUserID = assigneeUserID;
    }

    /*public boolean isAssigneeAnyUser() {
        return assigneeAnyUser;
    }

    public void setAssigneeAnyUser(boolean assigneeAnyUser) {
        this.assigneeAnyUser = assigneeAnyUser;
    }

    public int getHoursWorked() {
        return HoursWorked;
    }

    public void setHoursWorked(int hoursWorked) {
        HoursWorked = hoursWorked;
    }

    public boolean isBillable() {
        return billable;
    }

    public void setBillable(boolean billable) {
        this.billable = billable;
    }

    public long getPartsOrderID() {
        return partsOrderID;
    }

    public void setPartsOrderID(long partsOrderID) {
        this.partsOrderID = partsOrderID;
    }

    public String getCauseDesc() {
        return causeDesc;
    }

    public void setCauseDesc(String causeDesc) {
        this.causeDesc = causeDesc;
    }

    public long getMonetaryValue() {
        return monetaryValue;
    }

    public void setMonetaryValue(long monetaryValue) {
        this.monetaryValue = monetaryValue;
    }
*/
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

  /*  public long getAutoInsertForActionID() {
        return autoInsertForActionID;
    }

    public void setAutoInsertForActionID(long autoInsertForActionID) {
        this.autoInsertForActionID = autoInsertForActionID;
    }*/

    public long getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(long lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public long getLastUpdateUserID() {
        return lastUpdateUserID;
    }

    public void setLastUpdateUserID(long lastUpdateUserID) {
        this.lastUpdateUserID = lastUpdateUserID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(assigneeUserID);
        dest.writeLong(lastUpdateDate);
        dest.writeLong(lastUpdateUserID);

        dest.writeString(actionDesc);
        dest.writeString(priorityDesc);
        dest.writeString(departmentDesc);
        dest.writeString(assignedUser);

        dest.writeString(notes);
        dest.writeString(updatedByUser);
        dest.writeByte((byte) (assigneeAnyUser ? 1:0));
    }

    private void readFromParcel(Parcel parcel) {

        assigneeUserID = parcel.readLong();
        lastUpdateDate = parcel.readLong();
        lastUpdateUserID = parcel.readLong();

        actionDesc = parcel.readString();
        priorityDesc = parcel.readString();
        departmentDesc = parcel.readString();
        assignedUser = parcel.readString();
        notes = parcel.readString();
        updatedByUser = parcel.readString();
        assigneeAnyUser = parcel.readByte() ==1;

    }

    public static final Parcelable.Creator<HistoryModel> CREATOR
            = new Parcelable.Creator<HistoryModel>() {
        public HistoryModel createFromParcel(Parcel in) {
            return new HistoryModel(in);
        }

        public HistoryModel[] newArray(int size) {
            return new HistoryModel[size];
        }
    };



    public boolean isAssigneeAnyUser() {
        return assigneeAnyUser;
    }

    public void setAssigneeAnyUser(boolean assigneeAnyUser) {
        this.assigneeAnyUser = assigneeAnyUser;
    }

    public void setAssigneeAnyUser(String assigneeAnyUser) {
        this.assigneeAnyUser = assigneeAnyUser!=null && assigneeAnyUser.equals("true");
    }
}
