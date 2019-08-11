package com.videostatusiranna.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.videostatusiranna.activity.HomeActivity;
import com.videostatusiranna.getSet.categoryGetSet;
import com.videostatusiranna.R;

public class CustomCategoryListAdapter extends BaseAdapter {
    private final ArrayList<categoryGetSet> data;
    private final Context context;

    public CustomCategoryListAdapter(ArrayList<categoryGetSet> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (data != null) {
            return data.size();
        } else return 0;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageCategory;
        TextView txt_name;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_category, parent, false);
        }
        imageCategory = convertView.findViewById(R.id.imageCategory);
        txt_name = convertView.findViewById(R.id.txt_name);

        txt_name.setText(data.get(position).getSubCategoryName());
        txt_name.setTypeface(HomeActivity.tf_main_medium);
        Picasso.get().load(context.getString(R.string.link)+"images/subcategory/" + data.get(position).getImageName()).fit().into(imageCategory);


        return convertView;
    }
}
