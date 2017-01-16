package com.ttoview.nakayosi.ttoview.util.picker.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ttoview.nakayosi.ttoview.R;
import com.ttoview.nakayosi.ttoview.util.picker.media.MediaGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hwangtaeun on 2014. 10. 2..
 */
public class GallerySpinnerAdapter extends ArrayAdapter<MediaGroup> {

    private final Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<MediaGroup> mMediaGroupList;
    private ArrayList<String> mSelectedMediaGroupList;
    private int mResourceId;

    public GallerySpinnerAdapter(Activity context, int textViewResourceId, List<MediaGroup> objects) {
        super(context, textViewResourceId, objects);
        this.mContext = context;
        this.mMediaGroupList = (ArrayList<MediaGroup>) objects;
        this.mResourceId = textViewResourceId;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        mSelectedMediaGroupList = new ArrayList<String>();
    }


    public void addSelectedGroup(long groupid) {
        mSelectedMediaGroupList.add(String.valueOf(groupid));
    }

    public void removeSelectedGroup(long groupid) {
        mSelectedMediaGroupList.remove(String.valueOf(groupid));
    }

    public void setSelectedGroup(ArrayList<String> groupIds) {
        mSelectedMediaGroupList.addAll(groupIds);
    }

    public ArrayList<String> getSelectedGroups() {
        return mSelectedMediaGroupList;
    }

    public View getItem(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = mLayoutInflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        TextView label = (TextView) convertView.findViewById(android.R.id.text1);
        String title = mMediaGroupList.get(position).getName();

        if (0 < mSelectedMediaGroupList.size()) {
            for (int i = 0; i < mSelectedMediaGroupList.size(); i++) {
                if (Long.parseLong(mSelectedMediaGroupList.get(i)) == mMediaGroupList.get(position).getId()) {
                    label.setTextColor(Color.parseColor("#70c989"));
                    break;
                } else {
                    label.setTextColor(Color.parseColor("#000000"));
                }
            }
        }

        label.setText(title);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        return getItem(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (null == convertView) {
            convertView = mLayoutInflater.inflate(mResourceId, parent, false);
        }

        TextView typeLabel = (TextView) convertView.findViewById(R.id.typeLabel);
        TextView titleLabel = (TextView) convertView.findViewById(R.id.titleLabel);
        titleLabel.setText("사진추가");
        typeLabel.setText(mMediaGroupList.get(position).getName());

        return convertView;
    }
}