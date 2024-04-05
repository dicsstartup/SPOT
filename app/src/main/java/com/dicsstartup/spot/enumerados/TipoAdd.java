package com.dicsstartup.spot.enumerados;

public enum TipoAdd {
    IMAGE("image"),
    WEB("Web");

    private final String value;

    TipoAdd(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }



}
