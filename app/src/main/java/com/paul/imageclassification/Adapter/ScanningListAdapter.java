package com.paul.imageclassification.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.paul.imageclassification.Bean.KindsBean;
import com.paul.imageclassification.R;
import com.paul.imageclassification.Util.ImageUtil;

import java.util.List;

/**
 * 作者:created by 巴塞罗那的余晖 on 2019/11/19 19：06
 * 邮箱:zhubaoluo@outlook.com
 * 不会写BUG的程序猿不是好程序猿，嘤嘤嘤
 */
public class ScanningListAdapter extends BaseAdapter {
    List<KindsBean> kindsBeans;
    Context context;

    public ScanningListAdapter(List<KindsBean> kindsBeans, Context context) {
        this.kindsBeans = kindsBeans;
        this.context=context;
    }

    @Override
    public int getCount() {
        return kindsBeans.size();
    }

    @Override
    public Object getItem(int i) {
        return kindsBeans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View view1= LayoutInflater.from(context).inflate(R.layout.scanning_list_item,null);
        ImageView headIcon=view1.findViewById(R.id.album_lesson_iv);
        TextView courseName=view1.findViewById(R.id.album_lesson_tv);
        TextView numCourse=view1.findViewById(R.id.album_lesson_tv_count);
        Glide.with(context).
                load(ImageUtil.getBitmapByPath(context,kindsBeans.get(i).getFirstPhotoPath())).
                into(headIcon);
        //2020.1.8适配安卓Q
        //headIcon.setImageDrawable(Drawable.createFromPath(kindsBeans.get(i).getFirstPhotoPath()));
        courseName.setText(kindsBeans.get(i).getKindName());
        numCourse.setText(kindsBeans.get(i).getKindNumber()+"张");
        return view1;
    }
}
