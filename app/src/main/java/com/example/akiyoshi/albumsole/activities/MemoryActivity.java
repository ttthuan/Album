package com.example.akiyoshi.albumsole.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.akiyoshi.albumsole.R;
import com.example.akiyoshi.albumsole.models.MemoryMovieObject;
import com.example.akiyoshi.albumsole.models.MemoryMovies;
import com.example.akiyoshi.albumsole.models.MemorySoundObject;
import com.example.akiyoshi.albumsole.models.MemorySoundService;
import com.example.akiyoshi.albumsole.models.NextSound;
import com.example.akiyoshi.albumsole.models.PictureAlbum;
import com.example.akiyoshi.albumsole.models.PictureEditMovie;
import com.example.akiyoshi.albumsole.models.PictureLab;
import com.example.akiyoshi.albumsole.models.TabViewPagerAdapter;
import com.example.akiyoshi.albumsole.models.ThumbsManager;
import com.example.akiyoshi.albumsole.models.ViewPagerCustomDuration;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import static android.support.v4.view.ViewPager.SCROLL_STATE_DRAGGING;
import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;
import static android.support.v4.view.ViewPager.SCROLL_STATE_SETTLING;

public class MemoryActivity extends AppCompatActivity implements NextSound {
    ViewPagerCustomDuration pagerMemory;
    TabLayout tabDotsMemory;
    Intent svc;
    public static List<MemorySoundObject> listSound;
    public static ArrayList<PictureEditMovie> listEdit;
    public static List<MemoryMovieObject> listMovie;
    MemorySoundObject nowPlaying;
    public static NextSound nextSound;
    public static int now = 0;
    Context context;
    FloatingActionButton fabOpen, fabEdit, fabPlay;
    private boolean isFABOpen = false;
    private boolean isFABHide = false;
    TabViewPagerAdapter adapter;
    int positionEditInList = 0;
    int positionEditInTab = 0;


    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        nextSound = this;
        context = this;
        pagerMemory = (ViewPagerCustomDuration) findViewById(R.id.pagerMemory);
        tabDotsMemory = (TabLayout) findViewById(R.id.tabDotsMemory);
        listEdit = new ArrayList<>();
        adapter = new TabViewPagerAdapter(getSupportFragmentManager());

        InitListSound();
        nowPlaying = listSound.get(0);
        svc = new Intent(this, MemorySoundService.class);

        ImageButton btnMore = (ImageButton) findViewById(R.id.btnMore);
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                popup.setGravity(Gravity.END);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_memory, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.movie_create:
                                Intent intent = new Intent(context, CreateNewMovieActivity.class);
                                startActivityForResult(intent, 1);
                                break;
                            case R.id.movie_delete:

                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

        fabOpen = (FloatingActionButton) findViewById(R.id.fabOpen);
        fabEdit = (FloatingActionButton) findViewById(R.id.fabEdit);
        fabPlay = (FloatingActionButton) findViewById(R.id.fabPlay);

        fabOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pageSelected = pagerMemory.getCurrentItem();
                List<String> listImagePath = listMovie.get(listMovie.size() - pageSelected - 1).getListImagePath();
                String filter = listMovie.get(listMovie.size() - pageSelected - 1).getFilter();
                String holder = listMovie.get(listMovie.size() - pageSelected - 1).getTextHolder();
                String title = listMovie.get(listMovie.size() - pageSelected - 1).getTitle();
                String subTitle = listMovie.get(listMovie.size() - pageSelected - 1).getSubTitle();
                String sound = listMovie.get(listMovie.size() - pageSelected - 1).getSound();
                listEdit.clear();
                for (int i = 0; i < listImagePath.size(); i++) {
                    PictureEditMovie pictureEditMovie = new PictureEditMovie();
                    pictureEditMovie.setPath(listImagePath.get(i));
                    pictureEditMovie.setFilter(filter);
                    pictureEditMovie.setHolder(holder);
                    pictureEditMovie.setTitle(title);
                    pictureEditMovie.setSubtitle(subTitle);
                    pictureEditMovie.setSound(sound);
                    listEdit.add(pictureEditMovie);
                }
                positionEditInList = listMovie.size() - pageSelected - 1;
                positionEditInTab = pageSelected;
                Intent intent = EditMovieActivity.newIntent(context, listEdit, listMovie.size() - pageSelected - 1);
                startActivityForResult(intent, 2);
            }
        });
        fabPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pageSelected = pagerMemory.getCurrentItem();
                List<String> listImagePath = listMovie.get(listMovie.size() - pageSelected - 1).getListImagePath();
                String filter = listMovie.get(listMovie.size() - pageSelected - 1).getFilter();
                String holder = listMovie.get(listMovie.size() - pageSelected - 1).getTextHolder();
                String title = listMovie.get(listMovie.size() - pageSelected - 1).getTitle();
                String subTitle = listMovie.get(listMovie.size() - pageSelected - 1).getSubTitle();
                String sound = listMovie.get(listMovie.size() - pageSelected - 1).getSound();
                listEdit.clear();
                for (int i = 0; i < listImagePath.size(); i++) {
                    PictureEditMovie pictureEditMovie = new PictureEditMovie();
                    pictureEditMovie.setPath(listImagePath.get(i));
                    pictureEditMovie.setFilter(filter);
                    pictureEditMovie.setHolder(holder);
                    pictureEditMovie.setTitle(title);
                    pictureEditMovie.setSubtitle(subTitle);
                    pictureEditMovie.setSound(sound);
                    listEdit.add(pictureEditMovie);
                }
                Intent intent = PlayMovieMemoryActivity.newIntent(context, listEdit, 0);
                startActivity(intent);
            }
        });

        InitListMovie();
        if (listMovie != null) {
            if (listMovie.size() > 0)
                for (int i = listMovie.size() - 1; i >= 0; i--) {
                    MemoryTabItemFragment fragMemory = MemoryTabItemFragment.newInstance(listMovie.get(i));
                    adapter.addFragment(fragMemory, "");
                }
            else
                adapter.addFragment(new MemoryBlankFragment(), "");
        } else {
            adapter.addFragment(new MemoryBlankFragment(), "");
        }
        adapter.addFragment(new MemoryBlankFragment(), "");
        pagerMemory.setAdapter(adapter);
        pagerMemory.setPageMargin(10);
        pagerMemory.setPageMarginDrawable(android.R.color.black);
        tabDotsMemory.setupWithViewPager(pagerMemory, true);
//        pagerMemory.setPageTransformer(false, new ViewPager.PageTransformer() {
//            @Override
//            public void transformPage(@NonNull View page, float position) {
//                Log.i("Pager", "Swipe");
//            }
//        });
        pagerMemory.setScrollDurationFactor(2);
        pagerMemory.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                Log.i("Pager", "Swipe" + state);
                Animation anim;
                switch (state) {
                    case SCROLL_STATE_IDLE:
                        if (isFABHide) {
                            anim = AnimationUtils.loadAnimation(context, R.anim.memory_fab_scale_show);
                            anim.setFillAfter(true);
                            fabPlay.startAnimation(anim);
                            fabEdit.startAnimation(anim);
                            fabOpen.startAnimation(anim);

                            isFABHide = false;
                        }
                        break;
                    case SCROLL_STATE_DRAGGING:
                        if (isFABOpen) {
                            closeFABMenu();
                        }
                        anim = AnimationUtils.loadAnimation(context, R.anim.memory_fab_scale_hide);
                        anim.setFillAfter(true);
                        fabPlay.startAnimation(anim);
                        fabEdit.startAnimation(anim);
                        fabOpen.startAnimation(anim);
                        isFABHide = true;
                        break;
                    case SCROLL_STATE_SETTLING:
                        if (isFABOpen) {
                            closeFABMenu();
                        }
                        anim = AnimationUtils.loadAnimation(context, R.anim.memory_fab_scale_show);
                        anim.setFillAfter(true);
                        fabPlay.startAnimation(anim);
                        fabEdit.startAnimation(anim);
                        fabOpen.startAnimation(anim);
                        isFABHide = false;
                        break;
                }

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(svc);

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(svc);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                MemoryMovieObject result = (MemoryMovieObject) data.getSerializableExtra("result");
                listMovie.add(result);
                adapter = new TabViewPagerAdapter(getSupportFragmentManager());
                if (listMovie != null) {
                    for (int i = listMovie.size() - 1; i >= 0; i--) {
                        MemoryTabItemFragment fragMemory = MemoryTabItemFragment.newInstance(listMovie.get(i));
                        adapter.addFragment(fragMemory, "");
                    }
                }
                pagerMemory.setAdapter(adapter);
                tabDotsMemory.setupWithViewPager(pagerMemory, true);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                Log.i("EDIT RESULT", "OK");
                MemoryMovieObject result = (MemoryMovieObject) data.getSerializableExtra("result");
                listMovie.remove(positionEditInList);
                listMovie.add(positionEditInList, result);
                adapter = new TabViewPagerAdapter(getSupportFragmentManager());
                if (listMovie != null) {
                    for (int i = listMovie.size() - 1; i >= 0; i--) {
                        MemoryTabItemFragment fragMemory = MemoryTabItemFragment.newInstance(listMovie.get(i));
                        adapter.addFragment(fragMemory, "");
                    }
                }
                pagerMemory.setAdapter(adapter);
                tabDotsMemory.setupWithViewPager(pagerMemory, true);
                tabDotsMemory.getTabAt(positionEditInTab).select();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    void InitListSound() {
        listSound = new ArrayList<>();
        listSound.add(new MemorySoundObject("Beautiful Dawn", "Music/BeautifulDawn.mp3"));
        listSound.add(new MemorySoundObject("Cinematic Ambient", "Music/CinematicAmbient.mp3"));
        listSound.add(new MemorySoundObject("Cinematic Documentary", "Music/CinematicDocumentary.mp3"));
        listSound.add(new MemorySoundObject("Cinematic Piano Ambient", "Music/CinematicPianoAmbient.mp3"));
        listSound.add(new MemorySoundObject("Cinematic Trailer", "Music/CinematicTrailer.mp3"));
        listSound.add(new MemorySoundObject("Emotional Documentary", "Music/EmotionalDocumentary.mp3"));
        listSound.add(new MemorySoundObject("Emotional Piano", "Music/EmotionalPiano.mp3"));
        listSound.add(new MemorySoundObject("Epic Emotional", "Music/EpicEmotional.mp3"));
        listSound.add(new MemorySoundObject("Inspirational Acoustic", "Music/InspirationalAcoustic.mp3"));
        listSound.add(new MemorySoundObject("Inspirational Day", "Music/InspirationalDay.mp3"));
    }

    void InitListMovie() {
        File file = new File(getExternalFilesDir(null), "Movies.xml");
        AssetManager assetManager;
        InputStream is = null;
        OutputStream os = null;
//        file.delete();
        if (!file.exists()) {
            try {
                assetManager = getApplicationContext().getAssets();
                is = assetManager.open("Movie/Movies.xml");
                File outFile = new File(getExternalFilesDir(null), "Movies.xml");

                os = new FileOutputStream(outFile);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }

                Log.i("Copy movie", "DONE");
            } catch (IOException ioe) {
                Log.e("Error copy Movie", ioe.getMessage());
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
            }

        }


        try {
            file = new File(getExternalFilesDir(null), "Movies.xml");
            Log.i("File path", file.getPath());
            is = new FileInputStream(file);
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();

            MemoryMovies readMoviesXML = new MemoryMovies();
            readMoviesXML.clear();
            xr.setContentHandler(readMoviesXML);
            InputSource inStream = new InputSource(is);
            xr.parse(inStream);
            if (listMovie != null)
                listMovie.clear();
            listMovie = readMoviesXML.getListMovie();

        } catch (Exception e) {
            Log.e("Error Init Movie", e.getMessage());
        }
    }

    @Override
    public void Next() {
        if (now + 1 >= listSound.size())
            now = 0;
        else now++;
        nowPlaying = listSound.get(now);
        svc = new Intent(this, MemorySoundService.class);
        startService(svc);
    }

    private void showFABMenu() {
        isFABOpen = true;
        fabOpen.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fabOpen)));
        fabOpen.animate().rotation(180).setInterpolator(new AccelerateInterpolator()).setDuration(150);
        fabEdit.animate().translationY(-getResources().getDimension(R.dimen.fab_edit)).setInterpolator(new AccelerateInterpolator()).setDuration(150);
        fabPlay.animate().translationY(-getResources().getDimension(R.dimen.fab_play)).setInterpolator(new AccelerateInterpolator()).setDuration(150);
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fabOpen.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fabClose)));
        fabOpen.animate().rotation(0).setInterpolator(new DecelerateInterpolator()).setDuration(150);
        fabEdit.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).setDuration(150);
        fabPlay.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).setDuration(150);
    }

}

