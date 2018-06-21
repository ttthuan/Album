package com.example.akiyoshi.albumsole.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.akiyoshi.albumsole.models.ThumbsManager;
import com.zomato.photofilters.imageprocessors.Filter;

import java.util.concurrent.ExecutionException;

public class ClearImageFilterd extends AsyncTask<Void, Void, Bitmap> {

    private final Context context;
    private ImageView imageView;
    private String path;
    private String stringFilter;
    private int w;
    private int h;

    public ClearImageFilterd(Context context, ImageView imageView, String path, String stringFilter, int w, int h) {
        this.context = context;
        this.imageView = imageView;
        this.path = path;
        this.stringFilter = stringFilter;
        this.w = w;
        this.h = h;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            Bitmap bm = Glide.with(context).asBitmap()
                    .load(path)
                    .apply(new RequestOptions().override(w, h).centerCrop()).submit().get();
            Bitmap bitmap = Bitmap.createBitmap(bm);
            Filter filter = ThumbsManager.filters.get(Integer.parseInt(stringFilter));
            bitmap=filter.processFilter(bitmap);
            return bitmap;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);

        if (null != result) {
            // show notification using bitmap
            imageView.setImageBitmap(result);
        } else {
            // couldn't fetch the bitmap
        }
    }
}
