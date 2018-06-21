package com.example.akiyoshi.albumsole.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PictureAlbum extends PictureAbstract {

    public PictureAlbum (){
        pictures = new TreeMap<String, Map<Integer, Picture>>();
    }

    public Map<Integer, Picture> getMapPictureInAlbum(String folder){
        return pictures.get(folder);
    }

    public Picture getPictureInAlbum(String folder, int id){
        return getMapPictureInAlbum(folder).get(id);
    }


    protected List<Picture> getListPicturesInAlbum(String argument) {
        return new ArrayList<>(getMapPictureInAlbum(argument).values());
    }

    @Override
    public String getType(Picture picture) {
        return picture.getAlbum();
    }
}
