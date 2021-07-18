package com.mfuhr.vuelos.utils;


public enum Celda {
    
    NRO_VUELO(0),
    TIPO_VUELO(1),
    FECHA_DESDE(13),
    FECHA_HASTA(14),
    ORIGEN(15),
    STD(16),
    DESTINO(17),
    STA(18);

    private Integer nro;

    Celda(Integer nro){
        this.nro = nro;
    }

    public Integer getNro() {
        return nro;
    }

    public void setNro(Integer nro) {
        this.nro = nro;
    }
}
