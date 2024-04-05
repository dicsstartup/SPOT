package com.dicsstartup.spot.entities;

import java.io.Serializable;

public class Mensaje implements Serializable {
    String IdEmisor;
    String mensaje;
    String fecha;

    public Mensaje() {
    }

    public Mensaje(String idEmisor, String mensaje, String fecha) {
        IdEmisor = idEmisor;
        this.mensaje = mensaje;
        this.fecha = fecha;
    }

    public String getIdEmisor() {
        return IdEmisor;
    }

    public void setIdEmisor(String idEmisor) {
        IdEmisor = idEmisor;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
