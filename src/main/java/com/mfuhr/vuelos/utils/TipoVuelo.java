package com.mfuhr.vuelos.utils;

public enum TipoVuelo {
    
    REGULAR("REGULAR"),
    ESPECIAL("ESPECIAL"),
    PRIVADO("PRIVADO");

    private String valor;

    TipoVuelo(String valor){
        this.valor = valor;
    }

    public String getValor(){
        return valor;
    }
}
