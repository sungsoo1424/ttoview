package com.ttoview.nakayosi.ttoview.util.picker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ttoview.nakayosi.ttoview.R;
import com.ttoview.nakayosi.ttoview.util.picker.loader.MediaThumbnailLoader;
import com.ttoview.nakayosi.ttoview.util.picker.media.BaseMediaData;
import com.ttoview.nakayosi.ttoview.util.picker.media.MediaConstSet;
import com.ttoview.nakayosi.ttoview.util.picker.media.MediaGroup;
import com.ttoview.nakayosi.ttoview.util.picker.media.MediaItem;


public class PickerListRow extends LinearLayout {
	private Context mContext;
	
	private RecyclingImageView mImageView;
	private View mFrameView, mCheckView;;
	private TextView mTitleTextView;

	private ImageView mEndLineImageView;

	private BaseMediaData mMediaItem;
	

	public PickerListRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PickerListRow(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		View.inflate(context, R.layout.item_picker_list, this);
		setOrientation(VERTICAL);
		
		mContext = context;
		mFrameView = findViewById(R.id.viewframe);
		mCheckView = findViewById(R.id.viewCheck);

		mImageView = (RecyclingImageView) findViewById(R.id.pickerThumbImageView);
		mTitleTextView = (TextView) findViewById(R.id.titleLabel);
		mEndLineImageView = (ImageView) findViewById(R.id.endlineImageView);
	}

	public void onMovedToScrapHeap() {
		
		MediaThumbnailLoader.getInstance().cancelLoad(mImageView);
		mImageView.setImageDrawable(null);
		mMediaItem = null;
		
		mCheckView.setSelected(false);
	}

	public void setData(BaseMediaData data) {
		mMediaItem = data;
		
		if ( data instanceof MediaGroup) {
			MediaGroup folder = (MediaGroup)data;
			mTitleTextView.setText(folder.getName() + " (" + folder.getItemCount() + ")");
		}
		else if ( data instanceof MediaItem) {
			MediaItem item = (MediaItem)data;
			mTitleTextView.setText(item.getName());
		}
		mImageView.setImageDrawable(null);
		switch ( data.getType() ) {
		case MediaConstSet.Type.IMAGE:
//			mImageView.setImageResource(R.drawable.icon_add_photo_default);
			break;
//		case MediaConstSet.Type.AUDIO:
//			mImageView.setImageResource(R.drawable.audio_view_no_img);
//			break;
//		case MediaConstSet.Type.VIDEO:
//			mImageView.setImageResource(R.drawable.video_view_no_img);
//			break;
		}
		
		MediaThumbnailLoader.getInstance().cancelLoad(mImageView);
		MediaThumbnailLoader.getInstance().load(mImageView, data, null);
	}

	
	public void setSelectVisibility(int visibility) {
		mCheckView.setVisibility(visibility);
	}
	
	public void setSelected(boolean selected) {
		mCheckView.setSelected(selected);
	}
	
	public void setEndLine(boolean endLine) {
		mEndLineImageView.setVisibility(endLine ? View.VISIBLE : View.GONE);
	}
	
}