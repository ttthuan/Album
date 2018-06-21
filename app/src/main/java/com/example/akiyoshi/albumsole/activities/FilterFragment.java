package com.example.akiyoshi.albumsole.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.akiyoshi.albumsole.R;
import com.example.akiyoshi.albumsole.models.Picture;
import com.example.akiyoshi.albumsole.models.PictureLab;
import com.example.akiyoshi.albumsole.models.ThumbsManager;
import com.zomato.photofilters.imageprocessors.Filter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FilterFragment extends Fragment {

    private static final String ARG_PICTURE_ID = "com.example.albumsole.id";
    private static final String ARG_PICTURE_MONTH = "com.example.albumsole.month";
    private Picture picture = null;
    private ThumbAdapter thumbAdapter;
    private RecyclerView recyclerView;
    private Handler handlerAddAdapter;
    private ThumbAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int id = Integer.parseInt(getArguments().getString(ARG_PICTURE_ID));
        String month = getArguments().getString(ARG_PICTURE_MONTH);

        Log.d("FILTER", "id " + id);
        Log.d("FILTER", "month " + month);

        picture = PictureLab.getInstance(getActivity()).getPictureMonth().getAPictureInMonth(month, id);

        handlerAddAdapter = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                List<Bitmap> thumbs = ThumbsManager.bitmaps;
                adapter.dataSet.addAll(thumbs);
                adapter.notifyDataSetChanged();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        recyclerView = view.findViewById(R.id.recycle_filter);

        List<Bitmap> thumbs = new ArrayList<>();
        adapter = new ThumbAdapter(thumbs, (ThumbCallBack) getActivity());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);

        Thread threadFilter = new Thread(new Runnable() {
            @Override
            public void run() {
                int n = ThumbsManager.filters.size();
                ThumbsManager.clearBitmap();

                float sizeImage = getActivity().getResources().getDimension(R.dimen.thumbSize);
                Bitmap thumbImage = null;
                try {
                    thumbImage = Glide.with(getActivity())
                            .asBitmap()
                            .apply(new RequestOptions().override(200, 200).centerCrop().placeholder(R.color.colorBlack))
                            .load(picture.getPath())
                            .submit().get();

                    for(int i = 0; i < n; i++){
                        Bitmap bitmap = Bitmap.createBitmap(thumbImage);
                        ThumbsManager.addBitmap(bitmap);
                    }
                    Log.d("FILTER", "SIZE OF BITMAP " + ThumbsManager.bitmaps.size());

                    ThumbsManager.processBitmap();

                    Message message = handlerAddAdapter.obtainMessage(1, "done");

                    handlerAddAdapter.sendMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        threadFilter.start();

        return view;
    }

    public static FilterFragment newInstance(String id, String month){
        Bundle args = new Bundle();
        args.putString(ARG_PICTURE_ID, id);
        args.putString(ARG_PICTURE_MONTH, month);
        FilterFragment pictureDetailFragment = new FilterFragment();
        pictureDetailFragment.setArguments(args);
        return pictureDetailFragment;
    }
}
