package com.onbts.ITSMobile.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tigre on 01.04.14.
 */
public class PermissionModel implements Parcelable {
    public static final Parcelable.Creator<PermissionModel> CREATOR
            = new Parcelable.Creator<PermissionModel>() {
        public PermissionModel createFromParcel(Parcel in) {
            return new PermissionModel(in);
        }

        public PermissionModel[] newArray(int size) {
            return new PermissionModel[size];
        }
    };
    public final long id;
    public final boolean canCreateIssue,
            canTransferToPrevDepartament,
            canTransferToCreatorDepartament,
            canRequestInfoFromPrevDepartament,
            canRequestInfoFromCreatorDepartament;

    public int getPermissionGroupId() {
        return permissionGroupId;
    }

    public void setPermissionGroupId(int permissionGroupId) {
        this.permissionGroupId = permissionGroupId;
    }

    //for transfer bug fix
    private int permissionGroupId;

    public PermissionModel(long id, boolean canCreateIssue, boolean canTransferToPrevDepartament,
                           boolean canTransferToCreatorDepartament, boolean canRequestInfoFromPrevDepartament, boolean canRequestInfoFromCreatorDepartament, int permissionGroupId) {
        this.id = id;
        this.canCreateIssue = canCreateIssue;
        this.canTransferToPrevDepartament = canTransferToPrevDepartament;
        this.canTransferToCreatorDepartament = canTransferToCreatorDepartament;
        this.canRequestInfoFromPrevDepartament = canRequestInfoFromPrevDepartament;
        this.canRequestInfoFromCreatorDepartament = canRequestInfoFromCreatorDepartament;
        this.permissionGroupId = permissionGroupId;

    }

    public PermissionModel(long id, String canCreateIssue, String canTransferToPrevDepartament,
                           String canTransferToCreatorDepartament, String canRequestInfoFromPrevDepartament,
                           String canRequestInfoFromCreatorDepartament, int permissionGroupId) {
        this.id = id;
        this.canCreateIssue = canCreateIssue != null && canCreateIssue.equals("true");
        this.canTransferToPrevDepartament = canTransferToPrevDepartament != null && canTransferToPrevDepartament.equals
                ("true");
        this.canTransferToCreatorDepartament = canTransferToCreatorDepartament != null &&
                canTransferToCreatorDepartament.equals("true");
        this.canRequestInfoFromPrevDepartament = canRequestInfoFromPrevDepartament != null &&
                canRequestInfoFromPrevDepartament.equals("true");
        this.canRequestInfoFromCreatorDepartament = canRequestInfoFromCreatorDepartament != null &&
                canRequestInfoFromCreatorDepartament.equals("true");
        this.permissionGroupId = permissionGroupId;
    }

    /**
     * recreate object from parcel
     */
    private PermissionModel(Parcel in) {
        this.id = in.readLong();
        this.canCreateIssue = in.readByte() != 0;
        this.canTransferToPrevDepartament = in.readByte() != 0;
        this.canTransferToCreatorDepartament = in.readByte() != 0;
        this.canRequestInfoFromPrevDepartament = in.readByte() != 0;
        this.canRequestInfoFromCreatorDepartament = in.readByte() != 0;
        this.permissionGroupId = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeByte((byte) (canCreateIssue ? 1 : 0));
        dest.writeByte((byte) (canTransferToPrevDepartament ? 1 : 0));
        dest.writeByte((byte) (canTransferToCreatorDepartament ? 1 : 0));
        dest.writeByte((byte) (canRequestInfoFromPrevDepartament ? 1 : 0));
        dest.writeByte((byte) (canRequestInfoFromCreatorDepartament ? 1 : 0));
        dest.writeInt(permissionGroupId);
    }
}
