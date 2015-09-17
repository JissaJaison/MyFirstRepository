package com.onbts.ITSMobile.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.onbts.ITSMobile.model.base.Model;

import java.util.ArrayList;
import java.util.List;

public class DetailedIssue extends Model {

    public static final Parcelable.Creator<DetailedIssue> CREATOR
            = new Parcelable.Creator<DetailedIssue>() {
        public DetailedIssue createFromParcel(Parcel in) {
            return new DetailedIssue(in);
        }

        public DetailedIssue[] newArray(int size) {
            return new DetailedIssue[size];
        }
    };
    private long id;
    //    private String issueType;
    private String status;
    //    private String guestService;
    private String locationDesc;
    private String enteredBy;
    private String createdDate;
    private String notes;
    //    private String prior;
//    private int priorId;
    private int concurrencyId;
    private int statusId;
    //    private int issueTypeId;
    private long currentDepartmentId;
    private long creatorDepartmentId;
    private long locationOwnerDepartmentID;    // Creation details
    //AMOS
    private String compName;
    private String funcNo;
    private String funcDescr;

    // Creation details
    private String serialNo;
    private String reportedByCrew;
    private String crewDepartment;
    private String crewPosition;
    private String onbehalfofuser;
    private String onbehalfofgroup;
    private String creatorOfficePhoneNumber;
    private String creatorExtension;
    private String creatorMobile;
    private String creatorPager;
    private String creatorDepartmentDesc;
    // IssueDetails
    private String defect;
    // Location Details
    private String deckDesc;
    private String transverse;
    private String section;
    private String fireZone;
    private String locationOwner;
    // GuestDetails
//    private String guestServiceIssue;
//    private String requireGuestCallBack;
    private String guestFirstName;
    private String guestLastName;
    private String dateGuestExp;
    private String cabinNumber;
    private String reservationNumber;
    private String disembarkDate;
    private String severity;
    private List<HistoryModel> historyList;
    private List<ActionIssue> actionIssues;
    private IssueTypeModel issueType;
    private boolean favorite;
    private ArrayList<FileModel> fileList;
    private boolean openOnDevice;

    private int alert;

    public int getAlert() {
        return alert;
    }

    public void setAlert(int alert) {
        this.alert = alert;
    }

    public DetailedIssue(Parcel parcel) {

        readFromParcel(parcel);
    }

    public DetailedIssue() {
        issueType = new IssueTypeModel();
    }

    public List<ActionIssue> getActionIssues() {
        return actionIssues;
    }

    public void setActionIssues(List<ActionIssue> actionIssues) {
        this.actionIssues = actionIssues;
    }

    //    private boolean guestServiceIssueBoolean;
//    private boolean requiresGuestCallbackBoolean;
//    private int issueClassID;
    public long getLocationOwnerDepartmentID() {
        return locationOwnerDepartmentID;
    }

    public void setLocationOwnerDepartmentID(long locationOwnerDepartmentID) {
        this.locationOwnerDepartmentID = locationOwnerDepartmentID;
    }

    public boolean isGuestServiceIssueBoolean() {
        return issueType.isGuestServiceIssue();
    }

    public void setGuestServiceIssueBoolean(boolean guestServiceIssueBoolean) {
        issueType.setGuestServiceIssue(guestServiceIssueBoolean);
    }

    public void setGuestServiceIssueBoolean(String guestServiceIssue) {
        issueType.setGuestServiceIssue(guestServiceIssue != null && guestServiceIssue.equals("true"));
    }

    public boolean isRequiresGuestCallbackBoolean() {
        return issueType.isRequiresGuestCallback();
    }

    public void setRequiresGuestCallbackBoolean(boolean requiresGuestCallbackBoolean) {
        issueType.setRequiresGuestCallback(requiresGuestCallbackBoolean);
    }

    public void setRequiresGuestCallbackBoolean(String requiresGuestCallback) {
        issueType.setRequiresGuestCallback(requiresGuestCallback != null && requiresGuestCallback.equals("true"));
    }

    public List<HistoryModel> getHistoryList() {
        return historyList != null ? historyList : new ArrayList<HistoryModel>();
    }

    public void setHistoryList(List<HistoryModel> historyList) {
        this.historyList = historyList;
        if (historyList != null && historyList.size() > 0) {
            HistoryModel model = historyList.get(historyList.size() - 1);
            issueType.setAssigneeUserID(model.getAssigneeUserID());
            issueType.setAssigneeAnyUser(model.isAssigneeAnyUser());

        }

    }

    public void setAlert(String alert) {
        if (alert != null)
            switch (alert) {
                case "Alert":
                    this.alert = 2;
                    break;
                case "PreAlert":
                    this.alert = 1;
                    break;
                default:
                    this.alert = -1;
                    break;
            }
        else this.alert = -1;
    }

    public long getIssueClassID() {
        return issueType.getIssueClassID();
    }

    public void setIssueClassID(long issueClassID) {
        issueType.setIssueClassID(issueClassID);
    }

    public IssueTypeModel getIssueTypeModel() {
        return issueType;
    }

    public String getIssueType() {
        return issueType.getIssueTypeDesc();
    }

    public void setIssueType(String issueType) {
        this.issueType.setIssueTypeDesc(issueType);
    }

    public String getLocationDesc() {
        return locationDesc;
    }

    public void setLocationDesc(String locationDesc) {
        this.locationDesc = locationDesc;
    }

    public String getEnteredBy() {
        return enteredBy;
    }

    public void setEnteredBy(String enteredBy) {
        this.enteredBy = enteredBy;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getReportedByCrew() {
        return reportedByCrew;
    }

    public void setReportedByCrew(String reportedByCrew) {
        this.reportedByCrew = reportedByCrew != null && reportedByCrew.equals("G") ? "Yes" : "No";
    }

    public int getConcurrencyID() {
        return concurrencyId;
    }

    public void setConcurrencyID(int concurrencyID) {
        this.concurrencyId = concurrencyID;
    }

    public long getPriorId() {
        return issueType.getPriorityID();
    }

    public void setPriorId(long priorId) {
        issueType.setPriorityID(priorId);
    }

    public String getDeckDesc() {
        return deckDesc;
    }

    public void setDeckDesc(String deckDesc) {
        this.deckDesc = deckDesc;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPrior() {
        return issueType.getPriorityDesc();
    }

    public void setPrior(String prior) {
        issueType.setPriorityDesc(prior);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


   /* public String getGuestService() {
        return issueType.isGuestServiceIssue() ? "No" : "Yes";
    }
*/
/*    public void setGuestService(String guestService) {
        this.guestService = guestService;
    }
*/

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCrewDepartment() {
        return crewDepartment;
    }

    public void setCrewDepartment(String crewDepartment) {
        this.crewDepartment = crewDepartment;
    }

    public String getCrewPosition() {
        return crewPosition;
    }

    public void setCrewPosition(String crewPosition) {
        this.crewPosition = crewPosition;
    }

    public String getOnbehalfofuser() {
        return onbehalfofuser;
    }

    public void setOnbehalfofuser(String onbehalfofuser) {
        this.onbehalfofuser = onbehalfofuser;
    }

    public String getOnbehalfofgroup() {
        return onbehalfofgroup;
    }

    public void setOnbehalfofgroup(String onbehalfofgroup) {
        this.onbehalfofgroup = onbehalfofgroup;
    }

    public String getCreatorOfficePhoneNumber() {
        return creatorOfficePhoneNumber;
    }

    public void setCreatorOfficePhoneNumber(String creatorOfficePhoneNumber) {
        this.creatorOfficePhoneNumber = creatorOfficePhoneNumber;
    }

    public String getCreatorExtension() {
        return creatorExtension;
    }

    public void setCreatorExtension(String creatorExtension) {
        this.creatorExtension = creatorExtension;
    }

    public String getCreatorMobile() {
        return creatorMobile;
    }

    public void setCreatorMobile(String creatorMobile) {
        this.creatorMobile = creatorMobile;
    }

    public String getCreatorPager() {
        return creatorPager;
    }

    public void setCreatorPager(String creatorPager) {
        this.creatorPager = creatorPager;
    }

    public String getDefect() {
        return defect;
    }

    public void setDefect(String defect) {
        this.defect = defect;
    }

    public String getTransverse() {
        return transverse;
    }

    public void setTransverse(String transverse) {
        this.transverse = transverse;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getFireZone() {
        return fireZone;
    }

    public void setFireZone(String fireZone) {
        this.fireZone = fireZone;
    }

    public String getLocationOwner() {
        return locationOwner;
    }

    public void setLocationOwner(String locationOwner) {
        this.locationOwner = locationOwner;
    }


    public String getGuestServiceIssue() {
        return issueType.isGuestServiceIssue() ? "Yes" : "No";
    }

    /*
        public void setGuestServiceIssue(String guestServiceIssue) {
            this.guestServiceIssue = guestServiceIssue;
        }

    */
    public String getRequireGuestCallBack() {
        return issueType.isRequiresGuestCallback() ? "Yes" : "No";
    }
/*

    public void setRequireGuestCallBack(String requireGuestCallBack) {
        this.requireGuestCallBack = requireGuestCallBack;
    }
*/

    public String getGuestFirstName() {
        return guestFirstName;
    }

    public void setGuestFirstName(String guestFirstName) {
        this.guestFirstName = guestFirstName;
    }

    public String getGuestLastName() {
        return guestLastName;
    }

    public void setGuestLastName(String guestLastName) {
        this.guestLastName = guestLastName;
    }

    public String getDateGuestExp() {
        return dateGuestExp;
    }

    public void setDateGuestExp(String dateGuestExp) {
        this.dateGuestExp = dateGuestExp;
    }

    public String getCabinNumber() {
        return cabinNumber;
    }

    public void setCabinNumber(String cabinNumber) {
        this.cabinNumber = cabinNumber;
    }

    public String getReservationNumber() {
        return reservationNumber;
    }

    public void setReservationNumber(String reservationNumber) {
        this.reservationNumber = reservationNumber;
    }

    public String getDisembarkDate() {
        return disembarkDate;
    }

    public void setDisembarkDate(String disembarkDate) {
        this.disembarkDate = disembarkDate;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    //kostya
    public int getConcurrencyId() {
        return concurrencyId;
    }

    public void setConcurrencyId(int concurrencyId) {
        this.concurrencyId = concurrencyId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public long getIssueTypeId() {
        return issueType.getIssueTypeID();
    }

    public void setIssueTypeId(long issueTypeId) {
        issueType.setIssueTypeID(issueTypeId);
    }

    public long getCurrentDepartmentId() {
        return currentDepartmentId;
    }

    public void setCurrentDepartmentId(long currentDepartmentId) {
        this.currentDepartmentId = currentDepartmentId;
    }

    public long getCreatorDepartmentId() {
        return creatorDepartmentId;
    }

    public void setCreatorDepartmentId(long creatorDepartmentId) {
        this.creatorDepartmentId = creatorDepartmentId;
    }

    //    //AMOS
    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    public String getFuncNo() {
        return funcNo;
    }

    public String getCreatorDepartmentDesc() {
        return creatorDepartmentDesc;
    }

    public void setCreatorDepartmentDesc(String creatorDepartmentDesc) {
        this.creatorDepartmentDesc = creatorDepartmentDesc;
    }

    public void setFuncNo(String funcNo) {
        this.funcNo = funcNo;
    }

    public String getFuncDescr() {
        return funcDescr;
    }

    public void setFuncDescr(String funcDescr) {
        this.funcDescr = funcDescr;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public ArrayList<FileModel> getFileList() {
        return fileList;
    }

    public void setFileList(ArrayList<FileModel> fileList) {
        this.fileList = fileList;
    }

    public void readFromParcel(Parcel source) {
        id = source.readLong();
        issueType = source.readParcelable(IssueTypeModel.class.getClassLoader());
        status = source.readString();
        favorite = source.readByte() == 1;
        openOnDevice = source.readByte() == 1;
//        guestService = source.readString();
        locationDesc = source.readString();
        enteredBy = source.readString();
        createdDate = source.readString();
        notes = source.readString();
//        prior = source.readString();
//        priorId = source.readInt();
        concurrencyId = source.readInt();
        statusId = source.readInt();
//        issueTypeId = source.readInt();
        currentDepartmentId = source.readLong();
        creatorDepartmentId = source.readLong();
//        issueClassID = source.readInt();
//        guestServiceIssueBoolean = source.readByte() == 1;
//        requiresGuestCallbackBoolean = source.readByte() == 1;

        reportedByCrew = source.readString();
        crewDepartment = source.readString();
        crewPosition = source.readString();
        onbehalfofuser = source.readString();
        onbehalfofgroup = source.readString();
        creatorOfficePhoneNumber = source.readString();
        creatorExtension = source.readString();
        creatorMobile = source.readString();
        creatorPager = source.readString();
        creatorDepartmentDesc = source.readString();

        defect = source.readString();

        deckDesc = source.readString();
        transverse = source.readString();
        section = source.readString();
        fireZone = source.readString();
        locationOwner = source.readString();

//        guestServiceIssue = source.readString();
//        requireGuestCallBack = source.readString();
        guestFirstName = source.readString();
        guestLastName = source.readString();

        dateGuestExp = source.readString();
        cabinNumber = source.readString();
        reservationNumber = source.readString();
        disembarkDate = source.readString();
        severity = source.readString();
        //amos
        compName = source.readString();
        funcNo = source.readString();
        funcDescr = source.readString();
        serialNo = source.readString();
        alert = source.readInt();
        if (historyList == null)
            historyList = new ArrayList<HistoryModel>();
        source.readTypedList(historyList, HistoryModel.CREATOR);
        if (fileList == null)
            fileList = new ArrayList<FileModel>();
        source.readTypedList(fileList, FileModel.CREATOR);

        if (actionIssues == null)
            actionIssues = new ArrayList<ActionIssue>();
        source.readTypedList(actionIssues, ActionIssue.CREATOR);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeParcelable(issueType, flags);
        dest.writeString(status);
        dest.writeByte(favorite ? (byte) 1 : 0);
        dest.writeByte(openOnDevice ? (byte) 1 : 0);
//        dest.writeString(guestService);

        dest.writeString(locationDesc);
        dest.writeString(enteredBy);
        dest.writeString(createdDate);
        dest.writeString(notes);

        dest.writeInt(concurrencyId);
        dest.writeInt(statusId);

//        dest.writeInt(issueTypeId);
        dest.writeLong(currentDepartmentId);
        dest.writeLong(creatorDepartmentId);
//        dest.writeInt(issueClassID);
//        dest.writeByte(guestServiceIssueBoolean ? (byte) 1 : 0);
//        dest.writeByte(requiresGuestCallbackBoolean ? (byte) 1 : 0);

        dest.writeString(reportedByCrew);
        dest.writeString(crewDepartment);
        dest.writeString(crewPosition);
        dest.writeString(onbehalfofuser);
        dest.writeString(onbehalfofgroup);
        dest.writeString(creatorOfficePhoneNumber);
        dest.writeString(creatorExtension);
        dest.writeString(creatorMobile);
        dest.writeString(creatorPager);
        dest.writeString(creatorDepartmentDesc);

        dest.writeString(defect);

        dest.writeString(deckDesc);
        dest.writeString(transverse);
        dest.writeString(section);
        dest.writeString(fireZone);
        dest.writeString(locationOwner);

//        dest.writeString(guestServiceIssue);
//        dest.writeString(requireGuestCallBack);
        dest.writeString(guestFirstName);
        dest.writeString(guestLastName);

        dest.writeString(dateGuestExp);
        dest.writeString(cabinNumber);
        dest.writeString(reservationNumber);
        dest.writeString(disembarkDate);
        dest.writeString(severity);
        //amos
        dest.writeString(compName);
        dest.writeString(funcNo);
        dest.writeString(funcDescr);
        dest.writeString(serialNo);
        dest.writeInt(alert);

        dest.writeTypedList(historyList);
        dest.writeTypedList(fileList);
        dest.writeTypedList(actionIssues);
    }


    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite != null && favorite.equals("true");
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isOpen() {
        return openOnDevice;
    }

    public void setOpenOnDevice(boolean openOnDevice) {
        this.openOnDevice = openOnDevice;
    }

    public void setOpenOnDevice(String openOnDevice) {
        this.openOnDevice = openOnDevice != null && openOnDevice.equals("true");
    }
}
