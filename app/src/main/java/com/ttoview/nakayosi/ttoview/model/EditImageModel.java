package com.ttoview.nakayosi.ttoview.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Taeun on 15. 1. 7..
 */
public class EditImageModel implements Parcelable {

    private long mMediaId;
    private String mEditMediaPath;
    private String mOriginalMediaPath;

    public long getMediaId() {
        return mMediaId;
    }

    public void setMediaId(long mMediaId) {
        this.mMediaId = mMediaId;
    }

    public String getEditMediaPath() {
        return mEditMediaPath;
    }

    public void setEditMediaPath(String mMediaPath) {
        this.mEditMediaPath = mMediaPath;
    }

    public String getOriginalMediaPath() {
        return mOriginalMediaPath;
    }

    public void setOriginalMediaPath(String mOriginalMediaPath) {
        this.mOriginalMediaPath = mOriginalMediaPath;
    }

    public EditImageModel() {

    }

    public EditImageModel(Parcel src) {
        mMediaId = src.readLong();
        mEditMediaPath = src.readString();
        mOriginalMediaPath = src.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(mMediaId);
        parcel.writeString(mEditMediaPath);
        parcel.writeString(mOriginalMediaPath);
    }

    public static final Parcelable.Creator<EditImageModel> CREATOR = new Parcelable.Creator<EditImageModel>() {

        public EditImageModel createFromParcel(Parcel in) {
            return new EditImageModel(in);
        }

        public EditImageModel[] newArray(int size) {
            return new EditImageModel[size];
        }
    };
}
