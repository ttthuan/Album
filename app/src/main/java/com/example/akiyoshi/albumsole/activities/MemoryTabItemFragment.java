package com.example.akiyoshi.albumsole.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.akiyoshi.albumsole.R;
import com.example.akiyoshi.albumsole.models.MemoryMovieObject;
import com.example.akiyoshi.albumsole.models.Picture;
import com.example.akiyoshi.albumsole.models.PictureLab;
import com.example.akiyoshi.albumsole.models.ThumbsManager;
import com.zomato.photofilters.imageprocessors.Filter;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MemoryTabItemFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MemoryTabItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemoryTabItemFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private MemoryMovieObject movieParam;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    Handler handler;
    Animation zoomin;
    Animation zoomout;
    ImageView imgMemoryTabItem;

    public MemoryTabItemFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MemoryTabItemFragment newInstance(MemoryMovieObject movieParam) {
        MemoryTabItemFragment fragment = new MemoryTabItemFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, movieParam);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieParam = (MemoryMovieObject) getArguments().getSerializable(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_memory_tab_item, container, false);
        imgMemoryTabItem = (ImageView) v.findViewById(R.id.imgMemoryTabItem);

        zoomin = AnimationUtils.loadAnimation(getContext(), R.anim.memory_tab_item_img_zoom_in);
        zoomout = AnimationUtils.loadAnimation(getContext(), R.anim.memory_tab_item_img_zoom_out);

        imgMemoryTabItem.setAnimation(zoomin);
        imgMemoryTabItem.startAnimation(zoomin);

        if (movieParam != null) {

            ClearImageFilterd clearImageFilterd = new ClearImageFilterd(getContext(), imgMemoryTabItem, movieParam.getListImagePath().get(0), movieParam.getFilter(), imgMemoryTabItem.getMeasuredWidth(), imgMemoryTabItem.getMeasuredHeight());
            clearImageFilterd.execute();
        }

        String text_holder_style = movieParam.getTextHolder();
        ViewGroup text_holder_container = (ViewGroup) v.findViewById(R.id.text_holder_container);
        View textHolder;
        ImageView imgTextHolder;
//        View filter = v.findViewById(R.id.filter);
        switch (text_holder_style) {
            case "1":
                textHolder = inflater.inflate(R.layout.tab_item_text_holder_1, (ViewGroup) v, false);
                imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                Glide.with(getActivity())
                        .load(R.drawable.text_holder_1)
                        .apply(new RequestOptions().dontAnimate().override(imgTextHolder.getMeasuredWidth(), imgTextHolder.getMeasuredHeight()))
                        .into(imgTextHolder);
//                filter.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorFilter1, null));
                break;
            case "2":
                textHolder = inflater.inflate(R.layout.tab_item_text_holder_2, (ViewGroup) v, false);
                imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                Glide.with(getActivity())
                        .load(R.drawable.text_holder_2)
                        .apply(new RequestOptions().dontAnimate().override(imgTextHolder.getMeasuredWidth(), imgTextHolder.getMeasuredHeight()))
                        .into(imgTextHolder);
//                filter.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorFilter2, null));

                break;
            case "3":
                textHolder = inflater.inflate(R.layout.tab_item_text_holder_3, (ViewGroup) v, false);
                imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                Glide.with(getActivity())
                        .load(R.drawable.text_holder_3)
                        .apply(new RequestOptions().dontAnimate().override(imgTextHolder.getMeasuredWidth(), imgTextHolder.getMeasuredHeight()))
                        .into(imgTextHolder);
//                filter.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorFilter3, null));
                break;
            case "4":
                textHolder = inflater.inflate(R.layout.tab_item_text_holder_4, (ViewGroup) v, false);
                imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                Glide.with(getActivity())
                        .load(R.drawable.text_holder_4)
                        .apply(new RequestOptions().dontAnimate().override(imgTextHolder.getMeasuredWidth(), imgTextHolder.getMeasuredHeight()))
                        .into(imgTextHolder);
//                filter.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorFilter4, null));
                break;
            default:
                textHolder = inflater.inflate(R.layout.tab_item_text_holder_1, (ViewGroup) v, false);
                imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                Glide.with(getActivity())
                        .load(R.drawable.text_holder_1)
                        .apply(new RequestOptions().dontAnimate().override(imgTextHolder.getMeasuredWidth(), imgTextHolder.getMeasuredHeight()))
                        .into(imgTextHolder);
//                filter.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorFilter1, null));
                break;

        }
        TextView txtTextHolderTitle = (TextView) textHolder.findViewById(R.id.txtTextHolderTitle);
        TextView txtTextHolderSubtitle = (TextView) textHolder.findViewById(R.id.txtTextHolderSubtitle);
        txtTextHolderTitle.setText(movieParam.getTitle());
        txtTextHolderSubtitle.setText(movieParam.getSubTitle());
        text_holder_container.addView(textHolder);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (this.isVisible()) {
            if (!isVisibleToUser) {
                imgMemoryTabItem.animate().cancel();

                imgMemoryTabItem.setAnimation(zoomin);
                imgMemoryTabItem.startAnimation(zoomin);
            } else {
                imgMemoryTabItem.animate().cancel();
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imgMemoryTabItem.setAnimation(zoomout);
                        imgMemoryTabItem.startAnimation(zoomout);
                    }
                }, 600);
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
