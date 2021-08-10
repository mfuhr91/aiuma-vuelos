package com.mfuhr.vuelos.controllers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import com.mfuhr.vuelos.models.Vuelo;
import com.mfuhr.vuelos.models.VueloForm;
import com.mfuhr.vuelos.models.VueloImportado;
import com.mfuhr.vuelos.services.VueloService;
import com.mfuhr.vuelos.utils.Posicion;
import com.mfuhr.vuelos.utils.Puerta;
import com.mfuhr.vuelos.utils.TipoVuelo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vuelos")
public class VuelosController {

    private final Logger log = LoggerFactory.getLogger(VuelosController.class);

    @Autowired
    VueloService vueloService;

    private List<VueloImportado> vueloImportadoList;

    @GetMapping("")
    public String importar(Model model) {

        log.info("vista importar vuelos");
        model.addAttribute("titulo", "Importar vuelos");
        return "importar";
    }

    @PostMapping("/upload")
    public String upload(Model model, MultipartFile file, RedirectAttributes flash) throws IOException {
        try {
            log.info("upload archivo: ".concat(file.getOriginalFilename()));
            this.vueloImportadoList = this.vueloService.leerExcel(file);
        } catch (Exception e) {
            log.error("error al leer archivo");
            flash.addFlashAttribute("warning"," Debe seleccionar un archivo para importar!");
            System.out.println(e);
            return "redirect:/vuelos";
        }

        model.addAttribute("titulo", "Importar vuelos");
        model.addAttribute("vuelos", vueloImportadoList);
        return "importar";
    }

    @PostMapping("/guardarImport")
    public String guardarImport(RedirectAttributes flash) {
        if (vueloImportadoList.size() > 0) {
            log.info("guardar archivo importado");
            this.vueloService.guardarImport(vueloImportadoList);
            flash.addFlashAttribute("success","Vuelos importados con éxito!");
            /*
             * for (VueloImportado vuelo : vueloImportadoList) { }
             */
        }
        this.vueloImportadoList = null;
        return "redirect:/inicio";
    }

    @GetMapping("/privados")
    public String privados(Model model) {

        return "privados";
    }

    @GetMapping("/imprimirProg")
    public String formImprimir(Model model){
        log.info("vista imprimir programacion");
        model.addAttribute("titulo", "Imprimir programación");
        return "imprimir-programacion";
    }

    @PostMapping("/actualizar")
    public ResponseEntity<String> actualizar(Model model, @RequestBody Vuelo vuelo) {

        log.info("actualizar vuelo: ".concat(vuelo.getNroVuelo()));

        Vuelo vueloEncontrado = this.vueloService.buscarPorId(vuelo.getId());

        vueloEncontrado.setPos(vuelo.getPos());
        vueloEncontrado.setGranPorte(vuelo.esGranPorte());
        this.vueloService.guardar(vueloEncontrado);

        return new ResponseEntity<>("Vuelo actualizado con éxito!", HttpStatus.OK);
    }

    @PostMapping("/eliminar")
    public ResponseEntity<String> eliminar(Model model, @RequestParam String id) {
        log.info("eliminar vuelo con id: ".concat(id));
        this.vueloService.borrarPorId(Long.parseLong(id));

        return new ResponseEntity<>("Vuelo eliminado con éxito!", HttpStatus.OK);
    }

    @RequestMapping("/buscarArribos")
    public String buscarVuelosArribos(Model model, @RequestParam String fecha, RedirectAttributes flash) {

        log.info("buscar arribos de la fecha ".concat(fecha));
        List<Vuelo> vuelos = new ArrayList<Vuelo>();
        List<Vuelo> vuelosArribos = new ArrayList<Vuelo>();
        try {
            Date fechaVuelos = new SimpleDateFormat("dd/MM/yyyy").parse(fecha);
            vuelos = this.vueloService.buscarVuelosPorFecha(fechaVuelos);
        } catch (ParseException e) {
            log.error("La fecha recibida no se pudo formatear. FECHA RECIBIDA: ".concat(fecha).concat(" - ERROR")
                    .concat(e.toString()));

        }
        if (vuelos.size() > 0) {
            log.info("lista de vuelos cargada");
            for (Vuelo vuelo : vuelos) {
                if (vuelo.getDestino().equals("USH")) {
                    vuelosArribos.add(vuelo);
                }
            }

            Collections.sort(vuelosArribos, (v1, v2) -> v1.getHoraArribo().compareTo(v2.getHoraArribo()));

            model.addAttribute("puertas", Puerta.values());
            model.addAttribute("posiciones", Posicion.values());
            model.addAttribute("vuelosArribos", vuelosArribos);
        } 
        return "inicio :: tableArribos";
    }

    @RequestMapping("/buscarSalidas")
    public String buscarVuelosSalidas(Model model, @RequestParam String fecha, RedirectAttributes flash) {

        log.info("buscar salidas de la fecha ".concat(fecha));
        List<Vuelo> vuelos = new ArrayList<Vuelo>();
        List<Vuelo> vuelosSalidas = new ArrayList<Vuelo>();
        try {
            Date fechaVuelos = new SimpleDateFormat("dd/MM/yyyy").parse(fecha);
            vuelos = this.vueloService.buscarVuelosPorFecha(fechaVuelos);
        } catch (ParseException e) {
            log.error("La fecha recibida no se pudo formatear. FECHA RECIBIDA: ".concat(fecha).concat(" - ERROR")
                    .concat(e.toString()));
        }
        if (vuelos.size() > 0) {
            log.info("lista de vuelos cargada");
            for (Vuelo vuelo : vuelos) {
                if (vuelo.getOrigen().equals("USH")) {
                    vuelosSalidas.add(vuelo);
                }
            }

            Collections.sort(vuelosSalidas, (v1, v2) -> v1.getHoraSalida().compareTo(v2.getHoraSalida()));

            model.addAttribute("puertas", Puerta.values());
            model.addAttribute("posiciones", Posicion.values());
            model.addAttribute("vuelosSalidas", vuelosSalidas);
        }
        return "inicio :: tableSalidas";
    }

    @PostMapping("/borrarUltimoImport")
    public String borrarUltimoImport(Model model, RedirectAttributes flash){
        log.info("borrando ultimo import");
        this.vueloService.borrarUltimoImport();
        flash.addFlashAttribute("success", "Se ha eliminado la última importación realizada!");
        return "redirect:/inicio";
    }

    @GetMapping("/form/{tipo}") // tipo = arribo/salida
    public String nuevoVuelo(@PathVariable String tipo, Model model){
        
        VueloForm vueloForm = new VueloForm();

        model = this.vueloService.cargarVista(model, vueloForm, tipo);
        if(tipo.equals("arribo")){
            log.info("vista del form arribo");
            return "form-arribo";
        } else {
            log.info("vista del form salida");
            return "form-salida";
        }
    }

    @PostMapping("/guardar/{tipo}") // tipo = arribo/salida
    public String guardarArribo(@PathVariable String tipo, Model model,@Valid VueloForm vueloForm, BindingResult result, RedirectAttributes flash) {
        log.info("guardando ".concat(tipo));
        boolean esFormArribo = true;
        model = this.vueloService.validarAerolinea(model, vueloForm, tipo);
        model = this.vueloService.validarHora(model, vueloForm, tipo);

        if(!tipo.equals("arribo")){
            esFormArribo = false;
        }

        if(result.hasErrors()){
            log.error("el form tiene errores: ".concat(result.toString()));
            System.out.println(result);
            model = this.vueloService.validarAerolinea(model, vueloForm, tipo);
            model = this.vueloService.validarHora(model, vueloForm, tipo);
            model = this.vueloService.cargarVista(model, vueloForm, tipo);
            return esFormArribo ? "/form-arribo" : "/form-salida";
        }
        if(model.containsAttribute("aerolineaError")){
            log.error("error en la aerolinea");
            return esFormArribo ? "form-arribo" : "form-salida";
            
        }
        if(model.containsAttribute("horaError")){
            log.error("error en la hora");
            return esFormArribo ? "form-arribo" : "form-salida";
            
        }
        
        vueloForm.setNroVuelo(vueloForm.getAerolinea().concat(" ").concat(vueloForm.getNroVuelo()));
        
        Vuelo vuelo = new Vuelo();
        if(vueloForm.getId() != null){
            vuelo = this.vueloService.buscarPorId(vueloForm.getId());
        }
        if(vueloForm.getAerolinea().equals(TipoVuelo.PRIVADO.getCod())){
            vuelo.setTipoVuelo(TipoVuelo.PRIVADO);
        }
        
        vuelo.setFecha(vueloForm.getFecha());
        vuelo.setNroVuelo(vueloForm.getNroVuelo());
        vuelo.setGranPorte(vueloForm.esGranPorte());

        if(esFormArribo){
            vuelo.setOrigen(vueloForm.getOrigen());
            vuelo.setDestino("USH");
            vuelo.setHoraArribo(vueloForm.getHoraArribo());         
        } else {
            // es form salida
            vuelo.setOrigen("USH");
            vuelo.setDestino(vueloForm.getDestino());
            vuelo.setHoraSalida(vueloForm.getHoraSalida());
        }
        vuelo.setEstado(vueloForm.getEstado());
        vuelo.setPuerta(vueloForm.getPuerta());
        vuelo.setPos(vueloForm.getPos());

        this.vueloService.guardar(vuelo);
        log.info("vuelo guardado: ".concat(vuelo.getNroVuelo()));
        flash.addFlashAttribute("success","Vuelo guardado con éxito!");

        return "redirect:/inicio";
    }

    @GetMapping("/editar/{tipo}/{id}")
    public String editarVuelo(@PathVariable String tipo, @PathVariable Long id, Model model){
       
        

        Vuelo vuelo = this.vueloService.buscarPorId(id);
        log.info("vuelo encontrado ".concat(vuelo.getNroVuelo()).concat(" - id: ").concat(vuelo.getId().toString()));
        VueloForm vueloForm = new VueloForm();
        model = this.vueloService.cargarVista(model, vueloForm, tipo);
        
        vueloForm.setId(vuelo.getId());
        vueloForm.setOrigen(vuelo.getOrigen());
        vueloForm.setDestino(vuelo.getDestino());
        vueloForm.setFecha(vuelo.getFecha());
        vueloForm.setEstado(vuelo.getEstado());
        vueloForm.setPos(vuelo.getPos());
        vueloForm.setPuerta(vuelo.getPuerta());
        vueloForm.setAerolinea(vuelo.getNroVuelo().substring(0, 2));
        vueloForm.setNroVuelo(vuelo.getNroVuelo().substring(3, vuelo.getNroVuelo().length()).trim());
        vueloForm.setGranPorte(vuelo.esGranPorte());
        
        model.addAttribute("titulo", "Editar vuelo");
        if(tipo.equals("arribo")){
            log.info("cargar vista form-arribo");
            vueloForm.setHoraArribo(vuelo.getHoraArribo());
            return "form-arribo";
        } else {
            log.info("cargar vista form-salida");
            vueloForm.setHoraSalida(vuelo.getHoraSalida());
            return "form-salida";
        }
    }

    @GetMapping("/totalDiarios")
    public @ResponseBody Integer totalVuelosDiarios(@RequestParam String fechaString){

        LocalDate fecha = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        fecha = LocalDate.parse(fechaString,dtf);
        Integer total = this.vueloService.contarVuelosDiarios(fecha);
        
        log.info("total vuelos: ".concat(String.valueOf(total)).concat(" - del dia: ").concat(fechaString));
        return total;
    }


    // MAPEA A LA CLASE imprimirProgrmacionPDF.java o imprimirProgramacionXLS.java PARA GENERAR EL PDF
    @GetMapping("/imprimirProgramacion/{type}/{fechaDesdeString}/{fechaHastaString}")
    public String imprimirProgramacion(@PathVariable String type, @PathVariable String fechaDesdeString, @PathVariable String fechaHastaString,  Model model, RedirectAttributes flash){
        
        log.info("buscar vuelos para imprimir entre: ".concat(fechaDesdeString).concat(" hasta: ").concat(fechaHastaString));
        LocalDate fechaDesde = LocalDate.now();
        LocalDate fechaHasta = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        fechaDesde = LocalDate.parse(fechaDesdeString,dtf);
        fechaHasta = LocalDate.parse(fechaHastaString,dtf);
        List<Vuelo> vuelos = this.vueloService.buscarEntreFechas(fechaDesde, fechaHasta);
        

        if(fechaDesde.isAfter(fechaHasta)){
            log.info("fechaDesde posterior a fechaHasta");
            model.addAttribute("titulo", "Imprimir programación");
            flash.addFlashAttribute("error"," Debe seleccionar una 'fecha desde' menor a la 'fecha hasta'!");
            return "redirect:/vuelos/imprimirProg";
        }
        
        if(vuelos.isEmpty()){
            log.info("lista de vuelos para imprimir vacia");
            model.addAttribute("titulo", "Imprimir programación");
            flash.addFlashAttribute("error"," No existen vuelos entre las fechas ingresadas!");
            return "redirect:/vuelos/imprimirProg";
        }
        
        
        model.addAttribute("vuelos", vuelos);
        model.addAttribute("fechaDesde", fechaDesde).addAttribute("fechaHasta", fechaHasta);
        
        if(type.equals("pdf")){
            log.info("imprimir pdf");
            return "/vuelos/imprimirProgramacionPDF"; // debe quedar con el "/vuelos/imprimirProgramacion" ya que no es una vista html, sino la ruta de un componente clase
        } else {
            log.info("descargar xls");
            return "/vuelos/imprimirProgramacionXLS";
        }
    }

}
