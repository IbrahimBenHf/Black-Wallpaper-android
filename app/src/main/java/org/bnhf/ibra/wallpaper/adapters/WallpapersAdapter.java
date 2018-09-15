package org.bnhf.ibra.wallpaper.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.bnhf.ibra.wallpaper.R;
import org.bnhf.ibra.wallpaper.activities.WallpaperInterface;
import org.bnhf.ibra.wallpaper.models.Wallpaper;

import java.util.List;

public class WallpapersAdapter extends RecyclerView.Adapter<WallpapersAdapter.WallpaperViewHolder> {

    private Context mCtx;
    private List<Wallpaper> wallpaperList;
    private InterstitialAd mInterstitialAd;

    public WallpapersAdapter(Context mCtx, List<Wallpaper> wallpaperList) {
        this.mCtx = mCtx;
        this.wallpaperList = wallpaperList;
    }

    @Override
    public WallpaperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_wallpapers, parent, false);
        return new WallpaperViewHolder(view);

    }

    @Override
    public void onBindViewHolder(WallpaperViewHolder holder, int position) {

        Wallpaper w = wallpaperList.get(position);
        Glide.with(mCtx)
                .load(w.url)
                .into(holder.imageView);


    }

    @Override
    public int getItemCount() {
        return wallpaperList.size();
    }

    class WallpaperViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        ImageView imageView;



        public WallpaperViewHolder(View itemView) {
            super(itemView);


            imageView = itemView.findViewById(R.id.image_view);
            MobileAds.initialize(mCtx, "ca-app-pub-6100493979711956~5243620760");
            mInterstitialAd = new InterstitialAd(mCtx);
          //  mInterstitialAd.setAdUnitId("ca-app-pub-6100493979711956/6333167115");
            mInterstitialAd.loadAd(new AdRequest.Builder().build());


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = new Intent(mCtx, WallpaperInterface.class);
                    intent.putExtra("cat", wallpaperList.get(getAdapterPosition()).url);


                    mInterstitialAd.setAdListener(new AdListener() {
                        public void onAdLoaded() {
                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                            }
                        }

                        @Override
                        public void onAdClosed() {

                            // Code to be executed when when the interstitial ad is closed.
                            Log.i("Ads", "onAdClosed");
                        }
                    });

                    mCtx.startActivity(intent);
                }




            });

        }

        @Override
        public void onClick(View view) {


        }








        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        }
    }
}
