package com.ttoview.nakayosi.ttoview.util.picker.media;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.HashMap;

public abstract class BaseMediaData implements Parcelable, IMediaThumbnail {
    public static final String INTENT_FILER = BaseMediaData.class.getName();
    public static final String INTENT_FILER_LIST = BaseMediaData.class.getName() + "_LIST";

    private int mSelected;

    private long mId;
    private String mName;
    private int mType;

    private Uri mThumbnailPath;
    private HashMap<String, String> mMetaMap;

    public BaseMediaData() {
        mMetaMap = new HashMap<String, String>();
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public void setType(int type) {
        mType = type;
    }



    public void setThumbnailPath(Uri thumbnailPath) {
        this.mThumbnailPath = thumbnailPath;
    }

    public int isSelected() {
        return mSelected;
    }

    public void setSelected(int selected) {
        this.mSelected = selected;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }


    public void addMeta(String key, String value) {
        mMetaMap.put(key, value);
    }

    public String getMeta(String key) {
        return mMetaMap.get(key);
    }

    @Override
    public boolean equals(Object obj) {
        boolean retVal = super.equals(obj);
        if ( !retVal ) {
            if ( obj != null && (obj instanceof BaseMediaData) ) {
                BaseMediaData data = (BaseMediaData)obj;
                retVal = (data.mId == this.mId);

            }
        }


        return retVal;
    }

    @Override
    public int hashCode() {
        return String.valueOf(mId).hashCode();
    }

    //

    @Override
    public long getThumbnailId() {
        String id = getMeta(MediaConstSet.Meta.THUMBNAIL_ID);
        if ( !TextUtils.isEmpty(id) ) {
            return Long.parseLong(id);
        }
        else {
            return -1;
        }
    }

    @Override
    public int getRotate() {
        String rotate = getMeta(MediaConstSet.Meta.ROTATE);
        if ( !TextUtils.isEmpty(rotate) ) {
            return Integer.parseInt(rotate);
        }
        else {
            return 0;
        }
    }

    @Override
    public int getType() {
        return mType;
    }

    @Override
    public Uri getThumbnailPath() {
        return mThumbnailPath;
    }

    //
    @Override
    public int describeContents() {
        return 0;
    }

    public BaseMediaData(Parcel src) {
        mSelected = src.readInt();
        mId = src.readLong();
        mName = src.readString();
        mType = src.readInt();
        mThumbnailPath = src.readParcelable(Uri.class.getClassLoader());
        mMetaMap = src.readHashMap(HashMap.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mSelected);
        dest.writeLong(mId);
        dest.writeString(mName);
        dest.writeInt(mType);
        dest.writeParcelable(mThumbnailPath, 0);
        dest.writeMap(mMetaMap);
    }

}
