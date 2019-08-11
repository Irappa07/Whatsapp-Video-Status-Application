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
import com.videostatusiranna.getSet.menuGetSet;
import com.videostatusiranna.getSet.videoListGetSet;

import static com.videostatusiranna.activity.HomeActivity.changeLoad;
import static com.videostatusiranna.adapter.CustomVideoListAdapter.setLoaded;

public class HomeVideoViewModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<videoListGetSet>> videoList;
    private MutableLiveData<ArrayList<menuGetSet>> menuList;

    public HomeVideoViewModel(@NonNull Application application) {
        super(application);
        if (videoList == null) {
            videoList = new MutableLiveData<>();
        //    LoadVideoList(application.getSharedPreferences(prefName, MODE_PRIVATE).getString("Category", "1"), "", application.getSharedPreferences(prefName, MODE_PRIVATE).getString("orderBy", "asc"), "4", "1");
        }
        if (menuList == null) {
            menuList = new MutableLiveData<>();
            LoadDefaultCategory();
        }

    }


    public MutableLiveData<ArrayList<videoListGetSet>> getVideosList() {
        return videoList;
    }


    public MutableLiveData<ArrayList<menuGetSet>> getMenuList() {
        return menuList;
    }


    public void LoadVideoList(String category, String subCategory, String sortBy, String numberOfRecord, String pageNo) {
        //creating a string request to send request to the url
        String hp = getApplication().getString(R.string.link) + "api/video_list.php?category=" + category + "&subcategory=" + subCategory + "&sort_by=" + sortBy + "&noofrecords=" + numberOfRecord + "&pageno=" + pageNo;
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
                            } else {
                                if (videoList.getValue() != null) {

                                    videoList.setValue(null);

                                }
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

    public void LoadMoreData(String category, String subCategory, String sortBy, String numberOfRecord, final String pageNo) {

        //creating a string request to send request to the url
        String hp = getApplication().getString(R.string.link) + "api/video_list.php?category=" + category + "&subcategory=" + subCategory + "&sort_by=" + sortBy + "&noofrecords=" + numberOfRecord + "&pageno=" + pageNo;
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
                            int pageNUM = Integer.parseInt(pageNo);
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

    public void SearchData(String search, String sortBy, String numberOfRecord, String pageNo) {

        //creating a string request to send request to the url
        String hp = getApplication().getString(R.string.link) + "api/video_search.php?search=" + search + "&sort_by=" + sortBy + "&noofrecords=" + numberOfRecord + "&pageno=" + pageNo;
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
                                    tempArray.add(temp);
                                }

                                videoList.setValue(tempArray);


                            } else {
                                if (videoList.getValue() != null) {
                                    videoList.setValue(null);
                                }
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

    public void LoadMoreSearchData(String search, String sortBy, String numberOfRecord, final String pageNo) {

        //creating a string request to send request to the url
        String hp = getApplication().getString(R.string.link) + "api/video_search.php?search=" + search + "&sort_by=" + sortBy + "&noofrecords=" + numberOfRecord + "&pageno=" + pageNo;
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
                            int pageNUM = Integer.parseInt(pageNo);
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

    private void LoadDefaultCategory() {
        //creating a string request to send request to the url
        String hp = getApplication().getString(R.string.link) + "api/video_category.php";
        Log.w(getClass().getName(), hp);
        hp = hp.replace(" ", "%20");

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
                                ArrayList<menuGetSet> menuGetSets = new ArrayList<>();
                                menuGetSet temp;
                                JSONArray ja_categories = obj.getJSONArray("category");
                                for (int i = 0; i < ja_categories.length(); i++) {
                                    temp = new menuGetSet();
                                    JSONObject jo_data = ja_categories.getJSONObject(i);
                                    String txt_menu_category = jo_data.getString("category");
                                    temp.setMenu_category(txt_menu_category);
                                    temp.setCat_id(jo_data.getString("id"));
                                    menuGetSets.add(temp);
                                }
                                menuList.setValue(menuGetSets);
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
