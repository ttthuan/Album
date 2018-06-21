package com.example.akiyoshi.albumsole.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.akiyoshi.albumsole.R;
import com.example.akiyoshi.albumsole.models.MemoryMovieObject;
import com.example.akiyoshi.albumsole.models.Picture;
import com.example.akiyoshi.albumsole.models.PictureEditMovie;
import com.example.akiyoshi.albumsole.models.ThumbsManager;
import com.zomato.photofilters.imageprocessors.Filter;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class EditMovieActivity extends AppCompatActivity implements ThumbCallBack, EditSaveCallBack {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static ArrayList<PictureEditMovie> listPictureEdit;
    public static ArrayList<PictureEditMovie> listPictureEditTestFilter;
    public static AppBarLayout edit_movie_tool_appbar;
    public static ImageButton btn_edit_movie_filter;
    public static ImageButton btn_close_edit_movie;
    public static FloatingActionButton fabEditPlay;
    public static TextView txt_edit_mode;
    public static Button btn_edit_movie;
    static FragmentManager fragmentManagerMain;
    static EditMovieImageAdapter picAdapter;
    public static String preFilter = "";
    public static boolean isFilter = false;
    public static EditSaveCallBack editSaveCallBack;
    int position = -1;
    Context context;
    boolean isEdited = false;

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    public static Intent newIntent(Context context, ArrayList<PictureEditMovie> listPictureEdit, int position) {
        Intent intent = new Intent(context, EditMovieActivity.class);
        intent.putExtra(ARG_PARAM1, listPictureEdit);
        intent.putExtra(ARG_PARAM2, position);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_movie);
        if (getIntent() != null) {
            listPictureEdit = (ArrayList<PictureEditMovie>) getIntent().getSerializableExtra(ARG_PARAM1);
            position = getIntent().getIntExtra(ARG_PARAM2, 0);
        }
        context = this;
        listPictureEditTestFilter = new ArrayList<>();
        editSaveCallBack = this;
        picAdapter = new EditMovieImageAdapter(this, listPictureEdit, editSaveCallBack);
        RecyclerView rcvEditMovie = findViewById(R.id.rcvEditMovie);
        rcvEditMovie.setLayoutManager(new LinearLayoutManager(this));
        rcvEditMovie.setHasFixedSize(true);
        rcvEditMovie.setItemViewCacheSize(100);
        rcvEditMovie.setDrawingCacheEnabled(true);
        rcvEditMovie.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        ViewCompat.setNestedScrollingEnabled(rcvEditMovie, false);
        rcvEditMovie.setAdapter(picAdapter);

        btn_edit_movie_filter = findViewById(R.id.btn_edit_movie_filter);

        btn_edit_movie_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment filterEditMovieFragment = fragmentManager.findFragmentByTag("FILTER_EDIT");
                if (filterEditMovieFragment == null) {
                    filterEditMovieFragment = FilterEditMovieFragment.newInstance(listPictureEdit.get(0));
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.replace(R.id.edit_filter_container, filterEditMovieFragment, "FILTER_EDIT");
                    transaction.addToBackStack("FILTER_EDIT");
                    transaction.commit();
                } else {
                    fragmentManager.popBackStackImmediate("FILTER_EDIT", 0);
                }
                isFilter = true;
            }
        });

        preFilter = listPictureEdit.get(0).getFilter();
        fragmentManagerMain = getSupportFragmentManager();
        fabEditPlay = findViewById(R.id.fabEditPlay);
        txt_edit_mode = findViewById(R.id.txt_edit_mode);
        btn_close_edit_movie = findViewById(R.id.btn_close_edit_movie);
        btn_edit_movie = findViewById(R.id.btn_edit_movie);
        edit_movie_tool_appbar = findViewById(R.id.edit_movie_tool_appbar);

        fabEditPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = PlayMovieMemoryActivity.newIntent(context, listPictureEdit, 0);
                startActivity(intent);
            }
        });

        btn_close_edit_movie = findViewById(R.id.btn_close_edit_movie);
        btn_close_edit_movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEdited) {
                    MemoryMovieObject result = new MemoryMovieObject();
                    result.setName("Movie");
                    result.setTitle(listPictureEdit.get(0).getTitle());
                    result.setSubTitle(listPictureEdit.get(0).getSubtitle());
                    result.setSound(listPictureEdit.get(0).getSound());
                    result.setTextHolder(listPictureEdit.get(0).getHolder());
                    result.setFilter(listPictureEdit.get(0).getFilter());
                    List<String> listPath = new ArrayList<>();
                    for (int i = 0; i < listPictureEdit.size(); i++) {
                        listPath.add(listPictureEdit.get(i).getPath());
                    }
                    result.setListImagePath(listPath);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", result);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onThumbClick(Filter filter) {
        for (int j = 0; j < ThumbsManager.filters.size(); j++) {
            if (ThumbsManager.filters.get(j).getName() == filter.getName()) {
                for (int i = 0; i < listPictureEdit.size(); i++) {
                    listPictureEdit.get(i).setFilter(Integer.toString(j));
                }
                picAdapter.notifyDataSetChanged();
            }
        }
    }

    public static void changeFilter(boolean isChange) {
        if (!isChange) {
            for (int i = 0; i < listPictureEdit.size(); i++) {
                listPictureEdit.get(i).setFilter(preFilter);
            }
            picAdapter.notifyDataSetChanged();

        } else {
            preFilter = listPictureEdit.get(0).getFilter();
            editSaveCallBack.onSaveFilter();

        }
    }

    public static void changeTextHolder(String textHolder, String title, String subTitle) {
        for (int i = 0; i < listPictureEdit.size(); i++) {
            listPictureEdit.get(i).setHolder(textHolder);
            listPictureEdit.get(i).setTitle(title);
            listPictureEdit.get(i).setSubtitle(subTitle);
        }
        picAdapter.notifyDataSetChanged();
        editSaveCallBack.onSaveTextHolder();

    }

    public static void setFilterClick() {
        btn_edit_movie_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = fragmentManagerMain;
                Fragment filterEditMovieFragment = fragmentManager.findFragmentByTag("FILTER_EDIT");
                if (filterEditMovieFragment == null) {
                    filterEditMovieFragment = FilterEditMovieFragment.newInstance(listPictureEdit.get(0));
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.replace(R.id.edit_filter_container, filterEditMovieFragment, "FILTER_EDIT");
                    transaction.addToBackStack("FILTER_EDIT");
                    transaction.commit();
                } else {
                    fragmentManager.popBackStackImmediate("FILTER_EDIT", 0);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!isFilter)
            finish();
        else {
            if (isEdited) {
                MemoryMovieObject result = new MemoryMovieObject();
                result.setName("Movie");
                result.setTitle(listPictureEdit.get(0).getTitle());
                result.setSubTitle(listPictureEdit.get(0).getSubtitle());
                result.setSound(listPictureEdit.get(0).getSound());
                result.setTextHolder(listPictureEdit.get(0).getHolder());
                result.setFilter(listPictureEdit.get(0).getFilter());
                List<String> listPath = new ArrayList<>();
                for (int i = 0; i < listPictureEdit.size(); i++) {
                    listPath.add(listPictureEdit.get(i).getPath());
                }
                result.setListImagePath(listPath);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", result);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }
        super.onBackPressed();
    }

    public void saveDataXML(int save) {
        try {
            File file = new File(getExternalFilesDir(null), "Movies.xml");
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(file);
            if (save == 0) {
                Node nodes = doc.getElementsByTagName("Filter").item(position);
                nodes.setTextContent(listPictureEdit.get(0).getFilter());
            } else if (save == 1) {
                Node nodes = doc.getElementsByTagName("TextHolder").item(position);
                nodes.setTextContent(listPictureEdit.get(0).getHolder());

                nodes = doc.getElementsByTagName("Title").item(position);
                nodes.setTextContent(listPictureEdit.get(0).getTitle());

                nodes = doc.getElementsByTagName("SubTitle").item(position);
                nodes.setTextContent(listPictureEdit.get(0).getSubtitle());
            }
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            // initialize StreamResult with File object to save to file
            StreamResult result = new StreamResult(file);
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);

            isEdited = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveFilter() {
        saveDataXML(0);
    }

    @Override
    public void onSaveTextHolder() {
        saveDataXML(1);
    }

    @Override
    public void onOpenEditTextHolder() {
//         Toast.makeText(this, "edit holder", Toast.LENGTH_SHORT).show();
        Intent intent = EditMovieTextHolderActivity.newIntent(this, listPictureEdit, position);
        startActivity(intent);
    }
}
