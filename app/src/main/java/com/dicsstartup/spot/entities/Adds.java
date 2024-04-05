package com.dicsstartup.spot.entities;

import com.dicsstartup.spot.enumerados.TipoAdd;

public class Adds {
    String nombre;
    String url;
    String urlmedia;
    TipoAdd tipoadd;

    public Adds() {

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlmedia() {
        return urlmedia;
    }

    public void setUrlmedia(String urlmedia) {
        this.urlmedia = urlmedia;
    }



    public TipoAdd getTipoadd() {
        return tipoadd;
    }

    public void setTipoadd(TipoAdd tipoadd) {
        this.tipoadd = tipoadd;
    }


}
