package com.videostatusiranna.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import com.videostatusiranna.R;
import com.videostatusiranna.activity.HomeActivity;
import com.videostatusiranna.getSet.videoListGetSet;
import com.videostatusiranna.onClickListners.OnLoadListeners;
import com.videostatusiranna.onClickListners.onVideoListClick;

public class CustomVideoSubListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List data;
    private final Context context;
    private final com.videostatusiranna.onClickListners.onVideoListClick onVideoListClick;
    private OnLoadListeners onLoadMoreListener;
    private static boolean isLoading;
    private final GridLayoutManager gridLayoutManager;

    // A menu item view type.
    private static final int MENU_ITEM_VIEW_TYPE = 0;

    // The native app install ad view type.
    private static final int AD_VIEW_TYPE = 1;


    public CustomVideoSubListAdapter(List data, Context context, onVideoListClick onVideoListClick, RecyclerView recyclerView) {
        this.data = data;
        this.context = context;
        this.onVideoListClick = onVideoListClick;
        isLoading = false;

        gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (gridLayoutManager != null ) {
                    int visibleThreshold = gridLayoutManager.getChildCount();
                    int totalItemCount = gridLayoutManager.getItemCount();
                    int lastVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();
                    Log.e("Check", isLoading + "  " + "Total Item Count " + totalItemCount + "lastVisibleItem " + lastVisibleItem + "visible threshold " + visibleThreshold);
                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                }
            }
        });

    }

    public void setOnLoadMoreListener(OnLoadListeners mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        switch (viewType) {
            case AD_VIEW_TYPE:
                View bannerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_admob, parent, false);
                return new ViewHolderAdMob(bannerView);


            case MENU_ITEM_VIEW_TYPE:
                // Fall through.
            default:
                final View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cell_data_sub_item, parent, false);
                final MyViewSubListHolder myViewHolder = new MyViewSubListHolder(itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onVideoListClick.onItemClick(itemView, myViewHolder.getLayoutPosition());
                    }
                });
                return myViewHolder;
        }
    }

    public static void setLoaded(Boolean bool) {
        isLoading = bool;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);
        switch (viewType) {
            case AD_VIEW_TYPE:

                break;

            case MENU_ITEM_VIEW_TYPE:
                // fall through
            default: {
                if (holder instanceof MyViewSubListHolder) {
                    ((MyViewSubListHolder) holder).tv_video_title.setText(((videoListGetSet) data.get(position)).getVideo_title());
                    ((MyViewSubListHolder) holder).tv_video_title.setTypeface(HomeActivity.tf_main_medium);
                    Picasso.get().load(context.getString(R.string.link)+"images/thumbnail/" + ((videoListGetSet) data.get(position)).getVideoImage()).into(((MyViewSubListHolder) holder).iv_video);
                    Log.e("checkImageUrl",""+context.getString(R.string.link)+"images/thumbnail/" + ((videoListGetSet) data.get(position)).getVideoImage());

                }
            }


        }
    }

    @Override
    public int getItemViewType(int position) {
        Object recyclerViewItem = data.get(position);
        if (recyclerViewItem instanceof videoListGetSet) {
            return MENU_ITEM_VIEW_TYPE;
        }
        return AD_VIEW_TYPE;
    }

    @Override
    public int getItemCount() {

        return data.size();


    }


    class MyViewSubListHolder extends RecyclerView.ViewHolder {
        TextView tv_video_title;
        final ImageView iv_video;
        MyViewSubListHolder(View view) {
            super(view);
            tv_video_title = view.findViewById(R.id.tv_video_title);
            iv_video = view.findViewById(R.id.iv_video);
        }

    }
}