package com.ttoview.nakayosi.ttoview.util.picker.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ttoview.nakayosi.ttoview.util.picker.media.BaseMediaData;
import com.ttoview.nakayosi.ttoview.util.picker.media.MediaItem;
import com.ttoview.nakayosi.ttoview.util.picker.view.PickerListRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class PickerListAdapter extends BaseAdapter implements IPickerAdapter {
	private Context mContext;
	private List<? extends BaseMediaData> mMediaList;
	private HashSet<BaseMediaData> mSelectSet;
	private HashMap<BaseMediaData, PickerListRow> mRowMap;
	private HashSet<PickerListRow> mRowSet;
	private boolean mSelectMode;
	
	
	public PickerListAdapter(Context context, List<? extends BaseMediaData> mediaList) {
		mContext = context;
		mMediaList = mediaList;
		
		int capacity = mediaList.size();
		
		mSelectSet = new HashSet<BaseMediaData>(capacity);
		mRowMap = new HashMap<BaseMediaData, PickerListRow>(capacity);
		mRowSet = new HashSet<PickerListRow>(capacity);
	}
	
	@Override
	public int getCount() {
		return mMediaList.size();
	}

	@Override
	public BaseMediaData getItem(int position) {

		return mMediaList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PickerListRow row = (PickerListRow)convertView;
		if ( row == null ) {
			row = new PickerListRow(mContext);
			mRowSet.add(row);
		}
		
		
		BaseMediaData data = getItem(position);
		row.setData(data);
		row.setSelectVisibility(mSelectMode ? View.VISIBLE : View.GONE);

		row.setSelected(isSelected(position));
		
		mRowMap.put(data, row);
		
		return row;
	}
	
	@Override
	public void setSelectMode(boolean selectMode) {
		mSelectMode = selectMode;
		for ( PickerListRow row : mRowSet ) { 
			row.setSelectVisibility(selectMode ? View.VISIBLE : View.GONE);
		}
	}

    @Override
	public void setSelected(int position, boolean selected, long groupId) {
		BaseMediaData data = getItem(position);
		if ( selected ) {
			mSelectSet.add(data);
		}
		else {
			mSelectSet.remove(data);
		}
		
		mRowMap.get(data).setSelected(selected);
	}
	

	@Override
	public void setAllSelected(boolean selected) {
		if ( selected ) {
			for ( BaseMediaData data : mMediaList ) {
				if ( !mSelectSet.contains(data) ) {
					mSelectSet.add(data);
				}
			}
		}
		else {
			mSelectSet.clear();
		}
		notifyDataSetChanged();
	}

	@Override
	public boolean isAllSelected() {
		return mSelectSet.size() == mMediaList.size();
	}
	

	@Override
	public boolean isSelected(int position) {
		BaseMediaData data = getItem(position); 
		return mSelectSet.contains(data);
	}
	

	@Override
	public void clearSelected() {
		mSelectSet.clear();
	}

	@Override
	public int getSelectedSize() {
		return mSelectSet.size();
	}
	

	@Override
	public ArrayList<BaseMediaData> getSelectList() {
		ArrayList<BaseMediaData> selectList = new ArrayList<BaseMediaData>(mSelectSet.size());
		BaseMediaData[] datas = mSelectSet.toArray(new BaseMediaData[0]);
		for ( BaseMediaData data : datas ) {
			selectList.add(data);
		}
		return selectList;
	}

    @Override
    public void setSelectList(ArrayList<BaseMediaData> datas) {

    }

    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		
	}
	
	@Override
	public void release() {
		mMediaList.clear();
		mSelectSet.clear();
		mRowMap.clear();
		mRowSet.clear();
	}

    @Override
    public void setData(List<MediaItem> datas) {

    }

    @Override
    public void addCameraData(MediaItem data) {

    }
}
