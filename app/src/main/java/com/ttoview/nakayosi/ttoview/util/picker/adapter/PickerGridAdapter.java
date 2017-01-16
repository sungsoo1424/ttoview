package com.ttoview.nakayosi.ttoview.util.picker.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;


import com.ttoview.nakayosi.ttoview.util.picker.media.BaseMediaData;
import com.ttoview.nakayosi.ttoview.util.picker.media.MediaConstSet;
import com.ttoview.nakayosi.ttoview.util.picker.media.MediaItem;
import com.ttoview.nakayosi.ttoview.util.picker.view.PickerGridColumn;
import com.ttoview.nakayosi.ttoview.util.picker.view.PickerGridRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class PickerGridAdapter extends MultiplexColumnAdapter<PickerGridRow> implements IPickerAdapter {
	private Context mContext;

	private List<? extends BaseMediaData> mBaseMediaDatas;
	private ArrayList<BaseMediaData> mSelectedDataSet;

	private HashSet<PickerGridRow> mGridRowSet;
	private HashMap<BaseMediaData, PickerGridRow> mDataRowMatchMap;
	
	private boolean mSelectMode;
	private int mColumnCount;
	private DisplayMetrics mDisplayMetrics;
	
	private PickerGridAdapterDelegate mDelegate;
	
	public PickerGridAdapter(Context context, List<? extends BaseMediaData> mediaList) {
		mContext = context;
		mBaseMediaDatas = mediaList;
        mBaseMediaDatas.add(0, null);

		int capacity = mediaList.size();
        if(null == mSelectedDataSet) {
            mSelectedDataSet = new ArrayList<BaseMediaData>(capacity);
        }
		mGridRowSet = new HashSet<PickerGridRow>(capacity);
		mDataRowMatchMap = new HashMap<BaseMediaData, PickerGridRow>(capacity);
		
		mDisplayMetrics = mContext.getResources().getDisplayMetrics();		
		onConfigurationChanged(context.getResources().getConfiguration());
	}

    @Override
    public void setData(List<MediaItem> datas) {

        mBaseMediaDatas.clear();
        mBaseMediaDatas = datas;
        mBaseMediaDatas.add(0, null);
    }

    @Override
    public void addCameraData(MediaItem data) {
        mSelectedDataSet.add(data);
        PickerGridRow row = mDataRowMatchMap.get(data);

        if(null == row) {
            return;
        }

        int index = row.getColumnPosition(data);
        if ( index >= 0 ) {
            row.setSelected(index, true);
            setCountLabel();
        }
    }

    @Override
	public int getItemCount() {
		return mBaseMediaDatas.size();
	}

	@Override
	public int getColumnCount() {
		return mColumnCount;
	}

	@Override
	public void setColumnView(int rowPosition, int columnPosition, int itemPosition, PickerGridRow convertView) {
        BaseMediaData data = null;
        if(0 == itemPosition) {
            convertView.setSelectVisibility(columnPosition, View.VISIBLE);
            data = new MediaItem();
            data.setName("CAMERA");
            data.setType(MediaConstSet.Type.CAMERA);
            convertView.setData(columnPosition, data);
        } else {
            data = mBaseMediaDatas.get(itemPosition);
            convertView.setData(columnPosition, data);

            convertView.setSelectVisibility(columnPosition, mSelectMode ? View.VISIBLE : View.GONE);
            convertView.setSelected(columnPosition, mSelectedDataSet.contains(data));

            for(int i = 0 ; i < mSelectedDataSet.size() ; i++) {
                if(mSelectedDataSet.get(i).getThumbnailPath().equals(data.getThumbnailPath())) {
                    convertView.setCountLabel(columnPosition, i + 1);
                    break;
                }
            }
        }

		mDataRowMatchMap.put(data, convertView);
	}

	@Override
	public void clearColumnView(int rowPosition, int columnPosition, PickerGridRow convertView) {
		convertView.onMovedToScrapHeap(columnPosition);
	}

	
	@Override
	public PickerGridRow getViewEx(int position, PickerGridRow convertView, ViewGroup parent) {
		if ( convertView == null ) {
			convertView = new PickerGridRow(mContext);
			convertView.setDelegate(mGridRowDelegate);
			mGridRowSet.add(convertView);
		}
		
		convertView.reloadColumn(mColumnCount);
		
		return convertView;
	}

	
	@Override
	public BaseMediaData getItem(int position) {
		return mBaseMediaDatas.get(position);
	}
	
	public void setDelegate(PickerGridAdapterDelegate delegate) {
		mDelegate = delegate;
	}
	
	@Override
	public void setSelectMode(boolean selectMode) {
		mSelectMode = selectMode;
		notifyDataSetChanged();
	}

	@Override
	public void setSelected(int position, boolean selected, long groupId) {

		BaseMediaData data = getItem(position);

		if ( selected ) {
			mSelectedDataSet.add(data);
		}
		else {
			mSelectedDataSet.remove(data);
		}

		PickerGridRow row = mDataRowMatchMap.get(data);

        if(null == row) {
            return;
        }

		int index = row.getColumnPosition(data);
		if ( index >= 0 ) {
			row.setSelected(index, selected);
            setCountLabel();
		}
	}

    private void setCountLabel() {

        int count = 0;
        for(BaseMediaData data : mSelectedDataSet) {
            count++;
            PickerGridRow row = mDataRowMatchMap.get(data);
            if(null != row) {
                int index = row.getColumnPosition(data);
                if ( index >= 0 ) {
                    row.setCountLabel(index, count);
                }
            }
        }
    }

	@Override
	public void setAllSelected(boolean selected) {
		if ( selected ) {
			for ( BaseMediaData data : mBaseMediaDatas ) {
				if ( !mSelectedDataSet.contains(data) ) {
					mSelectedDataSet.add(data);
				}
			}
		}
		else {
			mSelectedDataSet.clear();
		}
		notifyDataSetChanged();
	}

	@Override
	public boolean isAllSelected() {
		return mSelectedDataSet.size() == mBaseMediaDatas.size();
	}

	@Override
	public boolean isSelected(int position) {
		BaseMediaData data = getItem(position);
		return mSelectedDataSet.contains(data);
	}

	@Override
	public void clearSelected() {
		mSelectedDataSet.clear();
	}

	@Override
	public int getSelectedSize() {
		return mSelectedDataSet.size();
	}
	
	@Override
	public void release() {
		mBaseMediaDatas.clear();

		mGridRowSet.clear();
		mDataRowMatchMap.clear();
	}

    @Override
	public ArrayList<BaseMediaData> getSelectList() {
		ArrayList<BaseMediaData> selectList = new ArrayList<BaseMediaData>(mSelectedDataSet.size());
		BaseMediaData[] datas = mSelectedDataSet.toArray(new BaseMediaData[0]);
		for ( BaseMediaData data : datas ) {
			selectList.add(data);
		}
		
		return selectList;
	}

    @Override
    public void setSelectList(ArrayList<BaseMediaData> datas) {
        mSelectedDataSet = datas;
    }

    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		switch ( newConfig.orientation ) {
		case Configuration.ORIENTATION_PORTRAIT:
			if ( DisplayMetrics.DENSITY_XHIGH <= mDisplayMetrics.densityDpi ) {
				mColumnCount = 3;
			}
			else {
				mColumnCount = 3;
			}
			
			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			if ( DisplayMetrics.DENSITY_XHIGH <= mDisplayMetrics.densityDpi ) {
				mColumnCount = 6;
			}
			else {
				mColumnCount = 5;
			}
			break;
		}
	}

	
	private PickerGridRow.PickerGridRowDelegate mGridRowDelegate = new PickerGridRow.PickerGridRowDelegate() {
		
		@Override
		public void onClick(PickerGridRow row, PickerGridColumn column, BaseMediaData data) {
			if ( mDelegate != null ) {
				
				mDelegate.onClick(PickerGridAdapter.this, mBaseMediaDatas.indexOf(data));
			}
		}
	};
	
	public static interface PickerGridAdapterDelegate {
		public void onClick(PickerGridAdapter adapter, int position);
	}
}
