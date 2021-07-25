package com.mfuhr.vuelos.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Posicion {

    NA("Seleccionar una posición"),
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

    public static List<Posicion> getPosiciones(){
        return Arrays.stream(Posicion.values()).filter(pos -> (!pos.getValor().isBlank())).collect(Collectors.toList());
     }
    
}
