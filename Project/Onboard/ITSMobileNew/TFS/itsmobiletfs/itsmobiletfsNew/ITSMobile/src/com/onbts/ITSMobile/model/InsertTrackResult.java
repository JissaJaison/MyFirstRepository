package com.onbts.ITSMobile.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.onbts.ITSMobile.model.base.Model;

/**
 * Created by tigre on 17.04.14.
 */
public class InsertTrackResult extends Model {
    public static final Parcelable.Creator<InsertTrackResult> CREATOR
            = new Parcelable.Creator<InsertTrackResult>() {
        public InsertTrackResult createFromParcel(Parcel in) {
            return new InsertTrackResult(in);
        }

        public InsertTrackResult[] newArray(int size) {
            return new InsertTrackResult[size];
        }
    };
    public final boolean result;
    public final String message;
    public InsertTrackResult(String result) {
        this.result = result==null;
        message = result;
    }

    public InsertTrackResult(Parcel in) {

        result = in.readByte() == 1;
        message = result ? null : in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(result ? (byte) 1 : 0);
        if (!result)
            dest.writeString(message);
    }
}
