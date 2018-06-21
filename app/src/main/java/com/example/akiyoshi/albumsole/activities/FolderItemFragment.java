package com.example.akiyoshi.albumsole.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArraySet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.akiyoshi.albumsole.R;
import com.example.akiyoshi.albumsole.models.Picture;
import com.example.akiyoshi.albumsole.models.PictureAreaItem;
import com.example.akiyoshi.albumsole.models.PictureLab;
import com.example.akiyoshi.albumsole.models.RcvItemType;
import com.example.akiyoshi.albumsole.models.SpaceItemDecorator;
import com.example.akiyoshi.albumsole.models.SpannedGridLayoutManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FolderItemFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FolderItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FolderItemFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String folder;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    PictureAreaAdapter picAdapter;
    RecyclerView rcvFolderItem;
    int numRowsOfRcv = 0;

    public static final int REQUEST_CODE_IN_VIEW_DETAIL = 222;

    public FolderItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment FolderItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FolderItemFragment newInstance(String param1) {
        FolderItemFragment fragment = new FolderItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            folder = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_folder_item, container, false);

        MainActivity.listFolderItem = new ArrayList<>();
        InitListPicture();
        Log.i("FolderItem", "" + MainActivity.listFolderItem.size());
        int heightOfRow = Math.round(MainActivity.widthScreen * 1.0f / MainActivity.rowInGrid);
        int heightOfRcv = (heightOfRow * numRowsOfRcv);
        rcvFolderItem = (RecyclerView) view.findViewById(R.id.rcvFolderItem);
        rcvFolderItem.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightOfRcv));
        picAdapter = new PictureAreaAdapter(getContext(), MainActivity.listFolderItem, REQUEST_CODE_IN_VIEW_DETAIL);


        if (MainActivity.listFolderItem.size() == 1) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),MainActivity.rowInGrid);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {return 2;
                }
            });
            rcvFolderItem.setLayoutManager(gridLayoutManager);
        } else {
            rcvFolderItem.setLayoutManager(new SpannedGridLayoutManager(
                    new SpannedGridLayoutManager.GridSpanLookup() {
                        @Override
                        public SpannedGridLayoutManager.SpanInfo getSpanInfo(int position) {
                            switch (picAdapter.getItemViewType(position)) {
                                case 0:
                                    return new SpannedGridLayoutManager.SpanInfo(MainActivity.rowInGrid, 1);
                                case 2:
                                    return new SpannedGridLayoutManager.SpanInfo(2, 2);
                                default:
                                    return new SpannedGridLayoutManager.SpanInfo(1, 1);
                            }
                        }
                    },
                    MainActivity.rowInGrid /* Three columns */,
                    1f /* We want our items to be 1:1 ratio */));
        }

        int spacingInPixels = dpToPx(MainActivity.itemMargin, getActivity());
        rcvFolderItem.addItemDecoration(new SpaceItemDecorator(spacingInPixels));

        rcvFolderItem.setHasFixedSize(true);
        rcvFolderItem.setItemViewCacheSize(100);
        rcvFolderItem.setDrawingCacheEnabled(true);
        rcvFolderItem.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        ViewCompat.setNestedScrollingEnabled(rcvFolderItem, false);
        rcvFolderItem.setAdapter(picAdapter);
        String[] str = folder.split("/");
        String f = str[str.length - 1];
        ((MainActivity) getActivity()).setTitle(f);
        FolderManagerFragment.folderToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        return view;

    }

    void InitListPicture() {
        int indexPic = 0;
        List<Picture> listPictureInFolder = new ArrayList<>(PictureLab.getInstance(getActivity()).getPictureAlbum().getMapPictureInAlbum(folder).values());
        for (int j = 0; j < listPictureInFolder.size(); j++) {
            Picture picTemp = listPictureInFolder.get(j);
            indexPic++;
            if (indexPic == 1) {
                MainActivity.listFolderItem.add(new PictureAreaItem(folder, picTemp, RcvItemType.FIRST_PIC));
                numRowsOfRcv += 2;
            } else {
                MainActivity.listFolderItem.add(new PictureAreaItem(folder, picTemp, RcvItemType.NORMAL));
                if ((indexPic+3) % (MainActivity.rowInGrid) == 1 && indexPic > (MainActivity.rowInGrid * 2 - 3))
                    numRowsOfRcv++;
            }

        }
    }

    public String[] deleteImageFrontUI(){
        List<Integer> list = ReadData();
        Log.d("DELETE", "Size of list " + list.size());
        RemoveData();
        int size = -1;
        String nameFolder = null;
        if(list != null){
            int n = list.size();
            nameFolder = picAdapter.getFolderName();
            for(int i = 0; i < n; i++){
                Log.d("DELETE", "delete id " + list.get(i));
                size = picAdapter.deleteItem(list.get(i));
            }
        }
        String[] arr = new String[2];
        arr[0] = size + "";
        arr[1] = nameFolder;

        return arr;
    }

    public void RemoveData(){
        SharedPreferences myPrefs = getActivity().getSharedPreferences("listItemDelete",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.remove("id");
        editor.commit();
    }

    public List<Integer> ReadData(){
        List<Integer> list = new ArrayList<Integer>();

        SharedPreferences myPrefs = getActivity().getSharedPreferences("listItemDelete", Activity.MODE_PRIVATE);
        Set<String> str = new ArraySet<>();
        str = myPrefs.getStringSet("id", null);
        Log.d("DELETE", "Size of set " + str.size());

        Iterator iterator = str.iterator();

        while (iterator.hasNext()){
            list.add(Integer.parseInt((String)iterator.next()));
        }

        return list;
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

    public static int dpToPx(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
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
