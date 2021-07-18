package com.mfuhr.vuelos.utils;


public enum Estado {
 
    EN_HORARIO("En horario","On time"),
    PREEMBARQUE("Pre-embarque","Pre-boarding"),
    EMBARCANDO("Embarcando","Boarding"),
    DEMORADO("Demorado","Delayed"),
    CANCELADO("Cancelado","Cancelled"),
    CERRADO("Cerrado","Closed");

    private String valorEs;
    private String valorEn;

    Estado(String valorEs, String valorEn){
        this.valorEs = valorEs;
        this.valorEn = valorEn;
    }

    public String getValorEs(){
        return this.valorEs;
    }
    
    public String getValorEn(){
        return this.valorEn;
    }
}
