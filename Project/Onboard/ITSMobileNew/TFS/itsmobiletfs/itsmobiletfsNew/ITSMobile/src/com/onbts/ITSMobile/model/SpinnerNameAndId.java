package com.onbts.ITSMobile.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.onbts.ITSMobile.model.base.Model;

public class SpinnerNameAndId extends Model {

    public static final Parcelable.Creator<SpinnerNameAndId> CREATOR
            = new Parcelable.Creator<SpinnerNameAndId>() {
        public SpinnerNameAndId createFromParcel(Parcel in) {
            return new SpinnerNameAndId(in);
        }

        public SpinnerNameAndId[] newArray(int size) {
            return new SpinnerNameAndId[size];
        }
    };
    protected String nameNote, nameTable;
    protected long id;

    public SpinnerNameAndId(long id, String nameNote, String nameTable) {
        this.id = id;
        this.nameNote = nameNote;
        this.nameTable = nameTable;
    }

    public SpinnerNameAndId(Parcel in) {
        id = in.readLong();
        nameNote = in.readString();
        nameTable = in.readString();
    }

    public String getNameNote() {
        return nameNote;
    }

    public long getId() {
        return id;
    }

    public String getNameTable() {
        return nameTable;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(nameNote);
        dest.writeString(nameTable);
    }
}