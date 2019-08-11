package com.videostatusiranna.activity;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.ShareToMessengerParams;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import com.videostatusiranna.adapter.CustomVideoSubListAdapter;
import com.videostatusiranna.getSet.videoListGetSet;
import com.videostatusiranna.R;
import com.videostatusiranna.onClickListners.OnLoadListeners;
import com.videostatusiranna.utilities.AdManager;
import com.videostatusiranna.utilities.DBAdapter;
import com.videostatusiranna.utilities.JZVideoPlayerNew;
import com.videostatusiranna.onClickListners.onVideoListClick;

import static com.videostatusiranna.activity.HomeActivity.isNetworkConnected;
import static com.videostatusiranna.activity.HomeActivity.prefName;
import static com.videostatusiranna.activity.HomeActivity.showErrorDialog;
import static com.videostatusiranna.adapter.CustomVideoSubListAdapter.setLoaded;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {
    private final ArrayList<Long> list = new ArrayList<>();
    private final int numberOfRecords = 4;
    private videoListGetSet videoData;
    private TextView tv_downloads;
    private RecyclerView list_extraVideo;
    private ArrayList<videoListGetSet> dataSuggested;
    private ImageView btn_download;
    private DownloadManager downloadManager;
    private String videoPath;
    private File file;
    private final BroadcastReceiver onComplete = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(context,
                    new String[]{file.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });


            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            Log.e("IN", "" + referenceId);
            list.remove(referenceId);
            if (list.isEmpty()) {
                Log.e("INSIDE", "" + referenceId);
                Notification.Builder mBuilder =
                        new Notification.Builder(DetailActivity.this)
                                .setSmallIcon(R.drawable.ic_play)
                                .setContentTitle(getPackageName())
                                .setContentText("All Download completed");

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (notificationManager != null) {
                    notificationManager.notify(455, mBuilder.build());
                }
            }

        }
    };
    private ImageButton btn_cancelDownload;
    private TextView txt_progress;
    private TextView txt_fileSize;
    private ProgressBar progressBar;
    private RelativeLayout rel_showProgress;
    private View detail_toolbar;
    private boolean isLoadAd = true;
    private int pageNumber;
    private CustomVideoSubListAdapter horizontalAdapter;
    private boolean isWhatsApp;
    private boolean isFacebook;
    private boolean isTwitter;
    private boolean isMessanger;
    private boolean isInstagram;
    private boolean isShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getIntents();

        initViews();

        if(isNetworkConnected(this))
        setUpVideoList();
        else showErrorDialog(this);


    }

    private void getIntents() {
        videoData = new videoListGetSet();
        Intent intent = getIntent();
        String videoTitle = intent.getStringExtra("videoTitle");
        String VideoId = intent.getStringExtra("videoId");
        String videoCategory = intent.getStringExtra("videoCategory");
        String videoSubCategory = intent.getStringExtra("videoSubCategory");
        String videoDownload = intent.getStringExtra("videoDownload");
        String videoImage = intent.getStringExtra("videoImage");
        String videoView = intent.getStringExtra("videoView");
        String videoCategoryId = intent.getStringExtra("videoCategoryId");
        String videoSubCategoryId = intent.getStringExtra("videoSubCategoryId");
        String id = intent.getStringExtra("id");


        videoData.setVideoFileName(VideoId);
        videoData.setVideo_title(videoTitle);
        videoData.setVideo_subCat_id(videoSubCategoryId);
        videoData.setVideo_cat_id(videoCategoryId);
        videoData.setVideo_subcategory(videoSubCategory);
        videoData.setVideo_category(videoCategory);
        videoData.setVideoDownload(videoDownload);
        videoData.setVideoImage(videoImage);
        videoData.setVideoView(videoView);
        videoData.setId(id);
    }

    private void initViews() {


        TextView tv_video_title = findViewById(R.id.tv_video_title);
        TextView tv_video_SubCat = findViewById(R.id.tv_video_subcat);
        TextView tv_num_views = findViewById(R.id.tv_num_views);
        tv_downloads = findViewById(R.id.tv_downloads);
        list_extraVideo = findViewById(R.id.list_extraVideo);
        btn_download = findViewById(R.id.btn_download);
        ImageView btn_facebook = findViewById(R.id.btn_facebook);
        ImageView btn_twitter = findViewById(R.id.btn_twitter);
        ImageView btn_Messanger = findViewById(R.id.btn_messanger);
        ImageView btn_instagram = findViewById(R.id.btn_instagram);
        ImageView btn_share = findViewById(R.id.btn_share);
        ImageView btn_whatsapp = findViewById(R.id.btn_whatsapp);
        progressBar = findViewById(R.id.progressBarHorizontal);
        txt_progress = findViewById(R.id.txt_progress);
        txt_fileSize = findViewById(R.id.txt_fileSize);
        btn_cancelDownload = findViewById(R.id.btn_cancelDownload);
        rel_showProgress = findViewById(R.id.rel_showProgress);
        detail_toolbar = findViewById(R.id.detail_toolbar);

        //setting typeface
        tv_video_title.setTypeface(HomeActivity.tf_main_medium);
        tv_video_SubCat.setTypeface(HomeActivity.tf_main_medium);
        tv_downloads.setTypeface(HomeActivity.tf_main_medium);
        tv_num_views.setTypeface(HomeActivity.tf_main_medium);
        tv_num_views.setTypeface(HomeActivity.tf_main_medium);
        tv_num_views.setTypeface(HomeActivity.tf_main_medium);


        //setting listeners
        btn_download.setOnClickListener(this);
        btn_facebook.setOnClickListener(this);
        btn_twitter.setOnClickListener(this);
        btn_Messanger.setOnClickListener(this);
        btn_instagram.setOnClickListener(this);
        btn_share.setOnClickListener(this);
        btn_whatsapp.setOnClickListener(this);



        //register broadcast
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


        //setting data

        tv_video_title.setText(videoData.getVideo_title());
        String temp = videoData.getVideo_category() + " - " + videoData.getVideo_subcategory();
        tv_video_SubCat.setText(temp);
        String tempViews = videoData.getVideoView() + " " + getString(R.string.txt_views);
        String tempDownload = videoData.getVideoDownload() + " " + getString(R.string.txt_downloads);
        tv_num_views.setText(tempViews);
        tv_downloads.setText(tempDownload);

        videoPath = getString(R.string.link) + "images/video/" + videoData.getVideoFileName();

        changeBar();

        //check whether the video is already downloaded or not
        JZVideoPlayerNew jzVideoPlayerStandard;
        jzVideoPlayerStandard = findViewById(R.id.videoPlayer);
        if (!hasCurrentVideo()) {
            videoPath = videoPath.replace(" ", "%20");
            jzVideoPlayerStandard.setUp(videoPath, JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, videoData.getVideo_title());
            Picasso.get().load(getString(R.string.link) + "images/thumbnail/" + videoData.getVideoImage()).into(jzVideoPlayerStandard.thumbImageView);
        } else {
            File path = Environment.getExternalStoragePublicDirectory("/VideoStatus");
            file = new File(path, videoData.getVideoFileName());
            videoPath = getString(R.string.link) + "images/video/" + videoData.getVideoFileName();
            jzVideoPlayerStandard.setUp(file.getPath(), JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, videoData.getVideo_title());
            Picasso.get().load(getString(R.string.link) + "images/thumbnail/" + videoData.getVideoImage()).into(jzVideoPlayerStandard.thumbImageView);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //load Interstial AD
        AdManager.setUpInterstial(this);

    }

    private void setUpVideoList() {
        //getting the progressbar
        final ProgressBar progressBarN = findViewById(R.id.progressBar);

        //making the progressbar visible
        progressBarN.setVisibility(View.VISIBLE);

        //setting Page Number
        pageNumber = 1;

        //creating a string request to send request to the url
        String hp = getString(R.string.link) + "api/video_list.php?category=" + videoData.getVideo_cat_id() + "&subcategory=" + videoData.getVideo_subCat_id() + "&sort_by=" + getSharedPreferences(prefName, MODE_PRIVATE).getString("orderBy", "asc") + "&noofrecords=" + numberOfRecords + "&pageno=" + pageNumber;

        hp = hp.replace(" ", "%20");
        Log.d("checkUrl", "" + hp);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, hp,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        progressBarN.setVisibility(View.INVISIBLE);

                        Log.e("Response", response);

                        try {
                            //getting the whole json object from the response
                            JSONObject obj = new JSONObject(response);
                            String status = obj.getString("success");
                            if (Objects.equals(status, "1")) {
                                dataSuggested = new ArrayList<>();
                                videoListGetSet temp;

                                JSONArray ja_video = obj.getJSONArray("video");
                                for (int i = 0; i < ja_video.length(); i++) {
                                    temp = new videoListGetSet();
                                    JSONObject jo_data = ja_video.getJSONObject(i);
                                    String txt_video_category = jo_data.getString("video_category");
                                    String txt_video_subcategory = jo_data.getString("video_subcategory");
                                    String txt_video_title = jo_data.getString("video_title");
                                    String txt_video = jo_data.getString("video");
                                    String txt_image = jo_data.getString("image");
                                    String txt_view = jo_data.getString("view");
                                    String txt_download = jo_data.getString("download");
                                    String txt_id = jo_data.getString("id");
                                    temp.setVideo_cat_id(jo_data.getString("cat_id"));
                                    temp.setVideo_subCat_id(jo_data.getString("subcat_id"));
                                    temp.setVideo_title(txt_video_title);
                                    temp.setVideo_category(txt_video_category);
                                    temp.setVideo_subcategory(txt_video_subcategory);
                                    temp.setVideoDownload(txt_download);
                                    temp.setVideoImage(txt_image);
                                    temp.setVideoView(txt_view);
                                    temp.setVideoFileName(txt_video);
                                    temp.setId(txt_id);

                                    if (!txt_video_title.equals(videoData.getVideo_title()))
                                        dataSuggested.add(temp);

                                }

                                updateUI();

                            } else {
                                Toast.makeText(DetailActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);


    }

    private void changeBar() {
        Boolean check = hasCurrentVideo();
        if (check) {
            btn_download.setImageResource(R.drawable.donwloadno);
        } else {
            btn_download.setImageResource(R.drawable.downloadyes);
        }

    }

    private void updateUI() {
        onVideoListClick addClick = new onVideoListClick() {
            @Override
            public void onItemClick(View v, int position) {
                increaseViewOfItem(dataSuggested.get(position).getId(), position);
            }
        };

        GridLayoutManager gridLayoutManager = new GridLayoutManager(DetailActivity.this, 2, LinearLayoutManager.VERTICAL, false);
        list_extraVideo.setLayoutManager(gridLayoutManager);
        horizontalAdapter = new CustomVideoSubListAdapter(dataSuggested, DetailActivity.this, addClick, list_extraVideo);

        list_extraVideo.setAdapter(horizontalAdapter);
        horizontalAdapter.setOnLoadMoreListener(new OnLoadListeners() {
            @Override
            public void onLoadMore() {
                progressBar.setVisibility(View.VISIBLE);
                getMoreDataFromServer();
            }
        });


    }

    private void getMoreDataFromServer() {
        pageNumber = pageNumber + 1;
        //creating a string request to send request to the url
        String hp = getString(R.string.link) + "api/video_list.php?category=" + videoData.getVideo_cat_id() + "&subcategory=" + videoData.getVideo_subCat_id() + "&sort_by=" + getSharedPreferences(prefName, MODE_PRIVATE).getString("orderBy", "asc") + "&noofrecords=" + numberOfRecords + "&pageno=" + pageNumber;
        hp = hp.replace(" ", "%20");
        Log.w(getClass().getName(), hp);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, hp,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        Log.e("Response", response);
                        ArrayList<videoListGetSet> tempArray = new ArrayList<>();

                        try {
                            //getting the whole json object from the response
                            JSONObject obj = new JSONObject(response);
                            String status = obj.getString("success");
                            if (Objects.equals(status, "1")) {
                                videoListGetSet temp;
                                JSONArray ja_video = obj.getJSONArray("video");
                                for (int i = 0; i < ja_video.length(); i++) {
                                    temp = new videoListGetSet();
                                    JSONObject jo_data = ja_video.getJSONObject(i);
                                    String txt_video_id = jo_data.getString("id");
                                    String txt_video_category = jo_data.getString("video_category");
                                    String txt_video_subcategory = jo_data.getString("video_subcategory");
                                    String txt_video_title = jo_data.getString("video_title");
                                    String txt_video = jo_data.getString("video");
                                    String txt_image = jo_data.getString("image");
                                    String txt_view = jo_data.getString("view");
                                    String txt_download = jo_data.getString("download");
                                    temp.setVideo_cat_id(jo_data.optString("cat_id"));
                                    temp.setVideo_subCat_id(jo_data.optString("subcat_id"));
                                    temp.setId(txt_video_id);
                                    temp.setVideo_title(txt_video_title);
                                    temp.setVideo_category(txt_video_category);
                                    temp.setVideo_subcategory(txt_video_subcategory);
                                    temp.setVideoDownload(txt_download);
                                    temp.setVideoImage(txt_image);
                                    temp.setVideoView(txt_view);
                                    temp.setVideoFileName(txt_video);
                                    if (!txt_video_title.equals(videoData.getVideo_title()))
                                        tempArray.add(temp);
                                }
                                dataSuggested.addAll(tempArray);

                                horizontalAdapter.notifyDataSetChanged();
                                setLoaded(false);
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
                            Toast.makeText(getApplication(), String.valueOf(networkResponse.statusCode), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getApplication());

        //adding the string request to request queue
        requestQueue.add(stringRequest);


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
                                (dataSuggested.get(position)).setVideoView(txt_view);
                            } else {
                                Toast.makeText(DetailActivity.this, "Error in network", Toast.LENGTH_SHORT).show();
                            }

                            //send intent
                            Intent intent = new Intent(DetailActivity.this, DetailActivity.class);
                            intent.putExtra("videoTitle", dataSuggested.get(position).getVideo_title());
                            intent.putExtra("videoId", dataSuggested.get(position).getVideoFileName());
                            intent.putExtra("id", dataSuggested.get(position).getId());
                            intent.putExtra("videoCategory", dataSuggested.get(position).getVideo_cat_id());
                            intent.putExtra("videoSubCategory", dataSuggested.get(position).getVideo_subCat_id());
                            intent.putExtra("videoDownload", dataSuggested.get(position).getVideoDownload());
                            intent.putExtra("videoSubCategoryId", dataSuggested.get(position).getVideo_subCat_id());
                            intent.putExtra("videoCategoryId", dataSuggested.get(position).getVideo_cat_id());
                            intent.putExtra("videoImage", dataSuggested.get(position).getVideoImage());
                            intent.putExtra("videoView", dataSuggested.get(position).getVideoView());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            AdManager.increaseCount(DetailActivity.this);
                            AdManager.showInterstial(DetailActivity.this);


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
    public void onClick(View v) {
        int id = v.getId();
        Boolean check = hasCurrentVideo();

        switch (id) {

            case R.id.btn_download:

                if (isStoragePermissionGranted()) {
                    if (check) {
                        Toast.makeText(DetailActivity.this, videoData.getVideoFileName() + " is already downloaded", Toast.LENGTH_SHORT).show();
                    } else {
                        isFacebook = false;
                        isTwitter = false;
                        isMessanger = false;
                        isInstagram = false;
                        isWhatsApp = false;
                        isShare = false;
                        startDownload();
                    }

                }
                break;
            case R.id.btn_facebook:
                if (isStoragePermissionGranted()) {
                    if (check) {
                        shareToFaceBook();
                    } else {
                        isFacebook = true;
                        isTwitter = false;
                        isMessanger = false;
                        isInstagram = false;
                        isWhatsApp = false;
                        isShare = false;
                        startDownload();
                    }

                }
                break;

            case R.id.btn_twitter:
                if (isStoragePermissionGranted()) {
                    if (check) {
                        shareToTwitter();
                    } else {
                        isFacebook = false;
                        isTwitter = true;
                        isMessanger = false;
                        isInstagram = false;
                        isWhatsApp = false;
                        isShare = false;
                        startDownload();
                    }

                }
                break;

            case R.id.btn_messanger:
                if (isStoragePermissionGranted()) {
                    if (check) {
                        shareToMessenger();
                    } else {
                        isFacebook = false;
                        isTwitter = false;
                        isMessanger = true;
                        isInstagram = false;
                        isWhatsApp = false;
                        isShare = false;
                        startDownload();
                    }

                }
                break;

            case R.id.btn_instagram:
                if (isStoragePermissionGranted()) {
                    if (check) {
                        shareToInstaGram();
                    } else {
                        isFacebook = false;
                        isTwitter = false;
                        isMessanger = false;
                        isInstagram = true;
                        isWhatsApp = false;
                        isShare = false;
                        startDownload();
                    }

                }
                break;

            case R.id.btn_share:
                if (isStoragePermissionGranted()) {
                    if (check) {
                        shareToDefault();
                    } else {
                        isFacebook = false;
                        isTwitter = false;
                        isMessanger = false;
                        isInstagram = false;
                        isWhatsApp = false;
                        isShare = true;
                        startDownload();
                    }

                }
                break;
            case R.id.btn_whatsapp:
                if (isStoragePermissionGranted()) {
                    if (check) {
                        shareToWhatsApp();
                    } else {
                        isFacebook = false;
                        isTwitter = false;
                        isMessanger = false;
                        isInstagram = false;
                        isWhatsApp = true;
                        isShare = false;
                        startDownload();
                    }

                }
                break;

        }


    }

    private void shareToWhatsApp() {
        String type = "video/*";

        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType(type);

        // Create the URI from the media
        Uri uri = FileProvider.getUriForFile(DetailActivity.this, getString(R.string.authority), file);
        share.setPackage("com.whatsapp");
        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        PackageManager packageManager = getPackageManager();
        if (share.resolveActivity(packageManager) != null) {
            startActivity(share);
            startActivity(Intent.createChooser(share, "Share to"));

        } else {
            alertForApp(getString(R.string.install_whatsapp), "com.whatsapp");
        }

        // Broadcast the Intent.
    }

    private void shareToDefault() {
        String type = "video/*";

        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType(type);

        // Create the URI from the media
        Uri uri = FileProvider.getUriForFile(DetailActivity.this, getString(R.string.authority), file);
        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        PackageManager packageManager = getPackageManager();
        if (share.resolveActivity(packageManager) != null) {
            startActivity(share);
            // Broadcast the Intent.
            startActivity(Intent.createChooser(share, "Share to"));

        } else Toast.makeText(this, R.string.try_later, Toast.LENGTH_SHORT).show();


    }

    private void shareToInstaGram() {
        String type = "video/*";

        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType(type);

        // Create the URI from the media
        Uri uri = FileProvider.getUriForFile(DetailActivity.this, getString(R.string.authority), file);
        share.setPackage("com.instagram.android");
        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        PackageManager packageManager = getPackageManager();
        if (share.resolveActivity(packageManager) != null) {
            startActivity(share);
            // Broadcast the Intent.
            startActivity(Intent.createChooser(share, "Share to"));

        } else {
            alertForApp(getString(R.string.install_insta), "com.instagram.android");
            //  Toast.makeText(this, R.string.instagram_warning, Toast.LENGTH_SHORT).show();
        }
    }

    private void shareToTwitter() {
        String type = "video/*";
        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);
        // Set the MIME type
        share.setType(type);
        // Create the URI from the media
        Uri uri = FileProvider.getUriForFile(DetailActivity.this, getString(R.string.authority), file);
        share.setPackage("com.twitter.android");
        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        PackageManager packageManager = getPackageManager();
        if (share.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(share, "Share to"));

        } else {
            alertForApp(getString(R.string.install_twitter), "com.twitter.android");
            //  Toast.makeText(this, R.string.twitter_warning, Toast.LENGTH_SHORT).show();
        }
    }

    private void shareToMessenger() {
        String mimeType = "video/mp4";
        Uri uri = FileProvider.getUriForFile(DetailActivity.this, getString(R.string.authority), file);

        // contentUri points to the content being shared to Messenger
        ShareToMessengerParams shareToMessengerParams =
                ShareToMessengerParams.newBuilder(uri, mimeType)
                        .build();

// Sharing from an Activity
        int REQUEST_CODE_SHARE_TO_MESSENGER = 11;
        MessengerUtils.shareToMessenger(this, REQUEST_CODE_SHARE_TO_MESSENGER, shareToMessengerParams);

    }

    private void shareToFaceBook() {

        Uri videoFileUri = Uri.fromFile(file);
        ShareVideo video = new ShareVideo.Builder().setLocalUrl(videoFileUri)
                .build();
        ShareVideoContent content = new ShareVideoContent.Builder()
                .setVideo(video)
                .build();

        if (ShareDialog.canShow(ShareVideoContent.class))
            ShareDialog.show(DetailActivity.this, content);
        else {
            alertForApp(getString(R.string.install_facebook), "com.facebook.katana");

            //Toast.makeText(this, R.string.installfb, Toast.LENGTH_SHORT).show();
        }

    }

    private void startDownload() {
        if (getString(R.string.show_admmob_ads).equals("yes")) {
            if (isLoadAd) {
                AdManager.showInterstial(this);
                isLoadAd = false;
            }
        }
        list.clear();
        videoPath = videoPath.replace(" ", "%20");
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(videoPath));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);
        request.setTitle("Downloading " + videoData.getVideoFileName());
        request.setVisibleInDownloadsUi(true);
        request.allowScanningByMediaScanner();
        request.setDestinationInExternalPublicDir(getString(R.string.destinationFolderName), videoData.getVideoFileName());
        final long refId = downloadManager.enqueue(request);
        Log.e("OUT", "" + refId);

        list.add(refId);

        setupProgress(refId);

        btn_cancelDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDownload(refId);
            }
        });
    }

    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onComplete);
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }


    private boolean hasCurrentVideo() {
        // Create a path where we will place our video in the user's
        // public pictures directory and check if the file exists.  If
        // external storage is not currently mounted this will think the
        // picture doesn't exist.
        File path = Environment.getExternalStoragePublicDirectory(
                "/VideoStatus");
        file = new File(path, videoData.getVideoFileName());
        return file.exists();
    }


    private void setupProgress(final Long enqueue) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean downloading = true;
                while (downloading) {
                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(enqueue);

                    try {
                        Cursor cursor = downloadManager.query(q);
                        if (cursor != null && cursor.getCount() != 0) {
                            cursor.moveToFirst();
                            final int bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                            final int totalBytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));


                            if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_FAILED) {
                                downloading = false;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rel_showProgress.setVisibility(View.INVISIBLE);
                                        detail_toolbar.setVisibility(View.VISIBLE);
                                    }
                                });
                            }

                            double progressPercent;
                            progressPercent = bytesDownloaded * 100.0 / totalBytesDownloaded;


                            final double finalProgressPercent = progressPercent;
                            final String msg = "Downloading " + (int) finalProgressPercent + "%";


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rel_showProgress.setVisibility(View.VISIBLE);
                                    detail_toolbar.setVisibility(View.INVISIBLE);
                                    txt_progress.setText(msg);
                                    float totalMb = (float) totalBytesDownloaded / (1024 * 1024);
                                    String size = String.format(Locale.ENGLISH, "%.2f", totalMb);

                                    String tempSize = size + " MB";
                                    txt_fileSize.setText(tempSize);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        progressBar.setProgress((int) finalProgressPercent, true);
                                    } else progressBar.setProgress((int) finalProgressPercent);

                                }
                            });


                            if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                                downloading = false;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rel_showProgress.setVisibility(View.INVISIBLE);
                                        detail_toolbar.setVisibility(View.VISIBLE);
                                        if (isWhatsApp) {
                                            shareToWhatsApp();
                                        } else if (isFacebook) {
                                            shareToFaceBook();
                                        } else if (isTwitter) {
                                            shareToTwitter();
                                        } else if (isMessanger) {
                                            shareToMessenger();
                                        } else if (isInstagram) {
                                            shareToInstaGram();
                                        } else if (isShare) {
                                            shareToDefault();
                                        }
                                        increaseDownload();
                                        saveToDatabase();
                                        changeBar();
                                    }
                                });

                            }
                            cursor.close();
                        } else {
                            downloading = false;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rel_showProgress.setVisibility(View.INVISIBLE);
                                    detail_toolbar.setVisibility(View.VISIBLE);
                                }
                            });

                        }

                    } catch (CursorIndexOutOfBoundsException exception) {
                        exception.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rel_showProgress.setVisibility(View.INVISIBLE);
                                detail_toolbar.setVisibility(View.VISIBLE);
                            }
                        });
                        throw exception;
                    }

                }
            }
        }).start();
    }

    private void increaseDownload() {
        //creating a string request to send request to the url
        String hp = getString(R.string.link) + "api/video_view_count.php?download=" + videoData.getId();
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
                            JSONObject data = obj.getJSONObject("data");
                            String status = data.getString("success");
                            if (Objects.equals(status, "1")) {
//                                JSONObject ja_video = data.getJSONObject("video");
                                String txt_download = data.getString("download");
                                videoData.setVideoDownload(txt_download);
                                String tempDownload = videoData.getVideoDownload() + " " + "Download";
                                tv_downloads.setText(tempDownload);

                            } else {
                                Toast.makeText(DetailActivity.this, "Error in network", Toast.LENGTH_SHORT).show();
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


    private void saveToDatabase() {

        DBAdapter myDbHelper = new DBAdapter(DetailActivity.this);
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
        ContentValues values = new ContentValues();
        values.put("category", videoData.getVideo_category());
        values.put("subcategory", videoData.getVideo_subcategory());
        values.put("categoryid", videoData.getVideo_cat_id());
        values.put("subcategoryid", videoData.getVideo_subCat_id());
        values.put("video", videoData.getVideoFileName());
        values.put("image", videoData.getVideoImage());
        values.put("view", videoData.getVideoView());
        values.put("downloads", videoData.getVideoDownload());
        values.put("title", videoData.getVideo_title());
        db.insert("Downloaded", null, values);
        myDbHelper.close();
    }

    private void cancelDownload(final Long enqueue) {

        downloadManager.remove(enqueue);

        Toast.makeText(DetailActivity.this, "Download Cancelled!", Toast.LENGTH_SHORT).show();

    }

    private void alertForApp(String errorMessage, final String packageToRedirect) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
        builder.setTitle(R.string.app_not_found);
        builder.setIcon(R.drawable.ic_error_black_24dp);
        builder.setMessage(errorMessage);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.txt_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                String url = getString(R.string.playStore_address) + packageToRedirect;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                dialog.cancel();
            }
        }).setNegativeButton(R.string.txt_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

}