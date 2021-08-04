package com.mfuhr.vuelos.models;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class VueloForm extends AbstractVuelo{
    
    @NotBlank
    private String aerolinea;
}
