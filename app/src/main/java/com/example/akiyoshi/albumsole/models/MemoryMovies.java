package com.example.akiyoshi.albumsole.models;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class MemoryMovies extends DefaultHandler {
    List<MemoryMovieObject> listMovie;
    List<String> listImagePath;
    MemoryMovieObject movie;
    boolean currentElement = false;
    String currentValue = "";

    public void clear() {
        if (listImagePath != null && listMovie != null) {
            listMovie.clear();
            listImagePath.clear();
        }
    }

    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        currentElement = true;

        if (qName.equals("Movies")) {
            listMovie = new ArrayList<>();
        } else if (qName.equals("Movie")) {
            movie = new MemoryMovieObject();
        } else if (qName.equals("ListImagePath")) {
            listImagePath = new ArrayList<>();
        }

    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        currentElement = false;

        if (qName.equalsIgnoreCase("Name"))
            movie.setName(currentValue.trim());
        else if (qName.equalsIgnoreCase("Title"))
            movie.setTitle(currentValue.trim());
        else if (qName.equalsIgnoreCase("SubTitle"))
            movie.setSubTitle(currentValue.trim());
        else if (qName.equalsIgnoreCase("Sound"))
            movie.setSound(currentValue.trim());
        else if (qName.equalsIgnoreCase("TextHolder"))
            movie.setTextHolder(currentValue.trim());
        else if (qName.equalsIgnoreCase("Filter"))
            movie.setFilter(currentValue.trim());
        else if (qName.equalsIgnoreCase("Image"))
            listImagePath.add(currentValue.trim());
        else if (qName.equalsIgnoreCase("ListImagePath"))
            movie.setListImagePath(listImagePath);
        else if (qName.equalsIgnoreCase("Movie"))
            listMovie.add(movie);

        currentValue = "";
    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {

        if (currentElement) {
            currentValue = currentValue + new String(ch, start, length);
        }
    }


    public List<MemoryMovieObject> getListMovie() {
        return listMovie;
    }

    public void setListMovie(List<MemoryMovieObject> listMovie) {
        this.listMovie = listMovie;
    }
}
