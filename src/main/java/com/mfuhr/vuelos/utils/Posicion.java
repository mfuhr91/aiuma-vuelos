package com.mfuhr.vuelos.utils;

public enum Posicion {

    NA(""),
    UNO("1"),
    DOS("2"),
    TRES("3"),
    CUATRO("4"),
    CINCO("5"),
    SEIS("6"),
    SIETE("7"),
    OCHO("8"),
    AEROCLUB("Aeroclub"),
    AUXILIAR("Auxiliar");

    private String valor;

    Posicion(String valor){
        this.valor = valor;
    }

    public String getNro(){
        return this.valor;
    }
    
}
