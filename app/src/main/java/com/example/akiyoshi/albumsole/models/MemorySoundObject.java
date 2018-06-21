package com.example.akiyoshi.albumsole.models;

public class MemorySoundObject {
    private String name;
    private String path;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public MemorySoundObject(String name, String path) {
        this.name = name;
        this.path = path;
    }


}
