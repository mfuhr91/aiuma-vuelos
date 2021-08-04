package com.mfuhr.vuelos.models.repos;

import com.mfuhr.vuelos.models.Aeropuerto;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AeropuertoRepository extends JpaRepository<Aeropuerto, Long> {
    
    Aeropuerto findByCodigo(String codigo);
}
