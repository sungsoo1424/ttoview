package com.ttoview.nakayosi.ttoview.util.picker.image;

import android.graphics.Bitmap;

public class RecyclingBitmapWrapper {
    static final String LOG_TAG = "RecyclingBitmapWrapper";
    
	private Bitmap mBitmap;
	
	private int mCacheRefCount = 0;
    private int mDisplayRefCount = 0;

    private boolean mHasBeenDisplayed;

	
	public RecyclingBitmapWrapper(Bitmap bitmap) {
		mBitmap = bitmap;
	}
	
	public Bitmap getBitmap() {
		return mBitmap;
	}
	
	public void retainCount() {
		synchronized (this) {
			mDisplayRefCount++;
		}
		checkState();
	}

	public void releaseCount() {
		synchronized (this) {
			mDisplayRefCount--;
		}
		checkState();
	}
	
	public void setIsDisplayed(boolean isDisplayed) {
		synchronized (this) {
            if (isDisplayed) {
                mDisplayRefCount++;
                mHasBeenDisplayed = true;
            } else {
                mDisplayRefCount--;
            }
        }

        // Check to see if recycle() can be called
        checkState();
	}
	
    public void setIsCached(boolean isCached) {
    	synchronized (this) {
            if (isCached) {
                mCacheRefCount++;
            } else {
                mCacheRefCount--;
            }
        }

        // Check to see if recycle() can be called
        checkState();
    }
    
    private synchronized void checkState() {
        // If the drawable cache and display ref counts = 0, and this drawable
        // has been displayed, then recycle
        if (mCacheRefCount <= 0 && mDisplayRefCount <= 0 && mHasBeenDisplayed && hasValidBitmap()) {
            getBitmap().recycle();
        }
    }
    
    private synchronized boolean hasValidBitmap() {
        Bitmap bitmap = getBitmap();
        return bitmap != null && !bitmap.isRecycled();
    }
}
