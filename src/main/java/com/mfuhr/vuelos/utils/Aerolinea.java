package com.mfuhr.vuelos.utils;

import lombok.AllArgsConstructor;

import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Aerolinea {

    NA(0,"Seleccionar una aerolínea","Seleccionar una aerolínea"),
    AR(1,"AR","Aerolineas Argentinas"),
    WJ(2,"WJ","JetSmart"),
    LD(3,"LD","LADE"),
    PR(4,"PR","Privado");

    private Integer nro;
    private String cod;
    private String nombre;
 
}
