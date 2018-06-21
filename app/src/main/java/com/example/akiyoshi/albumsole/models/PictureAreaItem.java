package com.example.akiyoshi.albumsole.models;

public class PictureAreaItem {
    private String monthly;
    private Picture picture;
    private RcvItemType type;

    public PictureAreaItem(String monthly, Picture picture, RcvItemType type) {
        this.monthly = monthly;
        this.picture = picture;
        this.type = type;
    }

    public String getMonthly() {
        return monthly;
    }

    public void setMonthly(String monthly) {
        this.monthly = monthly;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public RcvItemType getType() {
        return type;
    }

    public void setType(RcvItemType type) {
        this.type = type;
    }
}
