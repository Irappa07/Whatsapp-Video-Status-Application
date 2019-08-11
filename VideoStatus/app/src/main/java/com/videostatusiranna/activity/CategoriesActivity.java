package com.videostatusiranna.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import com.videostatusiranna.adapter.CustomCategoryListAdapter;
import com.videostatusiranna.adapter.CustomNavigationBarAdapter;
import com.videostatusiranna.getSet.categoryGetSet;
import com.videostatusiranna.getSet.menuGetSet;
import com.videostatusiranna.R;

import static com.videostatusiranna.activity.HomeActivity.isNetworkConnected;
import static com.videostatusiranna.activity.HomeActivity.showErrorDialog;

public class CategoriesActivity extends AppCompatActivity {
    private ListView listSliderMenu;
    private DrawerLayout drawer;
    private LinearLayout mainView;
    private TextView txt_language;
    private ArrayList<menuGetSet> menuGetSets;
    private ArrayList<categoryGetSet> categoryGetSets;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_categories);

        initViews();
    }


    private void initViews() {

        listSliderMenu = findViewById(R.id.list_slidermenu);
        progressBar = findViewById(R.id.progressBar);

        setUpDrawer();

        setUpNavigationBar();

    }


    private void setUpDrawer() {
        //drawer setup
        drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mainView = findViewById(R.id.mainView);
        setSupportActionBar(toolbar);
        //Overflow icon
        Drawable overflowIcon = toolbar.getOverflowIcon();
        if (overflowIcon != null) {
            overflowIcon.setColorFilter(new ColorFilter());
            toolbar.setOverflowIcon(overflowIcon);
        }
        txt_language = toolbar.findViewById(R.id.txt_language);

        TextView txt_tittle = toolbar.findViewById(R.id.title);
        txt_tittle.setText(R.string.txt_categories);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        if(isNetworkConnected(this))
        setUpMenu(menu);
        else showErrorDialog(this);

        return super.onCreateOptionsMenu(menu);
    }


    private void setUpMenu(final Menu menu) {
        //creating a string request to send request to the url
        String hp = getString(R.string.link) + "api/video_category.php";
        Log.w(getClass().getName(), hp);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, hp,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        Log.e("Response", response);
                        progressBar.setVisibility(View.GONE);
                        try {

                            //getting the whole json object from the response
                            JSONObject obj = new JSONObject(response);
                            String status = obj.getString("success");
                            if (Objects.equals(status, "1")) {
                                menuGetSets = new ArrayList<>();
                                menuGetSet temp;

                                JSONArray ja_categories = obj.getJSONArray("category");
                                for (int i = 0; i < ja_categories.length(); i++) {
                                    temp = new menuGetSet();
                                    JSONObject jo_data = ja_categories.getJSONObject(i);
                                    String txt_menu_category = jo_data.getString("category");
                                    String menu_cate_id = jo_data.getString("id");
                                    temp.setMenu_category(txt_menu_category);
                                    temp.setCat_id(menu_cate_id);
                                    menuGetSets.add(temp);
                                }


                                if (menuGetSets.size() > 0) {
                                    for (int i = 0; i < menuGetSets.size(); i++) {

                                        menu.add(0, i, 0, menuGetSets.get(i).getMenu_category());
                                    }
                                }
                                String Languages1 = (getSharedPreferences(HomeActivity.prefName, MODE_PRIVATE).getString("positionCategory", "0"));
                                int lang1 = Integer.parseInt(Languages1);
                                txt_language.setText(menuGetSets.get(lang1).getMenu_category());

                                String Languages = menuGetSets.get(lang1).getCat_id();
                                setUpCategoryList(Languages);
                            } else {

                                Toast.makeText(CategoriesActivity.this, obj.getString("video"), Toast.LENGTH_SHORT).show();

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


    private void setUpCategoryList(final String categoryName) {
        //creating a string request to send request to the url
        String hp = getString(R.string.link) + "api/video_subcategory.php?category=" + categoryName;
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
                                updateUI();
                            } else {
                                Toast.makeText(CategoriesActivity.this, obj.getString("video"), Toast.LENGTH_SHORT).show();
                                emptyUI();
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


    private void updateUI() {

        final CustomCategoryListAdapter adapter = new CustomCategoryListAdapter(categoryGetSets, CategoriesActivity.this);
        ListView list_category = findViewById(R.id.list_category);
        list_category.setAdapter(adapter);

        list_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(CategoriesActivity.this, HomeActivity.class);
                i.putExtra("subCategory", categoryGetSets.get(position).getSubCategoryId());
                startActivity(i);
            }
        });

    }


    private void emptyUI() {
        final CustomCategoryListAdapter adapter = new CustomCategoryListAdapter(null, CategoriesActivity.this);
        ListView list_category = findViewById(R.id.list_category);
        list_category.setAdapter(adapter);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        txt_language.setText(item.getTitle());
        getSharedPreferences(HomeActivity.prefName, MODE_PRIVATE).edit().putString("Category", "" + menuGetSets.get(item.getItemId()).getCat_id()).apply();
        getSharedPreferences(HomeActivity.prefName, MODE_PRIVATE).edit().putString("positionCategory", "" + item.getItemId()).apply();
        setUpCategoryList(menuGetSets.get(item.getItemId()).getCat_id());

        return true;
    }


    private void setUpNavigationBar() {
        CustomNavigationBarAdapter customNavigationBarAdapter = new CustomNavigationBarAdapter(CategoriesActivity.this, drawer);
        listSliderMenu.setAdapter(customNavigationBarAdapter);
    }

}
