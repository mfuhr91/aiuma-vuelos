package com.mfuhr.vuelos.utils;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Aviso {

    private LocalDate fecha;
    private Integer cantidadVuelos;
    
}
