package com.videostatusiranna.ObservableLayer;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import com.videostatusiranna.R;
import com.videostatusiranna.getSet.videoListGetSet;

import static com.videostatusiranna.activity.PopularActivity.changeLoad;
import static com.videostatusiranna.adapter.CustomVideoListAdapter.setLoaded;

public class PopularVideoViewModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<videoListGetSet>> videoList;

    public PopularVideoViewModel(@NonNull Application application) {
        super(application);
        if (videoList == null) {
            videoList = new MutableLiveData<>();
            LoadVideoList("4", "1");
        }
    }

    public MutableLiveData<ArrayList<videoListGetSet>> getPostsList() {
        return videoList;
    }

    private void LoadVideoList(String numberOfRecord, String pageNo) {
        //creating a string request to send request to the url
        String hp = getApplication().getString(R.string.link) + "api/popular_video.php?" + "noofrecords=" + numberOfRecord + "&pageno=" + pageNo;
        Log.d("CheckUrl", "" + hp);
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
                            tempArray.clear();

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
                                    temp.setVideo_cat_id(jo_data.getString("cat_id"));
                                    temp.setVideo_subCat_id(jo_data.getString("subcat_id"));
                                    temp.setId(txt_video_id);
                                    temp.setVideo_title(txt_video_title);
                                    temp.setVideo_category(txt_video_category);
                                    temp.setVideo_subcategory(txt_video_subcategory);
                                    temp.setVideoDownload(txt_download);
                                    temp.setVideoImage(txt_image);
                                    temp.setVideoView(txt_view);
                                    temp.setVideoFileName(txt_video);
                                    tempArray.add(temp);
                                }

                                videoList.setValue(tempArray);
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

    public void LoadMoreData(String numberOfRecord, String pageNo) {

        //creating a string request to send request to the url
        String hp = getApplication().getString(R.string.link) + "api/popular_video.php?" + "noofrecords=" + numberOfRecord + "&pageno=" + pageNo;
        Log.w(getClass().getName(), hp);
        hp = hp.replace(" ", "%20");
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
                                    temp.setVideo_cat_id(jo_data.getString("cat_id"));
                                    temp.setVideo_subCat_id(jo_data.getString("subcat_id"));
                                    temp.setId(txt_video_id);
                                    temp.setVideo_title(txt_video_title);
                                    temp.setVideo_category(txt_video_category);
                                    temp.setVideo_subcategory(txt_video_subcategory);
                                    temp.setVideoDownload(txt_download);
                                    temp.setVideoImage(txt_image);
                                    temp.setVideoView(txt_view);
                                    temp.setVideoFileName(txt_video);
                                    tempArray.add(temp);
                                }

//                                ArrayList<videoListGetSet> oldList = videoList.getValue();
//                                if (oldList != null) {
//                                    oldList.addAll(tempArray);
//                                }
                                changeLoad(true);
                                setLoaded(false);
                                videoList.setValue(tempArray);
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
}
