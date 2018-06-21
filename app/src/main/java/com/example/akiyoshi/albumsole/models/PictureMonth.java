package com.example.akiyoshi.albumsole.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Acer on 03/27/2018.
 */

public class PictureMonth extends PictureAbstract{

    public PictureMonth(){
       pictures = new TreeMap<String, Map<Integer, Picture>>(new MyMonthCompare());
    }

    public Map<Integer, Picture> getMapPictureInMonth(String month){
        return pictures.get(month);
    }

    public Picture getAPictureInMonth(String month, int id){
        return getMapPictureInMonth(month).get(id);
    }

    public List<Picture> getListPictureInMonth(String month) {
        return new ArrayList<>(getMapPictureInMonth(month).values());
    }

    @Override
    public String getType(Picture picture) {
        return picture.getMonth();
    }

}
