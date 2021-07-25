package com.mfuhr.vuelos.services;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.mfuhr.vuelos.models.Aeropuerto;
import com.mfuhr.vuelos.models.Vuelo;
import com.mfuhr.vuelos.models.VueloForm;
import com.mfuhr.vuelos.models.VueloImportado;
import com.mfuhr.vuelos.utils.Aviso;

import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

public interface VueloService {

    public void guardar(Vuelo vuelo);
    
    public List<VueloImportado> leerExcel(MultipartFile file) throws IOException;

    public List<Vuelo> buscarVuelosPorFecha(Date fecha);

    public Vuelo buscarPorId(Long id);

    public void borrarPorId(Long id);

    public void guardarImport(List<VueloImportado> vueloImportadoList);

    public List<Aviso> comprobarVuelos(LocalDate fecha);

    public void borrarUltimoImport();

    public List<Aeropuerto> buscarAeropuertos();

    public Aeropuerto buscarAeropuertoPorCodigo(String codigo);

    public Model cargarVista(Model model, VueloForm vueloForm, String tipo);
    public Model validarHora(Model model, VueloForm vueloForm, String tipo);
    public Model validarAerolinea(Model model, VueloForm vueloForm, String tipo);

    public Integer contarVuelosDiarios(LocalDate fecha);

}
