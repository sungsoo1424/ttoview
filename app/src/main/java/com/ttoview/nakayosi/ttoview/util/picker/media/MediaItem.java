package com.ttoview.nakayosi.ttoview.util.picker.media;

import android.os.Parcel;
import android.os.Parcelable;

public class MediaItem extends BaseMediaData {

	public static final String INTENT_FILER = MediaItem.class.getName();
	
	private String mPath;
	private long mSize;
    private long mGroupId;
	
	public MediaItem() {
		super();
	}
	
	public String getPath() {
		return mPath;
	}
	
	public void setPath(String path) {
		this.mPath = path;
	}
	
	public long getSize() {
		return mSize;
	}
	
	public void setSize(long size) {
		mSize = size;
	}

    public long getGroupId() {
        return mGroupId;
    }

    public void setGroupId(long mGroupId) {
        this.mGroupId = mGroupId;
    }

    //
	@Override
	public int describeContents() {
		return 0;
	}
	
	public MediaItem(Parcel src) {
		super(src);
		mPath = src.readString();
		mSize = src.readLong();
        mGroupId = src.readLong();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(mPath);
		dest.writeLong(mSize);
        dest.writeLong(mGroupId);
	}
	
	
	public static final Parcelable.Creator<MediaItem> CREATOR = new Parcelable.Creator<MediaItem>() {
		public MediaItem createFromParcel(Parcel in) {
			return new MediaItem(in);
		}
		public MediaItem[] newArray(int size) {
			return new MediaItem[size];
		}
	};
}
