package com.mfuhr.vuelos.services;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.mfuhr.vuelos.models.Importado;
import com.mfuhr.vuelos.models.Vuelo;
import com.mfuhr.vuelos.models.VueloImportado;
import com.mfuhr.vuelos.models.repos.ImportadoRepository;
import com.mfuhr.vuelos.models.repos.VueloRepository;
import com.mfuhr.vuelos.utils.Celda;
import com.mfuhr.vuelos.utils.Dia;
import com.mfuhr.vuelos.utils.Mes;
import com.mfuhr.vuelos.utils.TipoVuelo;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImportarVueloServiceImpl implements ImportarVueloService {

    private final Logger log = LoggerFactory.getLogger(VueloServiceImpl.class);
    
    @Autowired
    private VueloRepository vueloRepo;
    
    @Autowired
    private ImportadoRepository importadoRepo;
    
    public void guardarImportacion(Importado importado) {
        this.importadoRepo.save(importado);
    }
    
    @Override
    public List<VueloImportado> leerExcel(MultipartFile file) throws IOException {
        List<VueloImportado> vueloImportadoList = new ArrayList<>();

        boolean esXLSX = file.getOriginalFilename().endsWith("xlsx");
    
        // Row row = worksheet.getRow(0);    
        if(esXLSX){
            XSSFWorkbook workbook = new XSSFWorkbook((file.getInputStream()));
            XSSFSheet worksheet = workbook.getSheetAt(0);
            for (Row row : worksheet) {
                VueloImportado vueloImportado = leerFila(row);
                if(vueloImportado == null){
                    continue;
                }
                vueloImportadoList.add(vueloImportado);
            }
            workbook.close();
        } else {
            HSSFWorkbook workbook = new HSSFWorkbook((file.getInputStream()));
            HSSFSheet worksheet = workbook.getSheetAt(0);
            for (Row row : worksheet) {
                VueloImportado vueloImportado = leerFila(row);
                if(vueloImportado == null){
                    continue;
                }
                vueloImportadoList.add(vueloImportado);
            }
            workbook.close();
        }   
        log.info("fin importacion del archivo: ".concat(file.getOriginalFilename()));
        return vueloImportadoList;
    }

    private VueloImportado leerFila(Row row) {
        
        VueloImportado vueloImportado = new VueloImportado();

        if (row.getPhysicalNumberOfCells() < 15)
            return null;

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
            String equipo;
            try {
                equipo = row.getCell(Celda.EQUIPO.getNro()).getStringCellValue();
                if(equipo.contains("330")){
                    vueloImportado.setGranPorte(true);
                }
            } catch (Exception e){
                
                Integer equipoInt = (int) row.getCell(Celda.EQUIPO.getNro()).getNumericCellValue();
                if(equipoInt == 330){
                    vueloImportado.setGranPorte(true);
                }
                equipo = equipoInt.toString();
            }
            
            vueloImportado.setEquipo(equipo);
            
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

            return vueloImportado;
        }
        return null;
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
    public void guardarImport(List<VueloImportado> vueloImportadoList, String nombreArchivo) {
        if (vueloImportadoList.size() > 0) {

            Importado importado = new Importado(getNuevaVersion());

            importado.setCantidadRegistros(vueloImportadoList.size());
            importado.setNombreArchivo(nombreArchivo);
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
                    vuelo.setGranPorte(vueloImportado.getGranPorte());
                    vuelo.setFecha(fecha);
                    vuelo.setImportado(importado);

                    this.vueloRepo.save(vuelo);
                    
                    continue bucleFechas;
                }
            }
        }
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
    public List<Importado> buscarTodosImportados() {
        return this.importadoRepo.findAll();
    }

    @Override
    public void borrarImportPorId(Long id) {
       this.importadoRepo.deleteById(id);
    }

    @Override
    public List<Vuelo> buscarVuelosPorImportados(Importado importado) {
        return this.vueloRepo.findByImportado(importado);
    }

    @Override
    public Importado buscarImportadoPorId(Long id) {
        return this.importadoRepo.findById(id).orElse(null);
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

        return ultimoImport != null ? ultimoImport.getVersion() + 1 : 1;
    }
}
