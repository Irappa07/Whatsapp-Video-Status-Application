package com.videostatusiranna.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import com.videostatusiranna.adapter.CustomNavigationBarAdapter;
import com.videostatusiranna.R;
import com.videostatusiranna.utilities.DBAdapter;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private ListView list_SliderMenu;
    private DrawerLayout drawer;
    private LinearLayout mainView;
    private SwitchCompat switch_new_old_onoff;
    private SwitchCompat switch_old_new_onoff;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initViews();


    }


    private void initViews() {
        list_SliderMenu = findViewById(R.id.list_slidermenu);
        RelativeLayout rel_new_old = findViewById(R.id.rel_new_old);
        RelativeLayout rel_old_new = findViewById(R.id.rel_old_new);
        RelativeLayout rel_clearDownload = findViewById(R.id.rel_clearDownload);
        RelativeLayout rel_privacyPolicy = findViewById(R.id.rel_privacyPolicy);
        RelativeLayout rel_aboutUs = findViewById(R.id.rel_aboutUs);
        RelativeLayout rel_moreApps = findViewById(R.id.rel_moreApps);
        switch_new_old_onoff = findViewById(R.id.switch_new_old_onoff);
        switch_old_new_onoff = findViewById(R.id.switch_old_new_onoff);
        //switch toggle according to saved user preference
        if (!getSharedPreferences(HomeActivity.prefName, MODE_PRIVATE).getString("orderBy", "desc").equals("asc")) {
            switch_new_old_onoff.setChecked(true);
            switch_old_new_onoff.setChecked(false);
        } else {
            switch_new_old_onoff.setChecked(false);
            switch_old_new_onoff.setChecked(true);
        }

        //Setting Typeface

        TextView txt_sort_new_old = findViewById(R.id.txt_sort_new_old);
        txt_sort_new_old.setTypeface(HomeActivity.tf_main_medium);
        TextView txt_sort_old_new = findViewById(R.id.txt_sort_old_new);
        txt_sort_old_new.setTypeface(HomeActivity.tf_main_medium);
        TextView txt_clear_download = findViewById(R.id.txt_clear_download);
        txt_clear_download.setTypeface(HomeActivity.tf_main_medium);
        TextView txt_privacyPolicy = findViewById(R.id.txt_privacyPolicy);
        txt_privacyPolicy.setTypeface(HomeActivity.tf_main_medium);
        TextView txt_aboutUs = findViewById(R.id.txt_aboutUs);
        txt_aboutUs.setTypeface(HomeActivity.tf_main_medium);
        TextView txt_moreApps = findViewById(R.id.txt_moreApps);
        txt_moreApps.setTypeface(HomeActivity.tf_main_medium);

        setUpDrawer();

        setUpNavigationBar();

        //setting listeners
        rel_new_old.setOnClickListener(this);
        rel_old_new.setOnClickListener(this);
        rel_clearDownload.setOnClickListener(this);
        rel_privacyPolicy.setOnClickListener(this);
        rel_aboutUs.setOnClickListener(this);
        rel_moreApps.setOnClickListener(this);
        switch_new_old_onoff.setOnCheckedChangeListener(this);
        switch_old_new_onoff.setOnCheckedChangeListener(this);


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
        txt_tittle.setText(R.string.txt_setting);
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
                }                drawer.bringChildToFront(drawerView);
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
            Intent i = new Intent(SettingActivity.this,HomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(i);
        }
    }


    private void setUpNavigationBar() {
        CustomNavigationBarAdapter customNavigationBarAdapter = new CustomNavigationBarAdapter(SettingActivity.this, drawer);
        list_SliderMenu.setAdapter(customNavigationBarAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rel_clearDownload:
                clearDownload();
                break;
            case R.id.rel_privacyPolicy:
                openPrivacyPolicy();
                break;
            case R.id.rel_aboutUs:
                openAboutUs();
                break;
            case R.id.rel_moreApps:
                String url = getString(R.string.playStore_address) + getPackageName();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
        }

    }


    private void openAboutUs() {
        Intent i = new Intent(SettingActivity.this, AboutUsActivity.class);
        startActivity(i);
    }


    private void openPrivacyPolicy() {
        Intent i = new Intent(SettingActivity.this, PrivacyPolicyActivity.class);
        startActivity(i);
    }


    private void clearDownload() {

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this, AlertDialog.THEME_HOLO_DARK);
        builder.setTitle(R.string.txt_delete);
        builder.setIcon(R.drawable.ic_delete_black_24dp);
        builder.setMessage(R.string.delete_all);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.txt_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                deleteFromInternalStorage();
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


    private void deleteFromInternalStorage() {


        File path = Environment.getExternalStoragePublicDirectory(
                "/VideoStatus");
        if (path.exists()) {
            try {
                FileUtils.deleteDirectory(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            deleteRow();
        } else {
            Toast.makeText(SettingActivity.this, R.string.error_file_extst, Toast.LENGTH_SHORT).show();
            deleteRow();
        }

    }


    private void deleteRow() {
        DBAdapter myDbHelper = new DBAdapter(SettingActivity.this);
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
        db.execSQL("DELETE FROM " + "Downloaded");
        db.close();

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_new_old_onoff:
                Log.e("switch_new_old", isChecked + "");
                savePreference(isChecked, 1);
                break;
            case R.id.switch_old_new_onoff:
                Log.e("switch_old_new", isChecked + "");
                savePreference(isChecked, 2);
                break;

        }
    }


    private void savePreference(boolean i, int y) {
        if (y == 1) {
            if (i) {
                getSharedPreferences(HomeActivity.prefName, MODE_PRIVATE).edit().putString("orderBy", "desc").apply();
            } else
                getSharedPreferences(HomeActivity.prefName, MODE_PRIVATE).edit().putString("orderBy", "asc").apply();
            switch_old_new_onoff.setChecked(!i);


        } else if (y == 2) {
            if (i) {
                getSharedPreferences(HomeActivity.prefName, MODE_PRIVATE).edit().putString("orderBy", "asc").apply();
            } else
                getSharedPreferences(HomeActivity.prefName, MODE_PRIVATE).edit().putString("orderBy", "desc").apply();
            switch_new_old_onoff.setChecked(!i);

        }
    }
}
