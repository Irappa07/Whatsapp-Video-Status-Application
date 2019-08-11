package com.videostatusiranna.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

import com.videostatusiranna.R;



public class FacebookBannerViewHolder extends RecyclerView.ViewHolder {
    AdView adView;
    LinearLayout ll_ads;

    public FacebookBannerViewHolder(View itemView) {
        super(itemView);
        ll_ads = itemView.findViewById(R.id.ll_ads);

        // Instantiate an AdView view
        adView = new AdView(itemView.getContext(),itemView.getContext().getString(R.string.facebook_banner_id), AdSize.BANNER_HEIGHT_50);

        // Add the ad view to your activity layout
        ll_ads.addView(adView);

        // Request an ad
        adView.loadAd();

        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {
                  ll_ads.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });

    }
}
