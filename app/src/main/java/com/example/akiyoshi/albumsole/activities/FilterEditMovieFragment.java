package com.example.akiyoshi.albumsole.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.akiyoshi.albumsole.R;
import com.example.akiyoshi.albumsole.models.Picture;
import com.example.akiyoshi.albumsole.models.PictureEditMovie;
import com.example.akiyoshi.albumsole.models.ThumbsManager;
import com.zomato.photofilters.imageprocessors.Filter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FilterEditMovieFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FilterEditMovieFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterEditMovieFragment extends Fragment implements ThumbLoadedCallBack{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private PictureEditMovie pictureEditMovie;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private ThumbLoadedCallBack thumbLoadedCallBack;
    boolean isChange = false;
    Animation anim;
    public FilterEditMovieFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FilterEditMovieFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FilterEditMovieFragment newInstance(PictureEditMovie param1) {
        FilterEditMovieFragment fragment = new FilterEditMovieFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pictureEditMovie = (PictureEditMovie) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_filter_edit_movie, container, false);
        recyclerView = v.findViewById(R.id.recycle_filter);
        thumbLoadedCallBack = this;
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                int n = ThumbsManager.filters.size();
                ThumbsManager.clearBitmap();
                for(int i = 0; i < n; i++){
                    EditMovieLoadFilter editMovieLoadFilter = new EditMovieLoadFilter(getContext(), pictureEditMovie.getPath(), i+"", 200,200, thumbLoadedCallBack);
                    editMovieLoadFilter.execute();
                }

            }
        };
        handler.post(r);
        EditMovieActivity.isFilter = true;
        EditMovieActivity.edit_movie_tool_appbar.setExpanded(false, true);
        EditMovieActivity.txt_edit_mode.setText("Filter");
        anim = AnimationUtils.loadAnimation(getContext(), R.anim.memory_fab_scale_hide);
        anim.setFillAfter(true);
        EditMovieActivity.fabEditPlay.startAnimation(anim);
        EditMovieActivity.fabEditPlay.setVisibility(View.GONE);
        EditMovieActivity.fabEditPlay.setEnabled(false);
        EditMovieActivity.btn_edit_movie.setAlpha(1f);
        EditMovieActivity.btn_edit_movie.setEnabled(true);
        EditMovieActivity.btn_edit_movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChange = true;
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });
        EditMovieActivity.btn_edit_movie_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChange=false;
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EditMovieActivity.isFilter = false;
        EditMovieActivity.edit_movie_tool_appbar.setExpanded(true, true);
        EditMovieActivity.setFilterClick();
        EditMovieActivity.txt_edit_mode.setText("Edit");
        anim = AnimationUtils.loadAnimation(getContext(), R.anim.memory_fab_scale_show);
        anim.setFillAfter(true);
        EditMovieActivity.fabEditPlay.setVisibility(View.VISIBLE);
        EditMovieActivity.fabEditPlay.startAnimation(anim);
        EditMovieActivity.fabEditPlay.setEnabled(true);
        EditMovieActivity.btn_edit_movie.setAlpha(0f);
        EditMovieActivity.btn_edit_movie.setEnabled(false);
        if(isChange)
            EditMovieActivity.changeFilter(true);
        else
            EditMovieActivity.changeFilter(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EditMovieActivity.isFilter = false;
//        EditMovieActivity.btn_edit_movie_filter.setEnabled(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onLoaded() {
        List<Bitmap> thumbs = ThumbsManager.bitmaps;
//        List<Bitmap> thumbs = ThumbsManager.processBitmap();
        ThumbAdapter adapter = new ThumbAdapter(thumbs, (ThumbCallBack) getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
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
