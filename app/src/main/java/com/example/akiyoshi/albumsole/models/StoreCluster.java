package com.example.akiyoshi.albumsole.models;


import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class StoreCluster implements ClusterItem {
    private LatLng mPosition;
    private String mTitle;
    private String mSnippet;

    public StoreCluster(LatLng position, String snippet, int id){
        mPosition = position;
        mSnippet = snippet;
        mTitle = id+"";
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }
}
