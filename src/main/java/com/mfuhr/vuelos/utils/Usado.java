package com.mfuhr.vuelos.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Usado {
    
    AEP(1,"AEP"),
    EZE(2,"EZE"),
    FTE(3,"FTE"),
    REL(4,"REL"),
    CRD(5,"CRD");

    private Integer nro;
    private String cod;
}
