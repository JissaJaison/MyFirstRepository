package com.onbts.ITSMobile.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.onbts.ITSMobile.model.base.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * User model.
 * <p/>
 * Created by tigre on 30.03.14.
 */
public class UserModel extends Model {
    public static final Parcelable.Creator<UserModel> CREATOR
            = new Parcelable.Creator<UserModel>() {
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };
    private final long locationGroupID;
    private final long id;
    private final String name;
    private final String departmentName;
    private final long departmentId;
    private String deviceId;
    private int countCreate;
    private int alertCreate;
    private int preAlertCreate;
    private int countAssigned;
    private int preAlertAssigned;
    private int alertAssigned;
    private int countFavorite;
    private int preAlertFavorite;
    private int alertFavorite;
    private int countNewCreate;

    private int countNewAssigned;
    private int countNewFavorite;
    private int countDepCreate;
    private int preAlertDepCreated;
    private int alertDepCreated;
    private int countDepAssigned;
    private int preAlertDepAssigned;
    private int alertDepAssigned;
    private int countNewDepCreate;
    private int countNewDepAssigned;
    private long siteID;
    private List<IssueClassModel> issueClasses;
    private long attachFileLength;
    private int permissionGroupID;

    public UserModel(long locationGroupID, long id, String name, String departmentName, long departmentId) {
        this.locationGroupID = locationGroupID;
        this.id = id;
        this.name = name;
        this.departmentName = departmentName;
        this.departmentId = departmentId;
        attachFileLength = 500000;

    }

    /**
     * recreate object from parcel
     */
    private UserModel(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.departmentName = in.readString();
        this.departmentId = in.readLong();
        this.deviceId = in.readString();
        this.siteID = in.readLong();
        this.locationGroupID = in.readLong();

        countAssigned = in.readInt();
        countCreate = in.readInt();
        countDepAssigned = in.readInt();
        countDepCreate = in.readInt();
        countNewDepAssigned = in.readInt();
        countNewDepCreate = in.readInt();
        countNewAssigned = in.readInt();
        countNewCreate = in.readInt();
        countNewFavorite = in.readInt();
        countFavorite = in.readInt();

        alertAssigned = in.readInt();
        alertCreate = in.readInt();
        alertFavorite = in.readInt();
        alertDepCreated = in.readInt();
        alertDepAssigned = in.readInt();

        preAlertAssigned = in.readInt();
        preAlertCreate = in.readInt();
        preAlertFavorite = in.readInt();
        preAlertDepCreated = in.readInt();
        preAlertDepAssigned = in.readInt();

        attachFileLength = in.readLong();

        if (issueClasses == null) {
            issueClasses = new ArrayList<>();
        }
        in.readTypedList(issueClasses, IssueClassModel.CREATOR);


    }

    public int getPreAlertCreate() {
        return preAlertCreate;
    }

    public void setPreAlertCreate(int preAlertCreate) {
        this.preAlertCreate = preAlertCreate;
    }

    public int getPreAlertAssigned() {
        return preAlertAssigned;
    }

    public void setPreAlertAssigned(int preAlertAssigned) {
        this.preAlertAssigned = preAlertAssigned;
    }

    public int getPreAlertFavorite() {
        return preAlertFavorite;
    }

    public void setPreAlertFavorite(int preAlertFavorite) {
        this.preAlertFavorite = preAlertFavorite;
    }

    public int getPreAlertDepCreated() {
        return preAlertDepCreated;
    }

    public void setPreAlertDepCreated(int preAlertDepCreated) {
        this.preAlertDepCreated = preAlertDepCreated;
    }

    public int getPreAlertDepAssigned() {
        return preAlertDepAssigned;
    }

    public void setPreAlertDepAssigned(int preAlertDepAssigned) {
        this.preAlertDepAssigned = preAlertDepAssigned;
    }

    public int getAlertCreate() {
        return alertCreate;
    }

    public void setAlertCreate(int alertCreate) {
        this.alertCreate = alertCreate;
    }

    public int getAlertAssigned() {
        return alertAssigned;
    }

    public void setAlertAssigned(int alertAssigned) {
        this.alertAssigned = alertAssigned;
    }

    public int getAlertFavorite() {
        return alertFavorite;
    }

    public void setAlertFavorite(int alertFavorite) {
        this.alertFavorite = alertFavorite;
    }

    public int getAlertDepCreated() {
        return alertDepCreated;
    }

    public void setAlertDepCreated(int alertDepCreated) {
        this.alertDepCreated = alertDepCreated;
    }

    public int getAlertDepAssigned() {
        return alertDepAssigned;
    }

    public void setAlertDepAssigned(int alertDepAssigned) {
        this.alertDepAssigned = alertDepAssigned;
    }

    public long getAttachFileLength() {
        return attachFileLength;
    }

    public void setAttachFileLength(int attachFileLength) {
        this.attachFileLength = attachFileLength;
    }

    public int getCountNewDepAssigned() {
        return countNewDepAssigned;
    }

    public void setCountNewDepAssigned(int countNewDepAssigned) {
        this.countNewDepAssigned = countNewDepAssigned;
    }

    public int getCountNewDepCreate() {
        return countNewDepCreate;
    }

    public void setCountNewDepCreate(int countNewDepCreate) {
        this.countNewDepCreate = countNewDepCreate;
    }

    public int getCountCreate() {
        return countCreate;
    }

    public void setCountCreate(int countCreate) {
        this.countCreate = countCreate;
    }

    public int getCountAssigned() {
        return countAssigned;
    }

    public void setCountAssigned(int countAssigned) {
        this.countAssigned = countAssigned;
    }

    public int getCountFavorite() {
        return countFavorite;
    }

    public void setCountFavorite(int countFavorite) {
        this.countFavorite = countFavorite;
    }

    public int getCountNewCreate() {
        return countNewCreate;
    }

    public void setCountNewCreate(int countNewCreate) {
        this.countNewCreate = countNewCreate;
    }

    public int getCountNewAssigned() {
        return countNewAssigned;
    }

    public void setCountNewAssigned(int countNewAssigned) {
        this.countNewAssigned = countNewAssigned;
    }

    public int getCountNewFavorite() {
        return countNewFavorite;
    }

    public void setCountNewFavorite(int countNewFavorite) {
        this.countNewFavorite = countNewFavorite;
    }

    public int getCountDepCreate() {
        return countDepCreate;
    }

    public void setCountDepCreate(int countDepCreate) {
        this.countDepCreate = countDepCreate;
    }

    public int getCountDepAssigned() {
        return countDepAssigned;
    }

    public void setCountDepAssigned(int countDepAssigned) {
        this.countDepAssigned = countDepAssigned;
    }

    public long getSiteID() {
        return siteID;
    }

    public void setSiteID(long siteID) {
        this.siteID = siteID;
    }

    public List<IssueClassModel> getIssueClasses() {
        return issueClasses;
    }

    public void setIssueClasses(List<IssueClassModel> issueClasses) {
        if (issueClasses != null && issueClasses.size() > 0)
            this.issueClasses = issueClasses;
        else this.issueClasses = null;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public long getDepartmentId() {
        return departmentId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public long getLocationGroupID() {
        return locationGroupID;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(departmentName);
        dest.writeLong(departmentId);
        dest.writeString(deviceId);
        dest.writeLong(siteID);

        dest.writeLong(locationGroupID);

        dest.writeInt(countAssigned);
        dest.writeInt(countCreate);
        dest.writeInt(countDepAssigned);
        dest.writeInt(countDepCreate);
        dest.writeInt(countNewDepAssigned);
        dest.writeInt(countNewDepCreate);
        dest.writeInt(countNewAssigned);
        dest.writeInt(countNewCreate);
        dest.writeInt(countNewFavorite);
        dest.writeInt(countFavorite);

        dest.writeInt(alertAssigned);
        dest.writeInt(alertCreate);
        dest.writeInt(alertFavorite);
        dest.writeInt(alertDepCreated);
        dest.writeInt(alertDepAssigned);

        dest.writeInt(preAlertAssigned);
        dest.writeInt(preAlertCreate);
        dest.writeInt(preAlertFavorite);
        dest.writeInt(preAlertDepCreated);
        dest.writeInt(preAlertDepAssigned);

        dest.writeLong(attachFileLength);

        dest.writeTypedList(issueClasses);
    }

    public int getPermissionGroupID() {
        return permissionGroupID;
    }

    public void setPermissionGroupID(int permissionGroupID) {
        this.permissionGroupID = permissionGroupID;
    }
}
