package com.example.akiyoshi.albumsole.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;

import com.example.akiyoshi.albumsole.models.SpaceItemDecorator;

public class RenderListImageMovie extends AsyncTask<String, Void, Void> {
    Context context;
    CreateMovieImageFragment fragment;
    CreateMovieImageAdapter picAdapter;
    RecyclerView movie_create_all_image;
    ProgressBar waitProgress;

    public RenderListImageMovie(Context context,
                                CreateMovieImageFragment fragment,
                                RecyclerView movie_create_all_image,
                                ProgressBar waitProgress) {
        this.context = context;
        this.fragment = fragment;
        this.movie_create_all_image = movie_create_all_image;
        this.waitProgress = waitProgress;
    }


    @Override
    protected Void doInBackground(String... params) {
        picAdapter = new CreateMovieImageAdapter(context, MainActivity.listPictureItem, fragment);
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        if (context != null) {
            waitProgress.setVisibility(View.GONE);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 4);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    switch (picAdapter.getItemViewType(position)) {
                        case 0:
                            return 4;
                        default:
                            return 1;
                    }
                }
            });
            movie_create_all_image.setLayoutManager(gridLayoutManager);
            int spacingInPixels = dpToPx(MainActivity.itemMargin, context);
            movie_create_all_image.addItemDecoration(new SpaceItemDecorator(spacingInPixels));
            movie_create_all_image.setHasFixedSize(true);
            movie_create_all_image.setItemViewCacheSize(100);
            movie_create_all_image.setDrawingCacheEnabled(true);
            movie_create_all_image.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            ViewCompat.setNestedScrollingEnabled(movie_create_all_image, false);
            movie_create_all_image.setAdapter(picAdapter);
        }
    }

    public int dpToPx(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
