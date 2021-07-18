package com.mfuhr.vuelos.models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

import com.mfuhr.vuelos.utils.Estado;
import com.mfuhr.vuelos.utils.Posicion;
import com.mfuhr.vuelos.utils.Puerta;
import com.mfuhr.vuelos.utils.TipoVuelo;

import lombok.Data;

@Data
@MappedSuperclass
public abstract class AbstractVuelo implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nroVuelo;
    private TipoVuelo tipoVuelo;
    private Date fecha;
    private String origen;
    private String destino;
    private Date horaSalida;
    private Date horaArribo;
    private Estado estado;
    private Puerta puerta;
    private Posicion pos;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Importado importado;

    public AbstractVuelo(){
        this.estado = Estado.EN_HORARIO;
        this.puerta = Puerta.NA;
        this.pos = Posicion.NA;
    }
}