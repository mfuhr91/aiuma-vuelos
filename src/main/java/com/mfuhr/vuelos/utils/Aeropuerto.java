package com.mfuhr.vuelos.utils;


public enum Aeropuerto {
    
    USH(1,"USH"),
    FTE(2,"FTE"),
    REL(3,"REL");

    private String valor;
    
    private Integer nro;
    
    Aeropuerto(Integer nro, String valor){
        this.nro = nro;
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public Integer getNro() {
        return nro;
    }
}
