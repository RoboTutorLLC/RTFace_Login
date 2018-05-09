package com.example.iris.login1;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by Iris on 16/7/20.
 */

public class ScrollViewAdapter {
    static protected int accountNumber;
    static protected int accountChosen;
    static protected int iconNumber;
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Pair<Bitmap, Integer>> mDatas;
    private List<Integer> mIcons;
    private Boolean isIcon = false;

    public ScrollViewAdapter(Context context, List<Pair<Bitmap, Integer>> mDatas) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mDatas = mDatas;
    }

    public ScrollViewAdapter(Context context, List<Integer> mIcons, Boolean isIcon) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mIcons = mIcons;
        this.isIcon = isIcon;
    }


    public int getCount() {
        return mDatas.size();
    }

    public boolean isIcon(){
        return isIcon;
    }

    public int getCountIcons(){
        return mIcons.size();
    }

    public Pair<Bitmap, Integer> getItem(int position) {
        return mDatas.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void setMDatas(List<Pair<Bitmap, Integer>> mDatas_) {
        this.mDatas=mDatas_;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            if(!isIcon) {
                convertView = mInflater.inflate(
                        R.layout.activity_index_gallery_item, parent, false);
                viewHolder.mImg = (ImageView) convertView
                        .findViewById(R.id.id_index_gallery_item_image);
                viewHolder.mIcon = (ImageView) convertView.findViewById(R.id.gallery_icon);
            } else {
                convertView = mInflater.inflate(
                        R.layout.icons_gallery_item, parent, false);
                viewHolder.mImg = (ImageView) convertView
                        .findViewById(R.id.id_index_icons_item_image);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(!isIcon) {
            Pair<Bitmap, Integer> userinf = mDatas.get(position);


            viewHolder.mImg.setImageBitmap(userinf.first);
            viewHolder.mIcon.setImageDrawable(mContext.getResources().getDrawable(userinf.second));
        } else {
            Integer icon = mIcons.get(position);
            viewHolder.mImg.setImageDrawable(mContext.getResources().getDrawable(icon));
        }

        return convertView;
    }

    private class ViewHolder {
        ImageView mImg;
        ImageView mIcon;
    }

}
