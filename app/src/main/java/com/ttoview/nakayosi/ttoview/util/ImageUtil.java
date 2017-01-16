package com.ttoview.nakayosi.ttoview.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.ttoview.nakayosi.ttoview.SimpleNFCApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by hwangtaeun on 2014. 9. 4..
 * 비트맵 컨트롤
 */
public class ImageUtil {

    public static final int MAX_ORIGINAL_IMAGE_SIZE = 4080;
    public static final int MAX_IMAGE_SIZE = 1280;
    public static final int MAX_THUMB_IMAGE_SIZE = 300;
    public static ImageLoadingListener mAimateFirstListener = new AnimateFirstDisplayListener();

    /**
     * 유니버셜 이미지 로더 애니메이션
     */
    public static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        private final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

    /**
     * 유니버셜 이미지 로더 사용
     *
     * @param imageUrl
     * @param imageView
     */
    public static void displayImageLoad(String imageUrl, ImageView imageView) {

        ImageLoader.getInstance().displayImage(imageUrl, imageView, SimpleNFCApp.getInstance().mDisplayImageOptions, mAimateFirstListener);
    }

    /**
     * 유니버셜 이미지 로더 사용
     *
     * @param imageUrl
     * @param imageView
     */
    public static void displayImageLoad(String imageUrl, ImageView imageView, int resourceId) {


        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(resourceId)
                .showImageOnFail(resourceId)
                .showImageForEmptyUri(resourceId)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .considerExifParams(true)
                .build();
        ImageLoader.getInstance().displayImage(imageUrl, imageView, options, mAimateFirstListener);
    }

    /**
     * 유니버셜 이미지 로더 사용 - 모서리 라운드
     *
     * @param imageUrl
     * @param imageView
     */
    public static void displayImageLoadThumb(String imageUrl, ImageView imageView) {
        ImageLoader.getInstance().displayImage(imageUrl, imageView, SimpleNFCApp.getInstance().mDisplayImageOptionsThumbnail, mAimateFirstListener);
    }

    /**
     * 유니버셜 이미지 로더 사용 - 전체 라운드
     *
     * @param imageUrl
     * @param imageView
     */
    public static void displayImageLoadRoundProfile(String imageUrl, ImageView imageView) {
        ImageLoader.getInstance().displayImage(imageUrl, imageView, SimpleNFCApp.getInstance().mDisplayImageOptionsRound, mAimateFirstListener);
    }

    /**
     * 이미지의 크기 가져오기
     *
     * @param filePath
     * @return
     */
    public static int[] getImageSize(String filePath) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        BitmapFactory.decodeFile(filePath, options);
        return new int[]{options.outWidth, options.outHeight};
    }

    /**
     * 사진이 돌아가 있을 경우 사진을 돌려주는 처리를 한다. FilePath to TempPath
     *
     * @param path 사진 경로
     * @return 사진 경로
     */
    public static String getResizeBitmapPath(final String path) {

        // 임시 사진 저장 경로
        String extension = path.substring(path.lastIndexOf("."), path.length());
        String tempPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        tempPath += File.separator + ".temp";
        // 경로가 존재하지 않는다면 생성한다.
        File tempDir = new File(tempPath);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        tempPath += File.separator + System.currentTimeMillis() + extension;
        // 사진 회전 - 특정단말기에서 촬영한 사진의 로테이션(Exif)이 다를수도 있다.

        Bitmap dstBitmap = getBitmapLimitScale(path);

        // 파일 저장
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(tempPath);
            if (extension.equalsIgnoreCase(".JPG") || extension.equalsIgnoreCase(".JPEG")) {
                dstBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            } else if (extension.equalsIgnoreCase(".PNG")) {
                dstBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } else {
                return path;
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            //showDefaultErrorDialg();
            return null;
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != dstBitmap) {
                dstBitmap.recycle();
            }
        }

        return tempPath;
    }

    /**
     * 사진이 돌아가 있을 경우 사진을 돌려주는 처리를 한다. Bitmap to TempPath
     *
     * @param bitmap 사진
     * @return 사진 경로
     */
    public static String getResizeBitmapPath(final Bitmap bitmap) {

        // 임시 사진 저장 경로
        String tempPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        tempPath += File.separator + ".temp";
        // 경로가 존재하지 않는다면 생성한다.
        File tempDir = new File(tempPath);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        tempPath += File.separator + System.currentTimeMillis() + ".jpg";

        // 파일 저장
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(tempPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            if(null != bitmap) {
//                bitmap.recycle();
//            }
        }

        return tempPath;
    }

    /**
     * 이미지의 썸네일 스케일링
     *
     * @param filePath
     * @return
     */
    public static Bitmap getBitmapThumbScale(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int scale = 0;
        if (options.outHeight > MAX_THUMB_IMAGE_SIZE || options.outWidth > MAX_THUMB_IMAGE_SIZE) {
            scale = (int) Math.pow(2, (int) Math.round(Math.log(MAX_THUMB_IMAGE_SIZE / (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = scale;
        return rotateBitmap(BitmapFactory.decodeFile(filePath, options), filePath);
    }

    /**
     * 이미지의 최대크기제한 스케일링
     *
     * @param filePath
     * @return
     */
    public static Bitmap getBitmapLimitScale(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int scale = 0;
        if (options.outHeight > MAX_IMAGE_SIZE || options.outWidth > MAX_IMAGE_SIZE) {
            scale = (int) Math.pow(2, (int) Math.round(Math.log(MAX_IMAGE_SIZE / (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = scale;
        return rotateBitmap(BitmapFactory.decodeFile(filePath, options), filePath);
    }

    /**
     * 이미지의 원본 스케일링
     *
     * @param filePath
     * @return
     */
    public static Bitmap getBitmapScale(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int scale = 0;
        if (options.outHeight > MAX_ORIGINAL_IMAGE_SIZE || options.outWidth > MAX_ORIGINAL_IMAGE_SIZE) {
            scale = (int) Math.pow(2, (int) Math.round(Math.log(MAX_IMAGE_SIZE / (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = scale;
        return rotateBitmap(BitmapFactory.decodeFile(filePath, options), filePath);
    }

    /**
     * 비트맵 로테이트 변경
     *
     * @param bitmap
     * @param path
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, String path) {

        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int dipTopx(float dip, Context context) {
        int num = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dip,
                context.getResources().getDisplayMetrics());
        return num;
    }

    /**
     * px to dp
     *
     * @param px
     * @param context
     * @return
     */
    public static float pxTodip(int px, Context context) {
        float num = px / context.getResources().getDisplayMetrics().density;
        return num;
    }

    /**
     * Drawable을 Bitmap으로 바꾸어 준다.
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        final int width = drawable.getIntrinsicWidth();
        final int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    public static int[] drawableToIntArray(Drawable drawable) {
        Bitmap bitmap = drawableToBitmap(drawable);

        final int bitmapWidth = bitmap.getWidth();
        final int bitmapHeight = bitmap.getHeight();

        int[] colors = new int[bitmapWidth * bitmapHeight];
        bitmap.getPixels(colors, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);

        return colors;
    }

    public static int[] bitmapToIntArray(Bitmap bitmap) {
        final int bitmapWidth = bitmap.getWidth();
        final int bitmapHeight = bitmap.getHeight();

        int[] colors = new int[bitmapWidth * bitmapHeight];
        bitmap.getPixels(colors, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);

        return colors;
    }

    public static boolean isGifFile(String url) {
        return url.toUpperCase().contains(".GIF");
    }

}
