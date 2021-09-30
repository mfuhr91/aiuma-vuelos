package com.mfuhr.vuelos.services;

import java.io.IOException;
import java.util.List;

import com.mfuhr.vuelos.models.Importado;
import com.mfuhr.vuelos.models.Vuelo;
import com.mfuhr.vuelos.models.VueloImportado;

import org.springframework.web.multipart.MultipartFile;

public interface ImportarVueloService {
    
    public List<VueloImportado> leerExcel(MultipartFile file) throws IOException;

    public void guardarImport(List<VueloImportado> vueloImportadoList, String nombreArchivo);

    public void borrarUltimoImport();

    public List<Importado> buscarTodosImportados();

    public List<Vuelo> buscarVuelosPorImportados(Importado importado);

    public void borrarImportPorId(Long id);

    public Importado buscarImportadoPorId(Long id);
}
