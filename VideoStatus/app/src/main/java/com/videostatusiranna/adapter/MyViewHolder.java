package com.videostatusiranna.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.videostatusiranna.R;

class MyViewHolder extends RecyclerView.ViewHolder {
    TextView tv_video_title;
    final TextView tvVideoSubcat;
    final TextView tv_num_views;
    final TextView tv_views;
    final ImageView iv_video;


    MyViewHolder(View view) {
        super(view);
        tv_video_title = view.findViewById(R.id.tv_video_title);
        tvVideoSubcat = view.findViewById(R.id.tv_video_subcat);
        tv_num_views = view.findViewById(R.id.tv_num_views);
        tv_views = view.findViewById(R.id.tv_views);
        tv_video_title = view.findViewById(R.id.tv_video_title);
        iv_video = view.findViewById(R.id.iv_video);

    }

}