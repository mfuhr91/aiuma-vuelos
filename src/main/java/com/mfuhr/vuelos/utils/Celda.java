package com.mfuhr.vuelos.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
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
}
