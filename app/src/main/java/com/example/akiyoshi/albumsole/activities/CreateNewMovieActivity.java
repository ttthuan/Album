package com.example.akiyoshi.albumsole.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.akiyoshi.albumsole.R;
import com.example.akiyoshi.albumsole.models.MemoryMovieObject;
import com.example.akiyoshi.albumsole.models.Picture;
import com.example.akiyoshi.albumsole.models.TabViewPagerAdapter;
import com.example.akiyoshi.albumsole.models.ViewPagerCustomDuration;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class CreateNewMovieActivity extends AppCompatActivity {
    private static int numSelected;
    ViewPagerCustomDuration movie_create_pager;
    TabLayout movie_create_tab;
    private static Button btn_create_movie;
    private static TextView txt_num_selected;
    private static List<Picture> listSelected;
    MemoryMovieObject result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_movie);

        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CreateMovieImageFragment(), "Tất cả");
        adapter.addFragment(new CreateMoviAlbumImageFragment(), "Thư mục");
//        adapter.addFragment(new CreateMovieImageFragment(), "Đã chọn");

        movie_create_pager = findViewById(R.id.movie_create_pager);
        movie_create_tab = findViewById(R.id.movie_create_tab);
        movie_create_pager.setAdapter(adapter);
        movie_create_tab.setupWithViewPager(movie_create_pager, true);
        movie_create_pager.setScrollDurationFactor(2);

        listSelected = new ArrayList<>();
        txt_num_selected = findViewById(R.id.txt_num_selected);
        btn_create_movie = findViewById(R.id.btn_create_movie);
        btn_create_movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeXml();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", result);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        numSelected = 0;
    }


    public static void addSelected(Picture picture) {
        numSelected++;
        listSelected.add(picture);
        txt_num_selected.setText(numSelected + "");
        if (numSelected > 0 && !btn_create_movie.isEnabled()) {
            btn_create_movie.setAlpha(1f);
            btn_create_movie.setEnabled(true);
        }
    }

    public static void removeSelected(Picture picture) {
        numSelected--;
        listSelected.remove(picture);
        txt_num_selected.setText(numSelected + "");
        if (numSelected == 0) {
            btn_create_movie.setAlpha(0.4f);
            btn_create_movie.setEnabled(false);
        }
    }

    public void writeXml() {
        InputStream is = null;
        try {
            File file = new File(getExternalFilesDir(null), "Movies.xml");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);
            Element root = document.getDocumentElement();
            result = new MemoryMovieObject();

            // Movie
            Element newMovie = document.createElement("Movie");
            Element name = document.createElement("Name");
            name.appendChild(document.createTextNode("New Movie"));
            newMovie.appendChild(name);
            result.setName("New Movie");

            // Name
            Element title = document.createElement("Title");
            title.appendChild(document.createTextNode("New Movie"));
            newMovie.appendChild(title);
            result.setTitle("New Movie");

            // Title
            Element subTitle = document.createElement("SubTitle");
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
            subTitle.appendChild(document.createTextNode(dateFormat.format(Calendar.getInstance().getTime())));
            newMovie.appendChild(subTitle);
            result.setSubTitle(dateFormat.format(Calendar.getInstance().getTime()));
            // Sound
            Element sound = document.createElement("Sound");
            sound.appendChild(document.createTextNode("1"));
            newMovie.appendChild(sound);
            result.setSound("1");
            // TextHolder
            Element textHolder = document.createElement("TextHolder");
            textHolder.appendChild(document.createTextNode("1"));
            newMovie.appendChild(textHolder);
            result.setTextHolder("1");
            // Filter
            Element filter = document.createElement("Filter");
            filter.appendChild(document.createTextNode("1"));
            newMovie.appendChild(filter);
            result.setFilter("1");
            //ListImagePath
            Element listImagePath = document.createElement("ListImagePath");
            List<String> listPath = new ArrayList<>();
            for (int i = 0; i < listSelected.size(); i++) {
                //Image
                Element image = document.createElement("Image");
                image.appendChild(document.createTextNode(listSelected.get(i).getPath()));
                listImagePath.appendChild(image);
                listPath.add(listSelected.get(i).getPath());
            }
            newMovie.appendChild(listImagePath);
            result.setListImagePath(listPath);

            root.appendChild(newMovie);
            DOMSource source = new DOMSource(document);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = null;
            transformer = transformerFactory.newTransformer();
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // NOOP
                }
            }
        }
    }
}