package com.example.akiyoshi.albumsole.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.akiyoshi.albumsole.R;
import com.example.akiyoshi.albumsole.models.Picture;
import com.example.akiyoshi.albumsole.models.PictureAreaItem;
import com.example.akiyoshi.albumsole.models.RcvItemType;

import java.util.List;

public class PictureAreaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements AddPictureInAdapter {
    private Context context;
    private List<PictureAreaItem> listPictureItem;
    private int requestCode;
    public static boolean isOpenDetail = false;

    @Override
    public void addPicture(Picture picture) {
        if(requestCode == PictureAreaFragment.REQUEST_CODE_IN_VIEW_DETAIL){
            if(listPictureItem.get(0).getMonthly().equals(picture.getMonth())){
                listPictureItem.add(2, new PictureAreaItem(listPictureItem.get(0).getMonthly(), picture, RcvItemType.NORMAL));
                this.notifyDataSetChanged();
            }else{
                listPictureItem.add(0, new PictureAreaItem(picture.getMonth(), null, RcvItemType.HEADER));
                listPictureItem.add(1, new PictureAreaItem(picture.getMonth(), picture, RcvItemType.NORMAL));
                this.notifyDataSetChanged();
            }
        }else{
            listPictureItem.add( new PictureAreaItem(picture.getAlbum(), picture, RcvItemType.NORMAL));
        }
    }

    public class TextViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMonthly;

        public TextViewHolder(View itemView) {
            super(itemView);
            tvMonthly = (TextView) itemView.findViewById(R.id.tvMonthly);
        }
    }

    public int deleteItem(int id) {
        int n = listPictureItem.size();
        for (int i = 0; i < n; i++) {
            boolean isLastItem = false;
            if (listPictureItem.get(i).getPicture() != null && listPictureItem.get(i).getPicture().getId().equals(id)) {
                Log.d("DELETE", "DONE " + id);
                if (listPictureItem.get(i).getType() == RcvItemType.FIRST_PIC) {
                    if ((i + 1) >= listPictureItem.size()) {
                        isLastItem = true;
                    } else if (listPictureItem.get(i + 1).getType() == RcvItemType.HEADER) {
                        isLastItem = true;
                    } else {
                        listPictureItem.get(i + 1).setType(RcvItemType.FIRST_PIC);
                    }

                }
                listPictureItem.remove(i);
                this.notifyItemRemoved(i);

                if (requestCode == PictureAreaFragment.REQUEST_CODE_IN_VIEW_DETAIL) {
                    if (isLastItem) {
                        listPictureItem.remove(i - 1);
                        this.notifyItemRemoved(i - 1);
                    }
                }

                break;
            } else {
                Log.d("DELETE", "PICTURE NULL" + i);
            }
        }
        return listPictureItem.size();
    }

    public String getFolderName() {
        String nameFolder = null;

        if (listPictureItem != null && listPictureItem.size() > 0) {
            if (listPictureItem.get(0).getPicture() != null) {
                nameFolder = listPictureItem.get(0).getPicture().getAlbum();
            }
        }

        return nameFolder;
    }

    public class PictureViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView rawImageView;
        Picture mPicture;

        public PictureViewHolder(View itemView) {
            super(itemView);
            rawImageView = (ImageView) itemView.findViewById(R.id.rawImageView);
            itemView.setOnClickListener(this);
        }

        public void BindImage(Picture picture) {
            int wh = Math.round(MainActivity.widthScreen * 1.0f / MainActivity.rowInGrid);
            this.mPicture = picture;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Glide.with(context).load( picture.getPath())
                            .apply(new RequestOptions().override(wh, wh).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into(rawImageView);
//                    LoadImagePictureAreaItem loadImagePictureAreaItem = new LoadImagePictureAreaItem(context, rawImageView, picture.getPath(), wh, wh);
//                    loadImagePictureAreaItem.execute();
                }
            }, 500);

        }

        public void recycle() {
            Glide.with(context).clear(rawImageView);
            rawImageView.setImageDrawable(null);
        }

        @Override
        public void onClick(View view) {
            // open new activity
            Log.i("Image Path", "" + mPicture.getPath());
            if (!isOpenDetail) {
                Thread threadOpenDetail = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity mainActivity = (MainActivity) context;

                        Intent intent = PictureDetailViewActivity.newIntent(context, mPicture.getId());

                        mainActivity.startActivityForResult(intent, requestCode);
                    }
                });
                threadOpenDetail.start();
                isOpenDetail = !isOpenDetail;
            }
        }


    }

    public PictureAreaAdapter(Context context, List<PictureAreaItem> listPictureItem, int requestCode) {
        this.context = context;
        this.listPictureItem = listPictureItem;
        this.requestCode = requestCode;
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

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case 0:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_text, parent, false);
                return new TextViewHolder(itemView);
            default:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_picture, parent, false);
                break;
        }
        return new PictureViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case 0:
                TextViewHolder vhText = (TextViewHolder) holder;
                String monthly = listPictureItem.get(position).getMonthly();
                vhText.tvMonthly.setText(monthly);
                break;
            default:
                PictureViewHolder vhPicture = (PictureViewHolder) holder;
                vhPicture.BindImage(listPictureItem.get(position).getPicture());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listPictureItem == null ? 0 : listPictureItem.size();
    }

//    @Override
//    public void onViewRecycled(RecyclerView.ViewHolder holder) {
//        super.onViewRecycled(holder);
//        switch (holder.getItemViewType()) {
//            case 0:
//                break;
//            default:
//                ((PictureViewHolder) holder).recycle();
//        }
//    }
}
