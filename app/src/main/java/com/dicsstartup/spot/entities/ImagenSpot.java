package com.dicsstartup.spot.entities;

import java.io.Serializable;

public class ImagenSpot implements Serializable {
    String url;
    String path;

    public ImagenSpot() {
    }

    public ImagenSpot(String url, String path) {
        this.url = url;
        this.path = path;
    }

    public String getUri() {
        return url;
    }

    public void setUri(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
