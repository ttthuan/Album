package com.example.akiyoshi.albumsole.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArraySet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.akiyoshi.albumsole.R;
import com.example.akiyoshi.albumsole.models.Picture;
import com.example.akiyoshi.albumsole.models.PictureAreaItem;
import com.example.akiyoshi.albumsole.models.PictureLab;
import com.example.akiyoshi.albumsole.models.RcvItemType;
import com.example.akiyoshi.albumsole.models.SpaceItemDecorator;
import com.example.akiyoshi.albumsole.models.SpannedGridLayoutManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PictureAreaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PictureAreaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PictureAreaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Context context;
    private OnFragmentInteractionListener mListener;

    PictureAreaAdapter picAdapter;
    RecyclerView rcvPictureArea;
    NestedScrollView mainScrollView;
    ImageView imgHeader, imgHeader2;
    View fgImgHeader;
    RelativeLayout headerInScroll;
    int percentHeaderHeight = 34;
    Random random = new Random();
    boolean isRunTimer = true;
    boolean isPauseTimer = false;
    public static int prePic = -1;
    ImageView nav_menu_btn;
    Timer timer;
    int numRowsOfRcv = 0;
    int numRowsHeader = 0;
    int scrollYSave = 0;

    public static final int REQUEST_CODE_IN_VIEW_DETAIL = 111;

    public PictureAreaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PictureAreaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PictureAreaFragment newInstance(String param1, String param2) {
        PictureAreaFragment fragment = new PictureAreaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void deleteImageFrontUI() {
        List<Integer> list = ReadData();
        Log.d("DELETE", "Size of list " + list.size());
        RemoveData();
        if (list != null) {
            int n = list.size();
            for (int i = 0; i < n; i++) {
                Log.d("DELETE", "delete id " + list.get(i));
                picAdapter.deleteItem(list.get(i));
            }
        }
    }

    public void RemoveData() {
        SharedPreferences myPrefs = getActivity().getSharedPreferences("listItemDelete",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.remove("id");
        editor.commit();
    }

    public List<Integer> ReadData() {
        List<Integer> list = new ArrayList<Integer>();

        SharedPreferences myPrefs = getActivity().getSharedPreferences("listItemDelete", Activity.MODE_PRIVATE);
        Set<String> str = new ArraySet<>();
        str = myPrefs.getStringSet("id", null);

        if (str != null) {
            Log.d("DELETE", "Size of set " + str.size());

            Iterator iterator = str.iterator();

            while (iterator.hasNext()) {
                list.add(Integer.parseInt((String) iterator.next()));
            }
        }
        return list;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_picture_area, container, false);
        mainScrollView = (NestedScrollView) view.findViewById(R.id.mainScrollView);
        nav_menu_btn = (ImageView) view.findViewById(R.id.nav_menu_btn);
        nav_menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.drawer_layout.openDrawer(Gravity.START);
            }
        });
        imgHeader = (ImageView) view.findViewById(R.id.imgHeader);
        imgHeader2 = (ImageView) view.findViewById(R.id.imgHeader2);
        headerInScroll = (RelativeLayout) view.findViewById(R.id.headerInScroll);
        isPauseTimer = false;
        if (mainScrollView != null) {
            mainScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    if (scrollY > oldScrollY) {
                        //Log.i("Scroll position", "Scroll UP " + scrollY);
                        float parY = scrollY / 2.2f;
                        if (parY <= imgHeader.getHeight()) {
                            imgHeader.setY(-scrollY / 2.2f);
                            imgHeader2.setY(-scrollY / 2.2f);
                            fgImgHeader.setAlpha((float) scrollY / imgHeader.getHeight());
                        }
                    }
                    if (scrollY < oldScrollY) {
                        //Log.i("Scroll position", "Scroll DOWN " + scrollY);
                        float parY = scrollY / 2.2f;
                        if (parY <= imgHeader.getHeight()) {
                            imgHeader.setY(-scrollY / 2.2f);
                            imgHeader2.setY(-scrollY / 2.2f);
                            fgImgHeader.setAlpha((float) scrollY / imgHeader.getHeight());
                        }
                    }

                    if (scrollY == 0) {
                        //Log.i("Scroll position", "TOP SCROLL");
                    }

                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        //Log.i("Scroll position", "BOTTOM SCROLL");
                    }

                    scrollYSave = scrollY;
                }
            });
        }


        MainActivity.listPictureItem = new ArrayList<>();
        if (MainActivity.listPictureItem != null) {
            if (InitListPicture() > 0) {
                int hHeader = Math.round(MainActivity.heightScreen * percentHeaderHeight / 100);
                if (imgHeader.getMeasuredHeight() == 0) {
                    imgHeader.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, hHeader));
                    imgHeader2.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, hHeader));
                    headerInScroll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, hHeader));
                }
                fgImgHeader = view.findViewById(R.id.fgImgHeader);

                if (timer == null) {
                    ColorDrawable colorDrawable = new ColorDrawable(Color.WHITE);
                    colorDrawable.setBounds(0, 0, MainActivity.widthScreen, hHeader);
                    String prePath = "";
                    if (prePic == -1) {
                        int size = PictureLab.getInstance(getActivity()).getPictureMonth().getListAllPicture().size();
                        int num = 0;

                        if (size > 0) {
                            num = random.nextInt(size);

                            prePath = PictureLab.getInstance(getContext()).getPictureMonth().getListAllPicture().get(num).getPath();
                            Glide.with(context)
                                    .load(prePath)
                                    .apply(
                                            new RequestOptions().override(MainActivity.widthScreen, hHeader).centerCrop().placeholder(colorDrawable)
                                    ).transition(withCrossFade())
                                    .into(imgHeader);
                            Glide.with(context)
                                    .load(prePath)
                                    .apply(
                                            new RequestOptions().override(MainActivity.widthScreen, hHeader).centerCrop().placeholder(colorDrawable)
                                    ).transition(withCrossFade())
                                    .into(imgHeader2);
                        }
                    }

                    timer = new Timer("HeaderTimer");
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            while (isRunTimer) {
                                if (!isPauseTimer) {
                                    Log.i("HeaderTimer", "tick");
                                    int size = PictureLab.getInstance(getActivity()).getPictureMonth().getListAllPicture().size();

                                    if (size > 0) {
                                        Log.i("HeaderTimerSize", "" + size);
                                        int num = random.nextInt(size);
                                        prePic = num;
                                        Log.i("HeaderTimerNum", "" + num);

                                        String path = PictureLab.getInstance(getContext()).getPictureMonth().getListAllPicture().get(num).getPath();
                                        ShowImageAsyncTask showImageAsyncTask = new ShowImageAsyncTask(imgHeader, imgHeader2, MainActivity.widthScreen, hHeader, path, getActivity());
                                        showImageAsyncTask.execute(path);
                                    }
                                }
                                try {
                                    Thread.sleep(6000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, 0);
                } else {
                    ColorDrawable colorDrawable = new ColorDrawable(Color.WHITE);
                    colorDrawable.setBounds(0, 0, MainActivity.widthScreen, hHeader);
                    String prePath = "";
                    prePath = PictureLab.getInstance(getContext()).getPictureMonth().getListAllPicture().get(prePic).getPath();
                    Glide.with(context)
                            .load(prePath)
                            .apply(
                                    new RequestOptions().override(MainActivity.widthScreen, hHeader).centerCrop().placeholder(colorDrawable).dontAnimate()
                            ).transition(withCrossFade())
                            .into(imgHeader);
                    Glide.with(context)
                            .load(prePath)
                            .apply(
                                    new RequestOptions().override(MainActivity.widthScreen, hHeader).centerCrop().placeholder(colorDrawable).dontAnimate()
                            ).transition(withCrossFade())
                            .into(imgHeader2);
                }

                rcvPictureArea = (RecyclerView) view.findViewById(R.id.rcvPictureArea);

                Log.i("rcvPictureArea", "hei: " + rcvPictureArea.getMeasuredHeight());
                if (rcvPictureArea.getMeasuredHeight() == 0) {
                    Log.i("PicArea", "Header num: " + numRowsHeader);
                    int heightOfRow = Math.round(MainActivity.widthScreen * 1.0f / MainActivity.rowInGrid); // + (dpToPx(MainActivity.itemMargin, getActivity()) * 2)
                    int heightOfHeader = numRowsHeader * heightOfRow;
                    int heightOfRcv = (heightOfRow * numRowsOfRcv) + heightOfHeader;
                    rcvPictureArea.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightOfRcv));
                }
                picAdapter = new PictureAreaAdapter(context, MainActivity.listPictureItem, REQUEST_CODE_IN_VIEW_DETAIL);

                rcvPictureArea.setLayoutManager(new SpannedGridLayoutManager(
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
                        MainActivity.rowInGrid /* Num columns */,
                        1f /* We want our items to be 1:1 ratio */));


                int spacingInPixels = dpToPx(MainActivity.itemMargin, context);
                rcvPictureArea.addItemDecoration(new SpaceItemDecorator(spacingInPixels));
                rcvPictureArea.setHasFixedSize(true);
                rcvPictureArea.setItemViewCacheSize(100);
                rcvPictureArea.setDrawingCacheEnabled(true);
                rcvPictureArea.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                ViewCompat.setNestedScrollingEnabled(rcvPictureArea, false);
                rcvPictureArea.setAdapter(picAdapter);

            } else {
                // show message box
                Toast.makeText(getActivity(), "Please add more image in your device and restart this app later!", Toast.LENGTH_LONG).show();
            }
        }

        return view;

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    int InitListPicture() {
        Log.i("INITPIC", "START: " + Calendar.getInstance().getTime());
        MainActivity.listMonthly = PictureLab.getInstance(context).getPictureMonth().getListKey();
        String monthly;
        numRowsOfRcv = 0;
        numRowsHeader = 0;
        for (int i = 0; i < MainActivity.listMonthly.size(); i++) {
            numRowsHeader++;
            monthly = MainActivity.listMonthly.get(i);
            MainActivity.listPictureItem.add(new PictureAreaItem(monthly, null, RcvItemType.HEADER));
            int indexPic = 0;
            List<Picture> listPictureInMonth = PictureLab.getInstance(context).getPictureMonth().getListPictureInMonth(monthly);
            for (int j = 0; j < listPictureInMonth.size(); j++) {
                Picture picTemp = listPictureInMonth.get(j);
                indexPic++;
                if (indexPic == 1) {
                    MainActivity.listPictureItem.add(new PictureAreaItem(monthly, picTemp, RcvItemType.FIRST_PIC));
                    numRowsOfRcv += 2;
                } else {
//                    if ((indexPic+3) % (MainActivity.rowInGrid) == 1 && indexPic >= MainActivity.rowInGrid * 2 - 3)
//                        numRowsOfRcv++;

                    if (indexPic - 11 > 0 && (indexPic - 11) % 2 == 1 ){
                        numRowsOfRcv++;
                    }

                    MainActivity.listPictureItem.add(new PictureAreaItem(monthly, picTemp, RcvItemType.NORMAL));
                }
            }
            Log.d("SIZE_RCV", numRowsOfRcv +"");
        }


        Log.i("INITPIC", "END: " + Calendar.getInstance().getTime());
        return MainActivity.listMonthly.size();
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
        if (context instanceof MainActivity) {
            this.context = (MainActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        isPauseTimer = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        isPauseTimer = false;

        Log.i("Scroll Y", "" + scrollYSave);
        if (scrollYSave == 0) {
//            imgHeader.setY(0);
//            imgHeader2.setY(0);
//            fgImgHeader.setAlpha(0f);
        } else {
            imgHeader.setY(-scrollYSave / 2.2f);
            imgHeader2.setY(-scrollYSave / 2.2f);
            fgImgHeader.setAlpha((float) scrollYSave / imgHeader.getHeight());
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

    public static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            int type = parent.getChildViewHolder(view).getItemViewType();

            if (type == 2) {
                outRect.right = 0;
                outRect.bottom = space;
            } else {
                outRect.right = space;
                outRect.bottom = space;
            }
        }
    }

    public static int dpToPx(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
