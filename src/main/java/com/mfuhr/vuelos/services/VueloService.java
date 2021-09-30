package com.mfuhr.vuelos.services;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.mfuhr.vuelos.models.Aeropuerto;
import com.mfuhr.vuelos.models.Vuelo;
import com.mfuhr.vuelos.models.VueloForm;
import com.mfuhr.vuelos.utils.Aviso;

import org.springframework.ui.Model;

public interface VueloService {

    public void guardar(Vuelo vuelo);
    
    

    public List<Vuelo> buscarVuelosPorFecha(Date fecha);

    public Vuelo buscarPorId(Long id);

    public void borrarPorId(Long id);

    

    public List<Aviso> comprobarVuelos(LocalDate fecha);

    

    public List<Aeropuerto> buscarAeropuertos();

    public Aeropuerto buscarAeropuertoPorCodigo(String codigo);

    public Model cargarVista(Model model, VueloForm vueloForm, String tipo);
    public Model validarHora(Model model, VueloForm vueloForm, String tipo);
    public Model validarAerolinea(Model model, VueloForm vueloForm, String tipo);

    public Integer contarVuelosDiarios(LocalDate fecha);

    public List<Vuelo> buscarEntreFechas(LocalDate fechaDesde, LocalDate fechaHasta);

    

}
