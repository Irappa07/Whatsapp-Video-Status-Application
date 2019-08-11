package com.videostatusiranna.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.videostatusiranna.R;

class ViewHolderAdMob extends RecyclerView.ViewHolder {
    ViewHolderAdMob(final View view) {
        super(view);
        final AdView mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                view.findViewById(R.id.rel_ad).setVisibility(View.VISIBLE);
            }
        });
    }
}

