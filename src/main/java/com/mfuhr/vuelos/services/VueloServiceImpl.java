package com.mfuhr.vuelos.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.mfuhr.vuelos.models.Aeropuerto;
import com.mfuhr.vuelos.models.Importado;
import com.mfuhr.vuelos.models.Vuelo;
import com.mfuhr.vuelos.models.VueloForm;
import com.mfuhr.vuelos.models.repos.AeropuertoRepository;
import com.mfuhr.vuelos.models.repos.ImportadoRepository;
import com.mfuhr.vuelos.models.repos.VueloRepository;
import com.mfuhr.vuelos.utils.Aerolinea;
import com.mfuhr.vuelos.utils.Aviso;
import com.mfuhr.vuelos.utils.Estado;
import com.mfuhr.vuelos.utils.Posicion;
import com.mfuhr.vuelos.utils.Puerta;
import com.mfuhr.vuelos.utils.Usado;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class VueloServiceImpl implements VueloService {
    
    private final Logger log = LoggerFactory.getLogger(VueloServiceImpl.class);
    
    @Autowired
    private VueloRepository vueloRepo;
    
    @Autowired
    private ImportadoRepository importadoRepo;
    
    @Autowired
    private AeropuertoRepository aeropuertoRepo;
    
    public void guardarImportacion(Importado importado) {
        this.importadoRepo.save(importado);
    }

    @Override
    public List<Vuelo> buscarVuelosPorFecha(Date fecha) {
        return this.vueloRepo.buscarVuelosPorFecha(fecha);
    }

    @Override
    public Vuelo buscarPorId(Long id) {
        return this.vueloRepo.findById(id).orElse(null);
    }

    @Override
    public void borrarPorId(Long id) {
        this.vueloRepo.deleteById(id);
    }

    

    @Override
    public void guardar(Vuelo vuelo) {
        this.vueloRepo.save(vuelo);
    }

    

    @Override
    public List<Aviso> comprobarVuelos(LocalDate fecha) {

        Map<String, LocalDate> mapa = getPrimerUltimoDiaMes(fecha);
        LocalDate primerDiaMes = mapa.get("primerDiaMes");
        LocalDate ultimoDiaMes = mapa.get("ultimoDiaMes");

        List<Vuelo> vuelos =  this.vueloRepo.buscarArribosDelMes(primerDiaMes, ultimoDiaMes);
    

        List<LocalDate> diasDelMes = new ArrayList<>();
        while (primerDiaMes.isBefore(ultimoDiaMes) || primerDiaMes.equals(ultimoDiaMes)) {
            diasDelMes.add(primerDiaMes);
            primerDiaMes = primerDiaMes.plusDays(1);

        }

        List<Aviso> avisos = new ArrayList<>();
        for (LocalDate dia : diasDelMes) {
            List<Vuelo> vuelosDelDia = vuelos.stream().filter(vuelo -> dia.equals(vuelo.getFecha()))
                    .collect(Collectors.toList());

            if(vuelosDelDia.isEmpty()){
                continue;
            }
            List<Vuelo> vuelosDelDiaComp = vuelosDelDia;

            for (Vuelo vuelo : vuelosDelDia) {
                Set<String> vuelosSolapados = new HashSet<>();

                for (Vuelo vueloComp : vuelosDelDiaComp) {        

                    if (vuelo.getNroVuelo().equals(vueloComp.getNroVuelo())) {
                        vuelosSolapados.add(vuelo.getNroVuelo());
                        continue;
                    }

                    LocalTime horaPosterior = vuelo.getHoraArribo();
                    horaPosterior = horaPosterior.plusHours(1);
                
                    if ((vueloComp.getHoraArribo().isAfter(vuelo.getHoraArribo())
                            || vueloComp.getHoraArribo().equals(vuelo.getHoraArribo()))
                            && (vueloComp.getHoraArribo().isBefore(horaPosterior)
                                    || vueloComp.getHoraArribo().equals(horaPosterior))) {
                        
                        vuelosSolapados.add(vueloComp.getNroVuelo());

                    }
                }
                if (vuelosSolapados.size() > 2) {

                    Aviso aviso = new Aviso();
                    aviso.setCantidadVuelos(vuelosSolapados.size());
                    aviso.setFecha(dia);
                    avisos.add(aviso);
                    break;
                }
            }

           
        }

        return avisos.size() > 0 ? avisos : null;
    }


    public Map<String, LocalDate> getPrimerUltimoDiaMes(LocalDate fecha) {

        LocalDate primerDiaMes = fecha;
        LocalDate ultimoDiaMes = fecha;

        primerDiaMes = primerDiaMes.with(TemporalAdjusters.firstDayOfMonth());
    
        ultimoDiaMes = ultimoDiaMes.with(TemporalAdjusters.lastDayOfMonth());

        Map<String, LocalDate> mapa = new HashMap<String, LocalDate>();
        mapa.put("primerDiaMes", primerDiaMes);
        mapa.put("ultimoDiaMes", ultimoDiaMes);

        return mapa;
    }
    
    @Override
    public List<Aeropuerto> buscarAeropuertos() {
        return this.aeropuertoRepo.findAll();
    }

    @Override
    public Aeropuerto buscarAeropuertoPorCodigo(String codigo) {
        return this.aeropuertoRepo.findByCodigo(codigo);
    }

    @Override
    public Model cargarVista(Model model, VueloForm vueloForm, String tipo) {

        
        vueloForm.setFecha(vueloForm.getFecha() != null ? vueloForm.getFecha() : LocalDate.now());
        List<Aeropuerto> aeropuertos = this.buscarAeropuertos();

        List<Aerolinea> aerolineas = Arrays.asList(Aerolinea.values());
        List<Usado> usados = Arrays.asList(Usado.values());
        List<Estado> estados = Arrays.asList(Estado.values());
        List<Puerta> puertas = Arrays.asList(Puerta.values());
        List<Posicion> posiciones = Arrays.asList(Posicion.values());
        
        model.addAttribute("aeropuertos", aeropuertos);
        model.addAttribute("usados", usados);
        model.addAttribute("estados", estados);
        model.addAttribute("puertas", puertas);
        model.addAttribute("posiciones", posiciones);
        model.addAttribute("aerolineas", aerolineas);
        model.addAttribute("vueloForm", vueloForm);

        Aeropuerto aero = this.buscarAeropuertoPorCodigo("USH");
        if(tipo.equals("arribo")){
            vueloForm.setDestino(aero.getCodigo().concat(" - ".concat(aero.getNombre())));
            model.addAttribute("titulo", "Agregar arribo");
        } else {
            vueloForm.setOrigen(aero.getCodigo().concat(" - ".concat(aero.getNombre())));
            model.addAttribute("titulo", "Agregar salida");
        }
        return model;
    }

    @Override
    public Model validarHora(Model model, VueloForm vueloForm, String tipo) {

        if(tipo.equals("arribo")){
            if(vueloForm.getHoraArribo() == null){
                model = this.cargarVista(model, vueloForm, tipo);
                model.addAttribute("horaError", "Debe indicar la hora de arribo");
                return model;
            }
        } else {
            if(vueloForm.getHoraSalida() == null){
                model = this.cargarVista(model, vueloForm, tipo);
                model.addAttribute("horaError", "Debe indicar la hora de salida");
                return model;
            }
        }
        return model;
    }

    @Override
    public Model validarAerolinea(Model model, VueloForm vueloForm, String tipo) {
        if(vueloForm.getAerolinea().equals(Aerolinea.NA.name())){
            model = this.cargarVista(model, vueloForm, tipo);
            model.addAttribute("aerolineaError", "Debe seleccionar una aerolinea");
            return model;
        }
        return model;
    }

    @Override
    public Integer contarVuelosDiarios(LocalDate fecha) {
        return this.vueloRepo.totalVuelos(fecha);
    }

    @Override
    public List<Vuelo> buscarEntreFechas(LocalDate fechaDesde, LocalDate fechaHasta) {
        return this.vueloRepo.buscarEntreFechas(fechaDesde, fechaHasta);
    }


}