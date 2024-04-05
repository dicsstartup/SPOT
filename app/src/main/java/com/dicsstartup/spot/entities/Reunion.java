package com.dicsstartup.spot.entities;

import java.util.ArrayList;

public class Reunion {
    String Fecha;
    String idSpot;
    String id;
    String idCreador;
    String descripcion;
    ArrayList<String> Asistencias= new ArrayList<>();

    public Reunion() {
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public String getIdSpot() {
        return idSpot;
    }

    public void setIdSpot(String idSpot) {
        this.idSpot = idSpot;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public ArrayList<String> getAsistencias() {
        return Asistencias;
    }

    public void setAsistencias(ArrayList<String> asistencias) {
        Asistencias = asistencias;
    }

    public String getIdCreador() {
        return idCreador;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIdCreador(String idCreador) {
        this.idCreador = idCreador;
    }
    public boolean existeEnLista(ArrayList<Reunion> lista) {
        return lista.stream().anyMatch(reunion -> reunion.getId().equals(this.getId()));
    }
}
