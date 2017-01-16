package com.ttoview.nakayosi.ttoview.util.picker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;


import com.ttoview.nakayosi.ttoview.util.picker.media.BaseMediaData;

import java.util.ArrayList;
import java.util.List;

public class PickerGridRow extends LinearLayout {
	private Context mContext;
	private List<PickerGridColumn> mGridColumnList;
	private int mColumnCount;
	private int mSpacePx;
	
	private PickerGridRowDelegate mDelegate;
	
	public PickerGridRow(Context context) {
		super(context);
		init(context);
	}

	public PickerGridRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		setGravity(Gravity.CENTER);
		setClickable(true);

		mGridColumnList = new ArrayList<PickerGridColumn>();
		
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		mSpacePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, dm);
	}
	
	public void setDelegate(PickerGridRowDelegate delegate) {
		mDelegate = delegate;
	}
	
	public void reloadColumn(int columnCount) {
		if ( mColumnCount != columnCount ) {
			mColumnCount = columnCount;
			for ( PickerGridColumn column : mGridColumnList ) {
				column.onMovedToScrapHeap();
			}
			mGridColumnList.clear();
			removeAllViews();
			
			DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
			int screenWidth = dm.widthPixels;
			int spaceCount = columnCount + 1;
			int spacePx = mSpacePx;
			int spaceTotalWidth = spacePx * spaceCount;
			setPadding(0, spacePx, 0, 0);
			
			int columnSize = (int)((float)(screenWidth - spaceTotalWidth) / columnCount);
			
			
			for ( int i = 0; i < columnCount; i++ ) {
				View space = new View(mContext);
				addView(space, new LayoutParams(0, 0, 1));


				PickerGridColumn column = new PickerGridColumn(mContext);
				column.setDelegate(mPickerGridColumnDelegate);

				addView(column, new LayoutParams(columnSize, columnSize, 0));
				mGridColumnList.add(column);
			}

			View space = new View(mContext);
			addView(space, new LayoutParams(0, 0, 1));
		}
	}
	
	public void setData(int index, BaseMediaData listData) {
		if ( index < mGridColumnList.size() ) {
			PickerGridColumn column = mGridColumnList.get(index);
			column.setData(listData);
		}
	}
	
	public void setSelectVisibility(int index, int visibility) {
		if ( index < mGridColumnList.size() ) {
			PickerGridColumn column = mGridColumnList.get(index);
			column.setSelectVisibility(visibility);
		}
	}
	
	public void setSelected(int index, boolean selected) {
		if ( index < mGridColumnList.size() ) {
			PickerGridColumn column = mGridColumnList.get(index);
			column.setSelected(selected);
		}
	}

    public void setCountLabel(int index, int count) {
        if ( index < mGridColumnList.size() ) {
            PickerGridColumn column = mGridColumnList.get(index);
            column.setCountLabel(count);
        }
    }
	
	public int getColumnPosition(BaseMediaData listData) {

		int position = -1;
		int size = mGridColumnList.size();


		for ( int i = 0 ; i < size ; i++ ) {

            if(null == mGridColumnList.get(i).getData()) {
                System.out.println("column :: getData() :: null");
                break;
            } else if(null == listData) {
                System.out.println("column :: listData :: null");
            }

			if(null != mGridColumnList.get(i) && !mGridColumnList.get(i).getData().getName().equals("CAMERA") && listData.equals(mGridColumnList.get(i).getData())) {
				position = i;
				break;
			}
		}
		
		return position;
	}
	
	
	public void onMovedToScrapHeap() {
		int size = mGridColumnList.size();
		for ( int i = 0; i < size; i++ ) {
			PickerGridColumn column = mGridColumnList.get(i);
			column.onMovedToScrapHeap();	
		}
	}
	
	
	public void onMovedToScrapHeap(int index) {
		if ( index < mGridColumnList.size() ) {
			PickerGridColumn column = mGridColumnList.get(index);
			column.onMovedToScrapHeap();
		}
	}
	
	
	private PickerGridColumn.PickerGridColumnDelegate mPickerGridColumnDelegate = new PickerGridColumn.PickerGridColumnDelegate() {

		@Override
		public void onClick(PickerGridColumn column, BaseMediaData data) {
			if ( mDelegate != null ) {
				mDelegate.onClick(PickerGridRow.this, column, data);
			}
		}
	};
	
	public static interface PickerGridRowDelegate {
		public void onClick(PickerGridRow row, PickerGridColumn column, BaseMediaData data);
	}
}
