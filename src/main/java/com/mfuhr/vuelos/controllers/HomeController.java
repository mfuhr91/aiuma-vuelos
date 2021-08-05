package com.mfuhr.vuelos.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.mfuhr.vuelos.services.VueloService;
import com.mfuhr.vuelos.utils.Aviso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("")
public class HomeController {

    private final Logger log = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    VueloService vueloService;
    
    @GetMapping({"","/","/home","/index","/inicio"})
    public String home(Model model){    

        
        return "inicio";
    }

    @RequestMapping("/fechaSeleccionada")
    public String getFecha(Model model, @RequestParam String fechaString){

        log.info("fecha seleccionada:  ".concat(fechaString));
        LocalDate fecha = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        fecha = LocalDate.parse(fechaString,dtf);
        
        List<Aviso> avisos = this.vueloService.comprobarVuelos(fecha);
        log.info("total avisos: ".concat(String.valueOf(avisos != null ? avisos.size() : "null")));
        model.addAttribute("avisos", avisos);
       
        return "inicio :: avisos";
    }


}
