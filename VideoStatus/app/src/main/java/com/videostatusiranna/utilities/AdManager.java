package com.videostatusiranna.utilities;

import android.app.Activity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import com.videostatusiranna.R;



public class AdManager {
    private static InterstitialAd mInterstitialAd;
    private static com.facebook.ads.InterstitialAd interstitialAd;
    private static int adCount = 0;

    public static void setUpInterstial(Activity activity) {
        if (activity.getString(R.string.show_admmob_ads).equals("yes") && adCount == activity.getResources().getInteger(R.integer.adCount)) {
            mInterstitialAd = new InterstitialAd(activity);
            mInterstitialAd.setAdUnitId(activity.getString(R.string.interstial_ad_unit_id));
            mInterstitialAd.loadAd(new AdRequest.Builder().build());

        } else if (activity.getString(R.string.show_facebook_ads).equals("yes") && adCount == activity.getResources().getInteger(R.integer.adCount)) {
            interstitialAd = new com.facebook.ads.InterstitialAd(activity, activity.getString(R.string.facebook_interstial));
            interstitialAd.loadAd();
        }

    }
    public static void showInterstial(Activity activity) {
        if (activity.getString(R.string.show_admmob_ads).equals("yes") && adCount == activity.getResources().getInteger(R.integer.adCount)) {
            if (mInterstitialAd != null) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }
        } else if (activity.getString(R.string.show_facebook_ads).equals("yes") && adCount == activity.getResources().getInteger(R.integer.adCount)) {
            if (interstitialAd != null) {
                if (interstitialAd.isAdLoaded()) {
                    interstitialAd.show();
                }
            }
        }
    }
    public static void increaseCount(Activity activity) {
        if (adCount > activity.getResources().getInteger(R.integer.adCount)) {
            adCount = 0;
        }
        adCount++;
    }
}
