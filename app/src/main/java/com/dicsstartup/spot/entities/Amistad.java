package com.dicsstartup.spot.entities;

import com.dicsstartup.spot.enumerados.EstadoAmistad;

import java.io.Serializable;
import java.util.ArrayList;

public class Amistad implements Serializable {

    String idAmigo;
    String fecha;
    EstadoAmistad estadoAmistad;
    String idChat;
    boolean Envia;

    public Amistad() {
    }

    public String getIdAmigo() {
        return idAmigo;
    }

    public void setIdAmigo(String idAmigo) {
        this.idAmigo = idAmigo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public EstadoAmistad getEstadoAmistad() {
        return estadoAmistad;
    }

    public void setEstadoAmistad(EstadoAmistad estadoAmistad) {
        this.estadoAmistad = estadoAmistad;
    }

    public boolean isEnvia() {
        return Envia;
    }

    public void setEnvia(boolean envia) {
        Envia = envia;
    }

    public String getIdChat() {
        return idChat;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }
    public boolean existeEnLista(ArrayList<Amistad> lista) {
        return lista.stream().anyMatch(amistad -> amistad.idAmigo.equals(this.idAmigo));
    }
}
