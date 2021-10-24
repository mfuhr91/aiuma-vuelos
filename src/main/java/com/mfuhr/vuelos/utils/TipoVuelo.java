package com.mfuhr.vuelos.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoVuelo {
    
    NA("NA","NA"),
    REGULAR("RG","REGULAR"),
    NO_REGULAR("NR","NO REGULAR"),
    ESPECIAL("ES","ESPECIAL"),
    PRIVADO("PR","PRIVADO"),
    CHARTER("CH","CHARTER");

    private String cod;
    private String valor;
}
