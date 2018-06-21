package com.example.akiyoshi.albumsole.models;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class PictureAbstract {

    protected Map<String, Map<Integer, Picture>> pictures;

    public Picture AddPicture(Picture picture) {
        Map<Integer, Picture> target = pictures.get(getType(picture));
        if(target == null){
            target = new HashMap<Integer, Picture>();
            pictures.put(getType(picture), target);
        }else{
            if(target.get(picture.getId()) != null){
                return picture;
            }
        }

        return target.put(picture.getId(), picture);
    }


    public abstract String getType(Picture picture);

    public Map<String, Map<Integer, Picture>> getMapPictures(){
        return pictures;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Picture> getListAllPicture(){
        ArrayList<Picture> list = new ArrayList<>();

        int n = this.pictures.size();

        this.pictures.forEach((k,v)->list.addAll(v.values()));

        return list;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<String> getListKey(){
        ArrayList<String> list = new ArrayList<>();
        this.pictures.forEach((k,v) -> list.add(k));
        return list;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void deleteItem(Picture picture){
        if(this.pictures.containsKey(getType(picture))){
            if(this.pictures.get(getType(picture)).containsKey(picture.getId())){
                this.pictures.get(getType(picture)).remove(picture.getId());

                if(this.pictures.get(getType(picture)).size() == 0){
                    this.pictures.remove(getType(picture));
                }
            }
        }
    }

}
