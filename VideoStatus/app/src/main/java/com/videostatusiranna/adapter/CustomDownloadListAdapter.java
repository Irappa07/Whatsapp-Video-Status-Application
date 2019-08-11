package com.videostatusiranna.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

import com.videostatusiranna.activity.HomeActivity;
import com.videostatusiranna.getSet.videoListGetSet;
import com.videostatusiranna.R;
import com.videostatusiranna.onClickListners.onDeleteClick;
import com.videostatusiranna.onClickListners.onVideoListClick;

public class CustomDownloadListAdapter extends RecyclerView.Adapter<MyDownloadViewHolder> {
    private final List data;
    private final Context context;
    private final onVideoListClick onVideoListClick;
    private final onDeleteClick onDeleteClick;


    public CustomDownloadListAdapter(List data, Context context, onVideoListClick onVideoListClick, onDeleteClick onDeleteClick) {
        this.data = data;
        this.context = context;
        this.onVideoListClick = onVideoListClick;
        this.onDeleteClick = onDeleteClick;
    }

    @NonNull
    @Override
    public MyDownloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_download_item, parent, false);
        final MyDownloadViewHolder myViewHolder = new MyDownloadViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onVideoListClick.onItemClick(itemView, myViewHolder.getLayoutPosition());
            }
        });
        return myViewHolder;

    }


    @Override
    public void onBindViewHolder(@NonNull final MyDownloadViewHolder holder, int position) {
        holder.tv_video_title.setText(((videoListGetSet) data.get(position)).getVideo_title());
        holder.tv_video_title.setTypeface(HomeActivity.tf_main_medium);
        String subCat = ((videoListGetSet) data.get(position)).getVideo_category() + " : " + ((videoListGetSet) data.get(position)).getVideo_subcategory();
        holder.tvVideoSubCat.setText(subCat);
        holder.tvVideoSubCat.setTypeface(HomeActivity.tf_main_medium);
        Picasso.get().load(context.getString(R.string.link)+"images/thumbnail/" + ((videoListGetSet) data.get(position)).getVideoImage()).into(holder.iv_video);

        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onDeleteClick.onItemClick(v, holder.getAdapterPosition());
            }
        });

    }


    @Override
    public int getItemCount() {
        return data.size();
    }


}

