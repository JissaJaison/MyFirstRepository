package com.onbts.ITSMobile.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tigre on 01.04.14.
 */
public class IssueClassModel implements Parcelable{
    public final long id;

    public PermissionModel getPermissionses() {
        return permissionses;
    }

    public final PermissionModel permissionses;

    public IssueClassModel(long id, PermissionModel permissionses) {
        this.id = id;
        this.permissionses = permissionses;
    }

    /**
     * recreate object from parcel
     */
    private IssueClassModel(Parcel in) {
        this.id = in.readLong();
        this.permissionses = in.readParcelable(PermissionModel.class.getClassLoader());
    }

    public static final Parcelable.Creator<IssueClassModel> CREATOR
            = new Parcelable.Creator<IssueClassModel>() {
        public IssueClassModel createFromParcel(Parcel in) {
            return new IssueClassModel(in);
        }

        public IssueClassModel[] newArray(int size) {
            return new IssueClassModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeParcelable(permissionses, flags);
    }
}
