package com.mfuhr.vuelos.services;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.mfuhr.vuelos.models.Vuelo;
import com.mfuhr.vuelos.models.VueloImportado;
import com.mfuhr.vuelos.utils.Aviso;

import org.springframework.web.multipart.MultipartFile;

public interface VueloService {

    public void guardar(Vuelo vuelo);
    
    public List<VueloImportado> leerExcel(MultipartFile file) throws IOException;

    public List<Vuelo> buscarVuelosPorFecha(Date fecha);

    public List<Vuelo> buscarArribosDelMesActual();

    public Vuelo buscarPorId(Long id);

    public void borrarPorId(Long id);

    public void guardarImport(List<VueloImportado> vueloImportadoList);

    public List<Aviso> comprobarVuelos(List<Vuelo> vuelos);

    public void borrarUltimoImport();
}
