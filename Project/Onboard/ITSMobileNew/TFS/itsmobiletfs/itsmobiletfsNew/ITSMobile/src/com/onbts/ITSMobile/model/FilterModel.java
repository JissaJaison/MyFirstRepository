package com.onbts.ITSMobile.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.onbts.ITSMobile.model.base.Model;

/**
 * Created by tigre on 16.04.14.
 */
public class FilterModel extends Model {
    public final long id;
    public final String title;

    public static final Parcelable.Creator<FilterModel> CREATOR
            = new Parcelable.Creator<FilterModel>() {
        public FilterModel createFromParcel(Parcel in) {
            return new FilterModel(in);
        }

        public FilterModel[] newArray(int size) {
            return new FilterModel[size];
        }
    };

    public FilterModel(long id, String title) {
        this.id = id;
        this.title = title;
    }

    public FilterModel(Parcel in) {
        id = in.readLong();
        title = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
    }
}
