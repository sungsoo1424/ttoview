package com.ttoview.nakayosi.ttoview.util.picker.media;

public class MediaConstSet {


	public abstract static class MediaImage {
		public abstract static class GroupMeta {
			
		}
	}
	
	public abstract static class Type {
		public final static int IMAGE = 0x01;
		public final static int AUDIO = 0x02;
		public final static int VIDEO = 0x03;
        public final static int CAMERA = 0x04;
	}
	
	public abstract static class Meta {
		/**
		 * Type is Integer
		 * Image
		 */
		public final static String ROTATE = "ROTATE";
		
		/**
		 * Type is Long
		 * Image & Video
		 */
		public final static String THUMBNAIL_ID = "THUMBNAIL_ID";
		
		/**
		 * Type is Integer
		 * @see android.provider.MediaStore.Audio
		 */
		public final static String ALBUM_KEY = "ALBUM_KEY";
		
		/**
		 * Type is String
		 * @see android.provider.MediaStore.Audio
		 */
		public final static String ALBUM_NAME = "ALBUM_NAME";
		
		/**
		 * Type is String
		 * @see android.provider.MediaStore.Audio
		 */
		public final static String ARTIST = "ARTIST";
		
		/**
		 * Type is Long
		 * @see android.provider.MediaStore.Audio & Video
		 */
		public final static String DURATION = "DURATION";
		
		
		/**
		 * Type is String
		 * @see android.provider.MediaStore.Audio & Video
		 */
		public final static String TITLE = "TITLE";
		
		
		/**
		 * Type is String
		 * @see android.provider.MediaStore.Video
		 */
		public final static String DESCRIPTION = "DESCRIPTION";
		
		
	}

}
