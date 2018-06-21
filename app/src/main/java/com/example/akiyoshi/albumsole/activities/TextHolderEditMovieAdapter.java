package com.example.akiyoshi.albumsole.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.akiyoshi.albumsole.R;
import com.example.akiyoshi.albumsole.models.ThumbsManager;

import java.util.List;

public class TextHolderEditMovieAdapter extends RecyclerView.Adapter<TextHolderEditMovieAdapter.TextHolderViewHolder> {
    public List<String> dataSet;
    public static int LastPosition = -1;
    private TextHolderViewHolder lastThumbViewHolder = null;
    EditTextHolderCallBack editTextHolderCallBack;
    public Context context;

    public TextHolderEditMovieAdapter(Context context, List<String> data, EditTextHolderCallBack callBack) {
        this.dataSet = data;
        this.editTextHolderCallBack=callBack;
        this.context=context;
    }

    @NonNull
    @Override
    public TextHolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_edit_movie_text_holder_item_picture, parent, false);
        LastPosition = -1;
        return new TextHolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TextHolderViewHolder holder, int position) {
        holder.bindData(dataSet.get(position));
        Log.i("BIND",dataSet.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (LastPosition != position) {
                    editTextHolderCallBack.onClickEditTextHolder(dataSet.get(position));
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
    public class TextHolderViewHolder extends RecyclerView.ViewHolder {
        ViewGroup edit_movie_text_holder_rcv;

        public TextHolderViewHolder(View itemView) {
            super(itemView);
            edit_movie_text_holder_rcv = (ViewGroup) itemView.findViewById(R.id.edit_movie_text_holder_rcv);
        }

        public void bindData(String text_holder_style) {
            View textHolder;
            ImageView imgTextHolder;
            switch (text_holder_style) {
                case "1":
                    textHolder = LayoutInflater.from(context).inflate(R.layout.tab_item_text_holder_1, (ViewGroup) itemView, false);
                    imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                    Glide.with(context)
                            .load(R.drawable.text_holder_1)
                            .into(imgTextHolder);
                    break;
                case "2":
                    textHolder = LayoutInflater.from(context).inflate(R.layout.tab_item_text_holder_2, (ViewGroup) itemView, false);
                    imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                    Glide.with(context)
                            .load(R.drawable.text_holder_2)
                            .into(imgTextHolder);
                    break;
                case "3":
                    textHolder = LayoutInflater.from(context).inflate(R.layout.tab_item_text_holder_3, (ViewGroup) itemView, false);
                    imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                    Glide.with(context)
                            .load(R.drawable.text_holder_3)
                            .into(imgTextHolder);
                    break;
                case "4":
                    textHolder = LayoutInflater.from(context).inflate(R.layout.tab_item_text_holder_4, (ViewGroup) itemView, false);
                    imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                    Glide.with(context)
                            .load(R.drawable.text_holder_4)
                            .into(imgTextHolder);
                    break;
                default:
                    textHolder = LayoutInflater.from(context).inflate(R.layout.tab_item_text_holder_1, (ViewGroup) itemView, false);
                    imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                    Glide.with(context)
                            .load(R.drawable.text_holder_1)
                            .into(imgTextHolder);
                    break;

            }

            TextView txtTextHolderTitle = (TextView) textHolder.findViewById(R.id.txtTextHolderTitle);
            TextView txtTextHolderSubtitle = (TextView) textHolder.findViewById(R.id.txtTextHolderSubtitle);
            float titSize = txtTextHolderTitle.getTextSize()/7;
            float subSize = txtTextHolderSubtitle.getTextSize()/7;
            txtTextHolderTitle.setTextSize(titSize);
            txtTextHolderSubtitle.setTextSize(subSize);
            txtTextHolderTitle.setText("Title Movie");
            txtTextHolderSubtitle.setText("SubTitle Movie");
            edit_movie_text_holder_rcv.addView(textHolder);
        }

    }
}
