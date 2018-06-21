package com.example.akiyoshi.albumsole.activities;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.akiyoshi.albumsole.R;
import com.example.akiyoshi.albumsole.models.Picture;
import com.example.akiyoshi.albumsole.models.PictureLab;
import com.example.akiyoshi.albumsole.models.Screen;
import com.yayandroid.parallaxrecyclerview.ParallaxRecyclerView;
import com.yayandroid.parallaxrecyclerview.ParallaxViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FoldersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FoldersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FoldersFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ParallaxRecyclerView memoryRecycleView;
    FoldersFragment.MemoryAdapter adapter = null;
    private int widthScreen;
    private int heigthScreen;

    public FolderItemFragment folderItemFragment = null;

    public FoldersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FoldersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FoldersFragment newInstance(String param1, String param2) {
        FoldersFragment fragment = new FoldersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folders, container, false);
        memoryRecycleView = (ParallaxRecyclerView) view.findViewById(R.id.memory_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(memoryRecycleView.getContext(),
                linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getContext().getResources().getDrawable(R.drawable.foldes_rcv_divider));
        memoryRecycleView.addItemDecoration(dividerItemDecoration);
        memoryRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        memoryRecycleView.setHasFixedSize(true);
        memoryRecycleView.setItemViewCacheSize(100);
        memoryRecycleView.setDrawingCacheEnabled(true);
        memoryRecycleView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        getWithAndHeightOfScreen(getActivity());
        UpdateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //UpdateUI();
    }

    public void UpdateUI() {
        if (adapter == null) {
            List<String> list = PictureLab.getInstance(getActivity()).getPictureAlbum().getListKey();
            if (list != null) {
                adapter = new FoldersFragment.MemoryAdapter(list);
                memoryRecycleView.setAdapter(adapter);
            }
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    public void changeUiAfterRemove(int size, String nameFolder){
        int idx = adapter.list.indexOf(nameFolder);
        if(size == 0){
            adapter.list.remove(nameFolder);
            adapter.notifyItemRemoved(idx);
        }else{
            adapter.notifyItemChanged(idx);
        }

    }

    public void getWithAndHeightOfScreen(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        widthScreen = displayMetrics.widthPixels;
        heigthScreen = displayMetrics.heightPixels;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    public class MemoryHolder extends ParallaxViewHolder implements View.OnClickListener {
        TextView txtMemory;
        ImageView imgMemory, imgMemory2;
        TextView txtItemCount;
        String album;
        int prePic = -1;
        Timer timer;
        boolean isRunTimer = true;

        public MemoryHolder(LayoutInflater inflater, ViewGroup viewGroup) {
            super(inflater.inflate(R.layout.raw_recycler_view_card, viewGroup, false));
            txtMemory = itemView.findViewById(R.id.memory_txt);
            imgMemory = itemView.findViewById(R.id.memory_img);
            txtItemCount = itemView.findViewById(R.id.memory_txt_items);
            itemView.setOnClickListener(this);
        }

        public void Bind(String album) {
            this.album = album;
            String[] str = album.split("/");
            txtMemory.setText(str[str.length - 1]);
            int size = PictureLab.getInstance(getActivity()).getPictureAlbum().getMapPictureInAlbum(album).size();
            txtItemCount.setText(size + " items");

            List<Picture> pictures = new ArrayList<>(PictureLab.getInstance(getActivity()).getPictureAlbum().getMapPictureInAlbum(album).values());

            Picture picture = pictures.get(0);
            Glide.with(getActivity())
                    .load(picture.getPath())
                    .transition(withCrossFade())
                    .into(imgMemory);

        }

        @Override
        public int getParallaxImageId() {
            return R.id.memory_img;
        }

        @Override
        public void onClick(View v) {
            Log.i("Folder click", "click");
            MainActivity.nowScreen = Screen.FOLDER_ITEM;
            folderItemFragment = FolderItemFragment.newInstance(album);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.add(R.id.folders_container, folderItemFragment, "FOLDER_ITEM");
            transaction.addToBackStack("FOLDER_ITEM");
            transaction.commit();
        }
    }

    public class MemoryAdapter extends RecyclerView.Adapter<MemoryHolder> {
        List<String> list;

        public MemoryAdapter(List<String> list) {
            this.list = list;
        }

        @Override
        public MemoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

            return new MemoryHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(MemoryHolder holder, int position) {
            holder.Bind(list.get(position));
            holder.getBackgroundImage().reuse();
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }


    public static int dpToPx(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static float pxToDp(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }
}
