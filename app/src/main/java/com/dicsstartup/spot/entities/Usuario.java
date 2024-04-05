package com.dicsstartup.spot.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.IntStream;

public class Usuario implements Serializable {
    private String id;
    private String nick;
    private String email;
    private String password;
    private String avatar;
    public ArrayList<Amistad> amigos= new ArrayList<>();
   // public ArrayList<Solicitud> solicitudes= new ArrayList<>();
    public ArrayList<Notificacion> notificaciones= new ArrayList<>();
    //public ArrayList<NotificacionAmistad> solicitudesOut= new ArrayList<>();


    public Usuario() {
        // Constructor vacío requerido para Firebase
    }

    public Usuario(String nick, String email) {
        this.nick = nick;
        this.email = email;
        this.avatar= "https://ui-avatars.com/api/?background=random&name=" +nick.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public ArrayList<Amistad> getAmigos() {
        return amigos;
    }

    public void setAmigos(ArrayList<Amistad> amigos) {
        this.amigos = amigos;
    }

    public ArrayList<Notificacion> getNotificaciones() {
        return notificaciones;
    }

    public void setNotificaciones(ArrayList<Notificacion> notificaciones) {
        this.notificaciones = notificaciones;

    }



    public int obtenerIndiceAmistadConId(String idBuscado) {
        Optional<Integer> indice = IntStream.range(0, this.getAmigos().size())
                .filter(i -> amigos.get(i).idAmigo.equals(idBuscado))
                .boxed()
                .findFirst();
        return indice.orElse(-1);
    }

}