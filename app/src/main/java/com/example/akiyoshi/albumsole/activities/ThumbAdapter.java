package com.example.akiyoshi.albumsole.activities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.example.akiyoshi.albumsole.R;
import com.example.akiyoshi.albumsole.models.ThumbsManager;

import java.util.List;

public class ThumbAdapter extends RecyclerView.Adapter<ThumbAdapter.ThumbViewHolder> {

    public List<Bitmap> dataSet;
    private ThumbCallBack thumbCallBack;
    public static int LastPosition = -1;
    private ThumbViewHolder lastThumbViewHolder = null;

    public ThumbAdapter(List<Bitmap> data, ThumbCallBack callBack) {
        this.dataSet = data;
        this.thumbCallBack = callBack;
    }


    @NonNull
    @Override
    public ThumbViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter, parent, false);
        LastPosition = -1;
        return new ThumbViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbViewHolder holder, int position) {
        holder.bindData(position);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (LastPosition != position) {
//                    if (lastThumbViewHolder != null)
//                        lastThumbViewHolder.SetBackground(R.color.backgroundTransparent);
//                    holder.SetBackground(R.color.backgroundSelected);
                    thumbCallBack.onThumbClick(ThumbsManager.filters.get(position));
                    LastPosition = position;
                    lastThumbViewHolder = holder;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    // view holder
    public static class ThumbViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout itemFilter;
        ImageView image;

        public ThumbViewHolder(View itemView) {
            super(itemView);

            itemFilter = itemView.findViewById(R.id.item_filter);
            image = itemView.findViewById(R.id.image_filter);
        }

        public void bindData(int position) {
            image.setImageBitmap(ThumbsManager.bitmaps.get(position));
            //image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        public void SetBackground(int color) {
            itemFilter.setBackgroundColor(color);
        }
    }
}
