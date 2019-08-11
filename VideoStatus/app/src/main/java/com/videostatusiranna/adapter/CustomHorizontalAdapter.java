package com.videostatusiranna.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.videostatusiranna.activity.HomeActivity;
import com.videostatusiranna.getSet.categoryGetSet;
import com.videostatusiranna.R;
import com.videostatusiranna.onClickListners.onCategoryListClick;

public class CustomHorizontalAdapter extends RecyclerView.Adapter<CustomHorizontalAdapter.MyViewHolder> {
    private final Context currentContext;
    private final ArrayList<categoryGetSet> data;
    private final onCategoryListClick onCategoryListClick;




    public CustomHorizontalAdapter(Context context,ArrayList<categoryGetSet> info,onCategoryListClick onCategoryListClick) {
        currentContext=context;
        data=info;
        this.onCategoryListClick=onCategoryListClick;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_mood, parent, false);
         final MyViewHolder myViewHolder = new MyViewHolder(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCategoryListClick.onItemClick(itemView,myViewHolder.getLayoutPosition());
            }
        });
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txt_name.setText((data.get(position).getSubCategoryName()));
        holder.txt_name.setTypeface(HomeActivity.tf_main_medium);
        Log.e("temp",currentContext.getString(R.string.link)+"images/subcategory/" + data.get(position).getImageName());
        Picasso.get().load(currentContext.getString(R.string.link)+"images/subcategory/" + data.get(position).getImageName()).fit().into(holder.imageview);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView txt_name;
        final ImageView imageview;


        MyViewHolder(View view) {
            super(view);
            txt_name = view.findViewById(R.id.txt_name);
            imageview= view.findViewById(R.id.imageCategory);

    }

}}
