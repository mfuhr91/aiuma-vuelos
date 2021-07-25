package com.mfuhr.vuelos.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
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

}
