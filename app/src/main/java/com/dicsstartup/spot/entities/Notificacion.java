package com.dicsstartup.spot.entities;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;


public class Notificacion implements Serializable {
    Boolean Enterado;
	String Mensaje;
    String fecha;


    public Notificacion() {

    }

    public Notificacion(Boolean enterado, String mensaje, String fecha) {
        Enterado = enterado;
        Mensaje = mensaje;
        this.fecha = fecha;
    }

    public Boolean getEnterado() {
        return Enterado;
    }

    public void setEnterado(Boolean enterado) {
        Enterado = enterado;
    }

    public String getMensaje() {
        return Mensaje;
    }

    public void setMensaje(String mensaje) {
        Mensaje = mensaje;
    }


    public String getFecha() {
            return fecha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notificacion)) return false;
        Notificacion that = (Notificacion) o;
        return Objects.equals(getEnterado(), that.getEnterado()) && Objects.equals(getMensaje(), that.getMensaje()) && Objects.equals(getFecha(), that.getFecha());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEnterado(), getMensaje(), getFecha());
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    public boolean existeEnLista(ArrayList<Notificacion> lista) {
        return lista.stream().anyMatch(notificacion -> notificacion.equals(this));
    }

}
