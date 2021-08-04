package com.mfuhr.vuelos.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Estado {
 
    EN_HORARIO("En horario","On time"),
    PREEMBARQUE("Pre-embarque","Pre-boarding"),
    EMBARCANDO("Embarcando","Boarding"),
    DEMORADO("Demorado","Delayed"),
    CANCELADO("Cancelado","Cancelled"),
    CERRADO("Cerrado","Closed");

    private String valorEs;
    private String valorEn;

}
