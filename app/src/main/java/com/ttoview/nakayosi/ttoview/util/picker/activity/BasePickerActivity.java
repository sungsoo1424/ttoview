package com.ttoview.nakayosi.ttoview.util.picker.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.RecyclerListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ttoview.nakayosi.ttoview.ActionBarActivity;
import com.ttoview.nakayosi.ttoview.R;
import com.ttoview.nakayosi.ttoview.model.EditImageModel;
import com.ttoview.nakayosi.ttoview.util.picker.adapter.GallerySpinnerAdapter;
import com.ttoview.nakayosi.ttoview.util.picker.adapter.IPickerAdapter;
import com.ttoview.nakayosi.ttoview.util.picker.adapter.PickerGridAdapter;
import com.ttoview.nakayosi.ttoview.util.picker.loader.MediaThumbnailLoader;
import com.ttoview.nakayosi.ttoview.util.picker.media.BaseMediaData;
import com.ttoview.nakayosi.ttoview.util.picker.media.MediaGroup;
import com.ttoview.nakayosi.ttoview.util.picker.media.MediaItem;
import com.ttoview.nakayosi.ttoview.util.picker.view.PickerGridRow;
import com.ttoview.nakayosi.ttoview.util.picker.view.PickerListRow;

import java.util.ArrayList;
import java.util.List;


public abstract class BasePickerActivity extends ActionBarActivity {

    public static final String PICKER_OPTION_SINGLE_SELECT = "PICKER_OPTION_SINGLE_SELECT";
    public static final String PICKER_OPTION_MULTI_SELECT = "PICKER_OPTION_MULTI_SELECT";
    public static final String PICKER_OPTION_SINGLE_SELECT_ADD_FILE = "PICKER_OPTION_SINGLE_SELECT_ADD_FILE";
    public static final String PICKER_OPTION_SINGLE_SELECT_ADD_CAMERA = "PICKER_OPTION_SINGLE_SELECT_ADD_CAMERA";

    public static final int ACTIVITY_RESULT_EDIT_IMAGE = 0x01;
    public static final int ACTIVITY_RESULT_CAMERA = 0x02;

    private int mMaxSelectCount = 5;

    private GallerySpinnerAdapter mGallerySpinnerAdapter;
    private IPickerAdapter mMediaAdapter;
    protected ListView mListView;
    protected TextView mCurrentImageCountLabel;
    protected TextView mTotalImageCountLabel;
    protected LinearLayout mCountLabelLayout;

    private boolean isSingleItem = true;

    private ArrayList<EditImageModel> mEditImageModels;
    private List<MediaGroup> mMediaGroups;

    @Override
    protected void setActionBarStyle() {

        getActionBar().setTitle("");
        getActionBar().setLogo(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getIntent().hasExtra(PICKER_OPTION_SINGLE_SELECT_ADD_FILE)) {
            return false;
        }
        getMenuInflater().inflate(R.menu.photo_picker_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_next:
                setResultIntent();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ACTIVITY_RESULT_EDIT_IMAGE: {

                    ArrayList<EditImageModel> editImageModels = data.getParcelableArrayListExtra("editImagePathList");
                    Intent imageResult = new Intent();
                    imageResult.putExtra("editImagePathList", editImageModels);
                    imageResult.putExtra("mediaList", mMediaAdapter.getSelectList());
                    imageResult.putExtra("groupIds", mGallerySpinnerAdapter.getSelectedGroups());
                    setResult(RESULT_OK, imageResult);
                    BasePickerActivity.this.finish();
                }
                break;
                case ACTIVITY_RESULT_CAMERA: {

                    String[] projection = {
                            MediaStore.Images.ImageColumns._ID,
                            MediaStore.Images.ImageColumns.DATA
                    };

                    Cursor cursor = getContentResolver().query(data.getData(), projection, null, null, null);
                    if (null == cursor) {
                        return;
                    }
                    cursor.moveToFirst();
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));

                    if (isSingleItem && getIntent().hasExtra(PICKER_OPTION_SINGLE_SELECT_ADD_FILE)) {
                        Intent result = getIntent();
                        result.putExtra(PICKER_OPTION_SINGLE_SELECT_ADD_CAMERA, path);
                        setResult(RESULT_OK, result);
                        BasePickerActivity.this.finish();
                    } else {

                        MediaItem cameraItem = null;

                        List<MediaItem> items = getItemList(mMediaGroups.get(1));

                        if (null != items) {
                            for (int i = 0; i < items.size(); i++) {
                                if (items.get(i).getId() == id) {
                                    cameraItem = items.get(i);
                                    break;
                                }
                            }
                        }

                        if (null == cameraItem) {
                            return;
                        }

                        mMediaAdapter.addCameraData(cameraItem);
                        mGallerySpinnerAdapter.addSelectedGroup(cameraItem.getGroupId());
                    }
                }
                break;
            }
        } else if (resultCode == RESULT_CANCELED && requestCode == ACTIVITY_RESULT_CAMERA) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        setResult(RESULT_CANCELED);
        BasePickerActivity.this.finish();
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mMediaAdapter.onConfigurationChanged(newConfig);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        initUI();

        ArrayList<EditImageModel> editModels = getIntent().getParcelableArrayListExtra("editImages");

        mEditImageModels = new ArrayList<EditImageModel>();

        if (null != editModels) {
            mEditImageModels.addAll(editModels);
        }

        if (mListView == null) {
            throw new RuntimeException("Not found ListView");
        }
        if (getIntent().hasExtra(PICKER_OPTION_MULTI_SELECT)) {
            isSingleItem = false;
        } else if (getIntent().hasExtra(PICKER_OPTION_SINGLE_SELECT_ADD_FILE) || getIntent().hasExtra(PICKER_OPTION_SINGLE_SELECT)) {
            mCountLabelLayout.setVisibility(View.GONE);
        } else {
            mCountLabelLayout.setVisibility(View.GONE);
        }
        mListView.setOnItemClickListener(mItemClickListener);
        mListView.setRecyclerListener(mRecyclerListener);

        mMediaGroups = getGroupList();

        mGallerySpinnerAdapter = new GallerySpinnerAdapter(this, R.layout.item_feed_spinner_view, mMediaGroups);
        getActionBar().setListNavigationCallbacks(mGallerySpinnerAdapter, new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {

                loadGroupList(itemPosition);

                return true;
            }
        });
        loadGroupList(0);
        refreshSelectView();
    }

    @Override
    protected void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMediaAdapter != null) {
            mMediaAdapter.release();
        }
        MediaThumbnailLoader.getInstance().release();
    }


    protected void setResultIntent() {

        if (mMediaAdapter != null) {
            if (mMediaAdapter.getSelectedSize() > 0) { // multi

                ArrayList<EditImageModel> editImageModels = new ArrayList<EditImageModel>();

                ArrayList<BaseMediaData> items = mMediaAdapter.getSelectList();

                for (BaseMediaData item : items) {

                    MediaItem mediaItem = (MediaItem) item;

                    EditImageModel model = new EditImageModel();

                    model.setMediaId(mediaItem.getId());
                    model.setEditMediaPath(mediaItem.getPath());
                    model.setOriginalMediaPath(mediaItem.getPath());

                    if (0 < mEditImageModels.size()) {
                        for (int i = 0; i < mEditImageModels.size(); i++) {
                            if (mEditImageModels.get(i).getMediaId() == mediaItem.getId()) {
                                model.setOriginalMediaPath(mEditImageModels.get(i).getOriginalMediaPath());
                                model.setEditMediaPath(mEditImageModels.get(i).getEditMediaPath());
                                break;
                            }
                        }
                    }

                    editImageModels.add(model);
                }

                Intent intent = new Intent();
                intent.putExtra("editImagePathList", editImageModels);
                intent.putExtra("mediaList", mMediaAdapter.getSelectList());
                intent.putExtra("groupIds", mGallerySpinnerAdapter.getSelectedGroups());
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    private void loadGroupList(int position) {
        loadItemList(mMediaGroups.get(position));

//		if ( mMediaAdapter != null ) {
//			mMediaAdapter.release();
//		}
//
//		mMediaAdapter = getGroupAdapter(getGroupList());
//
//		mListView.setAdapter(mMediaAdapter);
    }

    private void loadItemList(MediaGroup group) {

        List<MediaItem> items = null;

        if (group.getId() == -1) {
            items = new ArrayList<MediaItem>();
            // 1부터 시작하는 이유는 0번 그룹(폴더)는 전체보기 폴더로 개발자가 강제로 만든 가상의 폴더
            for (int i = 1; i < mMediaGroups.size(); i++) {
                items.addAll(getItemList(mMediaGroups.get(i)));
            }
        } else {
            items = getItemList(group);
        }


        if (mMediaAdapter != null) {
            mMediaAdapter.release();
            mMediaAdapter.setData(items);
        } else {
            mMediaAdapter = getItemAdapter(items);
            if (getIntent().hasExtra("mediaList")) {
                ArrayList<BaseMediaData> datas = getIntent().getParcelableArrayListExtra("mediaList");
                ArrayList<String> groupIds = getIntent().getStringArrayListExtra("groupIds");
                mGallerySpinnerAdapter.setSelectedGroup(groupIds);
                mCurrentImageCountLabel.setText(datas.size() + "");
                mMediaAdapter.setSelectList(datas);
            }
        }

        if (getIntent().hasExtra(PICKER_OPTION_MULTI_SELECT)) {
            mMediaAdapter.setSelectMode(true);
        } else {
            mMediaAdapter.setSelectMode(false);
        }

        if (mMediaAdapter instanceof PickerGridAdapter) {
            ((PickerGridAdapter) mMediaAdapter).setDelegate(mGridAdapterDelegate);
        }

        mListView.setAdapter(mMediaAdapter);
    }

    private void refreshSelectView() {
        boolean notEmptySelected = mMediaAdapter.getSelectedSize() != 0;
        onItemSelectChanged(notEmptySelected);
    }

    private void proccessWhenClick(int position) {

        if (position < 0) {
            return;
        }

        BaseMediaData data = (BaseMediaData) mMediaAdapter.getItem(position);

        if (isSingleItem && getIntent().hasExtra(PICKER_OPTION_SINGLE_SELECT)) {

//            MediaItem item = (MediaItem) mMediaAdapter.getItem(position);
//            Intent intent = new Intent(this, ImageFilterActivity.class);
//            ArrayList<EditImageModel> items = new ArrayList<EditImageModel>();
//            EditImageModel model = new EditImageModel();
//            model.setEditMediaPath(item.getPath());
//            model.setOriginalMediaPath(item.getPath());
//            items.add(model);
//            intent.putExtra("imagePathList", items);
//            startActivityForResult(intent, ACTIVITY_RESULT_EDIT_IMAGE);
//            overridePendingTransition(0, 0);
        } else if (isSingleItem && getIntent().hasExtra(PICKER_OPTION_SINGLE_SELECT_ADD_FILE)) {

            MediaItem item = (MediaItem) mMediaAdapter.getItem(position);
            Intent result = getIntent();
            result.putExtra(PICKER_OPTION_SINGLE_SELECT_ADD_FILE, item);
            setResult(RESULT_OK, result);
            BasePickerActivity.this.finish();
        } else {

            boolean selected = !mMediaAdapter.isSelected(position);
            mMediaAdapter.setSelected(position, selected, ((MediaItem) data).getGroupId());
            refreshSelectView();

            if (selected) {
                mGallerySpinnerAdapter.addSelectedGroup(((MediaItem) data).getGroupId());
            } else {
                mGallerySpinnerAdapter.removeSelectedGroup(((MediaItem) data).getGroupId());

                if (0 < mEditImageModels.size()) {
                    for (int i = 0; i < mEditImageModels.size(); i++) {
                        if (mEditImageModels.get(i).getMediaId() == data.getId()) {
                            mEditImageModels.remove(i);
                            break;
                        }
                    }
                }
            }

            if (mMediaAdapter.getSelectedSize() > mMaxSelectCount) {
                mMediaAdapter.setSelected(position, !mMediaAdapter.isSelected(position), ((MediaItem) data).getGroupId());
                refreshSelectView();
                mGallerySpinnerAdapter.removeSelectedGroup(((MediaItem) data).getGroupId());
                Toast.makeText(this, "최대 5개까지만 추가 가능합니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            mCurrentImageCountLabel.setText((int) ((5 - mMaxSelectCount) + mMediaAdapter.getSelectedSize()) + "");
        }
    }

    private OnItemClickListener mItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
            int headerCnt = mListView.getHeaderViewsCount();
            position -= headerCnt;

            proccessWhenClick(position);
        }
    };

    private PickerGridAdapter.PickerGridAdapterDelegate mGridAdapterDelegate = new PickerGridAdapter.PickerGridAdapterDelegate() {

        @Override
        public void onClick(PickerGridAdapter adapter, int position) {
            proccessWhenClick(position);
        }
    };

    private RecyclerListener mRecyclerListener = new RecyclerListener() {

        @Override
        public void onMovedToScrapHeap(View view) {
            if (view instanceof PickerListRow) {
                ((PickerListRow) view).onMovedToScrapHeap();
            }

            if (view instanceof PickerGridRow) {
                ((PickerGridRow) view).onMovedToScrapHeap();
            }
        }
    };

    protected abstract void initUI();

    protected abstract List<MediaGroup> getGroupList();

    protected abstract List<MediaItem> getItemList(MediaGroup group);

    protected abstract IPickerAdapter getItemAdapter(List<MediaItem> datas);

    protected abstract IPickerAdapter getGroupAdapter(List<MediaGroup> datas);

    protected abstract void onItemSelectChanged(boolean select);
}
