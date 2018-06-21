package com.example.akiyoshi.albumsole.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.akiyoshi.albumsole.models.ThumbsManager;
import com.zomato.photofilters.imageprocessors.Filter;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class PlayMovieLoadFilter extends AsyncTask<Void, Void, Bitmap> {

    private final Context context;
    private String path;
    private String stringFilter;
    private int w;
    private int h;
    private ThumbLoadedCallBack thumbLoadedCallBack;

    public PlayMovieLoadFilter(Context context, String path, String stringFilter, int w, int h, ThumbLoadedCallBack thumbLoadedCallBack) {
        this.context = context;
        this.path = path;
        this.stringFilter = stringFilter;
        this.w = w;
        this.h = h;
        this.thumbLoadedCallBack = thumbLoadedCallBack;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            Bitmap bm = Glide.with(context).asBitmap()
                    .load(path)
                    .apply(new RequestOptions().override(w, h).centerCrop()).submit().get();
            Filter filter = ThumbsManager.filters.get(Integer.parseInt(stringFilter));
            Bitmap bitmap = Bitmap.createBitmap(bm);
            bitmap = filter.processFilter(bitmap);
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
            if(PlayMovieMemoryActivity.listBitmapFiltered==null)
                PlayMovieMemoryActivity.listBitmapFiltered = new ArrayList<>();
            PlayMovieMemoryActivity.listBitmapFiltered.add(result);
            if (PlayMovieMemoryActivity.listBitmapFiltered.size() == PlayMovieMemoryActivity.sizeList) {
                thumbLoadedCallBack.onLoaded();
            }
        } else {
        }
    }
}
