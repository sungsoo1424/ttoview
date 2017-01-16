package com.ttoview.nakayosi.ttoview.util.picker.loader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.ttoview.nakayosi.ttoview.util.picker.media.MediaConstSet;
import com.ttoview.nakayosi.ttoview.util.picker.media.MediaGroup;
import com.ttoview.nakayosi.ttoview.util.picker.media.MediaItem;

import java.io.File;
import java.util.ArrayList;

public class ImageMediaLoader {
	public static final String DEFAULT_BUCKET_SORT_ORDER = "upper("+ MediaStore.Images.ImageColumns.DATE_TAKEN + ") DESC";

	public static ArrayList<MediaGroup> getMediaGroupList(Context context) {
		
		String[] projection = {
				MediaStore.Images.ImageColumns.BUCKET_ID,
				MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME
		};
		
		
		final Uri uriFolder = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
				.buildUpon()
				.appendQueryParameter("distinct", "true")
				.build();

		Cursor cursor = context.getContentResolver().query(uriFolder, projection, null, null, DEFAULT_BUCKET_SORT_ORDER);
		ArrayList<MediaGroup> groupList = new ArrayList<MediaGroup>();
		ArrayList<MediaGroup> tempList = new ArrayList<MediaGroup>();

        MediaGroup groupModel = new MediaGroup();
        groupModel.setName("전체보기");
        groupModel.setId(-1);

        groupList.add(groupModel);

		if ( cursor != null ) {
			while (cursor.moveToNext()) {
				MediaGroup group = getMediaGroupFromCursor(cursor, context);
	
				if ( (group.getItemCount() != 0) && !groupList.contains(group) ) {
					String groupName = group.getName();
					if ( groupName.equals("Camera") ) {
						groupList.add(group);
					} 
					else {
						tempList.add(group);
					}
				}
			}
			
	
			groupList.addAll(tempList);
			
			cursor.close();
		}
		groupList.trimToSize();

		return groupList;
	}
	
	public static ArrayList<MediaItem> getMediaItemList(Context context) {
		
		String[] projection = {
				MediaStore.Images.ImageColumns._ID,
				MediaStore.Images.ImageColumns.DATA,
				MediaStore.Images.ImageColumns.ORIENTATION,
				MediaStore.Images.ImageColumns.SIZE,
				MediaStore.Images.ImageColumns.DISPLAY_NAME
		};

		
		Uri uriFolder = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
				.buildUpon()
				.build();

		Cursor cursor = context.getContentResolver().query(uriFolder, projection, null, null, DEFAULT_BUCKET_SORT_ORDER);

		ArrayList<MediaItem> itemList = new ArrayList<MediaItem>();

		if ( cursor != null ) {
			while (cursor.moveToNext()) {
				MediaItem item = getMediaItemFromCursor(cursor);

				if ( !itemList.contains(item) && new File(item.getPath()).exists() ) {
					itemList.add(item);
				}
			}
	
			cursor.close();
		}
		
		itemList.trimToSize();
		return itemList;
	}

	public static ArrayList<MediaItem> getMediaItemList(Context context, final MediaGroup group) {
		
		String[] projection = {
				MediaStore.Images.ImageColumns._ID,
				MediaStore.Images.ImageColumns.DATA,
				MediaStore.Images.ImageColumns.ORIENTATION,
				MediaStore.Images.ImageColumns.SIZE,
				MediaStore.Images.ImageColumns.DISPLAY_NAME
		};

		final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
		final String[] selectionArgs = { String.valueOf(group.getId()) };

		final Uri uriFolder = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
				.buildUpon()
				.build();

		Cursor cursor = context.getContentResolver().query(uriFolder, projection, selection, selectionArgs, DEFAULT_BUCKET_SORT_ORDER);
		
		ArrayList<MediaItem> itemList = new ArrayList<MediaItem>();
		
		if ( cursor != null ) {
			while (cursor.moveToNext()) {
				MediaItem item = getMediaItemFromCursor(cursor);
	
				if ( !itemList.contains(item) && new File(item.getPath()).exists() ) {
                    item.setGroupId(group.getId());
					itemList.add(item);
				}
			}

			cursor.close();
		}
		
		itemList.trimToSize();
		
		return itemList;
	}
	

	public static String getRealPathFromURI(final Context context, Uri contentUri) {
		String[] projection = { MediaStore.Images.Media.DATA };

		Cursor cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
		String path = contentUri.getPath();
		
		if ( cursor != null ) {
			int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			
			if (cursor.moveToNext()) {
				if ( cursor.getColumnCount() > 0) {
					path = cursor.getString(columnIndex);
				}
			}
			
			cursor.close();
		}

		return path;
	}

	private static int getMediaItemCount(final Context context, long bId) {
		int mediaCount = 0;
		String[] projection = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };
		final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
		final String[] selectionArgs = { String.valueOf(bId) };

		final Uri uriFolder = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
				.buildUpon()
				.build();

		Cursor cursor = context.getContentResolver().query(uriFolder, projection, selection, selectionArgs, null);
		if ( cursor != null ) {
			mediaCount = cursor.getCount();
			int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			
			while ( cursor.moveToNext() ) {
				if ( cursor.getColumnCount() > 0 ) {
					if ( !new File(cursor.getString(columnIndex)).exists() ) {
						mediaCount--;
					}	
				} 
			}
			cursor.close();
		}

		return mediaCount;
	}

	private static Uri getMediaGroupThumbUri(final Context context, long bId) {
		Uri thumbnailUri = null;
		String[] projection = {
				MediaStore.Images.ImageColumns._ID,
				MediaStore.Images.ImageColumns.DATA
		};

		final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
		final String[] selectionArgs = { String.valueOf(bId) };

		final Uri uriFolder = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
				.buildUpon()
				.appendQueryParameter("limit", "1")
				.build();

		Cursor cursor = context.getContentResolver().query(uriFolder, projection, selection, selectionArgs, DEFAULT_BUCKET_SORT_ORDER);

		if ( cursor != null ) {
			while (cursor.moveToNext()) {

				long imageId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
	
				thumbnailUri = Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, String.valueOf(imageId));
			}
	
			cursor.close();
		}
		

		return thumbnailUri;
	}

	private static long getMediaGroupThumbId(final Context context, long bid) {
		long thumbId = -1;
		String[] projection = {
				MediaStore.Images.ImageColumns._ID,
				MediaStore.Images.ImageColumns.DATA
		};

		final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
		final String[] selectionArgs = { String.valueOf(bid) };

		final Uri uriFolder = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
				.buildUpon()
				.appendQueryParameter("limit", "1")
				.build();

		Cursor cursor = context.getContentResolver().query(uriFolder, projection, selection, selectionArgs, DEFAULT_BUCKET_SORT_ORDER);

		if ( cursor != null ) {
			if ( cursor.moveToNext() ) {
				thumbId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
			}
	
			cursor.close();
		}
		return thumbId;
	}

	private static int getMediaGroupThumbRotate(final Context context, long bId) {
		int imageRotate = -1;
		String[] projection = { MediaStore.Images.ImageColumns.ORIENTATION };

		final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
		final String[] selectionArgs = { String.valueOf(bId) };

		final Uri uriFolder = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
				.buildUpon()
				.appendQueryParameter("limit", "1")
				.build();

		Cursor cursor = context.getContentResolver().query(uriFolder, projection, selection, selectionArgs, null);

		if ( cursor != null ) {
			if ( cursor.moveToNext() ) {
				imageRotate = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));
			}

			cursor.close();
		}
	
		return imageRotate;
	}



	private static String getMediaGroupPath(final Context context, String id) {
		String groupPath = null;
		String[] projection = { MediaStore.Images.ImageColumns.DATA };

		final Uri uriFolder = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
				.buildUpon()
				.appendQueryParameter("limit", "1")
				.build();

		Cursor cursor = context.getContentResolver().query(uriFolder, projection, null, null, DEFAULT_BUCKET_SORT_ORDER);

		if ( cursor != null ) {
			if (cursor.moveToNext()) {
				String mediaPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
	
				groupPath = new File(mediaPath).getParent();
			}
	
			cursor.close();
		}
		
		return groupPath;
	}
	
	private static MediaGroup getMediaGroupFromCursor(Cursor cursor, Context context) {
		MediaGroup group = new MediaGroup();

		long groupId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID));
		String groupName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME));
		
		group.setId(groupId);
		group.setName(groupName);
		group.setThumbnailPath(getMediaGroupThumbUri(context, groupId));
		group.setItemCount(getMediaItemCount(context, groupId));
		group.setType(MediaConstSet.Type.IMAGE);

		group.addMeta(MediaConstSet.Meta.ROTATE, String.valueOf(getMediaGroupThumbRotate(context, groupId)));
		group.addMeta(MediaConstSet.Meta.THUMBNAIL_ID, String.valueOf(getMediaGroupThumbId(context, groupId)));
		
		return group;
	}
	
	public static MediaItem getMediaItemFromCursor(Cursor cursor) {
		MediaItem item = new MediaItem();

		long imageId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
		int rotate = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));
		String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
		Uri thumbnailPath = Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, String.valueOf(imageId));
		long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE));
		String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));

		item.setName(name);
		item.setId(imageId);
		item.setThumbnailPath(thumbnailPath);
		item.setPath(path);
		item.setType(MediaConstSet.Type.IMAGE);
		item.setSize(size);
		item.addMeta(MediaConstSet.Meta.ROTATE, String.valueOf(rotate));
		item.addMeta(MediaConstSet.Meta.THUMBNAIL_ID, String.valueOf(imageId));

		return item;
	}
}
