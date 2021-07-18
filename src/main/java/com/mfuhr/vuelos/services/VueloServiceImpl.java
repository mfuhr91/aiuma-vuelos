package com.mfuhr.vuelos.services;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.mfuhr.vuelos.models.Importado;
import com.mfuhr.vuelos.models.Vuelo;
import com.mfuhr.vuelos.models.VueloImportado;
import com.mfuhr.vuelos.models.repos.ImportadoRepository;
import com.mfuhr.vuelos.models.repos.VueloRepository;
import com.mfuhr.vuelos.utils.Aviso;
import com.mfuhr.vuelos.utils.Celda;
import com.mfuhr.vuelos.utils.Dia;
import com.mfuhr.vuelos.utils.TipoVuelo;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class VueloServiceImpl implements VueloService {

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

        XSSFWorkbook workbook = new XSSFWorkbook((file.getInputStream()));
        XSSFSheet worksheet = workbook.getSheetAt(0);

        for (Row row : worksheet) {
            VueloImportado vueloImportado = new VueloImportado();

            if (row.getPhysicalNumberOfCells() < 15) {
                continue;
            }

            if (row.getCell(Celda.ORIGEN.getNro()).getStringCellValue().toLowerCase().equals("ush")
                    || row.getCell(Celda.DESTINO.getNro()).getStringCellValue().toLowerCase().equals("ush")) {

                List<Dia> diaList = new ArrayList<Dia>();

                vueloImportado.setNroVuelo(row.getCell(Celda.NRO_VUELO.getNro()).getStringCellValue());

                for (TipoVuelo tipoVuelo : TipoVuelo.values()) {
                    if (tipoVuelo.name().equals(row.getCell(Celda.TIPO_VUELO.getNro()).getStringCellValue())) {
                        vueloImportado.setTipoVuelo(tipoVuelo);
                    }
                }

                int i = 6;
                for (Dia dia : Dia.values()) {
                    if (row.getCell(i).getStringCellValue().equals("X")) {
                        diaList.add(dia);
                    } else {
                        diaList.add(null);
                    }
                    i++;
                }
                if (diaList.size() > 0) {
                    vueloImportado.setDias(diaList);
                }

                vueloImportado.setFechaDesde(row.getCell(Celda.FECHA_DESDE.getNro()).getDateCellValue());
                vueloImportado.setFechaHasta(row.getCell(Celda.FECHA_HASTA.getNro()).getDateCellValue());
                vueloImportado.setOrigen(row.getCell(Celda.ORIGEN.getNro()).getStringCellValue());
                vueloImportado.setHoraSalida(row.getCell(Celda.STD.getNro()).getDateCellValue());
                vueloImportado.setDestino(row.getCell(Celda.DESTINO.getNro()).getStringCellValue());
                vueloImportado.setHoraArribo(row.getCell(Celda.STA.getNro()).getDateCellValue());

                vueloImportadoList.add(vueloImportado);
            }
        }
        workbook.close();
        return vueloImportadoList;
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

        Calendar fechaDesde = Calendar.getInstance();
        fechaDesde.setTime(vueloImportado.getFechaDesde());
        Calendar fechaHasta = Calendar.getInstance();
        fechaHasta.setTime(vueloImportado.getFechaHasta());

        bucleFechas: for (Date fecha = fechaDesde.getTime(); fechaDesde.before(fechaHasta)
                || fechaDesde.equals(fechaHasta); fechaDesde.add(Calendar.DATE, 1), fecha = fechaDesde.getTime()) {

            int nroDia = fechaDesde.get(Calendar.DAY_OF_WEEK) - 1;

            if (nroDia == 0)
                nroDia = 7;

            for (Dia diaVuelo : vueloImportado.getDias()) {
                if (diaVuelo == null) {
                    continue;
                }

                if (diaVuelo.getNro().equals(nroDia)) {
                    Vuelo vuelo = new Vuelo();

                    vuelo.setNroVuelo(vueloImportado.getNroVuelo());
                    if (vueloImportado.getDestino().equals("USH")) {
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
    public List<Aviso> comprobarVuelos(List<Vuelo> vuelos) {

        Calendar primerDiaMes = Calendar.getInstance();
        Calendar ultimoDiaMes = Calendar.getInstance();

        primerDiaMes.add(Calendar.MONTH, 0);
        primerDiaMes.set(Calendar.DAY_OF_MONTH, 1);
        primerDiaMes.set(Calendar.HOUR_OF_DAY, 0);
        primerDiaMes.set(Calendar.MINUTE, 0);
        primerDiaMes.set(Calendar.SECOND, 0);
        primerDiaMes.set(Calendar.MILLISECOND,0);

        ultimoDiaMes.add(Calendar.MONTH, 0);
        ultimoDiaMes.set(Calendar.DAY_OF_MONTH, ultimoDiaMes.getActualMaximum(Calendar.DAY_OF_MONTH));
        ultimoDiaMes.set(Calendar.HOUR_OF_DAY, 23);
        ultimoDiaMes.set(Calendar.MINUTE, 0);
        ultimoDiaMes.set(Calendar.SECOND, 0);
        ultimoDiaMes.set(Calendar.MILLISECOND,0);

        List<Date> diasDelMes = new ArrayList<>();
        while(primerDiaMes.before(ultimoDiaMes) || primerDiaMes.equals(ultimoDiaMes)){
            diasDelMes.add(primerDiaMes.getTime());
            System.out.println(primerDiaMes.getTime());
            primerDiaMes.add(Calendar.DAY_OF_MONTH, 1);
            
        }
        
        List<Aviso> avisos = new ArrayList<>();
        for (Date dia : diasDelMes) {
            List<Vuelo> vuelosDelDia = vuelos.stream().filter(vuelo -> dia.equals(vuelo.getFecha())).collect(Collectors.toList());
            List<Vuelo> vuelosDelDiaComp = vuelosDelDia;
            List<Vuelo> vuelosSolapados = new ArrayList<>();
            int cantidadSolapados = 1;
            primerBucle:
            for (Vuelo vuelo : vuelosDelDia){
                for(Vuelo vueloComp : vuelosDelDiaComp){
                    
                    if(vuelo.getNroVuelo().equals(vueloComp.getNroVuelo())){
                        
                        continue;
                    }
                    boolean estaSolapado = vuelosSolapados.stream().anyMatch(vueloSol -> vuelo.getNroVuelo().equals(vueloSol.getNroVuelo()));

                    if(estaSolapado) continue primerBucle;

                    Calendar horaPosterior = Calendar.getInstance();
                    horaPosterior.setTime(vuelo.getHoraArribo());
                    horaPosterior.add(Calendar.HOUR_OF_DAY, 1);
                    horaPosterior.set(Calendar.SECOND, 0);
                    horaPosterior.set(Calendar.MILLISECOND,0);
                    if ((vueloComp.getHoraArribo().after(vuelo.getHoraArribo())
                        || vueloComp.getHoraArribo().equals(vuelo.getHoraArribo()))
                        && (vueloComp.getHoraArribo().before(horaPosterior.getTime())
                        || vueloComp.getHoraArribo().equals(horaPosterior.getTime()))) {
                                        
                        cantidadSolapados ++;                
                        vuelosSolapados.add(vueloComp);
                        
                    }
                }
            }

            if(cantidadSolapados > 2){

                Aviso aviso = new Aviso();
                aviso.setCantidadVuelos(cantidadSolapados);
                aviso.setFecha(dia);
                avisos.add(aviso);
                
            }
        }
        
        return avisos.size() > 0 ? avisos : null;
    }

    @Override
    public List<Vuelo> buscarArribosDelMesActual() {

        Calendar primerDiaMes = Calendar.getInstance();
        Calendar ultimoDiaMes = Calendar.getInstance();

        // ASIGNO EL PRIMER DIA DEL MES CON HORA 00:00:00
        primerDiaMes.add(Calendar.MONTH, 0);
        primerDiaMes.set(Calendar.DAY_OF_MONTH, 1);
        primerDiaMes.set(Calendar.HOUR_OF_DAY, 0);
        primerDiaMes.set(Calendar.MINUTE, 0);
        primerDiaMes.set(Calendar.SECOND, 0);

        // ASIGNO EL ULTIMO DIA DEL MES CON HORA 23:59:59
        ultimoDiaMes.add(Calendar.MONTH, 0);
        ultimoDiaMes.set(Calendar.DAY_OF_MONTH, ultimoDiaMes.getActualMaximum(Calendar.DAY_OF_MONTH));
        ultimoDiaMes.set(Calendar.HOUR_OF_DAY, 23);
        ultimoDiaMes.set(Calendar.MINUTE, 59);
        ultimoDiaMes.set(Calendar.SECOND, 59);

        Date fechaDesde = primerDiaMes.getTime();
        Date fechaHasta = ultimoDiaMes.getTime();
        
        return this.vueloRepo.buscarArribosDelMes(fechaDesde, fechaHasta);
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

}