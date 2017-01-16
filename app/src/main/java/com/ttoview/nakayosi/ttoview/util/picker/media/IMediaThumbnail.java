package com.ttoview.nakayosi.ttoview.util.picker.media;

import android.net.Uri;

public interface IMediaThumbnail {
	public int getType();
	
	public int getRotate();
	
	public long getThumbnailId();
	
	public Uri getThumbnailPath();
}
