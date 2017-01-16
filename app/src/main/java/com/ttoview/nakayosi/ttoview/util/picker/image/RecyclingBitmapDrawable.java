/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ttoview.nakayosi.ttoview.util.picker.image;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;

/**
 * A BitmapDrawable that keeps track of whether it is being displayed or cached.
 * When the drawable is no longer being displayed or cached,
 * {@link android.graphics.Bitmap#recycle() recycle()} will be called on this drawable's bitmap.
 */
public class RecyclingBitmapDrawable extends BitmapDrawable {

    static final String LOG_TAG = "RecyclingBitmapDrawable";

    private RecyclingBitmapWrapper mBitmapWrapper;

    public RecyclingBitmapDrawable(Resources res, RecyclingBitmapWrapper bitmapWrapper) {
        super(res, bitmapWrapper.getBitmap());
        mBitmapWrapper = bitmapWrapper;
    }

    /**
     * Notify the drawable that the displayed state has changed. Internally a
     * count is kept so that the drawable knows when it is no longer being
     * displayed.
     *
     * @param isDisplayed - Whether the drawable is being displayed or not
     */
    public void setIsDisplayed(boolean isDisplayed) {
    	mBitmapWrapper.setIsDisplayed(isDisplayed);
    }

    /**
     * Notify the drawable that the cache state has changed. Internally a count
     * is kept so that the drawable knows when it is no longer being cached.
     *
     * @param isCached - Whether the drawable is being cached or not
     */
    public void setIsCached(boolean isCached) {
    	mBitmapWrapper.setIsCached(isCached);
    }
}
