package com.videostatusiranna.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
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

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.videostatusiranna.ObservableLayer.PopularVideoViewModel;
import com.videostatusiranna.adapter.CustomNavigationBarAdapter;
import com.videostatusiranna.adapter.CustomVideoListAdapter;
import com.videostatusiranna.getSet.videoListGetSet;
import com.videostatusiranna.R;
import com.videostatusiranna.onClickListners.OnLoadListeners;
import com.videostatusiranna.onClickListners.onVideoListClick;
import com.videostatusiranna.utilities.AdManager;

import static com.videostatusiranna.activity.HomeActivity.isNetworkConnected;
import static com.videostatusiranna.activity.HomeActivity.showErrorDialog;

public class PopularActivity extends AppCompatActivity {
    private ListView list_SliderMenu;
    private DrawerLayout drawer;
    private LinearLayout mainView;
    private RecyclerView list_videos;
    private List<Object> dataCombined;
    private ProgressBar progressBar;
    private static Boolean isLoadMore = false;
    private CustomVideoListAdapter horizontalAdapter;
    private int pageNumber;
    private PopularVideoViewModel popularVideoViewModel;
    private int numberOfRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular);

        numberOfRecords = getResources().getInteger(R.integer.numberOfRecords);

        popularVideoViewModel = ViewModelProviders.of(this).get(PopularVideoViewModel.class);


        initViews();
    }


    private void initViews() {

        pageNumber = 1;

        list_SliderMenu = findViewById(R.id.list_slidermenu);
        list_videos = findViewById(R.id.list_videos);


        setUpDrawer();

        setUpNavigationBar();

        if (isNetworkConnected(this))
            setUpVideoList();
        else showErrorDialog(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AdManager.setUpInterstial(this);
    }


    private void setUpDrawer() {
        //drawer setup
        drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mainView = findViewById(R.id.mainView);
        progressBar = findViewById(R.id.progressBar);

        setSupportActionBar(toolbar);
        ImageView btn_setting = toolbar.findViewById(R.id.btn_setting);
        btn_setting.setVisibility(View.GONE);

        ImageView btn_search = toolbar.findViewById(R.id.btn_search);
        btn_search.setVisibility(View.GONE);

        TextView txt_tittle = toolbar.findViewById(R.id.title);
        txt_tittle.setText(R.string.txt_popular);
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
        CustomNavigationBarAdapter customNavigationBarAdapter = new CustomNavigationBarAdapter(PopularActivity.this, drawer);
        list_SliderMenu.setAdapter(customNavigationBarAdapter);
    }


    public static void changeLoad(Boolean b) {
        isLoadMore = b;
    }


    private void setUpVideoList() {
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(PopularActivity.this, LinearLayoutManager.VERTICAL, false);
        list_videos.setLayoutManager(verticalLayoutManager);

        popularVideoViewModel.getPostsList().observe(this, new Observer<ArrayList<videoListGetSet>>() {
            @Override
            public void onChanged(@Nullable ArrayList<videoListGetSet> videoListGetSets) {
                progressBar.setVisibility(View.VISIBLE);
                updateUI(videoListGetSets);
            }
        });

    }


    private void updateUI(final ArrayList<videoListGetSet> videoPopularList) {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        if (videoPopularList != null) {
            if (dataCombined == null || !isLoadMore) {
                dataCombined = new ArrayList<>();
            }
            //adding adMob ads to list
            for (int i = 0; i < videoPopularList.size(); i++) {

                if (getString(R.string.show_admmob_ads).equals("yes")) {
                    if (i % 2 == 0 && i != 0) {
                        dataCombined.add("ad");
                    }
                } else if (getString(R.string.show_facebook_ads).equals("yes")) {
                    if (i % 2 == 0 && i != 0) {
                        dataCombined.add("ad");
                    }
                }
                dataCombined.add(videoPopularList.get(i));

            }


            onVideoListClick addClick = new onVideoListClick() {
                @Override
                public void onItemClick(View v, int position) {
                    if (dataCombined.get(position) instanceof videoListGetSet)
                        increaseViewOfItem(((videoListGetSet) dataCombined.get(position)).getId(), position);
                }
            };


            if (!isLoadMore) {
                horizontalAdapter = new CustomVideoListAdapter(dataCombined, PopularActivity.this, addClick, list_videos);
                list_videos.setAdapter(horizontalAdapter);
                notifyAdapter();
            } else {
                notifyAdapter();
                isLoadMore = false;
            }


            horizontalAdapter.setOnLoadMoreListener(new OnLoadListeners() {
                @Override
                public void onLoadMore() {
                    progressBar.setVisibility(View.VISIBLE);
                    getMoreDataFromServer();
                }
            });

        }
    }


    private void notifyAdapter() {
        if (horizontalAdapter != null)
            horizontalAdapter.notifyDataSetChanged();
    }


    private void getMoreDataFromServer() {
        pageNumber = pageNumber + 1;
        Log.e("Check Page", "" + pageNumber);
        popularVideoViewModel.LoadMoreData(String.valueOf(numberOfRecords), String.valueOf(pageNumber));
        progressBar.setVisibility(View.GONE);
    }


    private void increaseViewOfItem(String id, final int position) {
        progressBar.setVisibility(View.VISIBLE);

        //creating a string request to send request to the url
        String hp = getString(R.string.link) + "api/video_view_count.php?video_id=" + id;
        Log.w(getClass().getName(), hp);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, hp,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);

                        //hiding the progressbar after completion
                        progressBar.setVisibility(View.GONE);
                        Log.e("Response", response);

                        try {

                            //getting the whole json object from the response
                            JSONObject obj = new JSONObject(response);
                            JSONObject data = obj.getJSONObject("data");
                            String status = data.getString("success");
                            if (Objects.equals(status, "1")) {
                                String txt_view = data.getString("success");
                                ;
                                ((videoListGetSet) dataCombined.get(position)).setVideoView(txt_view);
                            } else {
                                Toast.makeText(PopularActivity.this, "Error in network", Toast.LENGTH_SHORT).show();
                            }

                            //send intent
                            if (dataCombined.get(position) instanceof videoListGetSet) {
                                Intent intent = new Intent(PopularActivity.this, DetailActivity.class);
                                intent.putExtra("videoTitle", (((videoListGetSet) dataCombined.get(position)).getVideo_title()));
                                intent.putExtra("videoId", (((videoListGetSet) dataCombined.get(position)).getVideoFileName()));
                                intent.putExtra("videoCategory", (((videoListGetSet) dataCombined.get(position)).getVideo_category()));
                                intent.putExtra("videoSubCategory", (((videoListGetSet) dataCombined.get(position)).getVideo_subcategory()));
                                intent.putExtra("videoDownload", (((videoListGetSet) dataCombined.get(position)).getVideoDownload()));
                                intent.putExtra("videoImage", (((videoListGetSet) dataCombined.get(position)).getVideoImage()));
                                intent.putExtra("videoCategoryId", (((videoListGetSet) dataCombined.get(position)).getVideo_cat_id()));
                                intent.putExtra("videoSubCategoryId", (((videoListGetSet) dataCombined.get(position)).getVideo_subCat_id()));
                                intent.putExtra("videoView", (((videoListGetSet) dataCombined.get(position)).getVideoView()));
                                intent.putExtra("id", (((videoListGetSet) dataCombined.get(position)).getId()));
                                startActivity(intent);
                            }
                            AdManager.increaseCount(PopularActivity.this);
                            AdManager.showInterstial(PopularActivity.this);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurs
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null) {
                            Log.e("Status code", String.valueOf(networkResponse.statusCode));
                            Toast.makeText(getApplicationContext(), String.valueOf(networkResponse.statusCode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);


    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
