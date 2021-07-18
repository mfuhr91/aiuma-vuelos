package com.mfuhr.vuelos.controllers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.mfuhr.vuelos.models.Vuelo;
import com.mfuhr.vuelos.models.VueloImportado;
import com.mfuhr.vuelos.services.VueloService;
import com.mfuhr.vuelos.utils.Posicion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/nuevo")
    public String nuevoVuelo(Model model){

        return "form-vuelo";
    }


}
