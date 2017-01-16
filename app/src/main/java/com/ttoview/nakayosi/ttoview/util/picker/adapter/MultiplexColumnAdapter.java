package com.ttoview.nakayosi.ttoview.util.picker.adapter;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class MultiplexColumnAdapter<E extends View> extends BaseAdapter {
	private Handler mHandler = new Handler(Looper.getMainLooper());

	public abstract int getItemCount();

	public abstract int getColumnCount();

	public abstract void setColumnView(int rowPosition, int columnPosition, int itemPosition, E convertView);

	public abstract void clearColumnView(int rowPosition, int columnPosition, E convertView);

	public abstract E getViewEx(int position, E convertView, ViewGroup parent);

	public abstract Object getItem(int position);

	public final long getItemId(int position) {
		return position;
	}

	@Override
	public final int getCount() {
		return (int) Math.ceil(getItemCount() / (float) getColumnCount());
	}

	@SuppressWarnings("unchecked")
	@Override
	public final View getView(int position, View convertView, ViewGroup parent) {
		
		int columnItemCount = getColumnCount();

		int startIndex = position * columnItemCount;
		int lastIndex = startIndex + columnItemCount;
		int itemSize = getItemCount();

		convertView = getViewEx(position, (E)convertView, parent);
		
		for (int i = startIndex, columnPosition = 0; i < lastIndex; i++, columnPosition++) {
			final int finalPosition = position;
			final int finalColumnPosition = columnPosition;
			final int finalItemPosition = i;
			final E finalConvertView = (E)convertView;
			
			if (i < itemSize) {
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						int itemCount = getItemCount();
						if ( finalItemPosition < itemCount ) {
							setColumnView(finalPosition, finalColumnPosition, finalItemPosition, finalConvertView);
						}
					}
				});
				
				
			} else {
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						clearColumnView(finalPosition, finalColumnPosition, finalConvertView);		
					}
				});
				
			}

		}
		return convertView;
	}

}
