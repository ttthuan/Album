package com.example.akiyoshi.albumsole.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.akiyoshi.albumsole.R;
import com.example.akiyoshi.albumsole.models.Picture;
import com.example.akiyoshi.albumsole.models.PictureLab;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

public class PictureDetailFragment extends Fragment {
    private static String ARG_PICTURE_ID = "com.example.acer.picture_id";
    private static String EXTRAC_RESULT = "extrac_result";
    private Picture picture;
    private static PhotoView photoView;
    PictureDetailViewActivity paperActivity;

    private int widthScreen;
    private int heigthScreen;

    public void getWithOfScreen(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        widthScreen = displayMetrics.widthPixels;
        heigthScreen = displayMetrics.heightPixels;
    }

    public static PictureDetailFragment newInstance(String id, String month){
        Bundle args = new Bundle();
        ArrayList<String> arr = new ArrayList<>(2);
        arr.add(id);
        arr.add(month);
        args.putStringArrayList(ARG_PICTURE_ID, arr);
        PictureDetailFragment pictureDetailFragment = new PictureDetailFragment();
        pictureDetailFragment.setArguments(args);
        return pictureDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        picture = new Picture();
        paperActivity = (PictureDetailViewActivity) getActivity();

        ArrayList<String> arr = getArguments().getStringArrayList(ARG_PICTURE_ID);
        int id = Integer.parseInt(arr.get(0));
        picture = PictureLab.getInstance(getActivity()).getPictureMonth().getAPictureInMonth(arr.get(1), id);

    }

    public static int[] CalculatorSizeOfImageInDetailView(int sourceWidth, int sourceHeight, int widthScreen, int heigthScreen){
        int[] arr = new int[2];

        float ratio = sourceHeight * 1.0f / sourceWidth;
        int width, height;

        int subWidth = widthScreen - sourceWidth;
        int subHeight = heigthScreen - sourceHeight;


        if(subWidth <= 0 && subHeight <= 0){
            if(Math.abs(subWidth) - Math.abs(subHeight) > 0){
                width = widthScreen;
                height = Math.round(ratio * width);
            }else{
                height = heigthScreen;
                width = Math.round(height/ratio);
            }
        }else if(subHeight <= 0 & subWidth >= 0){
            height = heigthScreen;
            width = Math.round(height/ratio);
        }else if(subHeight >= 0 & subWidth <= 0 ){
            width = widthScreen;
            height = Math.round(ratio * width);
        }else{
            width = sourceWidth;
            height = sourceHeight;
        }
        arr[0] = width;
        arr[1] = height;
        return arr;
    }

    public void SetImageForPhotoView(PhotoView photoView, Picture picture, int widthScreen, int heigthScreen){
        photoView.setId(picture.getId());

        int sourceWidth, sourceHeight;

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(picture.getPath(), bmOptions);

        sourceWidth = bitmap.getWidth();
        sourceHeight = bitmap.getHeight();

        PictureLab.getInstance(getActivity()).getPictureMonth().getAPictureInMonth(picture.getMonth(), picture.getId()).setWidth(sourceWidth);
        PictureLab.getInstance(getActivity()).getPictureMonth().getAPictureInMonth(picture.getMonth(), picture.getId()).setHeight(sourceHeight);


        int width, height;

        int[] arr = CalculatorSizeOfImageInDetailView(sourceWidth, sourceHeight, widthScreen, heigthScreen);
        width = arr[0];
        height = arr[1];

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.override(width, height);
        requestOptions.fitCenter();
        requestOptions.placeholder(new ColorDrawable(Color.WHITE));
        //requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);

        Glide.with(getActivity())
                .load(picture.getPath())
                .apply(
                        requestOptions
                )
                .into(photoView);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture_detail, container, false);

        //imageView = view.findViewById(R.id.imageView);
        photoView = view.findViewById(R.id.photo_view);
        getWithOfScreen(getActivity());

        SetImageForPhotoView(photoView, picture, widthScreen, heigthScreen);

        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paperActivity.ToolBarControl();
            }
        });



        // test ExifInterface
//        ExifInterface exifReader = null;
//        int orientation = 0;
//        int rotate = 0;
//        try {
//            exifReader = new ExifInterface(picture.getPath());
//            orientation = exifReader.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if (orientation == ExifInterface.ORIENTATION_NORMAL) {
//            // Do nothing. The original image is fine.
//        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
//            rotate = 90;
//        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
//            rotate = 180;
//        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
//            rotate = 270;
//        }

        return view;
    }
}
