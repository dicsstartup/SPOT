package com.dicsstartup.spot.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Spot implements Serializable {

    private double latitud;
    private double longitud;
    private List<String> tags= new ArrayList<>();
    private List<ImagenSpot> imagenes= new ArrayList<>();
    private List<String> likes= new ArrayList<>();
    private List<String> comentarios= new ArrayList<>();

    private String nombre;
    private String id;
    private String nombreCiudad;
    private String tipoSpot;
    private String descripcion;
    private String usuarioCreador;


    public Spot() {

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<ImagenSpot> getImagenes() {
        return imagenes;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setImagenes(List<ImagenSpot> imagenes) {
        this.imagenes = imagenes;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public List<String> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<String> comentarios) {
        this.comentarios = comentarios;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombreCiudad() {
        return nombreCiudad;
    }

    public void setNombreCiudad(String nombreCiudad) {
        this.nombreCiudad = nombreCiudad;
    }

    public String getTipoSpot() {
        return tipoSpot;
    }

    public void setTipoSpot(String tipoSpot) {
        this.tipoSpot = tipoSpot;
    }

    public String getUsuarioCreador() {
        return usuarioCreador;
    }

    public void setUsuarioCreador(String usuarioCreador) {
        this.usuarioCreador = usuarioCreador;
    }
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + nombre.hashCode();
        long latitudBits = Double.doubleToLongBits(latitud);
        long longitudBits = Double.doubleToLongBits(longitud);
        result = 31 * result + (int) (latitudBits ^ (latitudBits >>> 32));
        result = 31 * result + (int) (longitudBits ^ (longitudBits >>> 32));
        return result;
    }
    public boolean existeEnLista(ArrayList<Spot> lista) {
        return lista.stream().anyMatch(spot -> spot.getId().equals(this.getId())||(spot.getNombre().equals(this.nombre)&& spot.getNombreCiudad().equals(this.getNombreCiudad())));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Spot other = (Spot) obj;
        return nombre.equals(other.nombre)|| Double.compare(latitud, other.latitud) == 0 && Double.compare(longitud, other.longitud) == 0;
    }

}