package com.mfuhr.vuelos.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoVuelo {
    
    NA("NA","NA"),
    REGULAR("RG","REGULAR"),
    ESPECIAL("ES","ESPECIAL"),
    PRIVADO("PR","PRIVADO");

    private String cod;
    private String valor;
}
