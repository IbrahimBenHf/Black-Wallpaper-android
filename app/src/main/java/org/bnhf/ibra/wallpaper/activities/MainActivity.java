package org.bnhf.ibra.wallpaper.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.bnhf.ibra.wallpaper.R;

public class MainActivity extends AppCompatActivity {

    InterstitialAd interstitial;

    private static final String AD_UNIT_ID = "ca-app-pub-6100493979711956/5403228825";
    private InterstitialAd interstitialAd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Handler handler = new Handler();
        handler.postDelayed(r,3000);
     //   MobileAds.initialize(this,"ca-app-pub-6100493979711956~5243620760");
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);


        interstitialAd = new InterstitialAd(this);

        interstitialAd.setAdUnitId(AD_UNIT_ID);
        AdRequest adRequest = new AdRequest.Builder().build();

        interstitialAd.loadAd(adRequest);

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }

            }

            @Override
            public void onAdOpened() {


            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }
        });


    }



Runnable r = new Runnable() {
    @Override
    public void run() {

        Intent intent = new Intent(MainActivity.this,WallpapersActivity.class);
        intent.putExtra("category", "Heroes");
        startActivity(intent);
        finish();
    }
};
}
