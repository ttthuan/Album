package com.example.akiyoshi.albumsole.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.akiyoshi.albumsole.R;
import com.example.akiyoshi.albumsole.models.Picture;
import com.example.akiyoshi.albumsole.models.PictureAreaItem;

import java.util.List;

public class CreateMovieImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<PictureAreaItem> listPictureItem;
    private CreateMovieImageFragment pictureAreaFragment;

    public CreateMovieImageAdapter(Context context, List<PictureAreaItem> listPictureItem) {
        this.context = context;
        this.listPictureItem = listPictureItem;
    }

    public CreateMovieImageAdapter(Context context, List<PictureAreaItem> listPictureItem, CreateMovieImageFragment fragment) {
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
            case 0:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_movie_item_text, parent, false);
                return new CreateMovieImageAdapter.TextViewHolder(itemView);
            default:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_movie_item_picture, parent, false);
                break;
        }
        return new CreateMovieImageAdapter.PictureViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                CreateMovieImageAdapter.TextViewHolder vhText = (CreateMovieImageAdapter.TextViewHolder) holder;
                String monthly = listPictureItem.get(position).getMonthly();
                vhText.tvMonthly.setText(monthly);
                break;
            default:
                CreateMovieImageAdapter.PictureViewHolder vhPicture = (CreateMovieImageAdapter.PictureViewHolder) holder;
                vhPicture.BindImage(listPictureItem.get(position).getPicture());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listPictureItem == null ? 0 : listPictureItem.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (listPictureItem.get(position).getType()) {
            case HEADER:
                return 0;
            case NORMAL:
                return 1;
            case FIRST_PIC:
                return 2;
            case LAST_PIC:
                return 3;
            default:
                return 1;
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        switch (holder.getItemViewType()) {
            case 0:
                break;
            default:
                ((CreateMovieImageAdapter.PictureViewHolder) holder).recycle();
        }
    }

    public class TextViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMonthly;

        public TextViewHolder(View itemView) {
            super(itemView);
            tvMonthly = (TextView) itemView.findViewById(R.id.tvMonthly);
        }
    }

    public class PictureViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView rawImageView;
        private RelativeLayout layout;
        private RelativeLayout checkedLayout;
        Picture mPicture;
        boolean isSelected;

        public PictureViewHolder(View itemView) {
            super(itemView);
            rawImageView = itemView.findViewById(R.id.rawImageView);
            layout = itemView.findViewById(R.id.layout_chosen);
            checkedLayout = itemView.findViewById(R.id.checkedLayout);
            itemView.setOnClickListener(this);
            isSelected = false;
        }

        public void BindImage(Picture picture) {
            int spacingInPixels = 2;
            int wh = Math.round(MainActivity.widthScreen * 1.0f / 4)-4;
            ViewGroup.LayoutParams params = layout.getLayoutParams();
            params.width = wh;
            params.height = wh;
            layout.setLayoutParams(params);
            this.mPicture = picture;
            Glide.with(context)
                    .load(picture.getPath())
                    .apply(new RequestOptions().override(wh, wh).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(rawImageView);

            layout.setBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.colorAccentMovieLight, null));
        }

        public void recycle() {
            Glide.with(context).clear(rawImageView);
            rawImageView.setImageDrawable(null);
        }

        @Override
        public void onClick(View view) {
            if (!isSelected) {
                rawImageView.animate().scaleX(0.92f).scaleY(0.92f).start();
                checkedLayout.setVisibility(View.VISIBLE);
                CreateNewMovieActivity.addSelected(mPicture);
                isSelected = true;
            } else {
                rawImageView.animate().scaleX(1).scaleY(1).start();
                checkedLayout.setVisibility(View.INVISIBLE);
                CreateNewMovieActivity.removeSelected(mPicture);
                isSelected = false;
            }
        }
    }
}
