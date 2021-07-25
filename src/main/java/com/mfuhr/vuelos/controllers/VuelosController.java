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

        return "importar";
    }

    @PostMapping("/upload")
    public String upload(Model model, MultipartFile file) throws IOException {
        try {
            this.vueloImportadoList = this.vueloService.leerExcel(file);
        } catch (Exception e) {
            System.out.println(e);

        }
        model.addAttribute("message", "File: " + file.getOriginalFilename() + " has been uploaded successfully!");

        model.addAttribute("vuelos", vueloImportadoList);
        return "importar";
    }

    @PostMapping("/guardarImport")
    public String guardarImport() {

        if (vueloImportadoList.size() > 0) {
            this.vueloService.guardarImport(vueloImportadoList);
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

    @PostMapping("/actualizar")
    public ResponseEntity<String> actualizar(Model model, @RequestBody Vuelo vuelo) {

        System.out.println(vuelo.getId().toString().concat(" - ").concat(vuelo.getNroVuelo()).concat(" - ")
                .concat(vuelo.getPos().toString()));

        Vuelo vueloEncontrado = this.vueloService.buscarPorId(vuelo.getId());

        vueloEncontrado.setPos(vuelo.getPos());
        this.vueloService.guardar(vueloEncontrado);

        return new ResponseEntity<>("Vuelo actualizado con éxito!", HttpStatus.OK);
    }

    @PostMapping("/eliminar")
    public ResponseEntity<String> eliminar(Model model, @RequestParam String id) {

        this.vueloService.borrarPorId(Long.parseLong(id));

        return new ResponseEntity<>("Vuelo eliminado con éxito!", HttpStatus.OK);
    }

    @RequestMapping("/buscarArribos")
    public String buscarVuelosArribos(Model model, @RequestParam String fecha, RedirectAttributes flash) {
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

            for (Vuelo vuelo : vuelos) {
                if (vuelo.getDestino().equals("USH")) {
                    vuelosArribos.add(vuelo);
                }
            }

            Collections.sort(vuelosArribos, (v1, v2) -> v1.getHoraArribo().compareTo(v2.getHoraArribo()));

            model.addAttribute("puertas", Puerta.values());
            model.addAttribute("posiciones", Posicion.values());
            model.addAttribute("vuelosArribos", vuelosArribos);
        } else {
            flash.addFlashAttribute("warning", "No hay vuelos este día!");
        }
        return "inicio :: home-table";
    }

    @RequestMapping("/buscarSalidas")
    public String buscarVuelosSalidas(Model model, @RequestParam String fecha, RedirectAttributes flash) {
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

            for (Vuelo vuelo : vuelos) {
                if (vuelo.getOrigen().equals("USH")) {
                    vuelosSalidas.add(vuelo);
                }
            }

            Collections.sort(vuelosSalidas, (v1, v2) -> v1.getHoraSalida().compareTo(v2.getHoraSalida()));

            model.addAttribute("puertas", Puerta.values());
            model.addAttribute("posiciones", Posicion.values());
            model.addAttribute("vuelosSalidas", vuelosSalidas);
        } else {
            flash.addFlashAttribute("warning", "No hay vuelos este día!"); // TODO: ARMAR BIEN
        }
        return "inicio :: home-table";
    }

    @PostMapping("/borrarUltimoImport")
    public String borrarUltimoImport(Model model){

        this.vueloService.borrarUltimoImport();

        return "redirect:/inicio";
    }

    @GetMapping("/form/{tipo}") // tipo = arribo/salida
    public String nuevoVuelo(@PathVariable String tipo, Model model){
        
        VueloForm vueloForm = new VueloForm();

        model = this.vueloService.cargarVista(model, vueloForm, tipo);
        if(tipo.equals("arribo")){
            return "form-arribo";
        } else {
            return "form-salida";
        }
    }

    @PostMapping("/guardar/{tipo}") // tipo = arribo/salida
    public String guardarArribo(@PathVariable String tipo, Model model,@Valid VueloForm vueloForm, BindingResult result, RedirectAttributes flash) {

        boolean esFormArribo = true;
        model = this.vueloService.validarAerolinea(model, vueloForm, tipo);
        model = this.vueloService.validarHora(model, vueloForm, tipo);

        if(!tipo.equals("arribo")){
            esFormArribo = false;
        }

        if(result.hasErrors()){
            System.out.println(result);
            model = this.vueloService.validarAerolinea(model, vueloForm, tipo);
            model = this.vueloService.validarHora(model, vueloForm, tipo);
            model = this.vueloService.cargarVista(model, vueloForm, tipo);
            return esFormArribo ? "/form-arribo" : "/form-salida";
        }
        if(model.containsAttribute("aerolineaError")){
            return esFormArribo ? "/form-arribo" : "/form-salida";
            
        }
        if(model.containsAttribute("horaError")){
            return esFormArribo ? "/form-arribo" : "/form-salida";
            
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

        flash.addFlashAttribute("msj"," Vuelo guardado con éxito!").addFlashAttribute("clase","success");

        return "redirect:/inicio";
    }

    @GetMapping("/editar/{tipo}/{id}")
    public String editarVuelo(@PathVariable String tipo, @PathVariable Long id, Model model){
       
        

        Vuelo vuelo = this.vueloService.buscarPorId(id);

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
        
        model.addAttribute("titulo", "Editar vuelo");
        if(tipo.equals("arribo")){
            vueloForm.setHoraArribo(vuelo.getHoraArribo());
            return "form-arribo";
        } else {
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

        return total;
    }

}
