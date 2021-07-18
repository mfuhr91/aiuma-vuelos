package com.mfuhr.vuelos.utils;

public enum Dia {
    
    LUNES(1,"LUNES"),
    MARTES(2,"MARTES"),
    MIERCOLES(3,"MIERCOLES"),
    JUEVES(4,"JUEVES"),
    VIERNES(5,"VIERNES"),
    SABADO(6,"SABADO"),
    DOMINGO(7,"DOMINGO");

    private Integer nro;
    private String valor;

    Dia(Integer nro, String valor){
        this.nro = nro;
        this.valor = valor;
    }

    public Integer getNro() {
        return nro;
    }

    public String getValor() {
        return valor;
    }
}
