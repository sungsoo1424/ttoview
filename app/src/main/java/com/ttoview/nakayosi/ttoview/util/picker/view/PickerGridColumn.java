package com.ttoview.nakayosi.ttoview.util.picker.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ttoview.nakayosi.ttoview.R;
import com.ttoview.nakayosi.ttoview.util.picker.activity.BasePickerActivity;
import com.ttoview.nakayosi.ttoview.util.picker.loader.MediaThumbnailLoader;
import com.ttoview.nakayosi.ttoview.util.picker.media.BaseMediaData;
import com.ttoview.nakayosi.ttoview.util.picker.media.MediaConstSet;

public class PickerGridColumn extends RelativeLayout {
    private RecyclingImageView mThumbImageView;
    private ImageView mFrameImageView, mCheckImageView;
    private RelativeLayout mCheckImageBorder;
    private TextView mSelectCountLabel;
    private BaseMediaData mBaseMediaData;
    private PickerGridColumnDelegate mDelegate;
    private Context mContext;

    public PickerGridColumn(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public PickerGridColumn(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PickerGridColumn(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        View.inflate(context, R.layout.view_column_picker_grid2, this);

        mThumbImageView = (RecyclingImageView) findViewById(R.id.column_picker_grid_ImageView_thumb);
        mFrameImageView = (ImageView) findViewById(R.id.column_picker_grid_ImageView_frame);
        mCheckImageView = (ImageView) findViewById(R.id.column_picker_grid_ImageView_check);
        mCheckImageBorder = (RelativeLayout) findViewById(R.id.column_picker_grid_border);
        mSelectCountLabel = (TextView) findViewById(R.id.column_picker_grid_check_text);

        setOnClickListener(mClickListener);
        setEnabled(false);
        onMovedToScrapHeap();
    }

    public void onMovedToScrapHeap() {
        mBaseMediaData = null;
        mFrameImageView.setVisibility(View.GONE);
        mThumbImageView.setVisibility(View.GONE);
        mCheckImageView.setVisibility(View.GONE);

        MediaThumbnailLoader.getInstance().cancelLoad(mThumbImageView);
        mThumbImageView.setImageDrawable(null);
    }


    public void setDelegate(PickerGridColumnDelegate delegate) {
        mDelegate = delegate;
    }

    public void setData(BaseMediaData data) {
        mBaseMediaData = data;
        setEnabled(true);
        mFrameImageView.setVisibility(View.VISIBLE);

        switch (data.getType()) {
            case MediaConstSet.Type.IMAGE:
               
                mThumbImageView.setVisibility(View.VISIBLE);
                MediaThumbnailLoader.getInstance().load(mThumbImageView, data, null);
                break;
            case MediaConstSet.Type.CAMERA:
                mFrameImageView.setImageResource(R.drawable.img_camera);
                break;
//		case MediaConstSet.Type.AUDIO:
//			mFrameImageView.setImageResource(R.drawable.audio_view_no_img);
//			break;
//		case MediaConstSet.Type.VIDEO:
//			mFrameImageView.setImageResource(R.drawable.video_view_no_img);
//			break;
        }

    }

    public BaseMediaData getData() {
        return mBaseMediaData;
    }

    public void setSelectVisibility(int visibility) {
        if (mBaseMediaData != null) {
            mCheckImageView.setVisibility(visibility);
        }
    }

    public void setSelected(boolean selected) {
        mCheckImageView.setSelected(selected);
        if (!selected) {
            mCheckImageBorder.setVisibility(View.GONE);
            mSelectCountLabel.setText("");
        } else {
            mCheckImageBorder.setVisibility(View.VISIBLE);
        }
    }

    public void setCountLabel(int count) {
        mSelectCountLabel.setText(count + "");
    }

    private OnClickListener mClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (null != mBaseMediaData && mBaseMediaData.getName().equals("CAMERA")) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                ((Activity) mContext).startActivityForResult(intent, BasePickerActivity.ACTIVITY_RESULT_CAMERA);
            } else if (mDelegate != null && mBaseMediaData != null) {
                mDelegate.onClick(PickerGridColumn.this, mBaseMediaData);
            }
        }
    };

    public static interface PickerGridColumnDelegate {
        public void onClick(PickerGridColumn column, BaseMediaData data);
    }
}
