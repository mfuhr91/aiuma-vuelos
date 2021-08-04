package com.mfuhr.vuelos.services;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import com.mfuhr.vuelos.models.VueloImportado;
import com.mfuhr.vuelos.models.repos.AeropuertoRepository;
import com.mfuhr.vuelos.models.repos.ImportadoRepository;
import com.mfuhr.vuelos.models.repos.VueloRepository;
import com.mfuhr.vuelos.utils.Aerolinea;
import com.mfuhr.vuelos.utils.Aviso;
import com.mfuhr.vuelos.utils.Celda;
import com.mfuhr.vuelos.utils.Dia;
import com.mfuhr.vuelos.utils.Estado;
import com.mfuhr.vuelos.utils.Mes;
import com.mfuhr.vuelos.utils.Posicion;
import com.mfuhr.vuelos.utils.Puerta;
import com.mfuhr.vuelos.utils.TipoVuelo;
import com.mfuhr.vuelos.utils.Usado;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

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
    public List<VueloImportado> leerExcel(MultipartFile file) throws IOException {
        List<VueloImportado> vueloImportadoList = new ArrayList<>();

        XSSFWorkbook workbook = new XSSFWorkbook((file.getInputStream()));
        XSSFSheet worksheet = workbook.getSheetAt(0);
        // Row row = worksheet.getRow(0);
        for (Row row : worksheet) {
            VueloImportado vueloImportado = new VueloImportado();

            if (row.getPhysicalNumberOfCells() < 15)
                continue;

            if (row.getCell(Celda.ORIGEN.getNro()).getStringCellValue().trim().toLowerCase().equals("ush")
                    || row.getCell(Celda.DESTINO.getNro()).getStringCellValue().trim().toLowerCase().equals("ush")) {

                List<Dia> diaList = new ArrayList<Dia>();
                String nroVueloString = row.getCell(Celda.NRO_VUELO.getNro()).getStringCellValue().trim();
                String aerolinea = nroVueloString.substring(0,2);
                String nro = nroVueloString.substring(2, nroVueloString.length()).trim();
                
                nroVueloString = aerolinea.concat(" ").concat(nro);

                vueloImportado.setNroVuelo(nroVueloString);

                TipoVuelo tipoVuelo = Arrays.stream(TipoVuelo.values())
                                            .filter( tipo -> tipo.name().equalsIgnoreCase(row.getCell(Celda.TIPO_VUELO.getNro()).getStringCellValue().trim()))
                                            .findFirst()
                                            .get();
                                            
                vueloImportado.setTipoVuelo(tipoVuelo);

                
                String equipo = row.getCell(Celda.EQUIPO.getNro()).getStringCellValue();
                
                if(equipo.contains("330")){
                    vueloImportado.setGranPorte(true);
                }
                
                int i = 6;
                for (Dia dia : Dia.values()) {
                    if (row.getCell(i).getStringCellValue().trim().equalsIgnoreCase("x")) {
                        diaList.add(dia);
                    } else {
                        diaList.add(null);
                    }
                    i++;
                }
                if (diaList.size() > 0) {
                    vueloImportado.setDias(diaList);
                }
                try {
                    vueloImportado.setFechaDesde(row.getCell(Celda.FECHA_DESDE.getNro()).getLocalDateTimeCellValue().toLocalDate());
                    vueloImportado.setFechaHasta(row.getCell(Celda.FECHA_HASTA.getNro()).getLocalDateTimeCellValue().toLocalDate());
                } catch (Exception e) {
                    LocalDate fechaDesde = parsearStringFecha(row.getCell(Celda.FECHA_DESDE.getNro()).getStringCellValue());
                    LocalDate fechaHasta = parsearStringFecha(row.getCell(Celda.FECHA_HASTA.getNro()).getStringCellValue());
                    vueloImportado.setFechaDesde(fechaDesde);
                    vueloImportado.setFechaHasta(fechaHasta);
                }

                vueloImportado.setOrigen(row.getCell(Celda.ORIGEN.getNro()).getStringCellValue().trim());
                vueloImportado.setHoraSalida(row.getCell(Celda.STD.getNro()).getLocalDateTimeCellValue().toLocalTime());
                vueloImportado.setDestino(row.getCell(Celda.DESTINO.getNro()).getStringCellValue().trim());
                vueloImportado.setHoraArribo(row.getCell(Celda.STA.getNro()).getLocalDateTimeCellValue().toLocalTime());

                vueloImportadoList.add(vueloImportado);
            }
        }
        workbook.close();
        return vueloImportadoList;
    }

    private LocalDate parsearStringFecha(String fechaString) {
        String dia = "";
        String mes = "";
        String anio = "";

        dia = fechaString.substring(0, 2);
        mes = fechaString.substring(2, 5);
        anio = fechaString.substring(5, 9);

        Mes mesEnum = Mes.getMesByCod(mes);

        fechaString = dia.concat("-").concat(mesEnum.getNro()).concat("-").concat(anio);

        LocalDate fecha = LocalDate.now();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        fecha = LocalDate.parse(fechaString,dtf);
        return fecha;
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
    public void guardarImport(List<VueloImportado> vueloImportadoList) {
        if (vueloImportadoList.size() > 0) {

            Importado importado = new Importado(getNuevaVersion());

            this.importadoRepo.save(importado);

            for (VueloImportado vueloImportado : vueloImportadoList) {
                guardarVuelo(importado, vueloImportado);
            }
        }
    }

    private void guardarVuelo(Importado importado, VueloImportado vueloImportado) {

        LocalDate fechaDesde = vueloImportado.getFechaDesde();
        LocalDate fechaHasta = vueloImportado.getFechaHasta();

        bucleFechas: 
        for (LocalDate fecha = fechaDesde; fechaDesde.isBefore(fechaHasta)
                || fechaDesde.equals(fechaHasta); fecha = fechaDesde = fechaDesde.plusDays(1)){

            int nroDia = fechaDesde.getDayOfWeek().getValue();

            for (Dia diaVuelo : vueloImportado.getDias()) {
                if (diaVuelo == null) {
                    continue;
                }

                if ( diaVuelo.getNro() == nroDia ) {
                    Vuelo vuelo = new Vuelo();

                    vuelo.setNroVuelo(vueloImportado.getNroVuelo());
                    if (vueloImportado.getDestino().equalsIgnoreCase("ush")) {
                        vuelo.setHoraArribo(vueloImportado.getHoraArribo());
                    } else {
                        vuelo.setHoraSalida(vueloImportado.getHoraSalida());
                    }
                    vuelo.setOrigen(vueloImportado.getOrigen());
                    vuelo.setDestino(vueloImportado.getDestino());
                    vuelo.setTipoVuelo(vueloImportado.getTipoVuelo());
                    vuelo.setFecha(fecha);
                    vuelo.setImportado(importado);

                    this.vueloRepo.save(vuelo);
                    
                    continue bucleFechas;
                }
            }
        }
    }

    @Override
    public void guardar(Vuelo vuelo) {
        this.vueloRepo.save(vuelo);
    }

    private Importado getUltimoImport() {
        List<Importado> importados = this.importadoRepo.findAll();

        if (importados.size() > 0) {

            Collections.sort(importados, (imp1, imp2) -> imp1.getVersion().compareTo(imp2.getVersion()));

            Importado ultimoImport = importados.get(importados.size() - 1);

            return ultimoImport;
        }
        return null;
    }

    private Integer getNuevaVersion() {

        Importado ultimoImport = getUltimoImport();

        return ultimoImport != null ? ultimoImport.getVersion() + 1 : 0;
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
    public void borrarUltimoImport() {

        Importado ultimoImport = getUltimoImport();

        if (ultimoImport == null) {
            return;
        }

        List<Vuelo> vuelos = this.vueloRepo.findByImportado(ultimoImport);

        for (Vuelo vuelo : vuelos) {
            this.vueloRepo.delete(vuelo);
        }

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