package com.example.akiyoshi.albumsole.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.concurrent.ExecutionException;

public class LoadImagePictureAreaItem extends AsyncTask<Void, Void, Void> {

    private final Context context;
    private ImageView imageView;
    private String path;
    private int w;
    private int h;

    public LoadImagePictureAreaItem(Context context, ImageView imageView, String path, int w, int h) {
        this.context = context;
        this.imageView = imageView;
        this.path = path;
        this.w = w;
        this.h = h;
    }

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        Glide.with(context).load(path)
                .apply(new RequestOptions().override(w, h).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(imageView);
    }

}
