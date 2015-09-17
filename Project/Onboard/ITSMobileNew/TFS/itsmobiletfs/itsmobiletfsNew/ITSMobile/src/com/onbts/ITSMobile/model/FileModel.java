package com.onbts.ITSMobile.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.onbts.ITSMobile.model.base.Model;

/**
 * Created by JLAB on 08.04.2014.
 */
public class FileModel extends Model {


    public static final Parcelable.Creator<FileModel> CREATOR
            = new Parcelable.Creator<FileModel>() {
        public FileModel createFromParcel(Parcel in) {
            return new FileModel(in);
        }

        public FileModel[] newArray(int size) {
            return new FileModel[size];
        }
    };
    private String extension;
    private byte[] file;
    private long filesize;
    private String filename;
    private long id;
    private String path;

    public FileModel(Parcel in) {
        readFromParcel(in);
    }

    public FileModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeByteArray(file);
        dest.writeLong(filesize);
        dest.writeLong(id);
        dest.writeString(extension);
        dest.writeString(filename);
        dest.writeString(path);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void readFromParcel(Parcel parcel) {
//        parcel.readByteArray(file);
        filesize = parcel.readLong();
        id = parcel.readLong();
        extension = parcel.readString();
        filename = parcel.readString();
        path = parcel.readString();

    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public long getFilesize() {
        return filesize;
    }

    public void setFilesize(long filesize) {
        this.filesize = filesize;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isImage() {
        return extension != null && (extension.toLowerCase().contains("jpg") || extension.toLowerCase().contains("png") ||
                extension.toLowerCase().contains("gif"));
    }
}
