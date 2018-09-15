package org.bnhf.ibra.wallpaper.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.bnhf.ibra.wallpaper.R;
import org.bnhf.ibra.wallpaper.models.Wallpaper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class WallpaperInterface extends AppCompatActivity  {

    ImageView wall;

    AsyncTask mMyTask;


    private Context mCtx;
    private Activity mActivity;
    private AdView adView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_interface);

        Intent intent = getIntent();
        final String category = intent.getStringExtra("cat");


        wall = findViewById(R.id.img_wall);

        Picasso.get().load(category).into(wall);

        mCtx=WallpaperInterface.this;

        /////////////////////////////////////////////Banner

        // Find Banner ad

        adView = findViewById(R.id.adView2);
        AdRequest madRequest = new AdRequest.Builder().build();
        // Display Banner ad
        adView.loadAd(madRequest);


        ///////////////////////////////////










        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                downloadWallpaper(category);

                Snackbar snackbar = Snackbar
                        .make(view, "Downloading..", Snackbar.LENGTH_LONG);

                snackbar.show();

            }
        });






    }





    private void downloadWallpaper(final String url) {

        Random r = new Random();

        final int id = r.nextInt((50000-10)+1)+10;
        final String ids= "Dark"+Integer.toString(id);
        Glide.with(mCtx)
                .asBitmap()
                .load(url)
                .into(new SimpleTarget<Bitmap>() {
                          @Override
                          public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {


                              Intent intent = new Intent(Intent.ACTION_VIEW);

                              Uri uri = saveWallpaperAndGetUri(resource, ids);



                              if (uri != null) {
                                  intent.setDataAndType(uri, "image/*");
                                  mCtx.startActivity(Intent.createChooser(intent, "DarkWallpaper"));
                              }
                          }
                      }
                );
    }

    private Uri saveWallpaperAndGetUri(Bitmap bitmap, String id) {
        if (ContextCompat.checkSelfPermission(mCtx, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat
                    .shouldShowRequestPermissionRationale((Activity) mCtx, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

                Uri uri = Uri.fromParts("package", mCtx.getPackageName(), null);
                intent.setData(uri);

                mCtx.startActivity(intent);

            } else {
                ActivityCompat.requestPermissions((Activity) mCtx, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
            return null;
        }

        File folder = new File(Environment.getExternalStorageDirectory().toString() + "/Dark Wallpaper");
        folder.mkdirs();

        File file = new File(folder, id + ".jpeg");
        new  SingleMediaScanner(mCtx,file);
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();


            return Uri.fromFile(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}
