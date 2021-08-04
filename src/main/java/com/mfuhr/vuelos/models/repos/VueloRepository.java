package com.mfuhr.vuelos.models.repos;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.mfuhr.vuelos.models.Importado;
import com.mfuhr.vuelos.models.Vuelo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VueloRepository extends JpaRepository<Vuelo, Long>{
    
    @Query(value = "SELECT * FROM vuelos as v WHERE v.fecha = :fecha", nativeQuery = true)
    public List<Vuelo> buscarVuelosPorFecha(@Param("fecha")Date fecha);

    public Optional<Vuelo> findById(Long id);

    public List<Vuelo> findByImportado(Importado importado);

    @Query(value = "SELECT * FROM vuelos as v WHERE v.fecha >= :fechaDesde AND v.fecha <= :fechaHasta AND v.destino = 'USH'", nativeQuery = true)
    public List<Vuelo> buscarArribosDelMes(@Param("fechaDesde") LocalDate fechaDesde, @Param("fechaHasta") LocalDate fechaHasta);

    @Query(value = "SELECT COUNT(*) FROM vuelos as v WHERE v.destino = 'USH' AND v.fecha = :fecha", nativeQuery = true)
    public Integer totalVuelos(@Param("fecha") LocalDate fecha);

    @Query(value = "SELECT * FROM vuelos as v WHERE v.fecha >= :fechaDesde AND v.fecha <= :fechaHasta", nativeQuery = true)
    public List<Vuelo> buscarEntreFechas(@Param("fechaDesde") LocalDate fechaDesde, @Param("fechaHasta") LocalDate fechaHasta);
}
