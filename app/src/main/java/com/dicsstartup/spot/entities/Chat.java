package com.dicsstartup.spot.entities;

import java.io.Serializable;
import java.util.ArrayList;

public class Chat implements Serializable {

    String id;
    ArrayList<String> miembros= new ArrayList<>();
    ArrayList<Mensaje> mensajes= new ArrayList<>();

    public Chat() {
    }

    public Chat(String id,ArrayList<String> miembros) {
        this.id= id;
        this.miembros = miembros;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getMiembros() {
        return miembros;
    }

    public void setMiembros(ArrayList<String> miembros) {
        this.miembros = miembros;
    }

    public ArrayList<Mensaje> getMensajes() {
        return mensajes;
    }

    public void setMensajes(ArrayList<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }
}
