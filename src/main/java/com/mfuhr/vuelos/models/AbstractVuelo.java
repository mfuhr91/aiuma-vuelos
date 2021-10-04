package com.mfuhr.vuelos.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.mfuhr.vuelos.utils.Estado;
import com.mfuhr.vuelos.utils.Posicion;
import com.mfuhr.vuelos.utils.Puerta;
import com.mfuhr.vuelos.utils.TipoVuelo;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
@MappedSuperclass
public abstract class AbstractVuelo implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Debe indicar un nro de vuelo")
    private String nroVuelo;

    @Transient
    private String equipo;

    @Enumerated(value = EnumType.STRING)
    private TipoVuelo tipoVuelo;

    private Boolean granPorte;

    @NotNull(message = "Debe indicar una fecha")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;

    @NotBlank(message = "Debe seleccionar un origen")
    private String origen;

    @NotBlank(message = "Debe seleccionar un destino")
    private String destino;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaSalida;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaArribo;

    @Enumerated(value = EnumType.STRING)
    private Estado estado;

    @Enumerated(value = EnumType.STRING)
    private Puerta puerta;

    @Enumerated(value = EnumType.STRING)
    private Posicion pos;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Importado importado;

    public Boolean esGranPorte(){
        return this.granPorte;
    }

    public void setGranPorte(Boolean granPorte){
        this.granPorte = granPorte;
    }

    public AbstractVuelo(){
        this.estado = Estado.EN_HORARIO;
        this.puerta = Puerta.NA;
        this.pos = Posicion.NA;
    }

    public String getCompania(){
        String arr[] = this.nroVuelo.split(" ");
        return arr[0];
    }

    public int getNro(){
        String arr[] = this.nroVuelo.split(" ");
        return Integer.parseInt(arr[1]);
    }
}