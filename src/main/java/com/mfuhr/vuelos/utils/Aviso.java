package com.mfuhr.vuelos.utils;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Aviso {

    private Date fecha;
    private Integer cantidadVuelos;
    
}
