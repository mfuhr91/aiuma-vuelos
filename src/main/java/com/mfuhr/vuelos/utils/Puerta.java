package com.mfuhr.vuelos.utils;

public enum Puerta {
    
    NA(""),
    UNO("1"),
    DOS("2"),
    TRES("3"),
    CUATRO("4"),
    CINCO("5"),
    SEIS("6");

    private String nro;

    Puerta(String nro){
        this.nro = nro;
    }

    public String getNro(){
        return this.nro;
    }

}
