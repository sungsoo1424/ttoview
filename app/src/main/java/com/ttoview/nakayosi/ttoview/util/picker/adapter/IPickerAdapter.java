package com.ttoview.nakayosi.ttoview.util.picker.adapter;

import android.content.res.Configuration;
import android.widget.ListAdapter;

import com.ttoview.nakayosi.ttoview.util.picker.media.BaseMediaData;
import com.ttoview.nakayosi.ttoview.util.picker.media.MediaItem;

import java.util.ArrayList;
import java.util.List;

public interface IPickerAdapter extends ListAdapter {
	public void setSelectMode(boolean selectMode);
	public void setSelected(int position, boolean selected, long groupId);
	public void setAllSelected(boolean selected);
	public boolean isAllSelected();
	public boolean isSelected(int position);
	public void clearSelected();
	public int getSelectedSize();
	public ArrayList<BaseMediaData> getSelectList();
    public void setSelectList(ArrayList<BaseMediaData> datas);
	public void onConfigurationChanged(Configuration newConfig);
	public void release();
    public void setData(List<MediaItem> datas);
    public void addCameraData(MediaItem data);
}
