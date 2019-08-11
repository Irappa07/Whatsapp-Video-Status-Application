package com.videostatusiranna.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.videostatusiranna.R;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        initViews();

    }

    private void initViews() {
        TextView title = findViewById(R.id.title);
        title.setText(R.string.txt_about_us);
        title.setTypeface(HomeActivity.tf_main_medium);
        ImageView btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        WebView webView = findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/"+getString(R.string.aboutUs_filename));
    }
}
