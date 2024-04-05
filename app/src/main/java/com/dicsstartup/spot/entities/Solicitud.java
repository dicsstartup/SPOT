package com.dicsstartup.spot.entities;


import java.io.Serializable;

public class Solicitud implements Serializable {
    String usuarioAmigo;
    String fecha;
    boolean aceptada;

    public Solicitud() {
    }

    public Solicitud(String id, String s, boolean b) {
    }

    public String getUsuarioAmigo() {
        return usuarioAmigo;
    }

    public void setUsuarioAmigo(String usuarioAmigo) {
        this.usuarioAmigo = usuarioAmigo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public boolean isAceptada() {
        return aceptada;
    }

    public void setAceptada(boolean aceptada) {
        this.aceptada = aceptada;
    }


}
