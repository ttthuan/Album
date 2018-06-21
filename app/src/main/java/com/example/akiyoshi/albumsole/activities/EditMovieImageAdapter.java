package com.example.akiyoshi.albumsole.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.akiyoshi.albumsole.R;
import com.example.akiyoshi.albumsole.models.Picture;
import com.example.akiyoshi.albumsole.models.PictureAreaItem;
import com.example.akiyoshi.albumsole.models.PictureEditMovie;
import com.example.akiyoshi.albumsole.models.ThumbsManager;
import com.zomato.photofilters.imageprocessors.Filter;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class EditMovieImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<PictureEditMovie> listPictureItem;
    private CreateMovieImageFragment pictureAreaFragment;
    private EditSaveCallBack editSaveCallBack;

    public EditMovieImageAdapter(Context context, List<PictureEditMovie> listPictureItem, EditSaveCallBack editSaveCallBack) {
        this.context = context;
        this.listPictureItem = listPictureItem;
        this.editSaveCallBack = editSaveCallBack;
    }

    public EditMovieImageAdapter(Context context, List<PictureEditMovie> listPictureItem, CreateMovieImageFragment fragment) {
        this.context = context;
        this.listPictureItem = listPictureItem;
        this.pictureAreaFragment = fragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View itemView;

        switch (viewType) {
            default:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_edit_movie_item_picture, parent, false);
                break;
        }
        return new EditMovieImageAdapter.PictureViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            default:
                EditMovieImageAdapter.PictureViewHolder vhPicture = (EditMovieImageAdapter.PictureViewHolder) holder;
                vhPicture.BindImage(listPictureItem.get(position).getPath(),
                        listPictureItem.get(position).getFilter(),
                        listPictureItem.get(position).getHolder(),
                        listPictureItem.get(position).getTitle(),
                        listPictureItem.get(position).getSubtitle(), position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listPictureItem == null ? 0 : listPictureItem.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public class PictureViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView rawImageView;
        ViewGroup edit_movie_text_holder_container;
        View textHolder;
        int pos=-1;
        String path;
        boolean isSelected;

        public PictureViewHolder(View itemView) {
            super(itemView);
            rawImageView = itemView.findViewById(R.id.rawImageView);
//            itemView.setOnClickListener(this);
            rawImageView.setOnClickListener(this);
            edit_movie_text_holder_container = (ViewGroup) itemView.findViewById(R.id.edit_movie_text_holder_container);
            isSelected = false;
        }

        public void BindImage(String path, String stringFilter, String text_holder_style, String title, String subTitle, int pos) {
            int w = Math.round(MainActivity.widthScreen * 1.0f / 2);
            int h = Math.round(w * 1.0f * 16 / 9);
            this.path = path;
            this.pos = pos;
            int textSizeTit=0, textSizeSub=0;
            ClearImageFilterd clearImageFilterd = new ClearImageFilterd(context, rawImageView, path, stringFilter, w, h);
            clearImageFilterd.execute();
            if (pos == 0) {
                ImageView imgTextHolder;
                switch (text_holder_style) {
                    case "1":
                        textHolder = LayoutInflater.from(context).inflate(R.layout.tab_item_text_holder_1, (ViewGroup) itemView, false);
                        imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                        Glide.with(context)
                                .load(R.drawable.text_holder_1)
                                .apply(new RequestOptions().dontAnimate().override(imgTextHolder.getMeasuredWidth(), imgTextHolder.getMeasuredHeight()))
                                .into(imgTextHolder);
                        textSizeTit=20;
                        textSizeSub=12;
                        break;
                    case "2":
                        textHolder = LayoutInflater.from(context).inflate(R.layout.tab_item_text_holder_2, (ViewGroup) itemView, false);
                        imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                        Glide.with(context)
                                .load(R.drawable.text_holder_2)
                                .apply(new RequestOptions().dontAnimate().override(imgTextHolder.getMeasuredWidth(), imgTextHolder.getMeasuredHeight()))
                                .into(imgTextHolder);
                        textSizeTit=14;
                        textSizeSub=8;
                        break;
                    case "3":
                        textHolder = LayoutInflater.from(context).inflate(R.layout.tab_item_text_holder_3, (ViewGroup) itemView, false);
                        imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                        Glide.with(context)
                                .load(R.drawable.text_holder_3)
                                .apply(new RequestOptions().dontAnimate().override(imgTextHolder.getMeasuredWidth(), imgTextHolder.getMeasuredHeight()))
                                .into(imgTextHolder);
                        textSizeTit=20;
                        textSizeSub=12;
                        break;
                    case "4":
                        textHolder = LayoutInflater.from(context).inflate(R.layout.tab_item_text_holder_4, (ViewGroup) itemView, false);
                        imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                        Glide.with(context)
                                .load(R.drawable.text_holder_4)
                                .apply(new RequestOptions().dontAnimate().override(imgTextHolder.getMeasuredWidth(), imgTextHolder.getMeasuredHeight()))
                                .into(imgTextHolder);
                        textSizeTit=24;
                        textSizeSub=12;
                        break;
                    default:
                        textHolder = LayoutInflater.from(context).inflate(R.layout.tab_item_text_holder_1, (ViewGroup) itemView, false);
                        imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                        Glide.with(context)
                                .load(R.drawable.text_holder_1)
                                .apply(new RequestOptions().dontAnimate().override(imgTextHolder.getMeasuredWidth(), imgTextHolder.getMeasuredHeight()))
                                .into(imgTextHolder);
                        textSizeTit=20;
                        textSizeSub=12;
                        break;

                }
                TextView txtTextHolderTitle = (TextView) textHolder.findViewById(R.id.txtTextHolderTitle);
                TextView txtTextHolderSubtitle = (TextView) textHolder.findViewById(R.id.txtTextHolderSubtitle);
                txtTextHolderTitle.setText(title);
                txtTextHolderSubtitle.setText(subTitle);
                txtTextHolderTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeTit);
                txtTextHolderSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSub);
                edit_movie_text_holder_container.removeAllViews();
                ViewGroup.LayoutParams param = edit_movie_text_holder_container.getLayoutParams();
                param.height = h;
                param.width = w;
                edit_movie_text_holder_container.setLayoutParams(param);
                edit_movie_text_holder_container.addView(textHolder);
//                edit_movie_text_holder_container.setClickable(true);
            }

        }

        @Override
        public void onClick(View view) {
            if(pos==0)
            {
//                Toast.makeText(context,"edit holder", Toast.LENGTH_SHORT).show();
                    editSaveCallBack.onOpenEditTextHolder();
            }
        }
    }
}

