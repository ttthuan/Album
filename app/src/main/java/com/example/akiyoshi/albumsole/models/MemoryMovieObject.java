package com.example.akiyoshi.albumsole.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MemoryMovieObject implements Serializable {
    String name;
    String title;
    String subTitle;
    String sound;
    String textHolder;
    String filter;
    List<String> listImagePath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getTextHolder() {
        return textHolder;
    }

    public void setTextHolder(String textHolder) {
        this.textHolder = textHolder;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public List<String> getListImagePath() {
        return listImagePath;
    }

    public void setListImagePath(List<String> listImagePath) {
        this.listImagePath = listImagePath;
    }

    public MemoryMovieObject(String name, String title, String subTitle, String sound, String textHolder, List<String> listImagePath) {
        this.name = name;
        this.title = title;
        this.subTitle = subTitle;
        this.sound = sound;
        this.textHolder = textHolder;
        this.listImagePath = listImagePath;
    }

    public MemoryMovieObject() {
        this.name = "";
        this.title = "";
        this.subTitle = "";
        this.sound = "";
        this.textHolder = "";
        this.listImagePath = null;
    }

}
