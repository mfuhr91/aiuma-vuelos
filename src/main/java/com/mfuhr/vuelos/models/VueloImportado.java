package com.mfuhr.vuelos.models;

import java.time.LocalDate;
import java.util.List;

import com.mfuhr.vuelos.utils.Dia;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VueloImportado extends AbstractVuelo{

    private List<Dia> dias;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;

}
