package com.example.akiyoshi.albumsole.activities;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.akiyoshi.albumsole.R;
import com.example.akiyoshi.albumsole.models.PictureEditMovie;

import java.util.ArrayList;
import java.util.List;

public class PlayMovieMemoryActivity extends AppCompatActivity implements ThumbLoadedCallBack {

    ArrayList<PictureEditMovie> playlist;
    String sound, filter, holder, title, subtitle;
    ViewFlipper play_movie_memory_flipper;
    public static List<Bitmap> listBitmapFiltered;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ThumbLoadedCallBack thumbLoadedCallBack;
    public static int sizeList = 0;
    long time = 0;
    long timeOut = 0;
    View fg_play_movie;
    boolean isPause = false;
    private int position;
    private int height, width;
    boolean isShowFg = false;
    TextView txt_time_play_movie_memory;
    ProgressBar prog_play_movie_memory;
    ImageButton btn_play_movie_memory, btn_play_movie_exit;
    CountDownTimer countDownTimer;

    static {
        System.loadLibrary("NativeImageProcessor");
    }


    public static Intent newIntent(Context context, ArrayList<PictureEditMovie> listPictureEdit, int position) {
        Intent intent = new Intent(context, PlayMovieMemoryActivity.class);
        intent.putExtra(ARG_PARAM1, listPictureEdit);
        intent.putExtra(ARG_PARAM2, position);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_movie_memory);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        if (getIntent() != null) {
            playlist = (ArrayList<PictureEditMovie>) getIntent().getSerializableExtra(ARG_PARAM1);
            position = getIntent().getIntExtra(ARG_PARAM2, 0);
        }
        listBitmapFiltered = new ArrayList<>();
        play_movie_memory_flipper = findViewById(R.id.play_movie_memory_flipper);
        sound = playlist.get(0).getSound();
        filter = playlist.get(0).getFilter();
        holder = playlist.get(0).getHolder();
        title = playlist.get(0).getTitle();
        subtitle = playlist.get(0).getSubtitle();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        thumbLoadedCallBack = this;
        sizeList = playlist.size();
        listBitmapFiltered.clear();
        for (int i = 0; i < playlist.size(); i++) {
            PlayMovieLoadFilter playMovieLoadFilter = new PlayMovieLoadFilter(this, playlist.get(i).getPath(), playlist.get(i).getFilter(), width, height, thumbLoadedCallBack);
            playMovieLoadFilter.execute();
        }

        time = sizeList * 6000;

        btn_play_movie_memory = findViewById(R.id.btn_play_movie_memory);
        btn_play_movie_memory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPause) {
                    countDownTimer.cancel();
                    play_movie_memory_flipper.stopFlipping();
                    isPause = true;
                } else {
                    startTimer(timeOut);
                    play_movie_memory_flipper.startFlipping();
                    isPause = false;
                }
            }
        });

        btn_play_movie_exit = findViewById(R.id.btn_play_movie_exit);
        btn_play_movie_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fg_play_movie = findViewById(R.id.fg_play_movie);
        fg_play_movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShowFg) {
                    fg_play_movie.animate().alpha(1f).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            btn_play_movie_memory.setClickable(true);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).start();
                } else {
                    fg_play_movie.animate().alpha(0f).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            btn_play_movie_memory.setClickable(false);
                            btn_play_movie_exit.setClickable(false);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).start();
                }
            }
        });

        prog_play_movie_memory = findViewById(R.id.prog_play_movie_memory);
        prog_play_movie_memory.setMax((int) time);
        txt_time_play_movie_memory = findViewById(R.id.txt_time_play_movie_memory);
        txt_time_play_movie_memory.setText("00:00/00:"+time/1000);
    }


    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public void onLoaded() {
        View view = LayoutInflater.from(this).inflate(R.layout.fragment_memory_tab_item, play_movie_memory_flipper, false);
        ViewGroup text_holder_container = (ViewGroup) view.findViewById(R.id.text_holder_container);
        ImageView imageView0 = view.findViewById(R.id.imgMemoryTabItem);
        imageView0.setImageBitmap(listBitmapFiltered.get(0));
        View textHolder;
        ImageView imgTextHolder;
        switch (playlist.get(0).getHolder()) {
            case "1":
                textHolder = LayoutInflater.from(this).inflate(R.layout.tab_item_text_holder_1, (ViewGroup) view, false);
                imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                Glide.with(this)
                        .load(R.drawable.text_holder_1)
                        .apply(new RequestOptions().dontAnimate().override(imgTextHolder.getMeasuredWidth(), imgTextHolder.getMeasuredHeight()))
                        .into(imgTextHolder);
                break;
            case "2":
                textHolder = LayoutInflater.from(this).inflate(R.layout.tab_item_text_holder_2, (ViewGroup) view, false);

                imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                Glide.with(this)
                        .load(R.drawable.text_holder_2)
                        .apply(new RequestOptions().dontAnimate().override(imgTextHolder.getMeasuredWidth(), imgTextHolder.getMeasuredHeight()))
                        .into(imgTextHolder);
                break;
            case "3":
                textHolder = LayoutInflater.from(this).inflate(R.layout.tab_item_text_holder_3, (ViewGroup) view, false);

                imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                Glide.with(this)
                        .load(R.drawable.text_holder_3)
                        .apply(new RequestOptions().dontAnimate().override(imgTextHolder.getMeasuredWidth(), imgTextHolder.getMeasuredHeight()))
                        .into(imgTextHolder);
                break;
            case "4":
                textHolder = LayoutInflater.from(this).inflate(R.layout.tab_item_text_holder_4, (ViewGroup) view, false);

                imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                Glide.with(this)
                        .load(R.drawable.text_holder_4)
                        .apply(new RequestOptions().dontAnimate().override(imgTextHolder.getMeasuredWidth(), imgTextHolder.getMeasuredHeight()))
                        .into(imgTextHolder);
                break;
            default:
                textHolder = LayoutInflater.from(this).inflate(R.layout.tab_item_text_holder_1, (ViewGroup) view, false);

                imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                Glide.with(this)
                        .load(R.drawable.text_holder_1)
                        .apply(new RequestOptions().dontAnimate().override(imgTextHolder.getMeasuredWidth(), imgTextHolder.getMeasuredHeight()))
                        .into(imgTextHolder);
                break;
        }

        TextView txtTextHolderTitle = (TextView) textHolder.findViewById(R.id.txtTextHolderTitle);
        TextView txtTextHolderSubtitle = (TextView) textHolder.findViewById(R.id.txtTextHolderSubtitle);
        txtTextHolderTitle.setText(playlist.get(0).getTitle());
        txtTextHolderSubtitle.setText(playlist.get(0).getSubtitle());
        ViewGroup.LayoutParams param = text_holder_container.getLayoutParams();
        param.height = height;
        param.width = width;
        text_holder_container.setLayoutParams(param);
        text_holder_container.addView(textHolder);
        play_movie_memory_flipper.addView(view);

        for (int i = 1; i < listBitmapFiltered.size(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(listBitmapFiltered.get(i));
            play_movie_memory_flipper.addView(imageView);
        }

        Animation in = AnimationUtils.loadAnimation(this, R.anim.movie_flip_anim_in); // load an animation
        Animation out = AnimationUtils.loadAnimation(this, R.anim.movie_flip_anim_out);
        play_movie_memory_flipper.setInAnimation(in);
        play_movie_memory_flipper.setOutAnimation(out);
        play_movie_memory_flipper.setAutoStart(true);
        play_movie_memory_flipper.setFlipInterval(6000);
        startTimer(time);
    }

    public void startTimer(long time) {
        play_movie_memory_flipper.startFlipping();
        countDownTimer = new CountDownTimer(time, 10) {

            public void onTick(long millisUntilFinished) {
                timeOut = millisUntilFinished;
                prog_play_movie_memory.setProgress((int) (time - timeOut));
            }

            public void onFinish() {
                play_movie_memory_flipper.stopFlipping();
                timeOut = time;
                isPause = true;
            }

        }.start();
    }
}
