package org.bnhf.ibra.wallpaper.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.bnhf.ibra.wallpaper.R;
import org.bnhf.ibra.wallpaper.adapters.WallpapersAdapter;
import org.bnhf.ibra.wallpaper.models.Wallpaper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WallpapersActivity extends AppCompatActivity {

    List<Wallpaper> wallpaperList;
    List<Wallpaper> favList;
    RecyclerView recyclerView;
    WallpapersAdapter adapter;


    DatabaseReference dbWallpapers, dbFavs;
    ProgressBar progressBar;
    InterstitialAd interstitial;
    private AdView adView;

    private static final String AD_UNIT_ID= "ca-app-pub-6100493979711956/7474309651";
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpapers);

        Intent intent = getIntent();
        final String category = intent.getStringExtra("category");

        //////admob
        MobileAds.initialize(this,"ca-app-pub-6100493979711956~5243620760");
        interstitialAd = new InterstitialAd(this);

      //  interstitialAd.setAdUnitId(AD_UNIT_ID);
        AdRequest adRequest = new AdRequest.Builder().build();

        interstitialAd.loadAd(adRequest);

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
                else{ Log.i("Ads4", "onAdClosed not found 404");}

            }

            @Override
            public void onAdOpened() {


            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }
        });

/////////////////////////////////////////////Banner

        // Find Banner ad

        adView = findViewById(R.id.adView);
        AdRequest madRequest = new AdRequest.Builder().build();
        // Display Banner ad
        adView.loadAd(madRequest);


        ///////////////////////////////////




        favList = new ArrayList<>();
        wallpaperList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new WallpapersAdapter(this, wallpaperList);

        recyclerView.setAdapter(adapter);

        progressBar = findViewById(R.id.progressbar);
        dbWallpapers = FirebaseDatabase.getInstance().getReference("images")
                .child(category);


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            dbFavs = FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("favourites")
                    .child(category);
            fetchFavWallpapers(category);
        } else {
            fetchWallpapers(category);
        }

    }

    private void fetchFavWallpapers(final String category) {
        progressBar.setVisibility(View.VISIBLE);
        dbFavs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot wallpaperSnapshot : dataSnapshot.getChildren()) {

                        String id = wallpaperSnapshot.getKey();
                        String title = wallpaperSnapshot.child("title").getValue(String.class);
                        String desc = wallpaperSnapshot.child("desc").getValue(String.class);
                        String url = wallpaperSnapshot.child("url").getValue(String.class);

                        Wallpaper w = new Wallpaper(id, title, desc, url, category);
                        favList.add(w);
                    }
                }
                fetchWallpapers(category);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fetchWallpapers(final String category) {
        progressBar.setVisibility(View.VISIBLE);
        dbWallpapers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot wallpaperSnapshot : dataSnapshot.getChildren()) {

                        String id = wallpaperSnapshot.getKey();
                        String title = wallpaperSnapshot.child("title").getValue(String.class);
                        String desc = wallpaperSnapshot.child("desc").getValue(String.class);
                        String url = wallpaperSnapshot.child("url").getValue(String.class);

                        Wallpaper w = new Wallpaper(id, title, desc, url, category);

                        if (isFavourite(w)) {
                            w.isFavourite = true;
                        }

                        wallpaperList.add(w);
                    }
                    Collections.reverse(wallpaperList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean isFavourite(Wallpaper w) {
        for (Wallpaper f : favList) {
            if (f.id.equals(w.id)) {
                return true;
            }
        }
        return false;
    }
}
