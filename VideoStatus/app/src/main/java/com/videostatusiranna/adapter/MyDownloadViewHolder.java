package com.videostatusiranna.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.videostatusiranna.R;

public class MyDownloadViewHolder extends RecyclerView.ViewHolder {
    public TextView tv_video_title;
    public final TextView tvVideoSubCat;
    public final ImageView iv_delete;
    public final ImageView iv_video;


    MyDownloadViewHolder(View view) {
        super(view);
        tv_video_title = view.findViewById(R.id.tv_video_title);
        tvVideoSubCat = view.findViewById(R.id.tv_video_subcat);
        iv_delete = view.findViewById(R.id.iv_delete);
        tv_video_title = view.findViewById(R.id.tv_video_title);
        iv_video = view.findViewById(R.id.iv_video);

    }
}
