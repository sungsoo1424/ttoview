package com.ttoview.nakayosi.ttoview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ttoview.nakayosi.ttoview.R;

/**
 * Created by jeongchanghyeon on 2015. 10. 13..
 */
public class WriteThumbView extends FrameLayout implements View.OnClickListener{

    private Context mContext;
    private ImageView mDimView;
    private ImageView mThumbImageView;
    private ImageButton mThumbImageDeleteButton;
    private DeleteButtonListner mDeleteButtonListner;

    public WriteThumbView(Context context) {
        super(context);
        init(context);
    }

    public WriteThumbView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WriteThumbView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {

        View.inflate(context, R.layout.view_write_thumb_view, this);
        mContext = context;
        mThumbImageView = (ImageView)findViewById(R.id.thumbImageView);
        mThumbImageDeleteButton = (ImageButton)findViewById(R.id.deleteImageButton);
        mDimView = (ImageView)findViewById(R.id.dimView);
        mThumbImageDeleteButton.setOnClickListener(this);
        mThumbImageDeleteButton.setVisibility(View.GONE);
    }

    public void showDimView(boolean visibility) {

        if(visibility) {
            mDimView.setVisibility(View.VISIBLE);
        } else {
            mDimView.setVisibility(View.GONE);
        }
    }

    public Drawable getThumbDrawable() {
        return mThumbImageView.getDrawable();
    }

    public void setThumbImageBitmap(Bitmap bitmap) {
        mThumbImageView.setImageBitmap(bitmap);
    }

//    public void setThumbImageLoad(String imageUrl) {
//        ImageUtil.displayImageLoad(imageUrl, mThumbImageView);
//    }

    public void showDeleteButton(boolean visiility) {
        if(visiility) {
            mThumbImageDeleteButton.setVisibility(View.VISIBLE);
        } else {
            mThumbImageDeleteButton.setVisibility(View.GONE);
        }
    }

    public void setScaleType(ImageView.ScaleType type) {
        mThumbImageView.setScaleType(type);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.deleteImageButton) {
            mDeleteButtonListner.onDeleteClick();
        }
    }

    public void setOnDeleteButtonListener(DeleteButtonListner listener) {
        mDeleteButtonListner = listener;
    }

    public static interface DeleteButtonListner {
        public void onDeleteClick();
    }
}
