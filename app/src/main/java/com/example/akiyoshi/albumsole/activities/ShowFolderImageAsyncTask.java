package com.example.akiyoshi.albumsole.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.ViewPropertyTransition;


public class ShowFolderImageAsyncTask extends AsyncTask<String, Object, Void> {
    ImageView imageView, imageView2;
    Context context;
    String path;
    AlphaAnimation animationHide;

    public ShowFolderImageAsyncTask(ImageView imageView, ImageView imageView2, String path, Context context) {
        this.imageView = imageView;
        this.imageView2 = imageView2;
        this.context = context;
        this.path = path;

        animationHide = new AlphaAnimation(1f, 0f);
        animationHide.setDuration(1000);
        animationHide.setFillAfter(true);
    }


    @Override
    protected Void doInBackground(String... strings) {
        return null;
    }

    @Override
    protected void onPostExecute(Void bitmap) {
        if (context != null) {

            Glide.with(context)
                    .load(path).transition(GenericTransitionOptions.with(animationShow))
                    .into(imageView);
            //imageView2.startAnimation(animationHide);

        }


    }

    ViewPropertyTransition.Animator animationShow = new ViewPropertyTransition.Animator() {
        @Override
        public void animate(View view) {
            ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
            fadeAnim.setDuration(1000);
            fadeAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    try {
                        super.onAnimationEnd(animation);
                        Glide.with(context)
                                .load(path)
                                .into(imageView2);
                        imageView2.setAlpha(1f);
                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                imageView.setAlpha(0f);
                            }
                        }, 300);
                    }catch (Exception e)
                    {

                    }
                }
            });
            fadeAnim.start();
        }
    };


}
