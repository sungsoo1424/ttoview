package com.ttoview.nakayosi.ttoview.util.picker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ttoview.nakayosi.ttoview.R;
import com.ttoview.nakayosi.ttoview.util.picker.adapter.IPickerAdapter;
import com.ttoview.nakayosi.ttoview.util.picker.adapter.PickerGridAdapter;
import com.ttoview.nakayosi.ttoview.util.picker.adapter.PickerListAdapter;
import com.ttoview.nakayosi.ttoview.util.picker.loader.ImageMediaLoader;
import com.ttoview.nakayosi.ttoview.util.picker.media.MediaGroup;
import com.ttoview.nakayosi.ttoview.util.picker.media.MediaItem;

import java.util.List;


public class PhotoPickerActivity extends BasePickerActivity {

	public void onBackButton(View view) {
		finish();
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_photo_picker);

        if(getIntent().hasExtra("camera")) {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, BasePickerActivity.ACTIVITY_RESULT_CAMERA);
        }
    }

    @Override
    protected void initUI() {
        mListView = (ListView) findViewById(R.id.pickerListView);
        mCurrentImageCountLabel = (TextView) findViewById(R.id.currentImageCountLabel);
        mTotalImageCountLabel = (TextView) findViewById(R.id.totalImageCountLabel);
        mCountLabelLayout = (LinearLayout) findViewById(R.id.countLabelLayout);
    }

	@Override
	protected List<MediaGroup> getGroupList() {
		return ImageMediaLoader.getMediaGroupList(this);
	}

	@Override
    protected List<MediaItem> getItemList(MediaGroup group) {
        return ImageMediaLoader.getMediaItemList(this, group);
    }

    @Override
    protected IPickerAdapter getItemAdapter(List<MediaItem> datas) {
        return new PickerGridAdapter(this, datas);
    }

	@Override
	protected IPickerAdapter getGroupAdapter(List<MediaGroup> datas) {
		return new PickerListAdapter(this, datas);
	}

	@Override
    protected void onItemSelectChanged(boolean select) {

    }
}
