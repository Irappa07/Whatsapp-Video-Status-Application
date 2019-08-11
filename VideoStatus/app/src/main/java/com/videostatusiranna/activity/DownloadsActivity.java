package com.videostatusiranna.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.videostatusiranna.adapter.CustomDownloadListAdapter;
import com.videostatusiranna.adapter.CustomNavigationBarAdapter;
import com.videostatusiranna.getSet.videoListGetSet;
import com.videostatusiranna.R;
import com.videostatusiranna.utilities.DBAdapter;
import com.videostatusiranna.onClickListners.onDeleteClick;
import com.videostatusiranna.onClickListners.onVideoListClick;

public class DownloadsActivity extends AppCompatActivity {
    private ListView list_sliderMenu;
    private DrawerLayout drawer;
    private LinearLayout mainView;
    private RecyclerView list_videos;
    private List<videoListGetSet> data;
    private CustomDownloadListAdapter horizontalAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);

        initViews();
    }


    private void initViews() {
        list_sliderMenu = findViewById(R.id.list_slidermenu);
        list_videos = findViewById(R.id.list_videos);

        setUpDrawer();

        setUpNavigationBar();

        setUpVideoList();

    }


    private void setUpDrawer() {
        //drawer setup
        drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mainView = findViewById(R.id.mainView);
        setSupportActionBar(toolbar);
        ImageView btn_setting = toolbar.findViewById(R.id.btn_setting);
        btn_setting.setVisibility(View.GONE);

        ImageView btn_search = toolbar.findViewById(R.id.btn_search);
        btn_search.setVisibility(View.GONE);

        TextView txt_tittle = toolbar.findViewById(R.id.title);
        txt_tittle.setText(R.string.txt_downloaded);
        txt_tittle.setTypeface(HomeActivity.tf_main_medium);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                Configuration config = getResources().getConfiguration();
                if (config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                    mainView.setTranslationX(-slideOffset * drawerView.getWidth());

                } else {
                    mainView.setTranslationX(slideOffset * drawerView.getWidth());
                }
                drawer.bringChildToFront(drawerView);
                drawer.requestLayout();
            }

        };

        DrawerArrowDrawable drawerArrowDrawable = new DrawerArrowDrawable(this);
        drawerArrowDrawable.setColor(getResources().getColor(R.color.colorArrow));
        toggle.setDrawerArrowDrawable(drawerArrowDrawable);


        drawer.addDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private void setUpNavigationBar() {
        CustomNavigationBarAdapter customNavigationBarAdapter = new CustomNavigationBarAdapter(DownloadsActivity.this, drawer);
        list_sliderMenu.setAdapter(customNavigationBarAdapter);
    }


    private void setUpVideoList() {
        gettingDataFromDatabase();
    }


    private void gettingDataFromDatabase() {
        DBAdapter myDbHelper;
        myDbHelper = new DBAdapter(DownloadsActivity.this);
        try {
            myDbHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqlError) {
            sqlError.printStackTrace();
        }
        final ProgressBar progressBar = findViewById(R.id.progressBar);

        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor cur;
        try {

            cur = db.rawQuery("SELECT * FROM Downloaded;", null);
            data = new ArrayList<>();
            Log.e(getClass().getName(), "" + cur.getCount());
            if (cur.getCount() != 0) {
                if (cur.moveToFirst()) {
                    do {
                        videoListGetSet obj = new videoListGetSet();
                        String txt_category = cur.getString(cur.getColumnIndex("category"));
                        String txt_subcategory = cur.getString(cur.getColumnIndex("subcategory"));
                        String txt_video = cur.getString(cur.getColumnIndex("video"));
                        String txt_image = cur.getString(cur.getColumnIndex("image"));
                        String txt_view = cur.getString(cur.getColumnIndex("view"));
                        String txt_downloads = cur.getString(cur.getColumnIndex("downloads"));
                        String txt_title = cur.getString(cur.getColumnIndex("title"));
                        String txt_categoryId = cur.getString(cur.getColumnIndex("categoryid"));
                        String txt_subcategoryId = cur.getString(cur.getColumnIndex("subcategoryid"));

                        obj.setVideo_title(txt_title);
                        obj.setVideoView(txt_view);
                        obj.setVideoImage(txt_image);
                        obj.setVideoFileName(txt_video);
                        obj.setVideo_category(txt_category);
                        obj.setVideo_subcategory(txt_subcategory);
                        obj.setVideoDownload(txt_downloads);
                        obj.setVideo_cat_id(txt_categoryId);
                        obj.setVideo_subCat_id(txt_subcategoryId);
                        data.add(obj);
                    } while (cur.moveToNext());
                }
            }
            cur.close();
            db.close();
            myDbHelper.close();
            progressBar.setVisibility(View.INVISIBLE);
            if (data.size() > 0)
                updateUI();
            else {

                Snackbar mSnackBar = Snackbar.make(mainView, R.string.txt_no_data_avail, Snackbar.LENGTH_LONG);
                View mView = mSnackBar.getView();
                TextView mTextView = mView.findViewById(android.support.design.R.id.snackbar_text);
                mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                mSnackBar.show();
            }

        } catch (Exception e) {
            Log.e("Error", e.getMessage());

        }

    }


    private void updateUI() {


        onVideoListClick addClick = new onVideoListClick() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(DownloadsActivity.this, DetailActivity.class);
                intent.putExtra("videoTitle", (data.get(position).getVideo_title()));
                intent.putExtra("videoId", (data.get(position).getVideoFileName()));
                intent.putExtra("videoCategory", (data.get(position).getVideo_category()));
                intent.putExtra("videoSubCategory", (data.get(position).getVideo_subcategory()));
                intent.putExtra("videoCategoryId", (data.get(position).getVideo_cat_id()));
                intent.putExtra("videoSubCategoryId", (data.get(position).getVideo_subCat_id()));
                intent.putExtra("videoDownload", (data.get(position).getVideoDownload()));
                intent.putExtra("videoImage", (data.get(position).getVideoImage()));
                intent.putExtra("videoView", (data.get(position).getVideoView()));
                startActivity(intent);
            }


        };


        onDeleteClick onDeleteClick = new onDeleteClick() {


            @Override
            public void onItemClick(View v, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DownloadsActivity.this);
                builder.setTitle(R.string.txt_delete);
                builder.setIcon(R.drawable.ic_delete_black_24dp);
                builder.setMessage(R.string.delete_vide_warning);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.txt_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        deleteFromInternalStorage(position);
                        dialog.cancel();
                    }
                })
                        .setNegativeButton(R.string.txt_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int arg1) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        };
        horizontalAdapter = new CustomDownloadListAdapter(data, DownloadsActivity.this, addClick, onDeleteClick);

        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(DownloadsActivity.this, LinearLayoutManager.VERTICAL, false);
        list_videos.setLayoutManager(verticalLayoutManager);
        list_videos.setAdapter(horizontalAdapter);


    }


    private void deleteFromInternalStorage(int position) {


        if (hasCurrentVideo(data.get(position))) {
            File path = Environment.getExternalStoragePublicDirectory(
                    "/VideoStatus");
            File file = new File(path, data.get(position).getVideoFileName());
            if (file.exists()) {
                try {
                    FileUtils.forceDelete(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                deleteRow(data.get(position).getVideo_title(), position);
            } else {
                Toast.makeText(DownloadsActivity.this, R.string.error_file_extst, Toast.LENGTH_SHORT).show();
            }


        } else {
            Toast.makeText(DownloadsActivity.this, R.string.error_no_file, Toast.LENGTH_SHORT).show();
            deleteRow(data.get(position).getVideo_title(), position);
        }
    }


    private boolean hasCurrentVideo(videoListGetSet videoData) {
        // Create a path where we will place our video in the user's
        // public pictures directory and check if the file exists.  If
        // external storage is not currently mounted this will think the
        // picture doesn't exist.
        File path = Environment.getExternalStoragePublicDirectory(
                "/VideoStatus");
        File file = new File(path, videoData.getVideoFileName());
        return file.exists();
    }


    private void deleteRow(String value, int position) {
        DBAdapter myDbHelper = new DBAdapter(DownloadsActivity.this);
        try {
            myDbHelper.createDataBase();
        } catch (IOException io) {
            throw new Error("Unable To Create DataBase");
        }
        try {
            myDbHelper.openDataBase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        SQLiteDatabase db = myDbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + "Downloaded" + " WHERE " + "title" + "='" + value + "'");
        db.close();
        //delete from the list also
        data.remove(position);
        horizontalAdapter.notifyDataSetChanged();
    }


}
