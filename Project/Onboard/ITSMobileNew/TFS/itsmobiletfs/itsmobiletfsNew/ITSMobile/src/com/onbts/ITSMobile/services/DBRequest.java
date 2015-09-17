package com.onbts.ITSMobile.services;

import android.os.Parcel;
import android.os.Parcelable;

import com.onbts.ITSMobile.model.base.Model;
import com.onbts.ITSMobile.panels.PanelAction;

import java.util.ArrayList;

/**
 * Created by tigre on 05.11.14.
 */
public class DBRequest implements Parcelable {

    public static final Parcelable.Creator<DBRequest> CREATOR = new Parcelable.Creator<DBRequest>() {
        public DBRequest createFromParcel(Parcel source) {
            return new DBRequest(source);
        }

        public DBRequest[] newArray(int size) {
            return new DBRequest[size];
        }
    };
    private DBRequestType type;

    private Model model;
    private long id;
    private ArrayList<? extends Model> models;

    public DBRequest(DBRequestType type) {
        this.type = type;
    }

    private DBRequest(Parcel in) {
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : DBRequestType.values()[tmpType];
        this.model = in.readParcelable(Model.class.getClassLoader());
        this.id = in.readLong();
//        if (models == null) {
//            models = new ArrayList<T>();
//        }
//        in.readTypedList(panelsList, T.CREATOR);

//        this.models = (ArrayList<? extends Model>) in.readSerializable();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Model getModel() {
        return model;
    }

    public DBRequest setModel(Model model) {
        this.model = model;
        return this;
    }

    public ArrayList<? extends Model> getModels() {
        return models;
    }

    public DBRequest setModels(ArrayList<? extends Model> models) {
        this.models = models;

        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeParcelable(this.model, 0);
        dest.writeLong(this.id);
//        dest.writeTypedList(this.models);
    }

    public enum DBRequestType {
        LOGIN,
        DETAILS_ISSUE,
        UPDATE_ISSUE,
        UPDATE_USER,
        OPEN_FILE,
        USER_CREATE_ISSUE_LIST,
        USER_ASSIGNED_ISSUE_LIST,
        USER_FAVORITE_ISSUE_LIST,
        DEPARTMENT_CREATE_ISSUE_LIST,
        DEPARTMENT_ASSIGNED_ISSUE_LIST,
        HISTORY_ISSUE,
        INSERT_ISSUE_TRACK,
        FILTER_LIST_DECK,
        GET_FILTER_PRIORITIES,
        GET_FILTER_LIST_STATUSES,
        GET_FILTER_LIST_TYPES,
        GET_FILTER_LIST_SECTIONS,
        GET_FILTER_LIST_DECKS,
        GET_FILTER_LIST_DEPARTMENTS,
        GET_FILTER_LIST_FIREZONES,
        GET_FILTER_LIST_LOCATIONGROUPS,
        GET_FILTER_LIST_LOCATION_ID,
        STATUS_SERVICE,
         }

    public DBRequestType getType() {
        return type;
    }

    public void setType(DBRequestType type) {
        this.type = type;
    }
}
