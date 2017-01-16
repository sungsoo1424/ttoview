package com.ttoview.nakayosi.ttoview.util.picker.loader;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.widget.ImageView;


import com.ttoview.nakayosi.ttoview.util.picker.image.ImageCache;
import com.ttoview.nakayosi.ttoview.util.picker.image.RecyclingBitmapDrawable;
import com.ttoview.nakayosi.ttoview.util.picker.image.RecyclingBitmapWrapper;
import com.ttoview.nakayosi.ttoview.util.picker.media.IMediaThumbnail;
import com.ttoview.nakayosi.ttoview.util.picker.media.MediaConstSet;
import com.ttoview.nakayosi.ttoview.util.picker.view.RecyclingImageView;

import java.util.HashMap;
import java.util.Set;


public class MediaThumbnailLoader {
	private HashMap<ImageView, ThumbnailLoaderTask> mThumbnailLoaderTaskMap;
	private int mThumbnailKind;
	private ImageCache mImageCache;
	
	
	private final Object LockObject = new Object();

	
	private static final MediaThumbnailLoader _instance = new MediaThumbnailLoader();
	
	public static MediaThumbnailLoader getInstance() {
		return _instance;
	}
		
	private MediaThumbnailLoader() {
		mThumbnailLoaderTaskMap = new HashMap<ImageView, ThumbnailLoaderTask>();
		mThumbnailKind = Thumbnails.MINI_KIND;
		mImageCache = new ImageCache(0.2F);
	}

	
	public void release() {
		synchronized (LockObject) {
			Set<ImageView> keySet = mThumbnailLoaderTaskMap.keySet();
			for ( ImageView keyView : keySet ) {

				ThumbnailLoaderTask loader = mThumbnailLoaderTaskMap.get(keyView);
				if ( loader != null && !loader.isCancelled() ) {
					loader.cancel(true);
				}

                keyView.setImageDrawable(null);
			}
			mThumbnailLoaderTaskMap.clear();
		
			mImageCache.clearCache();
		}
	}
	
	public void cancelLoad(RecyclingImageView imageView) {
		synchronized (LockObject) {
			ThumbnailLoaderTask loader = mThumbnailLoaderTaskMap.remove(imageView);
			if ( loader != null && !loader.isCancelled() ) {
				loader.cancel(true);
			}	
		}
	}
	
	public void load(RecyclingImageView imageView, IMediaThumbnail thumbnail, Options opts) {
		synchronized (LockObject) {

            if(null == thumbnail) {
//                imageView.setImageResource(R.drawable.btn_add);
                return;
            }

			RecyclingBitmapWrapper bitmapWrapper = getBitmapFromCache(thumbnail);
			if ( bitmapWrapper != null ) {
				Bitmap bitmap = bitmapWrapper.getBitmap();
				if ( bitmap != null && !bitmap.isRecycled() ) {
					RecyclingBitmapDrawable drawable = new RecyclingBitmapDrawable(imageView.getResources(), bitmapWrapper);
					imageView.setImageDrawable(drawable);
				}
				else {
					try {
						ThumbnailLoaderTask loader = new ThumbnailLoaderTask(imageView, thumbnail);
						mThumbnailLoaderTaskMap.put(imageView, loader);
						loader.execute(opts);
					} catch (Exception e) {
						mThumbnailLoaderTaskMap.remove(imageView);
					}
				}
			}
			else {
				try {
					ThumbnailLoaderTask loader = new ThumbnailLoaderTask(imageView, thumbnail);
					mThumbnailLoaderTaskMap.put(imageView, loader);
					loader.execute(opts);
				} catch (Exception e) {
					mThumbnailLoaderTaskMap.remove(imageView);
				}
			}
		}
	}
	
	private void addToCache(IMediaThumbnail thumbnail, RecyclingBitmapWrapper bitmapWrapper) {
		long id = thumbnail.hashCode();
		mImageCache.addBitmapToCache(String.valueOf(id), bitmapWrapper);
	}
	
	private RecyclingBitmapWrapper getBitmapFromCache(IMediaThumbnail thumbnail) {
		long id = thumbnail.hashCode();
		return mImageCache.getBitmapFromMemCache(String.valueOf(id));
	}

	
    private class ThumbnailLoaderTask extends AsyncTask<Options, Void, Bitmap> {
    	private final ImageView imageView;
    	private final IMediaThumbnail thumbnail;

        public ThumbnailLoaderTask(ImageView imageView, IMediaThumbnail thumbnail) {
            this.imageView = imageView;
            this.thumbnail = thumbnail;
        }
        
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Bitmap doInBackground(Options... args) {
			Bitmap newBitmap = null;
			try {
				Options options = args[0];
				
				
				switch ( thumbnail.getType() ) {
				case MediaConstSet.Type.IMAGE:
					newBitmap = Thumbnails.getThumbnail(
							imageView.getContext().getContentResolver(),
							thumbnail.getThumbnailId(),
							mThumbnailKind,
							options);
					
					int rotate = thumbnail.getRotate();
					Matrix matrix = new Matrix();
					matrix.setRotate(rotate);
					if ( newBitmap != null ) {
						newBitmap = Bitmap.createBitmap(newBitmap, 0, 0, newBitmap.getWidth(), newBitmap.getHeight(), matrix, true);
					}
					
					break;
				
				case MediaConstSet.Type.VIDEO:
					newBitmap = MediaStore.Video.Thumbnails.getThumbnail(
							imageView.getContext().getContentResolver(), 
							thumbnail.getThumbnailId(),
							mThumbnailKind, 
							options);
					break;
					
				case MediaConstSet.Type.AUDIO:
					Uri thumbUri = thumbnail.getThumbnailPath();
					if ( thumbUri != null ) {
						newBitmap = BitmapFactory.decodeFile(thumbUri.getPath(), options);
					}
					
					break;
				}
				
				if ( isCancelled() && newBitmap != null ) {
					newBitmap.recycle();
					newBitmap = null;
				}
				
				
			} catch (OutOfMemoryError e) {
				System.gc();
			}
			
			return newBitmap;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			if ( !isCancelled() ) {
				if ( result != null && !result.isRecycled() ) {
					RecyclingBitmapWrapper bitmapWrapper = new RecyclingBitmapWrapper(result);
					addToCache(thumbnail, bitmapWrapper);
					
					RecyclingBitmapDrawable drawable = new RecyclingBitmapDrawable(imageView.getResources(), bitmapWrapper);
					imageView.setImageDrawable(drawable);
				}
			}
		}
    }
}
