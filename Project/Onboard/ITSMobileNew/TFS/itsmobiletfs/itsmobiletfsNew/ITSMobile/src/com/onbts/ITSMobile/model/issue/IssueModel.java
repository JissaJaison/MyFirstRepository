package com.onbts.ITSMobile.model.issue;

import android.os.Parcel;
import android.os.Parcelable;

import com.onbts.ITSMobile.model.base.Model;

import java.util.Comparator;

/**
 * Created by tigre on 15.04.14.
 */
public class IssueModel extends Model {
    public static final Parcelable.Creator<IssueModel> CREATOR
            = new Parcelable.Creator<IssueModel>() {
        public IssueModel createFromParcel(Parcel in) {
            return new IssueModel(in);
        }

        public IssueModel[] newArray(int size) {
            return new IssueModel[size];
        }
    };

    private final long id;
    private long statusID;
    private long createDate;
    private boolean openOnDevice;
    private boolean favorite;
    private boolean hasFile;
    private String typeDesc;
    private String statusDesc;
    private String locationDesc;
    private String notes;
    private long departmentID;
    private long zoneID;
    private long typeID;
    private long deckID;
    private long fireZoneID;
    private int priority;
    private long locationID;
    private long locationGroupID;
    private int alert;

    public IssueModel(Parcel in) {
        id = in.readLong();
        statusID = in.readLong();
        createDate = in.readLong();
        openOnDevice = in.readByte() == 1;
        favorite = in.readByte() == 1;
        hasFile = in.readByte() == 1;
        typeDesc = in.readString();
        statusDesc = in.readString();
        locationDesc = in.readString();
        notes = in.readString();
        departmentID = in.readLong();
        zoneID = in.readLong();
        typeID = in.readLong();
        deckID = in.readLong();
        fireZoneID = in.readLong();
        priority = in.readInt();
        locationID = in.readLong();
        locationGroupID = in.readLong();
        alert = in.readInt();
    }

    public IssueModel(long id) {
        this.id = id;
    }

    public IssueModel(long id, long statusID, long createDate, boolean openOnDevice, String typeDesc, String statusDesc,
                      String locationDesc, long departmentID, long zoneID, long typeID, long deckID, int priority, long fireZoneID, long locationID, long locationGroupID) {
        this.id = id;
        this.statusID = statusID;
        this.createDate = createDate;
        this.openOnDevice = openOnDevice;
        this.typeDesc = typeDesc;
        this.statusDesc = statusDesc;
        this.locationDesc = locationDesc;
        this.departmentID = departmentID;
        this.zoneID = zoneID;
        this.typeID = typeID;
        this.deckID = deckID;
        this.fireZoneID = fireZoneID;
        this.priority = priority;
        this.fireZoneID = fireZoneID;
        this.locationID = locationID;
        this.locationGroupID = locationGroupID;

    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public long getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(long departmentID) {
        this.departmentID = departmentID;
    }

    public long getZoneID() {
        return zoneID;
    }

    public void setZoneID(long zoneID) {
        this.zoneID = zoneID;
    }

    public long getTypeID() {
        return typeID;
    }

    public void setTypeID(long typeID) {
        this.typeID = typeID;
    }

    public long getDeckID() {
        return deckID;
    }

    public void setDeckID(long deckID) {
        this.deckID = deckID;
    }

    public long getId() {
        return id;
    }

    public long getStatusID() {
        return statusID;
    }

    public void setStatusID(long statusID) {
        this.statusID = statusID;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public boolean isOpenOnDevice() {
        return openOnDevice;
    }

    public void setOpenOnDevice(boolean openOnDevice) {
        this.openOnDevice = openOnDevice;
    }

    public void setOpenOnDevice(String openOnDevice) {
        this.openOnDevice = openOnDevice != null && openOnDevice.equals("true");
    }

    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getLocationDesc() {
        return locationDesc;
    }

    public void setLocationDesc(String locationDesc) {
        this.locationDesc = locationDesc;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getLocationID() {
        return locationID;
    }

    public void setLocationID(long locationID) {
        this.locationID = locationID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getAlert() {
        return alert;
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

    public void setAlert(int alert) {
        this.alert = alert;
    }

    public long getLocationGroupID() {
        return locationGroupID;
    }

    public void setLocationGroupID(long locationGroupID) {
        this.locationGroupID = locationGroupID;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(statusID);
        dest.writeLong(createDate);
        dest.writeByte(openOnDevice ? (byte) 1 : 0);
        dest.writeByte(favorite ? (byte) 1 : 0);
        dest.writeByte(hasFile ? (byte) 1 : 0);
        dest.writeString(typeDesc);
        dest.writeString(statusDesc);
        dest.writeString(locationDesc);
        dest.writeString(notes);
        dest.writeLong(departmentID);
        dest.writeLong(zoneID);
        dest.writeLong(typeID);
        dest.writeLong(deckID);
        dest.writeLong(fireZoneID);
        dest.writeInt(priority);
        dest.writeLong(locationID);
        dest.writeLong(locationGroupID);
        dest.writeInt(alert);

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

    public boolean isHasFile() {
        return hasFile;
    }

    public void setHasFile(boolean hasFile) {
        this.hasFile = hasFile;
    }

    public void setFireZoneID(long fireZoneID) {
        this.fireZoneID = fireZoneID;
    }

    public long getFireZoneID() {
        return fireZoneID;
    }
//    iss.IssueID as _id,iss.StatusID, iss.CreateDate, iss.OpenedOnDevice, itracks.Notes, itype.IssueTypeDesc, stat.StatusDesc, loc.LocationDesc,  pr.PriorityLevel

    public static class ascIdComparator implements Comparator<IssueModel> {
        @Override
        public int compare(IssueModel lhs, IssueModel rhs) {
            return (int) lhs.getId() - (int) rhs.getId();
        }
    }

    public static class descIdComparator implements Comparator<IssueModel> {
        @Override
        public int compare(IssueModel lhs, IssueModel rhs) {
            return (int) rhs.getId() - (int) lhs.getId();
        }
    }

    public static class ascDateComparator implements Comparator<IssueModel> {
        @Override
        public int compare(IssueModel lhs, IssueModel rhs) {
            if (lhs.getCreateDate() > rhs.getCreateDate())
                return 1;
            if (lhs.getCreateDate() < rhs.getCreateDate())
                return -1;

            return 0;
        }
    }

    public static class descDateComparator implements Comparator<IssueModel> {
        @Override
        public int compare(IssueModel lhs, IssueModel rhs) {
            if (lhs.getCreateDate() > rhs.getCreateDate())
                return -1;
            if (lhs.getCreateDate() < rhs.getCreateDate())
                return 1;

            return 0;
        }
    }

    public static class ascLocationComparator implements Comparator<IssueModel> {
        @Override
        public int compare(IssueModel lhs, IssueModel rhs) {
            return rhs.getLocationDesc().compareTo(lhs.getLocationDesc());
        }
    }

    public static class descLocationComparator implements Comparator<IssueModel> {
        @Override

        public int compare(IssueModel lhs, IssueModel rhs) {
            return lhs.getLocationDesc().compareTo(rhs.getLocationDesc());
        }
    }

    //Filtering by priority
    public static class ascPriorityComparator implements Comparator<IssueModel> {
        @Override
        public int compare(IssueModel lhs, IssueModel rhs) {
            return lhs.getPriority() - rhs.getPriority();
        }
    }

    public static class descPriorityComparator implements Comparator<IssueModel> {
        @Override
        public int compare(IssueModel lhs, IssueModel rhs) {
            return rhs.getPriority() - lhs.getPriority();
        }
    }

}
