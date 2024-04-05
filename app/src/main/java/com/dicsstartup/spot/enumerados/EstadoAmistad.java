package com.dicsstartup.spot.enumerados;

public enum EstadoAmistad {
    ACEPTADA(0),
    RECHAZADA(-1),
    PENDIENTE(1);

    private final int value;

    EstadoAmistad(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }



}
