package com.videostatusiranna.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.google.firebase.messaging.FirebaseMessaging;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.videostatusiranna.ObservableLayer.HomeVideoViewModel;
import com.videostatusiranna.adapter.CustomHorizontalAdapter;
import com.videostatusiranna.adapter.CustomNavigationBarAdapter;
import com.videostatusiranna.adapter.CustomVideoListAdapter;
import com.videostatusiranna.getSet.categoryGetSet;
import com.videostatusiranna.getSet.menuGetSet;
import com.videostatusiranna.getSet.videoListGetSet;
import com.videostatusiranna.R;
import com.videostatusiranna.onClickListners.OnLoadListeners;
import com.videostatusiranna.onClickListners.onCategoryListClick;
import com.videostatusiranna.onClickListners.onVideoListClick;
import com.videostatusiranna.utilities.AdManager;
import com.videostatusiranna.utilities.Config;


public class HomeActivity extends AppCompatActivity {

    public static final String prefName = "VideoStatus";
    public static Typeface tf_main_medium, tf_main_bold;
    private static Boolean isLoadMore = false;
    private static Boolean isSearch = false;
    private int numberOfRecords;
    private LinearLayout mainView;
    private DrawerLayout drawer;
    private ListView list_sliderMenu;
    private RecyclerView list_moods, list_videos;
    private String categoryString;
    private ProgressBar progressBar;
    private String subCategoryString;
    private ArrayList<categoryGetSet> categoryGetSets;
    private View searchView;
    private ImageView iv_back, iv_clear_text;
    private EditText edt_search_text;
    private List<Object> dataCombined;
    private int pageNumber;
    private CustomVideoListAdapter verticalAdapter;
    private HomeVideoViewModel homeVideoViewModel;
    private String search = "";


    public static void changeLoad(Boolean b) {
        isLoadMore = b;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        numberOfRecords = getResources().getInteger(R.integer.numberOfRecords);

        homeVideoViewModel = ViewModelProviders.of(this).get(HomeVideoViewModel.class);

        globalTypeface();

        if(isNetworkConnected(this))
        getDefaultCategory();
        else showErrorDialog(this);

        initViews();

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);


        if (pref.getString("regId", null) != null) {
            String regId = pref.getString("regId", null);
            Log.e("fireBaseRid132", "Firebase Reg id: " + regId);
            //Registering device id to server
        } else {
            BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    // checking for type intent filter
                    if (Objects.equals(intent.getAction(), Config.REGISTRATION_COMPLETE)) {
                        // gcm successfully registered
                        // now subscribe to `global` topic to receive app wide notifications
                        FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                        displayFireBaseRegId();
                    } else if (Objects.equals(intent.getAction(), Config.PUSH_NOTIFICATION)) {
                        // new push notification is received
                        String message = intent.getStringExtra("message");
                        Toast.makeText(getApplicationContext(), "notification: " + message, Toast.LENGTH_LONG).show();
                    }
                }
            };
        }

        //create notification channel for device oreo
        createNotificationChannel();


    }




    private void globalTypeface() {
        tf_main_medium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        tf_main_bold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");
    }


    private void getDefaultCategory() {

        homeVideoViewModel.getMenuList().observe(this, new Observer<ArrayList<menuGetSet>>() {
            @Override
            public void onChanged(@Nullable ArrayList<menuGetSet> menuGetSets) {

                if (menuGetSets != null) {

                    setUpVideoList();

                    categoryString = getSharedPreferences(prefName, MODE_PRIVATE).getString("Category", menuGetSets.get(0).getCat_id());

                    pageNumber = 1;

                    homeVideoViewModel.LoadVideoList(getSharedPreferences(prefName, MODE_PRIVATE).getString("Category", categoryString), "", getSharedPreferences(prefName, MODE_PRIVATE).getString("orderBy", "desc"), String.valueOf(numberOfRecords), String.valueOf(pageNumber));

                    setUpCategoryView(categoryString);

                    getIntents();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        AdManager.setUpInterstial(this);

    }

    private void getIntents() {
        Intent intent = getIntent();
        subCategoryString = intent.getStringExtra("subCategory");
        if (subCategoryString == null) {
            subCategoryString = "";
        } else {
            pageNumber = 1;
            homeVideoViewModel.LoadVideoList(getSharedPreferences(prefName, MODE_PRIVATE).getString("Category", categoryString), subCategoryString, getSharedPreferences(prefName, MODE_PRIVATE).getString("orderBy", "desc"), String.valueOf(numberOfRecords), String.valueOf(pageNumber));
        }
    }


    private void setUpCategoryView(final String categoryString) {
        //creating a string request to send request to the url
        String hp = getString(R.string.link) + "api/video_subcategory.php?category=" + categoryString;
        hp = hp.replace(" ", "%20");
        Log.w(getClass().getName(), hp);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, hp,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        Log.e("Response", response);
                        try {
                            //getting the whole json object from the response
                            JSONObject obj = new JSONObject(response);
                            String status = obj.getString("success");
                            if (Objects.equals(status, "1")) {
                                categoryGetSets = new ArrayList<>();
                                categoryGetSet temp;
                                JSONArray ja_video = obj.getJSONArray("video");
                                for (int i = 0; i < ja_video.length(); i++) {
                                    temp = new categoryGetSet();
                                    JSONObject jo_data = ja_video.getJSONObject(i);
                                    String txt_sub_category = jo_data.getString("subcategory");
                                    String txt_cat_id = jo_data.getString("id");
                                    String txt_image = jo_data.getString("subcategory_icon");
                                    temp.setImageName(txt_image.replace(" ", "%20"));
                                    temp.setSubCategoryId(txt_cat_id);
                                    temp.setSubCategoryName(txt_sub_category);
                                    categoryGetSets.add(temp);
                                }
                            }
                            if (categoryGetSets != null) {
                                LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false);
                                list_moods.setLayoutManager(horizontalLayoutManager);
                                CustomHorizontalAdapter horizontalAdapter = new CustomHorizontalAdapter(HomeActivity.this, categoryGetSets, new onCategoryListClick() {
                                    @Override
                                    public void onItemClick(View v, int position) {
                                        subCategoryString = categoryGetSets.get(position).getSubCategoryId();
                                        pageNumber = 1;
                                        homeVideoViewModel.LoadVideoList(getSharedPreferences(prefName, MODE_PRIVATE).getString("Category", categoryString), subCategoryString, getSharedPreferences(prefName, MODE_PRIVATE).getString("orderBy", "desc"), String.valueOf(numberOfRecords), String.valueOf(pageNumber));
                                    }
                                });
                                list_moods.setAdapter(horizontalAdapter);
                            }
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


    private void displayFireBaseRegId() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        Log.e("fireBaseRid", "Firebase Reg id: " + regId);

    }


    private void setUpVideoList() {
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false);
        list_videos.setLayoutManager(verticalLayoutManager);

        homeVideoViewModel.getVideosList().observe(this, new Observer<ArrayList<videoListGetSet>>() {
            @Override
            public void onChanged(@Nullable ArrayList<videoListGetSet> videoListGetSets) {
                progressBar.setVisibility(View.VISIBLE);
                if (videoListGetSets != null) {
                    updateUI(videoListGetSets);
                }
                else {
                    emptyUI();
                }
            }
        });
    }


    private void initViews() {
        list_sliderMenu = findViewById(R.id.list_slidermenu);
        list_moods = findViewById(R.id.list_moods);
        list_videos = findViewById(R.id.list_videos);
        progressBar = findViewById(R.id.progressBar);
        pageNumber = 1;

        //Search Implementation
        searchView = findViewById(R.id.searchView);
        iv_back = findViewById(R.id.iv_back);
        iv_clear_text = findViewById(R.id.iv_clear_text);
        edt_search_text = findViewById(R.id.edt_search_text);

        //AdMob Integration

        AdManager.setUpInterstial(this);
//        if (getString(R.string.show_admmob_ads).equals("yes")) {
//            mInterstitialAd = new InterstitialAd(this);
//            mInterstitialAd.setAdUnitId(getString(R.string.interstial_ad_unit_id));
//        }
//        else if(getString(R.string.show_facebook_ads).equals("yes"))
//        {
//            interstitialAd = new com.facebook.ads.InterstitialAd(this, getString(R.string.facebook_interstial));
//        }

        setUpDrawer();

        setUpNavigationBar();

        setUpSearch();

    }



    private void setUpSearch() {

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setVisibility(View.GONE);
                edt_search_text.setText("");
                isSearch = false;
                pageNumber = 1;
                search = "";
                progressBar.setVisibility(View.VISIBLE);
                homeVideoViewModel.LoadVideoList(getSharedPreferences(prefName, MODE_PRIVATE).getString("Category", categoryString), "", getSharedPreferences(prefName, MODE_PRIVATE).getString("orderBy", "desc"), String.valueOf(numberOfRecords), String.valueOf(pageNumber));
            }
        });

        iv_clear_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_search_text.setText("");

            }
        });


        /*search btn clicked*/
        edt_search_text.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    isSearch = true;
                    pageNumber = 1;
                    performSearch(v.getText().toString());
                    search = v.getText().toString();
                    return true;
                }
                return false;
            }
        });

        TextWatcher searchViewTextWatcher = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() == 0) {
                    iv_clear_text.setVisibility(View.GONE);
                } else {
                    iv_clear_text.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        };
        /*hide/show clear button in search view*/
        edt_search_text.addTextChangedListener(searchViewTextWatcher);


    }


    private void performSearch(String s) {
        progressBar.setVisibility(View.VISIBLE);
        homeVideoViewModel.SearchData(s, getSharedPreferences(prefName, MODE_PRIVATE).getString("orderBy", "desc"), String.valueOf(numberOfRecords), String.valueOf(pageNumber));
        progressBar.setVisibility(View.GONE);

    }


    private void setUpDrawer() {
        //drawer setup
        drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mainView = findViewById(R.id.mainView);
        toolbar.setNavigationIcon(R.drawable.btn_selectsidemenu);

        setSupportActionBar(toolbar);
        ImageView btn_setting = toolbar.findViewById(R.id.btn_setting);
        TextView title = toolbar.findViewById(R.id.title);
        title.setTypeface(tf_main_medium);

        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, SettingActivity.class);
                startActivity(i);
            }
        });

        ImageView btn_search = toolbar.findViewById(R.id.btn_search);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setVisibility(View.VISIBLE);
                edt_search_text.requestFocus();
                // Show soft keyboard for the user to enter the value.
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(edt_search_text, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

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


    private void setUpNavigationBar() {
        CustomNavigationBarAdapter customNavigationBarAdapter = new CustomNavigationBarAdapter(HomeActivity.this, drawer);
        list_sliderMenu.setAdapter(customNavigationBarAdapter);
    }


    private void updateUI(ArrayList<videoListGetSet> videoData) {
        list_videos.setVisibility(View.VISIBLE);

        if (videoData != null) {
            Log.e("Checking Search", " " + isSearch);

            if (dataCombined == null || !isLoadMore) {
                dataCombined = new ArrayList<>();

            }

            //inserting adMOb Ads in list
            for (int i = 0; i < videoData.size(); i++) {
                if (getString(R.string.show_admmob_ads).equals("yes") || getString(R.string.show_facebook_ads).equals("yes")) {
                    if (i % 2 == 0 && i != 0) {
                        dataCombined.add("ad");
                    }
                }
                dataCombined.add(videoData.get(i));
            }




            progressBar.setVisibility(View.GONE);

            onVideoListClick addClick = new onVideoListClick() {
                @Override
                public void onItemClick(View v, int position) {
                    if (dataCombined.get(position) instanceof videoListGetSet)
                        increaseViewOfItem(((videoListGetSet) dataCombined.get(position)).getId(), position);
                }
            };

            if (!isLoadMore) {
                verticalAdapter = new CustomVideoListAdapter(dataCombined, HomeActivity.this, addClick, list_videos);
                list_videos.setAdapter(verticalAdapter);
                notifyAdapter();
            } else {
                notifyAdapter();
                isLoadMore = false;
            }

            verticalAdapter.setOnLoadMoreListener(new OnLoadListeners() {
                @Override
                public void onLoadMore() {
                    progressBar.setVisibility(View.VISIBLE);
                    getMoreDataFromServer();
                }
            });
        }

    }


    private void emptyUI() {
        if(progressBar!=null)
        {
            progressBar.setVisibility(View.GONE);
        }
         list_videos.setVisibility(View.GONE);
        Snackbar mSnackBar = Snackbar.make(mainView, R.string.txt_no_data_avail, Snackbar.LENGTH_LONG);
        View mView = mSnackBar.getView();
        TextView mTextView = mView.findViewById(android.support.design.R.id.snackbar_text);
        mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        mSnackBar.show();
    }


    private void notifyAdapter() {
        if (verticalAdapter != null)
            verticalAdapter.notifyDataSetChanged();
    }


    private void getMoreDataFromServer() {

        pageNumber = pageNumber + 1;
        if (!isSearch) {
            Log.e("Check Page", "" + pageNumber);
            homeVideoViewModel.LoadMoreData(getSharedPreferences(prefName, MODE_PRIVATE).getString("Category", categoryString), subCategoryString, getSharedPreferences(prefName, MODE_PRIVATE).getString("orderBy", "desc"), String.valueOf(numberOfRecords), String.valueOf(pageNumber));
            progressBar.setVisibility(View.GONE);

        } else {
            homeVideoViewModel.LoadMoreSearchData(search, getSharedPreferences(prefName, MODE_PRIVATE).getString("orderBy", "desc"), String.valueOf(numberOfRecords), String.valueOf(pageNumber));
            progressBar.setVisibility(View.GONE);
        }

    }


    private void increaseViewOfItem(String id, final int position) {
        //    progressBar.setVisibility(View.VISIBLE);

        //creating a string request to send request to the url
        String hp = getString(R.string.link) + "api/video_view_count.php?video_id=" + id;
        Log.w(getClass().getName(), hp);
        hp = hp.replace(" ", "%20");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, hp,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        //    progressBar.setVisibility(View.GONE);
                        Log.e("Response", response);

                        try {
                            //getting the whole json object from the response
                            JSONObject obj = new JSONObject(response);
                            JSONObject data = obj.getJSONObject("data");
                            String status = data.getString("success");
                            if (Objects.equals(status, "1")) {
//                                JSONObject ja_video = data.getJSONObject("video");
                                String txt_view = data.getString("view");
                                ((videoListGetSet) dataCombined.get(position)).setVideoView(txt_view);
                            } else {
                                Toast.makeText(HomeActivity.this, "Error in network", Toast.LENGTH_SHORT).show();
                            }

                            //send intent
                            if (dataCombined.get(position) instanceof videoListGetSet) {
                                Log.d("CheckAllValue", "" + (((videoListGetSet) dataCombined.get(position)).getVideo_title()) + "::::" +
                                        (((videoListGetSet) dataCombined.get(position)).getVideoFileName()) + "::::" +
                                        (((videoListGetSet) dataCombined.get(position)).getVideo_cat_id()) + "::::" +
                                        (((videoListGetSet) dataCombined.get(position)).getVideo_subCat_id()) + "::::" + (((videoListGetSet) dataCombined.get(position)).getVideoDownload()) + "::::" +
                                        (((videoListGetSet) dataCombined.get(position)).getVideoImage()) + "::::" + (((videoListGetSet) dataCombined.get(position)).getVideoView()) + "::::" +
                                        (((videoListGetSet) dataCombined.get(position)).getId()));
                                Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
                                intent.putExtra("videoTitle", (((videoListGetSet) dataCombined.get(position)).getVideo_title()));
                                intent.putExtra("videoId", (((videoListGetSet) dataCombined.get(position)).getVideoFileName()));
                                intent.putExtra("videoCategory", (((videoListGetSet) dataCombined.get(position)).getVideo_category()));
                                intent.putExtra("videoSubCategory", (((videoListGetSet) dataCombined.get(position)).getVideo_subcategory()));
                                intent.putExtra("videoCategoryId", (((videoListGetSet) dataCombined.get(position)).getVideo_cat_id()));
                                intent.putExtra("videoSubCategoryId", (((videoListGetSet) dataCombined.get(position)).getVideo_subCat_id()));
                                intent.putExtra("videoDownload", (((videoListGetSet) dataCombined.get(position)).getVideoDownload()));
                                intent.putExtra("videoImage", (((videoListGetSet) dataCombined.get(position)).getVideoImage()));
                                intent.putExtra("videoView", (((videoListGetSet) dataCombined.get(position)).getVideoView()));
                                intent.putExtra("id", (((videoListGetSet) dataCombined.get(position)).getId()));
                                startActivity(intent);
                                AdManager.increaseCount(HomeActivity.this);
                                AdManager.showInterstial(HomeActivity.this);


                            }

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
    public void onBackPressed() {

        super.onBackPressed();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("100", name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo() != null;
    }
    public static void showErrorDialog(Context context) {
        try {

            ProgressBar progressBar = ((Activity)context).findViewById(R.id.progressBar);
            if(progressBar.getVisibility()==View.VISIBLE)
                progressBar.setVisibility(View.GONE);


            final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle(context.getString(R.string.info));
            alertDialog.setMessage(context.getString(R.string.noInternet));
            alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,context.getString(R.string.txt_ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }
        catch(Exception e)
        {
            Log.d("Home", "Show Dialog: "+e.getMessage());
        }
    }
}
