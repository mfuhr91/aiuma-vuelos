package com.mfuhr.vuelos.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Puerta {
    
    NA("Seleccionar una puerta"),
    UNO("1"),
    DOS("2"),
    TRES("3"),
    CUATRO("4"),
    CINCO("5"),
    SEIS("6");

    private String nro;

    public static List<Puerta> getPuertas(){
       return Arrays.stream(Puerta.values()).filter(puerta -> (!puerta.getNro().isBlank())).collect(Collectors.toList());
    }

}
