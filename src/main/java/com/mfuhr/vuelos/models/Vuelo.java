package com.mfuhr.vuelos.models;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "vuelos")
public class Vuelo extends AbstractVuelo{
    
}
